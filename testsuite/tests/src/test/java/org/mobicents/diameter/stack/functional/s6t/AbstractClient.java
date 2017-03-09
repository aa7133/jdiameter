package org.mobicents.diameter.stack.functional.s6t;

import org.jdiameter.api.*;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.s6t.ClientS6tSession;
import org.jdiameter.api.s6t.ClientS6tSessionListener;
import org.jdiameter.api.s6t.ServerS6tSession;
import org.jdiameter.api.s6t.events.JConfigurationInformationAnswer;
import org.jdiameter.api.s6t.events.JConfigurationInformationRequest;
import org.jdiameter.api.s6t.events.JReportingInformationRequest;
import org.jdiameter.api.s6t.events.JNIDDInformationAnswer;
import org.jdiameter.api.s6t.events.JNIDDInformationRequest;
import org.jdiameter.common.impl.app.s6t.S6tSessionFactoryImpl;
import org.mobicents.diameter.stack.functional.TBase;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * Created by Adi Enzel on 3/6/17.
 */
public abstract class AbstractClient extends TBase implements ClientS6tSessionListener {

  protected ClientS6tSession clientS6tSession;

  public void init(InputStream configStream, String clientID) throws Exception {
    try {
      super.init(configStream, clientID, ApplicationId.createByAuthAppId(10415, 16777345));
      S6tSessionFactoryImpl s6tSessionFactory = new S6tSessionFactoryImpl(this.sessionFactory);
      sessionFactory.registerAppFacory(ServerS6tSession.class, s6tSessionFactory);
      sessionFactory.registerAppFacory(ClientS6tSession.class, s6tSessionFactory);

      s6tSessionFactory.setClientSessionListener(this);

      this.clientS6tSession =
              this.sessionFactory.getNewAppSession(this.sessionFactory.getSessionId("xxTESTxx"), getApplicationId(), ClientS6tSession.class, null); // true...
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
  public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    fail("Received \"S6t Other\" event, request[" + request + "], answer[" + answer + "], on session[" + session + "]", null);
  }

  @Override
  public void doConfigurationInformationAnswerEvent(ClientS6tSession session, JConfigurationInformationRequest request, JConfigurationInformationAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    fail("Received \"S6t CIA\" event, request[" + request + "], answer[" + answer + "], on session[" + session + "]", null);
  }

  @Override
  public void doReportingInformationRequestEvent(ClientS6tSession session, JReportingInformationRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    fail("Received \"S6t RIR\" event, request[" + request + "], on session[" + session + "]", null);
  }

  @Override
  public void doNIDDInformationAnswerEvent(ClientS6tSession session, JNIDDInformationRequest request, JNIDDInformationAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    fail("Received \"S6t NIA\" event, request[" + request + "], answer[" + answer + "], on session[" + session + "]", null);
  }

  public String getSessionId() {
    return this.clientS6tSession.getSessionId();
  }

  public ClientS6tSession getSession() {
    return this.clientS6tSession;
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
