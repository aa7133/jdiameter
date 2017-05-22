package org.mobicents.diameter.stack.functional.t6a.base;

import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.ResultCode;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.t6a.events.JReportingInformationAnswer;
import org.jdiameter.api.t6a.ClientT6aSession;
import org.jdiameter.api.t6a.ServerT6aSession;
import org.jdiameter.api.t6a.events.JReportingInformationRequest;
import org.jdiameter.common.impl.app.t6a.JReportingInformationAnswerImpl;
import org.jdiameter.common.impl.app.t6a.JReportingInformationRequestImpl;
import org.jdiameter.common.impl.app.t6a.T6aSessionFactoryImpl;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.t6a.AbstractServer;

import java.io.InputStream;

/*
 * Copyright (c) 2017. AT&T Intellectual Property. All rights reserved
 */

/**
 * Created by Adi Enzel on 3/19/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public class ServerRecvRIR extends AbstractServer {
  private boolean receivedRiportingInformation;
  private boolean sentReportingInformation;

  protected JReportingInformationRequest request;

  protected ServerRecvRIR() {
  }

  protected void sendReportingInformationAnswer() throws Exception {
    if (!receivedRiportingInformation || request == null) {
      fail("T6a - Did not receive Reporting-Information-Request (RIR) or answer already sent.", null);
      throw new Exception("T6a Did not receive Reporting-Information-Request (NIR) or answer already sent. Request: " + this.request);
    }

    JReportingInformationAnswer answer = new JReportingInformationAnswerImpl((Request) this.request.getMessage(), ResultCode.SUCCESS);

    AvpSet reqSet = request.getMessage().getAvps();
    AvpSet set = answer.getMessage().getAvps();
    set.removeAvp(Avp.DESTINATION_HOST);
    set.removeAvp(Avp.DESTINATION_REALM);
    set.addAvp(reqSet.getAvp(Avp.CC_REQUEST_TYPE), reqSet.getAvp(Avp.CC_REQUEST_NUMBER), reqSet.getAvp(Avp.AUTH_APPLICATION_ID));

    request = null;
    //TODO set paramters
    // <Location-Info-Answer> ::= < Diameter Header: 302, PXY, 16777216 >
    // < Session-Id >
    // { Vendor-Specific-Application-Id }
    if (set.getAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID) == null) {
      AvpSet vendorSpecificApplicationId = set.addGroupedAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID, 0, false, false);
      // 1* [ Vendor-Id ]
      vendorSpecificApplicationId.addAvp(Avp.VENDOR_ID, getApplicationId().getVendorId(), true);
      // 0*1{ Auth-Application-Id }
      vendorSpecificApplicationId.addAvp(Avp.AUTH_APPLICATION_ID, getApplicationId().getAuthAppId(), true);
    }
    // [ Result-Code ]
    if (set.getAvp(Avp.RESULT_CODE) == null) {
      set.addAvp(Avp.RESULT_CODE, ResultCode.SUCCESS);
    }
    // [ Experimental-Result ]
    // { Auth-Session-State }
    if (set.getAvp(Avp.AUTH_SESSION_STATE) == null) {
      set.addAvp(Avp.AUTH_SESSION_STATE, 1);
    }
    // { Origin-Host }
    // { Origin-Realm }
    // *[ Supported-Features ]
    // [ Server-Name ]
    // [ Server-Capabilities ]
    // [ Wildcarded-PSI ]
    // [ Wildcarded-IMPU ]
    // *[ AVP ]
    // *[ Failed-AVP ]
    // *[ Proxy-Info ]
    // *[ Route-Record ]
    this.serverT6aSession.sendReportingInformationAnswer(answer);
    Utils.printMessage(log, super.stack.getDictionary(), answer.getMessage(), true);
    this.request = null;
    this.sentReportingInformation = true;
  }

  @Override
  public Answer processRequest(Request request) {
    int code = request.getCommandCode();
    if (code != JReportingInformationRequest.code) {
      fail("T6a - Received Request with code not used by T6a!. Code[" + request.getCommandCode() + "]", null);
      return null;
    }
    if (super.serverT6aSession != null) {
      // do fail?
      fail("T6a - Received Request in base listener, not in app specific!" + code, null);
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
  public void doSendReportingInformationRequestEvent(ServerT6aSession session, JReportingInformationRequest request)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    if (this.receivedRiportingInformation) {
      fail("T6a - Received Reporting-Information-Request (RIR) more than once", null);
      return;
    }

    this.receivedRiportingInformation = true;
    this.request = request;
  }



  /**
   * Getter
   * @return boolean
   */
  protected boolean isReceivedRiportingInformation() {
    return receivedRiportingInformation;
  }

  /**
   * Getter
   * @return boolean
   */
  protected boolean isSentReportingInformation() {
    return sentReportingInformation;
  }

  private class MonitoringType {
    public static final int LOSS_OF_CONNECTIVITY = 0;
    public static final int UE_REACHABILITY = 1;
    public static final int LOCATION_REPORTING = 2;
    public static final int CHANGE_OF_IMSI_IMEI_ASSOCIATION = 3;
    public static final int ROAMING_STATUS = 4;
    public static final int COMMUNICATION_FAILURE = 5;
    public static final int AVAILABILITY_AFTER_DDN_FAILURE = 6;
    public static final int NUMBER_OF_UES_PRESENT_IN_A_GEOGRAPHICAL_AREA = 7;
  }


}
