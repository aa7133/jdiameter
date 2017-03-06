package org.mobicents.diameter.stack.functional.s6t;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.Mode;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.s6t.ClientS6tSession;
import org.jdiameter.api.s6t.ServerS6tSession;
import org.jdiameter.api.s6t.ServerS6tSessionListener;
import org.jdiameter.api.s6t.events.JConfigurationInformationRequest;
import org.jdiameter.api.s6t.events.JNIDDInformationRequest;
import org.jdiameter.api.s6t.events.JReportingInformationAnswer;
import org.jdiameter.api.s6t.events.JReportingInformationRequest;
import org.jdiameter.common.impl.app.s6t.S6tSessionFactoryImpl;
import org.mobicents.diameter.stack.functional.TBase;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * Created by Adi Enzel on 3/6/17.
 */
public abstract class AbstractServer extends TBase implements ServerS6tSessionListener {
  protected ServerS6tSession serverS6tSession;

  public void init(InputStream configStream, String clientID) throws Exception {
    try {
      super.init(configStream, clientID, ApplicationId.createByAuthAppId(10415, 16777345));
      S6tSessionFactoryImpl S6tSessionFactory = new S6tSessionFactoryImpl(this.sessionFactory);
      sessionFactory.registerAppFacory(ServerS6tSession.class, S6tSessionFactory);
      sessionFactory.registerAppFacory(ClientS6tSession.class, S6tSessionFactory);
      S6tSessionFactory.setServerSessionListener(this);
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

  // ----------- delegate methods so

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

  public String getSessionId() {
    return this.serverS6tSession.getSessionId();
  }

  public ServerS6tSession getSession() {
    return this.serverS6tSession;
  }

  // ----------- helper
  protected Request createRequest(AppSession session, int code) {
    Request r = session.getSessions().get(0).createRequest(code, getApplicationId(), getClientRealmName());

    AvpSet reqSet = r.getAvps();
    AvpSet vendorSpecificApplicationId = reqSet.addGroupedAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID, 0, false, false);
    // 1* [ Vendor-Id ]
    vendorSpecificApplicationId.addAvp(Avp.VENDOR_ID, getApplicationId().getVendorId(), true);
    // 0*1{ Auth-Application-Id }
    vendorSpecificApplicationId.addAvp(Avp.AUTH_APPLICATION_ID, getApplicationId().getAuthAppId(), true);
    // 0*1{ Acct-Application-Id }
    // { Auth-Session-State }
    reqSet.addAvp(Avp.AUTH_SESSION_STATE, 1);
    // { Origin-Host }
    reqSet.removeAvp(Avp.ORIGIN_HOST);
    reqSet.addAvp(Avp.ORIGIN_HOST, getServerURI(), true);
    return r;
  }

  @Override
  public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    fail("Received \"S6t Other\" event, request[" + request + "], answer[" + answer + "], on session[" + session + "]", null);
  }

  @Override
  public void doConfigurationInformationRequestEvent(ServerS6tSession session, JConfigurationInformationRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    fail("Received \"S6t CIR\" event, request[" + request + "], on session[" + session + "]", null);
  }

  @Override
  public void doReportingInformationAnswerEvent(ServerS6tSession session, JReportingInformationRequest request, JReportingInformationAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    fail("Received \"S6t RIA\" event, request[" + request + "], answer[" + answer + "], on session[" + session + "]", null);
  }

  @Override
  public void doNIDDInformationRequestEvent(ServerS6tSession session, JNIDDInformationRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    fail("Received \"S6t NIR\" event, request[" + request + "], on session[" + session + "]", null);
  }
}
