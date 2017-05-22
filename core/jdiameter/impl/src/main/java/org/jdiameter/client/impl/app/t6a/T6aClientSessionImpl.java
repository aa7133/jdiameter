package org.jdiameter.client.impl.app.t6a;


import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.OverloadException;


import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.api.t6a.ClientT6aSession;
import org.jdiameter.api.t6a.ClientT6aSessionListener;
import org.jdiameter.api.t6a.events.JConfigurationInformationAnswer;
import org.jdiameter.api.t6a.events.JConfigurationInformationRequest;
import org.jdiameter.api.t6a.events.JConnectionManagementRequest;
import org.jdiameter.api.t6a.events.JConnectionManagementAnswer;
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
public class T6aClientSessionImpl extends T6aSession implements ClientT6aSession, EventListener<Request, Answer>, NetworkReqListener {
  private static final Logger logger = LoggerFactory.getLogger(T6aClientSessionImpl.class);

  // Factories and Listeners --------------------------------------------------
  private transient ClientT6aSessionListener listener;

  protected long appId = -1;
  protected IclientT6aSessionData sessionData;

  public T6aClientSessionImpl(IclientT6aSessionData sessionData, IT6aMessageFactory fct, ISessionFactory sf, ClientT6aSessionListener lst) {
    super(sf, sessionData);
    if (lst == null) {
      throw new IllegalArgumentException("Listener can not be null");
    }
    if (fct.getApplicationId() < 0) {
      throw new IllegalArgumentException("ApplicationId can not be less than zero");
    }

    this.appId = fct.getApplicationId();
    this.listener = lst;
    super.messageFactory = fct;
    this.sessionData = sessionData;
  }

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
          logger.debug("S6t Failure handling Timeout event.");
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
  public void sendReportingInformationRequest(JReportingInformationRequest request)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, request, null);
  }

  @Override
  public void sendMODataRequest(JMO_DataRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, request, null);
  }

  @Override
  public void sendMTDataAnswer(JMT_DataAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
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
  public void receivedSuccessMessage(Request request, Answer answer) {
    AnswerDelivery rd = new AnswerDelivery();
    rd.session = this;
    rd.request = request;
    rd.answer = answer;
    super.scheduler.execute(rd);
  }

  @Override
  public void timeoutExpired(Request request) {
    try {
      handleEvent(new Event(Event.Type.TIMEOUT_EXPIRES, new AppRequestEventImpl(request), null));
    }
    catch (Exception e) {
      logger.debug("T6a Failed to process timeout message", e);
    }

  }

  @Override
  public Answer processRequest(Request request) {
    RequestDelivery rd  = new RequestDelivery();
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
      T6aSessionState newState = T6aSessionState.IDLE;
      Event localEvent = (Event) event;
      Event.Type eventType = (Event.Type) event.getType();
      switch (state) {

        case IDLE:
          switch (eventType) {
            case RECEIVE_CIR:
              this.sessionData.setBuffer((Request) ((AppEvent) event.getData()).getMessage());
              super.startMsgTimer();
              newState = T6aSessionState.OPEN;
              setState(newState);
              listener.doConfigurationInformationRequestEvent(this, (JConfigurationInformationRequest) event.getData());
              break;
            case RECEIVE_CMR:
              this.sessionData.setBuffer((Request) ((AppEvent) event.getData()).getMessage());
              super.startMsgTimer();
              newState = T6aSessionState.OPEN;
              setState(newState);
              listener.doConnectionManagementRequestEvent(this, (JConnectionManagementRequest) event.getData());
              break;
            case RECEIVE_TDR:
              this.sessionData.setBuffer((Request) ((AppEvent) event.getData()).getMessage());
              super.startMsgTimer();
              newState = T6aSessionState.OPEN;
              setState(newState);
              listener.doMT_DataRequestEvent(this, (JMT_DataRequest) event.getData());
              break;

            case SEND_MESSAGE:
              newState = T6aSessionState.OPEN;
              super.session.send(((AppEvent) event.getData()).getMessage(), this);
              setState(newState); //FIXME: is this ok to be here?
              break;

            default:
              logger.error("Invalid Event Type {} for T6a Client Session at state {}.", eventType, sessionData.getT6aSessionState());
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
              newState = T6aSessionState.DISCONNECTED;
              setState(newState);
              super.cancelMsgTimer();
              listener.doConfigurationInformationAnswerEvent(this, (JConfigurationInformationRequest)localEvent.getRequest(),
                    (JConfigurationInformationAnswer)localEvent.getAnswer());
              break;
            case RECEIVE_CMA:
              newState = T6aSessionState.DISCONNECTED;
              setState(newState);
              super.cancelMsgTimer();
              listener.doConnectionManagementAnswerEvent(this, (JConnectionManagementRequest)localEvent.getRequest(),
                    (JConnectionManagementAnswer) localEvent.getAnswer());
              break;
            case RECEIVE_ODA:
              newState = T6aSessionState.DISCONNECTED;
              setState(newState);
              super.cancelMsgTimer();
              listener.doMO_DataAnswerEvent(this, (JMO_DataRequest) localEvent.getRequest(),
                    (JMO_DataAnswer) localEvent.getAnswer());
              break;
            case RECEIVE_RIA:
              newState = T6aSessionState.DISCONNECTED;
              setState(newState);
              super.cancelMsgTimer();
              listener.doReportingInformationAnswerEvent(this, (JReportingInformationRequest)localEvent.getRequest(),
                    (JReportingInformationAnswer) localEvent.getAnswer());
              break;
            default:
              throw new InternalException("Unexpected/Unknown message received: " + event.getData());
          }
          break;

        case DISCONNECTED:
          throw new InternalException("T6a Can't receive message in state TERMINATED. Command: " + event.getData());

        case TIMEDOUT:
          throw new InternalException("T6a Can't receive message in state TIMEDOUT. Command: " + event.getData());

        default:
          logger.error("T6a Client FSM in wrong state: {}", state);
          break;
      }
    }
    catch (Exception e) {
      throw new InternalException(e);
    }
    finally {
      sendAndStateLock.unlock();
    }

    return true;
  }

  @Override
  public <E> E getState(Class<E> stateType) {
    return stateType == T6aSessionState.class ? (E) this.sessionData.getT6aSessionState() : null;
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
   * @param type
   * @param request
   * @param answer
   * @throws InternalException
   */
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


  private class RequestDelivery implements Runnable {
    ClientT6aSession session;
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
          case JMT_DataRequest.code:
            handleEvent(new Event(Event.Type.RECEIVE_TDR, messageFactory.createMT_DataRequest(request), null));
            break;
          default:
            listener.doOtherEvent(session, new AppRequestEventImpl(request), null);
            break;
        }
      }
      catch (Exception e) {
        logger.debug("T6a Failed to process request message", e);
      }
    }
  }




  /**
   *
   */
  private class AnswerDelivery implements Runnable {
    ClientT6aSession session;
    Answer answer;
    Request request;

    @Override
    public void run() {
      try {
        switch (answer.getCommandCode()) {
          case JConfigurationInformationAnswer.code:
            handleEvent(new Event(Event.Type.RECEIVE_CIA, messageFactory.createConfigurationInformationRequest(request),
                  messageFactory.createConfigurationInformationAnswer(answer)));
            break;
          case JConnectionManagementAnswer.code:
            handleEvent(new Event(Event.Type.RECEIVE_CMA, messageFactory.createConnectionManagementRequest(request),
                  messageFactory.createConnectionManagementAnswer(answer)));
            break;
          case JMO_DataAnswer.code:
            handleEvent(new Event(Event.Type.RECEIVE_ODA, messageFactory.createMO_DataRequest(request),
                  messageFactory.createMO_DataAnswer(answer)));
            break;
          case JReportingInformationAnswer.code:
            handleEvent(new Event(Event.Type.RECEIVE_RIA, messageFactory.createReportingInformationRequest(request),
                  messageFactory.createReportingInformationAnswer(answer)));
            break;


          default:
            listener.doOtherEvent(session, new AppRequestEventImpl(request), new AppAnswerEventImpl(answer));
            break;
        }
      }
      catch (Exception e) {
        logger.debug("T6a Failed to process success message", e);
      }
    }
  }




}
