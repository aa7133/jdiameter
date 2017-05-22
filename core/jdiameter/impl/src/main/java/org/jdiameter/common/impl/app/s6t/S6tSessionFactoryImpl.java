package org.jdiameter.common.impl.app.s6t;

import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Request;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.OverloadException;


import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.s6t.ClientS6tSession;
import org.jdiameter.api.s6t.ClientS6tSessionListener;
import org.jdiameter.api.s6t.ServerS6tSession;
import org.jdiameter.api.s6t.ServerS6tSessionListener;
import org.jdiameter.api.s6t.events.JConfigurationInformationAnswer;
import org.jdiameter.api.s6t.events.JConfigurationInformationRequest;
import org.jdiameter.api.s6t.events.JReportingInformationAnswer;
import org.jdiameter.api.s6t.events.JReportingInformationRequest;
import org.jdiameter.api.s6t.events.JNIDDInformationAnswer;
import org.jdiameter.api.s6t.events.JNIDDInformationRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.impl.app.s6t.IClientS6tSessionData;
import org.jdiameter.client.impl.app.s6t.S6tClientSessionImpl;
import org.jdiameter.common.api.app.IAppSessionDataFactory;
import org.jdiameter.common.api.app.s6t.IS6tMessageFactory;
import org.jdiameter.common.api.app.s6t.IS6tSessionData;
import org.jdiameter.common.api.app.s6t.IS6tSessionFactory;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.jdiameter.server.impl.app.s6t.IServerS6tSessionData;
import org.jdiameter.server.impl.app.s6t.S6tServerSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Copyright (c) 2017. AT&T Intellectual Property. All rights reserved
 */

