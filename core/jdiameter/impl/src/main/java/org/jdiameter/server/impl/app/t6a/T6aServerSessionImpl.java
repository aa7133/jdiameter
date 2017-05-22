package org.jdiameter.server.impl.app.t6a;


import org.jdiameter.api.Answer;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;

import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.app.StateEvent;

import org.jdiameter.api.t6a.ServerT6aSession;
import org.jdiameter.api.t6a.ServerT6aSessionListener;
import org.jdiameter.api.t6a.events.JConfigurationInformationAnswer;
import org.jdiameter.api.t6a.events.JConfigurationInformationRequest;
import org.jdiameter.api.t6a.events.JConnectionManagementAnswer;
import org.jdiameter.api.t6a.events.JConnectionManagementRequest;
import org.jdiameter.api.t6a.events.JMO_DataAnswer;
import org.jdiameter.api.t6a.events.JMO_DataRequest;
import org.jdiameter.api.t6a.events.JMT_DataAnswer;
import org.jdiameter.api.t6a.events.JMT_DataRequest;

import org.jdiameter.api.t6a.events.JReportingInformationAnswer;
import org.jdiameter.api.t6a.events.JReportingInformationRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.app.t6a.IT6aMessageFactory;

import org.jdiameter.common.api.app.t6a.T6aSessionState;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.jdiameter.common.impl.app.t6a.T6aSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Copyright (c) 2017. AT&T Intellectual Property. All rights reserved
 */

