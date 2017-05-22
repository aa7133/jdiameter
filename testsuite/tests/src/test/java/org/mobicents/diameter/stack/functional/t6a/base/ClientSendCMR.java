package org.mobicents.diameter.stack.functional.t6a.base;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.t6a.ClientT6aSession;
import org.jdiameter.api.t6a.events.JConnectionManagementAnswer;
import org.jdiameter.api.t6a.events.JConnectionManagementRequest;
import org.jdiameter.common.impl.app.t6a.JConnectionManagementRequestImpl;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.t6a.AbstractClient;

import static org.jdiameter.client.impl.helpers.Parameters.OwnDiameterURI;

/*
 * Copyright (c) 2017. AT&T Intellectual Property. All rights reserved
 */

/**
 * Created by Adi Enzel on 3/15/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public class ClientSendCMR extends AbstractClient {
  protected boolean receivedConnnectionManagement;
  protected boolean sentConnectionManagement;

  public ClientSendCMR() {

  }

  public void sendConnectionManagementRequest()  throws Exception {
    JConnectionManagementRequest request =
          new JConnectionManagementRequestImpl(super.createRequest(this.clientT6aSession, JConnectionManagementRequest.code));
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
    String str = "17325437654";
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


    this.clientT6aSession.sendConnectionManagementRequest(request);
    Utils.printMessage(log, super.stack.getDictionary(), request.getMessage(), true);
    this.sentConnectionManagement = true;

  }

  @Override
  public void doConnectionManagementAnswerEvent(ClientT6aSession session, JConnectionManagementRequest request, JConnectionManagementAnswer answer)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    if (this.receivedConnnectionManagement) {
      fail("T6a Received Connection-Managemet-Answer CMA more than once", null);
      return;
    }

    this.receivedConnnectionManagement = true;
  }

  public boolean isReceivedConnnectionManagement() {
    return receivedConnnectionManagement;
  }

  public boolean isSentConnectionManagement() {
    return sentConnectionManagement;
  }


}