/**
 * Created by Adi Enzel on 3/5/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public class S6tSessionFactoryImpl implements IS6tSessionFactory, ServerS6tSessionListener, ClientS6tSessionListener, IS6tMessageFactory,
        StateChangeListener<AppSession> {

  private static final Logger logger = LoggerFactory.getLogger(S6tSessionFactoryImpl.class);

  private static long APPLICATION_ID = 16777345;
  protected ISessionFactory sessionFactory;

  protected ServerS6tSessionListener serverSessionListener;
  protected ClientS6tSessionListener clientSessionListener;

  protected IS6tMessageFactory messageFactory;
  protected StateChangeListener<AppSession> stateListener;
  protected ISessionDatasource iss;
  protected IAppSessionDataFactory<IS6tSessionData> sessionDataFactory;

  public S6tSessionFactoryImpl(SessionFactory sessionFactory) {
    super();

    this.sessionFactory = (ISessionFactory) sessionFactory;
    this.iss = this.sessionFactory.getContainer().getAssemblerFacility().getComponentInstance(ISessionDatasource.class);
    this.sessionDataFactory = (IAppSessionDataFactory<IS6tSessionData>) this.iss.getDataFactory(IS6tSessionData.class);
  }

  /**
   * @return the serverSessionListener
   */
  @Override
  public ServerS6tSessionListener getServerSessionListener() {
    return serverSessionListener != null ? serverSessionListener : this;
  }

  /**
   * @param serverSessionListener
   *            the serverSessionListener to set
   */
  @Override
  public void setServerSessionListener(ServerS6tSessionListener serverSessionListener) {
    this.serverSessionListener = serverSessionListener;
  }

  /**
   * @return the serverSessionListener
   */
  @Override
  public ClientS6tSessionListener getClientSessionListener() {
    return clientSessionListener != null ? clientSessionListener : this;
  }

  /**
   * @param clientSessionListener
   *            the serverSessionListener to set
   */
  @Override
  public void setClientSessionListener(ClientS6tSessionListener clientSessionListener) {
    this.clientSessionListener = clientSessionListener;
  }

  /**
   * @return the messageFactory
   */
  @Override
  public IS6tMessageFactory getMessageFactory() {
    return messageFactory != null ? messageFactory : this;
  }

  /**
   * @param messageFactory
   *            the messageFactory to set
   */
  @Override
  public void setMessageFactory(IS6tMessageFactory messageFactory) {
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
      throw new IllegalArgumentException("S6t SessionId must not be null");
    }
    if (!this.iss.exists(sessionId)) {
      return null;
    }
    AppSession appSession = null;
    try {

      if (aClass == ServerS6tSession.class) {
        IServerS6tSessionData sessionData = (IServerS6tSessionData) this.sessionDataFactory.getAppSessionData(ServerS6tSession.class, sessionId);
        S6tServerSessionImpl serverSession = new S6tServerSessionImpl(sessionData, getMessageFactory(), sessionFactory, this.getServerSessionListener());
        serverSession.getSessions().get(0).setRequestListener(serverSession);
        appSession = serverSession;
      }
      else if (aClass == ClientS6tSession.class) {
        IClientS6tSessionData sessionData = (IClientS6tSessionData) this.sessionDataFactory.getAppSessionData(ClientS6tSession.class, sessionId);
        S6tClientSessionImpl clientSession = new S6tClientSessionImpl(sessionData, getMessageFactory(), sessionFactory, this.getClientSessionListener());
        clientSession.getSessions().get(0).setRequestListener(clientSession);
        appSession = clientSession;
      }
      else {
        throw new IllegalArgumentException("Wrong session class: " + aClass + ". Supported[" + ServerS6tSession.class + "]");
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

    if (aClass == ServerS6tSession.class) {
      if (sessionId == null) {
        if (args != null && args.length > 0 && args[0] instanceof Request) {
          Request request = (Request) args[0];
          sessionId = request.getSessionId();
        }
        else {
          sessionId = this.sessionFactory.getSessionId();
        }
      }
      IServerS6tSessionData sessionData = (IServerS6tSessionData) this.sessionDataFactory.getAppSessionData(ServerS6tSession.class, sessionId);
      S6tServerSessionImpl serverSession = new S6tServerSessionImpl(sessionData, getMessageFactory(), sessionFactory, this.getServerSessionListener());

      iss.addSession(serverSession);
      serverSession.getSessions().get(0).setRequestListener(serverSession);
      appSession = serverSession;
    }
    else if (aClass == ClientS6tSession.class) {
      if (sessionId == null) {
        if (args != null && args.length > 0 && args[0] instanceof Request) {
          Request request = (Request) args[0];
          sessionId = request.getSessionId();
        }
        else {
          sessionId = this.sessionFactory.getSessionId();
        }
      }
      IClientS6tSessionData sessionData = (IClientS6tSessionData) this.sessionDataFactory.getAppSessionData(ClientS6tSession.class, sessionId);
      S6tClientSessionImpl clientSession = new S6tClientSessionImpl(sessionData, getMessageFactory(), sessionFactory, this.getClientSessionListener());

      iss.addSession(clientSession);
      clientSession.getSessions().get(0).setRequestListener(clientSession);
      appSession = clientSession;
    }
    else {
      throw new IllegalArgumentException("S6t Wrong session class: " + aClass + ". Supported[" + ServerS6tSession.class + "]");
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
    logger.info("Diameter S6t Session Factory :: stateChanged :: oldState[{}], newState[{}]", oldState, newState);
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
    logger.info("Diameter S6t Session Factory :: stateChanged :: Session, [{}], oldState[{}], newState[{}]", new Object[]{source, oldState, newState});
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
  public JNIDDInformationRequest createNIDDInformationRequest(Request request) {
    return new JNIDDInformationRequestImpl(request);
  }

  @Override
  public JNIDDInformationAnswer createNIDDInformationAnswer(Answer answer) {
    return new JNIDDInformationAnswerImpl(answer);
  }

  @Override
  public void doConfigurationInformationRequestEvent(ServerS6tSession session, JConfigurationInformationRequest request)
            throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter S6t Session Factory :: doConfigurationInformationRequestEvent :: appSession[{}], Request[{}]", session, request);
  }

  @Override
  public void doConfigurationInformationAnswerEvent(ClientS6tSession session, JConfigurationInformationRequest request, JConfigurationInformationAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter S6t Session Factory :: doConfigurationInformationAnswerEvent :: appSession[{}], Request[{}]", session, request);
  }

  @Override
  public void doReportingInformationRequestEvent(ClientS6tSession session, JReportingInformationRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter S6t Session Factory :: doReportingInformationRequestEvent :: appSession[{}], Request[{}]", session, request);
  }

  @Override
  public void doReportingInformationAnswerEvent(ServerS6tSession session, JReportingInformationRequest request, JReportingInformationAnswer answer)
            throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter S6t Session Factory :: doReportingInformationAnswerEvent :: appSession[{}], Request[{}]", session, request);
  }

  @Override
  public void doNIDDInformationRequestEvent(ServerS6tSession session, JNIDDInformationRequest request)
            throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter S6t Session Factory :: doNIDDInformationRequestEvent :: appSession[{}], Request[{}]", session, request);
  }

  @Override
  public void doNIDDInformationAnswerEvent(ClientS6tSession session, JNIDDInformationRequest request, JNIDDInformationAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter S6t Session Factory :: doNIDDInformationAnswerEvent :: appSession[{}], Request[{}]", session, request);
  }

  @Override
  public void doOtherEvent(AppSession appSession, AppRequestEvent request, AppAnswerEvent answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    logger.info("Diameter S6t Session Factory :: doOtherEvent :: appSession[{}], Request[{}], Answer[{}]", new Object[]{appSession, request, answer});
  }

}