/**
 * Created by Adi Enzel on 3/13/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public class T6aServerSessionImpl extends T6aSession implements ServerT6aSession, EventListener<Request, Answer>, NetworkReqListener {
  private static final Logger logger = LoggerFactory.getLogger(T6aServerSessionImpl.class);

  // Factories and Listeners --------------------------------------------------
  private transient ServerT6aSessionListener listener;
  protected long appId = -1;
  protected IServerT6aSessionData sessionData;


    /**
     *
     * @param sessionData
     * @param fct
     * @param sf
     * @param lst
     */
  public T6aServerSessionImpl(IServerT6aSessionData sessionData, IT6aMessageFactory fct, ISessionFactory sf, ServerT6aSessionListener lst) {
    super(sf, sessionData);
    if (lst == null) {
      throw new IllegalArgumentException("Listener can not be null");
    }
    if ((this.appId = fct.getApplicationId()) < 0) {
      throw new IllegalArgumentException("ApplicationId can not be less than zero");
    }

    this.listener = lst;
    super.messageFactory = fct;
    this.sessionData = sessionData;
  }

  protected void send(Event.Type type, AppEvent request, AppEvent answer) throws InternalException {
    try {
      if (type != null) {
        handleEvent(new Event(type, request, answer));
      }
    }
    catch (Exception e) {
      throw new InternalException(e);
    }
  }

  @Override
  public void sendConfigurationInformationRequest(JConfigurationInformationRequest request)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, request, null);
  }

  @Override
  public void sendConfigurationInformationAnswer(JConfigurationInformationAnswer answer)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, null, answer);
  }

  @Override
  public void sendReportingInformationAnswer(JReportingInformationAnswer answer)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, null, answer);
  }

  @Override
  public void sendConnectionManagementRequest(JConnectionManagementRequest request)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, request, null);
  }

  @Override
  public void sendConnectionManagementAnswer(JConnectionManagementAnswer answer)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, null, answer);
  }

  @Override
  public void sendMT_DataRequest(JMT_DataRequest request)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, request, null);
  }

  @Override
  public void sendMO_DataAnswer(JMO_DataAnswer answer)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, null, answer);
  }

  /**
   *
   * @param timerName
   */
  @Override
  public void onTimer(String timerName) {
    if (timerName.equals(IDLE_SESSION_TIMER_NAME)) {
      checkIdleAppSession();
    }
    else if (timerName.equals(T6aSession.TIMER_NAME_MSG_TIMEOUT)) {
      try {
        sendAndStateLock.lock();
        try {
          handleEvent(new Event(Event.Type.TIMEOUT_EXPIRES, new AppRequestEventImpl(this.sessionData.getBuffer()), null));
        }
        catch (Exception e) {
          logger.debug("Failure handling Timeout event.");
        }
        this.sessionData.setBuffer(null);
        this.sessionData.setTsTimerId(null);
      }
      finally {
        sendAndStateLock.unlock();
      }
    }
    else {
      logger.warn("Received an unknown timer '{}' for Session-ID '{}'", timerName, getSessionId());
    }
  }

  @Override
  public Answer processRequest(Request request) {
    RequestDelivery rd = new RequestDelivery();
    rd.session = this;
    rd.request = request;
    super.scheduler.execute(rd);
    return null;
  }


  @Override
  public boolean handleEvent(StateEvent event) throws InternalException, OverloadException {
    try {
      sendAndStateLock.lock();
      if (!super.session.isValid()) {
        // FIXME: throw new InternalException("Generic session is not valid.");
        return false;
      }
      final T6aSessionState state = this.sessionData.getT6aSessionState();
      T6aSessionState newState = null;
      Event localEvent = (Event) event;
      Event.Type eventType = (Event.Type) event.getType();
      switch (state) {

        case IDLE:
          switch (eventType) {
            case RECEIVE_CIR:
              this.sessionData.setBuffer((Request) ((AppEvent) event.getData()).getMessage());
              super.cancelMsgTimer();
              super.startMsgTimer();
              newState = T6aSessionState.OPEN;
              setState(newState);
              listener.doSendConfigurationInformationRequestEvent(this, (JConfigurationInformationRequest) event.getData());
              break;

            case RECEIVE_CMR:
              this.sessionData.setBuffer((Request) ((AppEvent) event.getData()).getMessage());
              super.cancelMsgTimer();
              super.startMsgTimer();
              newState = T6aSessionState.OPEN;
              setState(newState);
              listener.doSendConnectionManagementRequestEvent(this, (JConnectionManagementRequest) event.getData());
              break;
            case RECEIVE_ODR:
              this.sessionData.setBuffer((Request) ((AppEvent) event.getData()).getMessage());
              super.cancelMsgTimer();
              super.startMsgTimer();
              newState = T6aSessionState.OPEN;
              setState(newState);
              listener.doSendMO_DataRequestEvent(this, (JMO_DataRequest) event.getData());
              break;
            case RECEIVE_RIR:
              this.sessionData.setBuffer((Request) ((AppEvent) event.getData()).getMessage());
              super.cancelMsgTimer();
              super.startMsgTimer();
              newState = T6aSessionState.OPEN;
              setState(newState);
              listener.doSendReportingInformationRequestEvent(this, (JReportingInformationRequest) event.getData());
              break;
            case SEND_MESSAGE:
              super.session.send(((AppEvent) event.getData()).getMessage(), this);
              newState = T6aSessionState.OPEN;
              setState(newState);
              break;

            default:
              logger.error("Wrong action in S6t Server FSM. State: IDLE, Event Type: {}", eventType);
              break;
          }
          break;

        case OPEN:
          switch (eventType) {
            case TIMEOUT_EXPIRES:
              newState = T6aSessionState.TIMEDOUT;
              setState(newState);
              break;

            case SEND_MESSAGE:
              try {
                super.session.send(((AppEvent) event.getData()).getMessage(), this);
              }
              finally {
                newState = T6aSessionState.DISCONNECTED;
                setState(newState);
              }
              break;
            case RECEIVE_CIA:
              try {
                super.cancelMsgTimer();
                listener.doSendConfigurationInformationAnswerEvent(this, (JConfigurationInformationRequest) localEvent.getRequest(),
                      (JConfigurationInformationAnswer) localEvent.getAnswer());
              }
              finally {
                newState = T6aSessionState.DISCONNECTED;
                setState(newState);
              }
              break;
            case RECEIVE_CMA:
              try {
                super.cancelMsgTimer();
                listener.doSendConnectionManagementAnswertEvent(this, (JConnectionManagementRequest) localEvent.getRequest(),
                      (JConnectionManagementAnswer) localEvent.getAnswer());
              }
              finally {
                newState = T6aSessionState.DISCONNECTED;
                setState(newState);
              }
              break;
            case RECEIVE_TDA:
              try {
                super.cancelMsgTimer();
                listener.doSendMT_DataAnswertEvent(this, (JMT_DataRequest) localEvent.getRequest(),
                      (JMT_DataAnswer) localEvent.getAnswer());
              }
              finally {
                newState = T6aSessionState.DISCONNECTED;
                setState(newState);
              }
              break;

            default:
              throw new InternalException("T6a Should not receive more messages after initial. Command: " + event.getData());
          }
          break;

        case DISCONNECTED:
          throw new InternalException("T6a Can't receive message in state DISCONNECTED. Command: " + event.getData());

        case TIMEDOUT:
          throw new InternalException("T6a Can't receive message in state TIMEDOUT. Command: " + event.getData());

        default:
          logger.error("T6a Server FSM in wrong state: {}", state);
          break;
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      logger.error(e.toString());
      throw new InternalException(e);
    }
    finally {
      sendAndStateLock.unlock();
    }

    return true;
  }


  /**
   *
   * @param stateType type of state
   * @param <E>
   * @return
   */
  @Override
  //@SuppressWarnings("unchecked")
  public <E> E getState(Class<E> stateType) {
    return stateType == T6aSessionState.class ? (E) this.sessionData.getT6aSessionState() : null;
  }

  /**
   *
   * @param request the request has timeout
   */
  @Override
  public void timeoutExpired(Request request) {
    try {
      handleEvent(new Event(Event.Type.TIMEOUT_EXPIRES, new AppRequestEventImpl(request), null));
    }
    catch (Exception e) {
      logger.debug("Failed to process timeout message", e);
    }
  }


  /**
   *
   * @param newState
   */
  protected void setState(T6aSessionState newState) {
    T6aSessionState oldState = this.sessionData.getT6aSessionState();
    this.sessionData.setT6aSessionState(newState);

    for (StateChangeListener i : stateListeners) {
      i.stateChanged(this, oldState, newState);
    }
    if (newState == T6aSessionState.DISCONNECTED || newState == T6aSessionState.TIMEDOUT) {
      super.cancelMsgTimer();
      this.release();
    }
  }

  /**
   *
   * @param request the request message
   * @param answer the answer on application request
   */
  @Override
  public void receivedSuccessMessage(Request request, Answer answer) {
    AnswerDelivery rd = new AnswerDelivery();
    rd.session = this;
    rd.request = request;
    rd.answer = answer;
    super.scheduler.execute(rd);
  }

  /**
   *
   */
  private class RequestDelivery implements Runnable {
    ServerT6aSession session;
    Request request;

    @Override
    public void run() {
      try {
        switch (request.getCommandCode()) {
          case JConfigurationInformationRequest.code:
            handleEvent(new Event(Event.Type.RECEIVE_CIR, messageFactory.createConfigurationInformationRequest(request), null));
            break;
          case JConnectionManagementRequest.code:
            handleEvent(new Event(Event.Type.RECEIVE_CMR, messageFactory.createConnectionManagementRequest(request), null));
            break;
          case JReportingInformationRequest.code:
            handleEvent(new Event(Event.Type.RECEIVE_RIR, messageFactory.createReportingInformationRequest(request), null));
            break;
          case JMO_DataRequest.code:
            handleEvent(new Event(Event.Type.RECEIVE_ODR, messageFactory.createMO_DataRequest(request), null));
            break;

          default:
            listener.doOtherEvent(session, new AppRequestEventImpl(request), null);
            break;
        }
      }
      catch (Exception e) {
        logger.debug("Failed to process request message", e);
      }
    }
  }


  /**
   *
   */
  private class AnswerDelivery implements Runnable {
    ServerT6aSession session;
    Answer answer;
    Request request;

    @Override
    public void run() {
      try {
        switch (answer.getCommandCode()) {
          case JConfigurationInformationAnswer.code:
            handleEvent(new Event(Event.Type.RECEIVE_CIA ,messageFactory.createConfigurationInformationRequest(request),
                  messageFactory.createConfigurationInformationAnswer(answer)));
            break;
          case JConnectionManagementAnswer.code:
            handleEvent(new Event(Event.Type.RECEIVE_CMA ,messageFactory.createConnectionManagementRequest(request),
                  messageFactory.createConnectionManagementAnswer(answer)));
            break;
          case JMT_DataAnswer.code:
            handleEvent(new Event(Event.Type.RECEIVE_TDA ,messageFactory.createMT_DataRequest(request),
                  messageFactory.createMT_DataAnswer(answer)));
            break;

          default:
            listener.doOtherEvent(session, new AppRequestEventImpl(request), new AppAnswerEventImpl(answer));
            break;
        }
      }
      catch (Exception e) {
        logger.debug("Failed to process success message", e);
      }
    }
  }




}
