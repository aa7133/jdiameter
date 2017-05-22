package org.mobicents.diameter.stack.functional.t6a.base;

import org.jdiameter.api.*;
import org.jdiameter.api.t6a.ClientT6aSession;
import org.jdiameter.api.t6a.events.JMO_DataAnswer;
import org.jdiameter.api.t6a.events.JMO_DataRequest;
import org.jdiameter.common.impl.app.t6a.JMO_DataRequestImpl;
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
public class ClientSendODR extends AbstractClient {
  private boolean receivedMobileOriginationData;
  private boolean sentMobileOriginationData;

  protected ClientSendODR() {

  }

  protected void sendMobileOrihginatioDatanRequest()  throws Exception {
    JMO_DataRequest request =
          new JMO_DataRequestImpl(super.createRequest(this.clientT6aSession, JMO_DataRequest.code));
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

    //< MO-Data-Request > ::=   < Diameter Header: 8388733, PXY, 16777346 >
    //< Session-Id >
    //< User-Identifier >
    AvpSet userIdentifier = reqSet.addGroupedAvp(Avp.USER_IDENTIFIER, getApplicationId().getVendorId(), true, false);
    //add to group
    userIdentifier.addAvp(Avp.EXTERNAL_IDENTIFIER, "kuku@stam.com", getApplicationId().getVendorId(), true, false, true);
    //< Bearer-Identifier >
    reqSet.addAvp(Avp.BEARER_IDENTIFIER, "kukuriku.com", getApplicationId().getVendorId(), true, false, true);

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
    AvpSet ocSupportedFeature = reqSet.addGroupedAvp(Avp.OC_SUPPORTED_FEATURES, getApplicationId().getVendorId(), true, false);
    // add OC-Feature-Vector
    ocSupportedFeature.addAvp(Avp.OC_FEATURE_VECTOR, 28, getApplicationId().getVendorId(), true, false, true);
    //*[ Supported-Features ]
    //[ Non-IP-Data ]
    reqSet.addAvp(Avp.NON_IP_DATA, "hello world of IOT data, we got data",
          getApplicationId().getVendorId(), true, false, true);

    //*[ Proxy-Info ]
    //*[ Route-Record ]
    //[ RRC-Cause-Counter ]
    //*[AVP]


    this.clientT6aSession.sendMODataRequest(request);
    Utils.printMessage(log, super.stack.getDictionary(), request.getMessage(), true);
    this.sentMobileOriginationData = true;

  }

  @Override
  public void doMO_DataAnswerEvent(ClientT6aSession session, JMO_DataRequest request, JMO_DataAnswer answer)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    if (this.receivedMobileOriginationData) {
      fail("T6a Received CIA more than once", null);
      return;
    }

    this.receivedMobileOriginationData = true;
  }

  protected boolean isReceivedMobileOriginationData() {
    return receivedMobileOriginationData;
  }

  protected boolean isSentMobileOriginationData() {
    return sentMobileOriginationData;
  }


}
