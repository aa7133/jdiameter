package org.mobicents.diameter.stack.functional.s6t.base;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.Request;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.ResultCode;

import org.jdiameter.api.s6t.ServerS6tSession;
import org.jdiameter.api.s6t.events.JConfigurationInformationAnswer;
import org.jdiameter.api.s6t.events.JConfigurationInformationRequest;
import org.jdiameter.common.impl.app.s6t.JConfigurationInformationAnswerImpl;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.s6t.AbstractServer;

/**
 * Created by Adi Enzel on 3/7/17.
 *
 *  @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public class ServerCIR extends AbstractServer {

  protected boolean receivedConfigurationInfo;
  protected boolean sentConfigurationInfo;

  protected JConfigurationInformationRequest request;

  /**
   *
   */
  public ServerCIR() {
  }

  public void sendConfigurationInformationAnswer() throws Exception {
    if (!receivedConfigurationInfo || request == null) {
      fail("S6t Did't got Configuration-Information-Request (CIR) or Configuration-Information-Answer (CIA) already sent.", null);
      throw new Exception("S6t Did't got Configuration-Information-Request (CIR) or Configuration-Information-Answer (CIA) already sent. Request: " + this.request);
    }

    JConfigurationInformationAnswer answer = new JConfigurationInformationAnswerImpl((Request) this.request.getMessage(), ResultCode.SUCCESS);

    AvpSet reqSet = request.getMessage().getAvps();
    AvpSet set = answer.getMessage().getAvps();
    set.removeAvp(Avp.DESTINATION_HOST);
    set.removeAvp(Avp.DESTINATION_REALM);


    AvpSet monitoringEvents = reqSet.getAvps(Avp.MONITORING_EVENT_CONFIGURATION);


    StringBuffer str = new StringBuffer("");
    for (Avp avp: monitoringEvents) {
      str.setLength(0);
      AvpSet monEv = set.addGroupedAvp(Avp.MONITORING_EVENT_REPORT, getApplicationId().getVendorId(),true, false);
      for (Avp a: avp.getGrouped()) {
        if (a.getCode() == Avp.SCEF_REFERENCE_ID) {
          monEv.addAvp(a);
          if (log.isInfoEnabled()) {
            str.append("\t SCEF_REFERENCE_ID : ").append(a.getInteger32()).append("\n");
          }
        }
        if (a.getCode() == Avp.SCEF_ID) {
          monEv.addAvp(a);
          if (log.isInfoEnabled()) {
            str.append(new StringBuffer("\tSCEF_ID : ")).append(a.getDiameterIdentity()).append("\n");
          }
        }
        if (a.getCode() == Avp.MONITORING_TYPE) {
          monEv.addAvp(a);
          if (log.isInfoEnabled()) {
            str.append(new StringBuffer("\tMONITORING_TYPE : ")).append(a.getInteger32()).append("\n");
          }
        }
      }
      if (log.isInfoEnabled()) {
        log.info(str.toString());
      }
      }

    set.addAvp(reqSet.getAvp(Avp.CC_REQUEST_TYPE), reqSet.getAvp(Avp.CC_REQUEST_NUMBER), reqSet.getAvp(Avp.AUTH_APPLICATION_ID));

    request = null;

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
    this.serverS6tSession.sendConfigurationInformationAnswer(answer);
    Utils.printMessage(log, super.stack.getDictionary(), answer.getMessage(), true);
    this.request = null;
    this.sentConfigurationInfo = true;
  }

  @Override
  public Answer processRequest(Request request) {
    int code = request.getCommandCode();
    if (code != JConfigurationInformationRequest.code) {
      fail("Received Request with code not used by S6t!. Code[" + request.getCommandCode() + "]", null);
      return null;
    }
    if (this.serverS6tSession != null) {
      // do fail?
      fail("Received Request in base listener, not in app specific!" + code, null);
    }
    else {
      try {
        this.serverS6tSession = this.sessionFactory.getNewAppSession(request.getSessionId(), getApplicationId(), ServerS6tSession.class, (Object) null);
        ((NetworkReqListener) this.serverS6tSession).processRequest(request);
      }
      catch (Exception e) {
        e.printStackTrace();
        fail(null, e);
      }

    }
    return null;
  }

  @Override
  public void doConfigurationInformationRequestEvent(ServerS6tSession session, JConfigurationInformationRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    if (this.receivedConfigurationInfo) {
      fail("Received Configuration-Information-Request (CIR) more than once", null);
      return;
    }

    this.receivedConfigurationInfo = true;
    this.request = request;
  }

  public boolean isReceivedConfigurationInfo() {
    return receivedConfigurationInfo;
  }

  public boolean isSentConfigurationInfo() {
    return sentConfigurationInfo;
  }

}
