package org.jdiameter.server.impl.app.s6t;

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
import org.jdiameter.api.s6t.ServerS6tSession;
import org.jdiameter.api.s6t.ServerS6tSessionListener;
import org.jdiameter.api.s6t.events.JConfigurationInformationAnswer;
import org.jdiameter.api.s6t.events.JConfigurationInformationRequest;
import org.jdiameter.api.s6t.events.JReportingInformationAnswer;
import org.jdiameter.api.s6t.events.JReportingInformationRequest;
import org.jdiameter.api.s6t.events.JNIDDInformationAnswer;
import org.jdiameter.api.s6t.events.JNIDDInformationRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.app.s6t.IS6tMessageFactory;

import org.jdiameter.common.api.app.s6t.S6tSessionState;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.jdiameter.common.impl.app.s6t.S6tSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Adi Enzel on 3/5/17.
 */
public class S6tServerSessionImpl extends S6tSession implements ServerS6tSession, EventListener<Request, Answer>, NetworkReqListener {

  private static final Logger logger = LoggerFactory.getLogger(S6tServerSessionImpl.class);

  // Factories and Listeners --------------------------------------------------
  private transient ServerS6tSessionListener listener;
  protected long appId = -1;
  protected IServerS6tSessionData sessionData;


    /**
     *
     * @param sessionData
     * @param fct
     * @param sf
     * @param lst
     */
  public S6tServerSessionImpl(IServerS6tSessionData sessionData, IS6tMessageFactory fct, ISessionFactory sf, ServerS6tSessionListener lst) {
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

    /**
     *
     * @param request JReportingInformationRequest
     * @throws InternalException
     * @throws IllegalDiameterStateException
     * @throws RouteException
     * @throws OverloadException
     */
  @Override
  public void sendReportingInformationRequest(JReportingInformationRequest request)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, request, null);
  }

    /**\
     *
     * @param answer JConfigurationInformationAnswer
     * @throws InternalException
     * @throws IllegalDiameterStateException
     * @throws RouteException
     * @throws OverloadException
     */
  @Override
  public void sendConfigurationInformationAnswer(JConfigurationInformationAnswer answer)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, null, answer);
  }

    /**
     *
     * @param answer JNIDDInformationAnswer
     * @throws InternalException
     * @throws IllegalDiameterStateException
     * @throws RouteException
     * @throws OverloadException
     */
  @Override
  public void sendNIDDInformationAnswer(JNIDDInformationAnswer answer)
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
    else if (timerName.equals(S6tSession.TIMER_NAME_MSG_TIMEOUT)) {
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

    /**
     *
     * @param request
     * @param answer
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
     * @param event
     * @return
     * @throws InternalException
     * @throws OverloadException
     */
  @Override
  public boolean handleEvent(StateEvent event) throws InternalException, OverloadException {
    try {
      sendAndStateLock.lock();
      if (!super.session.isValid()) {
        // FIXME: throw new InternalException("Generic session is not valid.");
        return false;
      }
      final S6tSessionState state = this.sessionData.getS6tSessionState();
      S6tSessionState newState = null;
      Event localEvent = (Event) event;
      Event.Type eventType = (Event.Type) event.getType();
      switch (state) {

        case IDLE:
          switch (eventType) {
            case RECEIVE_CIR:
              this.sessionData.setBuffer((Request) ((AppEvent) event.getData()).getMessage());
              super.cancelMsgTimer();
              super.startMsgTimer();
              newState = S6tSessionState.OPEN;
              setState(newState);
              listener.doConfigurationInformationRequestEvent(this, (JConfigurationInformationRequest) event.getData());
              break;

            case RECEIVE_NIR:
              this.sessionData.setBuffer((Request) ((AppEvent) event.getData()).getMessage());
              super.cancelMsgTimer();
              super.startMsgTimer();
              newState = S6tSessionState.OPEN;
              setState(newState);
              listener.doNIDDInformationRequestEvent(this, (JNIDDInformationRequest) localEvent.getRequest());
              break;
            case SEND_MESSAGE:
              super.session.send(((AppEvent) event.getData()).getMessage(), this);
              newState = S6tSessionState.OPEN;
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
              newState = S6tSessionState.TIMEDOUT;
              setState(newState);
              break;

            case SEND_MESSAGE:
              try {
                super.session.send(((AppEvent) event.getData()).getMessage(), this);
              }
              finally {
                newState = S6tSessionState.DISCONNECTED;
                setState(newState);
              }
              break;
            case RECEIVE_RIA:
              try {
                super.cancelMsgTimer();
                listener.doReportingInformationAnswerEvent(this, (JReportingInformationRequest) localEvent.getRequest(),
                    (JReportingInformationAnswer) localEvent.getAnswer());
              }
              finally {
                newState = S6tSessionState.DISCONNECTED;
                setState(newState);
              }
              break;

            default:
              throw new InternalException("S6t Should not receive more messages after initial. Command: " + event.getData());
          }
          break;

        case DISCONNECTED:
          throw new InternalException("S6T Can't receive message in state DISCONNECTED. Command: " + event.getData());

        case TIMEDOUT:
          throw new InternalException("S6t Can't receive message in state TIMEDOUT. Command: " + event.getData());

        default:
          logger.error("S6t Server FSM in wrong state: {}", state);
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
     * @param stateType
     * @param <E>
     * @return
     */
  @Override
  //@SuppressWarnings("unchecked")
  public <E> E getState(Class<E> stateType) {
    return stateType == S6tSessionState.class ? (E) this.sessionData.getS6tSessionState() : null;
  }

    /**
     *
     * @param request
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
     * @param request
     * @return
     */
  @Override
  public Answer processRequest(Request request) {
    RequestDelivery rd = new RequestDelivery();
    rd.session = this;
    rd.request = request;
    super.scheduler.execute(rd);
    return null;
  }



  /**
   *
   * @param newState
   */
  protected void setState(S6tSessionState newState) {
    S6tSessionState oldState = this.sessionData.getS6tSessionState();
    this.sessionData.setS6tSessionState(newState);

    for (StateChangeListener i : stateListeners) {
      i.stateChanged(this, oldState, newState);
    }
    if (newState == S6tSessionState.DISCONNECTED || newState == S6tSessionState.TIMEDOUT) {
      super.cancelMsgTimer();
      this.release();
    }
  }


  private class RequestDelivery implements Runnable {
    ServerS6tSession session;
    Request request;

    @Override
    public void run() {
      try {
        switch (request.getCommandCode()) {
          case JConfigurationInformationRequest.code:
            handleEvent(new Event(Event.Type.RECEIVE_CIR, messageFactory.createConfigurationInformationRequest(request), null));
            break;

          case JNIDDInformationRequest.code:
            handleEvent(new Event(Event.Type.RECEIVE_NIR, messageFactory.createNIDDInformationRequest(request), null));
            break;

          default:
            logger.info("RequestDelivery default command code = " + request.getCommandCode());
            listener.doOtherEvent(session, new AppRequestEventImpl(request), null);
            break;
        }
      }
      catch (Exception e) {
        logger.debug("Failed to process request message", e);
      }
    }
  }


  private class AnswerDelivery implements Runnable {

    ServerS6tSession session;
    Answer answer;
    Request request;

    @Override
    public void run() {
      try {
        switch (answer.getCommandCode()) {
          case JReportingInformationAnswer.code:
            handleEvent(new Event(Event.Type.RECEIVE_RIA ,messageFactory.createReportingInformationRequest(request),
                messageFactory.createReportingInformationAnswer(answer)));
            break;

          default:
            logger.info("AnswerDelivery default command code = " + request.getCommandCode());
            listener.doOtherEvent(session, new AppRequestEventImpl(request), new AppAnswerEventImpl(answer));
            break;
        }
      }
      catch (Exception e) {
        logger.debug("Failed to process success message", e);
      }
    }
  }



  protected void send(org.jdiameter.server.impl.app.s6t.Event.Type type, AppEvent request, AppEvent answer) throws InternalException {
    try {
      if (type != null) {
        handleEvent(new Event(type, request, answer));
      }
    }
    catch (Exception e) {
      throw new InternalException(e);
    }
  }

}
