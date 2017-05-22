package org.mobicents.diameter.stack.functional.t6a.base;


import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.ResultCode;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.t6a.ServerT6aSession;
import org.jdiameter.api.t6a.events.JConnectionManagementAnswer;
import org.jdiameter.api.t6a.events.JConnectionManagementRequest;
import org.jdiameter.common.impl.app.t6a.JConnectionManagementAnswerImpl;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.t6a.AbstractServer;

/*
 * Copyright (c) 2017. AT&T Intellectual Property. All rights reserved
 */

/**
 * Created by Adi Enzel on 3/16/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public class ServerRecvCMR extends AbstractServer {

  protected boolean receivedConnectionManagement;
  protected boolean sentConnectionManagement;

  protected JConnectionManagementRequest request;

  /**
   *
   */
  public ServerRecvCMR() {
  }

  public void sendConnectionManagementAnswer() throws Exception {
    if (!receivedConnectionManagement || request == null) {
      fail("T6a Did't got Connection-Management-Request (CMR)or Connection-Management-Answer (CMA) already sent.", null);
      throw new Exception("T6a Did't got Connection-Management-Request (CMR) or Configuration-Information-Answer (CIA) already sent. Request: " + this.request);
    }

    JConnectionManagementAnswer answer = new JConnectionManagementAnswerImpl((Request) this.request.getMessage(), ResultCode.SUCCESS);

    AvpSet reqSet = request.getMessage().getAvps();
    AvpSet set = answer.getMessage().getAvps();
    set.removeAvp(Avp.DESTINATION_HOST);
    set.removeAvp(Avp.DESTINATION_REALM);

    if (set.getAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID) == null) {
      AvpSet vendorSpecificApplicationId = set.addGroupedAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID, 0, false, false);
      // 1* [ Vendor-Id ]
      vendorSpecificApplicationId.addAvp(Avp.VENDOR_ID, getApplicationId().getVendorId(), true);
      // 0*1{ Auth-Application-Id }
      vendorSpecificApplicationId.addAvp(Avp.AUTH_APPLICATION_ID, getApplicationId().getAuthAppId(), true);
    }


    //< Connection-Management-Answer > ::=  	< Diameter Header: 8388732, PXY, 16777346 >
    //< Session-Id >
    //[ DRMP ]
    //[ Result-Code ]
    if (set.getAvp(Avp.RESULT_CODE) == null) {
      set.addAvp(Avp.RESULT_CODE, ResultCode.SUCCESS);
    }
    //[ Experimental-Result ]
    //{ Auth-Session-State }
    if (set.getAvp(Avp.AUTH_SESSION_STATE) == null) {
      set.addAvp(Avp.AUTH_SESSION_STATE, 1);
    }
    //{ Origin-Host }
    //{ Origin-Realm }
    //[ OC-Supported-Features ]
    //[ OC-OLR ]
    //*[ Load ]
    //*[ Supported-Features ]
    //[ PDN-Connection-Charging-Id ]
    //[ Extended-PCO ]
    //*[ Failed-AVP ]
    //*[ Proxy-Info ]
    //*[ Route-Record ]
    //*[AVP]

    this.serverT6aSession.sendConnectionManagementAnswer(answer);
    Utils.printMessage(log, super.stack.getDictionary(), answer.getMessage(), true);
    this.request = null;
    this.sentConnectionManagement = true;
  }

  @Override
  public Answer processRequest(Request request) {
    int code = request.getCommandCode();
    if (code != JConnectionManagementRequest.code) {
      fail("Received Request with code not used by T6a. Code[" + request.getCommandCode() + "]", null);
      return null;
    }
    if (super.serverT6aSession != null) {
      // do fail?
      fail("Received Request in base listener, not in app specific!" + code, null);
    }
    else {
      try {
        super.serverT6aSession = this.sessionFactory.getNewAppSession(request.getSessionId(), getApplicationId(), ServerT6aSession.class, (Object) null);
        ((NetworkReqListener) this.serverT6aSession).processRequest(request);
      }
      catch (Exception e) {
        e.printStackTrace();
        fail(null, e);
      }

    }
    return null;
  }

  @Override
  public void doSendConnectionManagementRequestEvent(ServerT6aSession session, JConnectionManagementRequest request)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    if (this.receivedConnectionManagement) {
      fail("Received Connection-Management-Request (CMR) more than once", null);
      return;
    }

    this.receivedConnectionManagement = true;
    this.request = request;
  }

  public boolean isReceivedConnectionManagement() {
    return receivedConnectionManagement;
  }

  public boolean isSentConfigurationInfo() {
    return sentConnectionManagement;
  }


}
