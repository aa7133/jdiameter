package org.mobicents.diameter.stack.functional.t6a.base;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.t6a.ClientT6aSession;
import org.jdiameter.api.t6a.ServerT6aSession;
import org.jdiameter.api.t6a.events.JConnectionManagementAnswer;
import org.jdiameter.api.t6a.events.JConnectionManagementRequest;
import org.jdiameter.common.impl.app.t6a.JConnectionManagementRequestImpl;
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
public class ServerSendCMR extends AbstractServer {
  private boolean receivedConnectionManagement;
  private boolean sentConnectionManagement;

  protected ServerSendCMR() {
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

  protected void sendConnectionManagementRequest() throws Exception {

    JConnectionManagementRequest request =
          new JConnectionManagementRequestImpl(super.createRequest(this.serverT6aSession, JConnectionManagementRequest.code));
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


    //TODO set values for Connection-Management-Request
    //< Connection-Management-Request > ::=   < Diameter Header: 8388732, PXY, 16777346 >
    //< Session-Id >
    //< User-Identifier >
    AvpSet userIdentifier = reqSet.addGroupedAvp(Avp.USER_IDENTIFIER, getApplicationId().getVendorId(), true, false);
    //add to group
    userIdentifier.addAvp(Avp.EXTERNAL_IDENTIFIER, "kuku@stam.com", getApplicationId().getVendorId(), true, false, false);
    //< Bearer-Identifier >
    reqSet.addAvp(Avp.BEARER_IDENTIFIER, "kukuriku.com", getApplicationId().getVendorId(), true, false, false);

    reqSet.addAvp(vid);
    //[ DRMP ]
    //{ Auth-Session-State }
    reqSet.addAvp(authSessionState);
    //{ Origin-Host }
    reqSet.addAvp(origHost);
    //{ Origin-Realm }
    reqSet.addAvp(origRelm);
    //[ Destination-Host ]
    //{ Destination-Realm }
    reqSet.addAvp(destRelm);
    //[ OC-Supported-Features ]
    //[ CMR-Flags ]
    //[ Maximum-UE-Availability-Time ]
    //*[ Supported-Features ]
    //[ Connection-Action ]
    //[ Service-Selection ]
    //[ Serving-PLMN-Rate-Control ]
    //[ Extended-PCO ]
    //[ 3GPP-Charging-Characteristics ]
    //[ RAT-Type ]
    //[ Terminal-Information ]
    //[ Visited-PLMN-Id ]
    //*[ Failed-AVP ]
    //*[ Proxy-Info ]
    //*[ Route-Record ]
    //*[AVP]


    this.serverT6aSession.sendConnectionManagementRequest(request);
    Utils.printMessage(log, super.stack.getDictionary(), request.getMessage(), true);

    this.sentConnectionManagement = true;
  }

  @Override
  public void doSendConnectionManagementAnswertEvent(ServerT6aSession session, JConnectionManagementRequest request, JConnectionManagementAnswer answer)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    if (this.receivedConnectionManagement) {
      fail("T6a Received Connection-Management-Answer (CMA) more than once", null);
      return;
    }

    this.receivedConnectionManagement = true;
  }


  protected boolean isReceivedConnectionManagement() {
    return receivedConnectionManagement;
  }

  public boolean isSentConnectionManagement() {
    return sentConnectionManagement;
  }
}
