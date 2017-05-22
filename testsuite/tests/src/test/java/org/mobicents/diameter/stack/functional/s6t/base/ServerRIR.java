package org.mobicents.diameter.stack.functional.s6t.base;

import java.io.InputStream;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.s6t.ClientS6tSession;
import org.jdiameter.api.s6t.ServerS6tSession;
import org.jdiameter.api.s6t.events.JReportingInformationAnswer;
import org.jdiameter.api.s6t.events.JReportingInformationRequest;
import org.jdiameter.common.impl.app.s6t.S6tSessionFactoryImpl;
import org.jdiameter.common.impl.app.s6t.JReportingInformationRequestImpl;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.s6t.AbstractServer;


/*
 * Copyright (c) 2017. AT&T Intellectual Property. All rights reserved
 */

/**
 * Created by Adi Enzel on 3/8/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public class ServerRIR extends AbstractServer {

  protected boolean receivedReportingInformation;
  protected boolean sentReportingInformation;

  /**
   *
   */
  public ServerRIR() {
  }

  @Override
  public void init(InputStream configStream, String clientID) throws Exception {
    try {
      super.init(configStream, clientID, ApplicationId.createByAuthAppId(10415, 16777345));
      S6tSessionFactoryImpl S6tSessionFactory = new S6tSessionFactoryImpl(this.sessionFactory);
      sessionFactory.registerAppFacory(ServerS6tSession.class, S6tSessionFactory);
      sessionFactory.registerAppFacory(ClientS6tSession.class, S6tSessionFactory);
      S6tSessionFactory.setServerSessionListener(this);
      this.serverS6tSession = sessionFactory.getNewAppSession(getApplicationId(), ServerS6tSession.class);
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

  public void sendReportingInformationRequest() throws Exception {

    JReportingInformationRequest request =
        new JReportingInformationRequestImpl(super.createRequest(this.serverS6tSession, JReportingInformationRequest.code));
    AvpSet reqSet = request.getMessage().getAvps();

    //TODO set values for Reporting-Information-Request
    // <Registration-Termination-Request> ::= < Diameter Header: 304, REQ, PXY, 16777216 >
    // < Session-Id >
    // { Vendor-Specific-Application-Id }
    // { Auth-Session-State }
    // { Origin-Host }
    // { Origin-Realm }
    // { Destination-Host }
    // { Destination-Realm }
    // { User-Name }
    reqSet.addAvp(Avp.USER_NAME, "8388719", false);
    // [ Associated-Identities ]
    // *[ Supported-Features ]
    // *[ Public-Identity ]
    // { Deregistration-Reason }
    AvpSet deregeReason = reqSet.addGroupedAvp(Avp.DEREGISTRATION_REASON, getApplicationId().getVendorId(), true, false);
    deregeReason.addAvp(Avp.REASON_CODE, 0, getApplicationId().getVendorId(), true, false, true);
    // *[ AVP ]
    // *[ Proxy-Info ]
    // *[ Route-Record ]

    this.serverS6tSession.sendReportingInformationRequest(request);
    Utils.printMessage(log, super.stack.getDictionary(), request.getMessage(), true);

    this.sentReportingInformation = true;
  }

  @Override
  public void doReportingInformationAnswerEvent(ServerS6tSession session, JReportingInformationRequest request, JReportingInformationAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    if (this.receivedReportingInformation) {
      fail("S6t Received Reporting-Information-Answer (RIA) more than once", null);
      return;
    }

    this.receivedReportingInformation = true;
  }

  public boolean isReceivedReportingInformation() {
    return receivedReportingInformation;
  }

  public boolean isSentReportingInformation() {
    return sentReportingInformation;
  }

}
