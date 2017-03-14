package org.jdiameter.common.impl.app.t6a;


import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.t6a.ClientT6aSession;
import org.jdiameter.api.t6a.ClientT6aSessionListener;
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
import org.jdiameter.client.impl.app.t6a.IclientT6aSessionData;
import org.jdiameter.client.impl.app.t6a.T6aClientSessionImpl;
import org.jdiameter.common.api.app.IAppSessionDataFactory;
import org.jdiameter.common.api.app.t6a.IT6aMessageFactory;
import org.jdiameter.common.api.app.t6a.IT6aSessionData;
import org.jdiameter.common.api.app.t6a.IT6aSessionFactory;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.jdiameter.server.impl.app.t6a.IServerT6aSessionData;
import org.jdiameter.server.impl.app.t6a.T6aServerSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Adi Enzel on 3/13/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public class T6aSessionFactoryImpl implements IT6aSessionFactory, ServerT6aSessionListener, ClientT6aSessionListener, IT6aMessageFactory,
      StateChangeListener<AppSession> {

  private static final Logger logger = LoggerFactory.getLogger(T6aSessionFactoryImpl.class);

  private static long APPLICATION_ID = 16777346;
  protected ISessionFactory sessionFactory;

  protected ServerT6aSessionListener serverSessionListener;
  protected ClientT6aSessionListener clientSessionListener;

  protected IT6aMessageFactory messageFactory;
  protected StateChangeListener<AppSession> stateListener;
  protected ISessionDatasource iss;
  protected IAppSessionDataFactory<IT6aSessionData> sessionDataFactory;

  public T6aSessionFactoryImpl(SessionFactory sessionFactory) {
    super();

    this.sessionFactory = (ISessionFactory) sessionFactory;
    this.iss = this.sessionFactory.getContainer().getAssemblerFacility().getComponentInstance(ISessionDatasource.class);
    this.sessionDataFactory = (IAppSessionDataFactory<IT6aSessionData>) this.iss.getDataFactory(IT6aSessionData.class);
  }

  /**
   * @return the serverSessionListener
   */
  @Override
  public ServerT6aSessionListener getServerSessionListener() {
    return serverSessionListener != null ? serverSessionListener : this;
  }

  /**
   * @param serverSessionListener
   *            the serverSessionListener to set
   */
  @Override
  public void setServerSessionListener(ServerT6aSessionListener serverSessionListener) {
    this.serverSessionListener = serverSessionListener;
  }

  /**
   * @return the serverSessionListener
   */
  @Override
  public ClientT6aSessionListener getClientSessionListener() {
    return clientSessionListener != null ? clientSessionListener : this;
  }

  /**
   *
   * @param clientSessionListener
   */
  @Override
  public void setClientSessionListener(ClientT6aSessionListener clientSessionListener) {
    this.clientSessionListener = clientSessionListener;
  }

  /**
   * @return the messageFactory
   */
  @Override
  public IT6aMessageFactory getMessageFactory() {
    return messageFactory != null ? messageFactory : this;
  }

  /**
   * @param messageFactory
   *            the messageFactory to set
   */
  @Override
  public void setMessageFactory(IT6aMessageFactory messageFactory) {
    this.messageFactory = messageFactory;
  }

  /**
   * @return the stateListener
   */
  @Override
  public StateChangeListener<AppSession> getStateListener() {
    return stateListener != null ? stateListener : this;
  }

  /**
   * @param stateListener
   *            the stateListener to set
   */
  @Override
  public void setStateListener(StateChangeListener<AppSession> stateListener) {
    this.stateListener = stateListener;
  }

  /**
   *
   * @param sessionId
   * @param aClass
   * @return
   */
  @Override
  public AppSession getSession(String sessionId, Class<? extends AppSession> aClass) {
    if (sessionId == null) {
      throw new IllegalArgumentException("T6a SessionId must not be null");
    }
    if (!this.iss.exists(sessionId)) {
      return null;
    }
    AppSession appSession = null;
    try {

      if (aClass == ServerT6aSession.class) {
        IServerT6aSessionData sessionData = (IServerT6aSessionData) this.sessionDataFactory.getAppSessionData(ServerT6aSession.class, sessionId);
        T6aServerSessionImpl serverSession = new T6aServerSessionImpl(sessionData, getMessageFactory(), sessionFactory, this.getServerSessionListener());
        serverSession.getSessions().get(0).setRequestListener(serverSession);
        appSession = serverSession;
      }
      else if (aClass == ClientT6aSession.class) {
        IclientT6aSessionData sessionData = (IclientT6aSessionData) this.sessionDataFactory.getAppSessionData(ClientT6aSession.class, sessionId);
        T6aClientSessionImpl clientSession = new T6aClientSessionImpl(sessionData, getMessageFactory(), sessionFactory, this.getClientSessionListener());
        clientSession.getSessions().get(0).setRequestListener(clientSession);
        appSession = clientSession;
      }
      else {
        throw new IllegalArgumentException("Wrong session class: " + aClass + ". Supported[" + ServerT6aSession.class + "]");
      }
    }
    catch (Exception e) {
      logger.error("Failure to obtain new S6a Session.", e);
    }

    return appSession;
  }


  /**
   *
   * @param sessionId
   * @param aClass
   * @param applicationId
   * @param args
   * @return
   */
  @Override
  public AppSession getNewSession(String sessionId, Class<? extends AppSession> aClass, ApplicationId applicationId, Object[] args) {
    AppSession appSession = null;

    if (aClass == ServerT6aSession.class) {
      if (sessionId == null) {
        if (args != null && args.length > 0 && args[0] instanceof Request) {
          Request request = (Request) args[0];
          sessionId = request.getSessionId();
        }
        else {
          sessionId = this.sessionFactory.getSessionId();
        }
      }
      IServerT6aSessionData sessionData = (IServerT6aSessionData) this.sessionDataFactory.getAppSessionData(ServerT6aSession.class, sessionId);
      T6aServerSessionImpl serverSession = new T6aServerSessionImpl(sessionData, getMessageFactory(), sessionFactory, this.getServerSessionListener());

      iss.addSession(serverSession);
      serverSession.getSessions().get(0).setRequestListener(serverSession);
      appSession = serverSession;
    }
    else if (aClass == ClientT6aSession.class) {
      if (sessionId == null) {
        if (args != null && args.length > 0 && args[0] instanceof Request) {
          Request request = (Request) args[0];
          sessionId = request.getSessionId();
        }
        else {
          sessionId = this.sessionFactory.getSessionId();
        }
      }
      IclientT6aSessionData sessionData = (IclientT6aSessionData) this.sessionDataFactory.getAppSessionData(ClientT6aSession.class, sessionId);
      T6aClientSessionImpl clientSession = new T6aClientSessionImpl(sessionData, getMessageFactory(), sessionFactory, this.getClientSessionListener());

      iss.addSession(clientSession);
      clientSession.getSessions().get(0).setRequestListener(clientSession);
      appSession = clientSession;
    }
    else {
      throw new IllegalArgumentException("T6a Wrong session class: " + aClass + ". Supported[" + ServerT6aSession.class + "]");
    }

    return appSession;
  }

  /**
   *
   * @param oldState
   * @param newState
   */
  @Override
  public void stateChanged(Enum oldState, Enum newState) {
    logger.info("Diameter T6a Session Factory :: stateChanged :: oldState[{}], newState[{}]", oldState, newState);
  }

  @Override
  public JConfigurationInformationRequest createConfigurationInformationRequest(Request request) {
    return new JConfigurationInformationRequestImpl(request);
  }

  @Override
  public JConfigurationInformationAnswer createConfigurationInformationAnswer(Answer answer) {
    return new JConfigurationInformationAnswerImpl(answer);
  }

  @Override
  public JReportingInformationRequest createReportingInformationRequest(Request request) {
    return new JReportingInformationRequestImpl(request);
  }

  @Override
  public JReportingInformationAnswer createReportingInformationAnswer(Answer answer) {
    return new JReportingInformationAnswerImpl(answer);
  }

  @Override
  public JConnectionManagementRequest createConnectionManagementRequest(Request request) {
    return new JConnectionManagementRequestImpl(request);
  }

  @Override
  public JConnectionManagementAnswer createConnectionManagementAnswer(Answer answer) {
    return new JConnectionManagementAnswerImpl(answer);
  }

  @Override
  public JMO_DataRequest createMO_DataRequest(Request request) {
    return new JMO_DataRequestImpl(request);
  }

  @Override
  public JMO_DataAnswer createMO_DataAnswer(Answer answer) {
    return new JMO_DataAnswerImpl(answer);
  }

  @Override
  public JMT_DataRequest createMT_DataRequest(Request request) {
    return new JMT_DataRequestImpl(request);
  }

  @Override
  public JMT_DataAnswer createMT_DataAnswer(Answer answer) {
    return new JMT_DataAnswerImpl(answer);
  }

  @Override
  public long getApplicationId() {
    return APPLICATION_ID;
  }


  /*
   * (non-Javadoc)
   * @see org.jdiameter.api.app.StateChangeListener#stateChanged(java.lang.Object, java.lang.Enum, java.lang.Enum)
   */
  @Override
  public void stateChanged(AppSession source, Enum oldState, Enum newState) {
    logger.info("Diameter T6a Session Factory :: stateChanged :: Session, [{}], oldState[{}], newState[{}]", new Object[]{source, oldState, newState});
  }


  @Override
  public void doOtherEvent(AppSession appSession, AppRequestEvent request, AppAnswerEvent answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter T6a Session Factory :: doOtherEvent :: appSession[{}], Request[{}], Answer[{}]", new Object[]{appSession, request, answer});
  }

  @Override
  public void doSendConfigurationInformationAnswerEvent(ServerT6aSession session,
                                                        JConfigurationInformationRequest request,JConfigurationInformationAnswer answer)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter T6a Session Factory :: doSendConfigurationInformationAnswerEvent :: appSession[{}], Request[{}]", session, request);
  }

  @Override
  public void doSendConfigurationInformationRequestEvent(ServerT6aSession session, JConfigurationInformationRequest request)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter T6a Session Factory :: doSendConfigurationInformationRequestEvent :: appSession[{}], Request[{}]", session, request);
  }

  @Override
  public void doSendReportingInformationRequestEvent(ServerT6aSession session, JReportingInformationRequest request)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter T6a Session Factory :: doSendReportingInformationRequestEvent :: appSession[{}], Request[{}]", session, request);
  }

  @Override
  public void doSendMO_DataRequestEvent(ServerT6aSession session, JMO_DataRequest request)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter T6a Session Factory :: doSendMO_DataRequestEvent :: appSession[{}], Request[{}]", session, request);
  }

  @Override
  public void doSendMT_DataAnswertEvent(ServerT6aSession session, JMT_DataRequest request, JMT_DataAnswer answer)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter T6a Session Factory :: doSendMT_DataAnswertEvent :: appSession[{}], Request[{}]", session, request);
  }

  @Override
  public void doSendConnectionManagementAnswertEvent(ServerT6aSession session, JConnectionManagementRequest request, JConnectionManagementAnswer answer)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter T6a Session Factory :: doSendConnectionManagementAnswertEvent :: appSession[{}], Request[{}]", session, request);
  }

  @Override
  public void doSendConnectionManagementRequestEvent(ServerT6aSession session, JConnectionManagementRequest request)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter T6a Session Factory :: doSendConnectionManagementRequestEvent :: appSession[{}], Request[{}]", session, request);
  }

  @Override
  public void doConfigurationInformationAnswerEvent(ClientT6aSession session, JConfigurationInformationRequest request, JConfigurationInformationAnswer answer)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter T6a Session Factory :: doConfigurationInformationAnswerEvent :: appSession[{}], Request[{}]", session, request);
  }

  @Override
  public void doConfigurationInformationRequestEvent(ClientT6aSession session, JConfigurationInformationRequest request)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter T6a Session Factory :: doConfigurationInformationRequestEvent :: appSession[{}], Request[{}]", session, request);
  }

  @Override
  public void doReportingInformationAnswerEvent(ClientT6aSession session, JReportingInformationRequest request, JReportingInformationAnswer answer)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter T6a Session Factory :: doConfigurationInformationRequestEvent :: appSession[{}], Request[{}]", session, request);
  }

  @Override
  public void doMO_DataAnswerEvent(ClientT6aSession session, JMO_DataRequest request, JMO_DataAnswer answer)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter T6a Session Factory :: doReportingInformationAnswerEvent :: appSession[{}], Request[{}]", session, request);
  }

  @Override
  public void doMT_DataRequestEvent(ClientT6aSession session, JMT_DataRequest request)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter T6a Session Factory :: doMT_DataRequestEvent :: appSession[{}], Request[{}]", session, request);
  }

  @Override
  public void doConnectionManagementAnswerEvent(ClientT6aSession session, JConnectionManagementRequest request, JConnectionManagementAnswer answer)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter T6a Session Factory :: doConnectionManagementAnswerEvent :: appSession[{}], Request[{}]", session, request);
  }

  @Override
  public void doConnectionManagementRequestEvent(ClientT6aSession session, JConnectionManagementRequest request)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter T6a Session Factory :: doConnectionManagementRequestEvent :: appSession[{}], Request[{}]", session, request);

  }
}
