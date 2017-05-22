package org.mobicents.diameter.stack.functional.t6a;


import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Mode;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Request;
import org.jdiameter.api.Answer;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.t6a.ClientT6aSession;
import org.jdiameter.api.t6a.ClientT6aSessionListener;
import org.jdiameter.api.t6a.ServerT6aSession;
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
import org.jdiameter.common.impl.app.t6a.T6aSessionFactoryImpl;
import org.mobicents.diameter.stack.functional.TBase;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/*
 * Copyright (c) 2017. AT&T Intellectual Property. All rights reserved
 */

/**
 * Created by Adi Enzel on 3/15/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public abstract class AbstractClient extends TBase implements ClientT6aSessionListener {

  protected ClientT6aSession clientT6aSession;

  public void init(InputStream configStream, String clientID) throws Exception {
    try {
      super.init(configStream, clientID, ApplicationId.createByAuthAppId(10415, 16777346));
      T6aSessionFactoryImpl t6aSessionFactory = new T6aSessionFactoryImpl(this.sessionFactory);
      sessionFactory.registerAppFacory(ServerT6aSession.class, t6aSessionFactory);
      sessionFactory.registerAppFacory(ClientT6aSession.class, t6aSessionFactory);

      t6aSessionFactory.setClientSessionListener(this);

      this.clientT6aSession =
              this.sessionFactory.getNewAppSession(this.sessionFactory.getSessionId("xxTESTxx"), getApplicationId(), ClientT6aSession.class, null); // true...
    }
    finally {
      try {
        configStream.close();
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
  }


  public void start() throws IllegalDiameterStateException, InternalException {
    stack.start();
  }

  public void start(Mode mode, long timeOut, TimeUnit timeUnit) throws IllegalDiameterStateException, InternalException {
    stack.start(mode, timeOut, timeUnit);
  }

  public void stop(long timeOut, TimeUnit timeUnit, int disconnectCause) throws IllegalDiameterStateException, InternalException {
    stack.stop(timeOut, timeUnit, disconnectCause);
  }

  public void stop(int disconnectCause) {
    stack.stop(disconnectCause);
  }



  @Override
  public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    fail("Received \"T6a Other\" event, request[" + request + "], answer[" + answer + "], on session[" + session + "]", null);
  }

  @Override
  public void doConfigurationInformationAnswerEvent(ClientT6aSession session, JConfigurationInformationRequest request,
                                                    JConfigurationInformationAnswer answer)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    fail("Received \"T6a CIA\" event, request[" + request + "], answer[" + answer + "], on session[" + session + "]", null);
  }

  @Override
  public void doConfigurationInformationRequestEvent(ClientT6aSession session, JConfigurationInformationRequest request)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    fail("Received \"T6a CIR\" event, request[" + request + "], on session[" + session + "]", null);
  }

  @Override
  public void doReportingInformationAnswerEvent(ClientT6aSession session, JReportingInformationRequest request, JReportingInformationAnswer answer)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    fail("Received \"T6a RIA\" event, request[" + request + "], answer[" + answer + "], on session[" + session + "]", null);
  }

  @Override
  public void doMO_DataAnswerEvent(ClientT6aSession session, JMO_DataRequest request, JMO_DataAnswer answer)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    fail("Received \"T6a ODA\" event, request[" + request + "], answer[" + answer + "], on session[" + session + "]", null);
  }

  @Override
  public void doMT_DataRequestEvent(ClientT6aSession session, JMT_DataRequest request)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    fail("Received \"T6a TDR\" event, request[" + request + "], on session[" + session + "]", null);
  }

  @Override
  public void doConnectionManagementAnswerEvent(ClientT6aSession session, JConnectionManagementRequest request, JConnectionManagementAnswer answer)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    fail("Received \"T6a CMA\" event, request[" + request + "], answer[" + answer + "], on session[" + session + "]", null);
  }

  @Override
  public void doConnectionManagementRequestEvent(ClientT6aSession session, JConnectionManagementRequest request)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    fail("Received \"T6a CMR\" event, request[" + request + "], on session[" + session + "]", null);
  }

  public String getSessionId() {
    return this.clientT6aSession.getSessionId();
  }

  public ClientT6aSession getSession() {
    return this.clientT6aSession;
  }

  // ----------- helper
  protected Request createRequest(AppSession session, int code) {
    Request r = session.getSessions().get(0).createRequest(code, getApplicationId(), getServerRealmName());

    AvpSet reqSet = r.getAvps();
    AvpSet vendorSpecificApplicationId = reqSet.addGroupedAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID, 0, false, false);
    // 1* [ Vendor-Id ]
    vendorSpecificApplicationId.addAvp(Avp.VENDOR_ID, getApplicationId().getVendorId(), true);
    // 0*1{ Auth-Application-Id }
    vendorSpecificApplicationId.addAvp(Avp.AUTH_APPLICATION_ID, getApplicationId().getAuthAppId(), true);
    // 0*1{ Acct-Application-Id }
    // { Auth-Session-State }
    reqSet.addAvp(Avp.AUTH_SESSION_STATE, 1);  // no session maintiand
    // { Origin-Host }
    reqSet.removeAvp(Avp.ORIGIN_HOST);
    reqSet.addAvp(Avp.ORIGIN_HOST, getClientURI(), true);

    return r;
  }

}
