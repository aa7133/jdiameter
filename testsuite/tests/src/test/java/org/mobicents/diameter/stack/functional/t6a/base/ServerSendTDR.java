package org.mobicents.diameter.stack.functional.t6a.base;

import org.jdiameter.api.*;
import org.jdiameter.api.t6a.ClientT6aSession;
import org.jdiameter.api.t6a.ServerT6aSession;
import org.jdiameter.api.t6a.events.JMT_DataAnswer;
import org.jdiameter.api.t6a.events.JMT_DataRequest;
import org.jdiameter.common.impl.app.t6a.JMT_DataRequestImpl;
import org.jdiameter.common.impl.app.t6a.T6aSessionFactoryImpl;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.t6a.AbstractServer;

import java.io.InputStream;

/*
 * Copyright (c) 2017. AT&T Intellectual Property. All rights reserved
 */

/**
 * Created by Adi Enzel on 16/03/2017.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public class ServerSendTDR extends AbstractServer {
  private boolean receivedMTData;
  private boolean sentMTData;

  protected ServerSendTDR() {
  }

  @Override
  public void init(InputStream configStream, String clientID) throws Exception {
    try {
      super.init(configStream, clientID, ApplicationId.createByAuthAppId(10415, 16777346));
      T6aSessionFactoryImpl t6aSessionFactory = new T6aSessionFactoryImpl(this.sessionFactory);
      sessionFactory.registerAppFacory(ServerT6aSession.class, t6aSessionFactory);
      sessionFactory.registerAppFacory(ClientT6aSession.class, t6aSessionFactory);
      t6aSessionFactory.setServerSessionListener(this);
      this.serverT6aSession = sessionFactory.getNewAppSession(getApplicationId(), ServerT6aSession.class);
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

  protected void sendMTDataRequest() throws Exception {

    JMT_DataRequest request =
          new JMT_DataRequestImpl(super.createRequest(this.serverT6aSession, JMT_DataRequest.code));
    AvpSet reqSet = request.getMessage().getAvps();

    Avp vid = reqSet.getAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID);
    reqSet.removeAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID);
    reqSet.removeAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID);

    Avp destRelm = reqSet.getAvp(Avp.DESTINATION_REALM);
    reqSet.removeAvp(Avp.DESTINATION_REALM);

    Avp origRelm = reqSet.getAvp(Avp.ORIGIN_REALM);
    reqSet.removeAvp(Avp.ORIGIN_REALM);

    Avp authSessionState = reqSet.getAvp(Avp.AUTH_SESSION_STATE);
    reqSet.removeAvp(Avp.AUTH_SESSION_STATE);

    Avp origHost = reqSet.getAvp(Avp.ORIGIN_HOST);
    reqSet.removeAvp(Avp.ORIGIN_HOST);

    //< MT-Data-Request > ::=   < Diameter Header: 8388734, PXY, 16777346 >
    // < Session-Id >
    // < User-Identifier >
    AvpSet userIdentifier = reqSet.addGroupedAvp(Avp.USER_IDENTIFIER, getApplicationId().getVendorId(), true, false);
    //add to group
    userIdentifier.addAvp(Avp.EXTERNAL_IDENTIFIER, "kuku@stam.com", getApplicationId().getVendorId(), true, false, false);
    // < Bearer-Identifier >
    reqSet.addAvp(Avp.BEARER_IDENTIFIER, "kukuriku.com", getApplicationId().getVendorId(), true, false, false);

    reqSet.addAvp(vid);
    // [ DRMP ]
    // { Auth-Session-State }
    reqSet.addAvp(authSessionState);
    // { Origin-Host }
    reqSet.addAvp(origHost);
    // { Origin-Realm }
    reqSet.addAvp(origRelm);
    // [ Destination-Host ]
    // { Destination-Realm }
    reqSet.addAvp(destRelm);
    // [ OC-Supported-Features ]
    // *[ Supported-Features ]
    // [ Non-IP-Data ]
    reqSet.addAvp(Avp.NON_IP_DATA, "hello world of IOT data, we got data",
          getApplicationId().getVendorId(), true, false, true);
    // [ SCEF-Wait-Time ]
    // [ Maximum-Retransmission-Time ]
    // *[ Proxy-Info ]
    // *[ Route-Record ]
    // *[AVP]

    this.serverT6aSession.sendMT_DataRequest(request);
    Utils.printMessage(log, super.stack.getDictionary(), request.getMessage(), true);

    this.sentMTData = true;
  }

  @Override
  public void doSendMT_DataAnswertEvent(ServerT6aSession session, JMT_DataRequest request, JMT_DataAnswer answer)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    if (this.receivedMTData) {
      fail("T6a Received MT-Data-Answer (TDA) more than once", null);
      return;
    }

    this.receivedMTData = true;
  }


  protected boolean isReceivedMTData() {
    return receivedMTData;
  }

  protected boolean isSentMTData() {
    return sentMTData;
  }
}
