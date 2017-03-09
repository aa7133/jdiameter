package org.jdiameter.client.impl.app.s6t;

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
import org.jdiameter.api.s6t.ClientS6tSession;
import org.jdiameter.api.s6t.ClientS6tSessionListener;
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
import org.jdiameter.common.impl.app.s6t.JReportingInformationRequestImpl;
import org.jdiameter.common.impl.app.s6t.S6tSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Adi Enzel on 3/5/17.
 *
 */
public class S6tClientSessionImpl extends S6tSession implements ClientS6tSession, EventListener<Request, Answer>, NetworkReqListener {
  private static final Logger logger = LoggerFactory.getLogger(S6tClientSessionImpl.class);

  // Factories and Listeners --------------------------------------------------
  private transient ClientS6tSessionListener listener;

  protected long appId = -1;
  protected IClientS6tSessionData sessionData;

  public S6tClientSessionImpl(IClientS6tSessionData sessionData, IS6tMessageFactory fct, ISessionFactory sf, ClientS6tSessionListener lst) {
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
  public void sendConfigurationInformationRequest(JConfigurationInformationRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, request, null);
  }


  @Override
  public void sendReportingInformationAnswer(JReportingInformationAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, null, answer);
  }
  @Override
  public void sendNIDDInformationRequest(JNIDDInformationRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_MESSAGE, request, null);
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
  public void onTimer(String timerName) {
    if (timerName.equals(S6tSession.TIMER_NAME_MSG_TIMEOUT)) {
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
  public boolean handleEvent(StateEvent event) throws InternalException, OverloadException {
    try {
      sendAndStateLock.lock();
      if (!super.session.isValid()) {
        // FIXME: throw new InternalException("Generic session is not valid.");
        return false;
      }
      final S6tSessionState state = this.sessionData.getS6tSessionState();
      S6tSessionState newState = S6tSessionState.IDLE;
      Event localEvent = (Event) event;
      Event.Type eventType = (Event.Type) event.getType();
      switch (state) {

        case IDLE:
          switch (eventType) {
            case RECEIVE_RIR:
              this.sessionData.setBuffer((Request) ((AppEvent) event.getData()).getMessage());
              super.startMsgTimer();
              newState = S6tSessionState.OPEN;
              setState(newState);
              Object request = (Object)event.getData();
              System.out.println("=============================  " + request.getClass().getName());
              System.out.println("============================== " + request.getClass().getCanonicalName());
              listener.doReportingInformationRequestEvent(this, (JReportingInformationRequestImpl) request);
              //listener.doReportingInformationRequestEvent(this, (JReportingInformationRequestImpl) event.getData());
              break;

            case SEND_MESSAGE:
              newState = S6tSessionState.OPEN;
              super.session.send(((AppEvent) event.getData()).getMessage(), this);
              setState(newState); //FIXME: is this ok to be here?
              break;

            default:
              logger.error("Invalid Event Type {} for S6t Client Session at state {}.", eventType, sessionData.getS6tSessionState());
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

            case RECEIVE_CIA:
              newState = S6tSessionState.DISCONNECTED;
              setState(newState);
              super.cancelMsgTimer();
              listener.doConfigurationInformationAnswerEvent(this, (JConfigurationInformationRequest) localEvent.getRequest(),
                      (JConfigurationInformationAnswer) localEvent.getAnswer());
              break;

            case RECEIVE_NIA:
              newState = S6tSessionState.DISCONNECTED;
              setState(newState);
              super.cancelMsgTimer();
              listener.doNIDDInformationAnswerEvent(this, (JNIDDInformationRequest) localEvent.getRequest(),
                      (JNIDDInformationAnswer) localEvent.getAnswer());
              break;

            default:
              throw new InternalException("Unexpected/Unknown message received: " + event.getData());
          }
          break;

        case DISCONNECTED:
          throw new InternalException("S6t Can't receive message in state TERMINATED. Command: " + event.getData());

        case TIMEDOUT:
          throw new InternalException("S6t Can't receive message in state TIMEDOUT. Command: " + event.getData());

        default:
          logger.error("S6t Client FSM in wrong state: {}", state);
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

  @Override
  public void timeoutExpired(Request request) {
    try {
      handleEvent(new Event(Event.Type.TIMEOUT_EXPIRES, new AppRequestEventImpl(request), null));
    }
    catch (Exception e) {
      logger.debug("S6t Failed to process timeout message", e);
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
  //@SuppressWarnings("unchecked")
  public <E> E getState(Class<E> stateType) {
    return stateType == S6tSessionState.class ? (E) this.sessionData.getS6tSessionState() : null;
  }







  private class RequestDelivery implements Runnable {
    ClientS6tSession session;
    Request request;

    @Override
    public void run() {
      try {
        switch (request.getCommandCode()) {
          case JReportingInformationRequest.code:
            handleEvent(new Event(Event.Type.RECEIVE_RIR, messageFactory.createConfigurationInformationRequest(request), null));
            break;
          default:
            listener.doOtherEvent(session, new AppRequestEventImpl(request), null);
            break;
        }
      }
      catch (Exception e) {
        logger.debug("S6t Failed to process request message", e);
      }
    }
  }

  private class AnswerDelivery implements Runnable {
    ClientS6tSession session;
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

          case JNIDDInformationAnswer.code:
            handleEvent(new Event(Event.Type.RECEIVE_NIA, messageFactory.createNIDDInformationRequest(request),
                messageFactory.createNIDDInformationAnswer(answer)));
            break;

          default:              System.out.println("========= RECEIVE_RIR 333333================= got request : ");

            listener.doOtherEvent(session, new AppRequestEventImpl(request), new AppAnswerEventImpl(answer));
            break;
        }
      }
      catch (Exception e) {
        logger.debug("S6t Failed to process success message", e);
      }
    }
  }

}
