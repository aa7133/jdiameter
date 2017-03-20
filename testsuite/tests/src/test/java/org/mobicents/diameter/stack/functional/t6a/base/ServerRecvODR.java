package org.mobicents.diameter.stack.functional.t6a.base;


import org.jdiameter.api.*;
import org.jdiameter.api.t6a.ServerT6aSession;
import org.jdiameter.api.t6a.events.JMO_DataAnswer;
import org.jdiameter.api.t6a.events.JMO_DataRequest;
import org.jdiameter.common.impl.app.t6a.JMO_DataAnswerImpl;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.t6a.AbstractServer;

/**
 * Created by Adi Enzel on 3/16/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public class ServerRecvODR extends AbstractServer {

  private boolean receivedMoData;
  private boolean sentMoData;

  protected JMO_DataRequest request;

  /**
   *
   */
  protected ServerRecvODR() {
  }

  protected void sendMODataAnswer() throws Exception {
    if (!receivedMoData || request == null) {
      fail("T6a Did't got MO-Data-Request (ODR) or MO-Data-Answer (ODA) already sent.", null);
      throw new Exception("T6a Did't got MO-Data-Request (ODR) or MO-Data-Answer ODA) already sent. Request: " + this.request);
    }

    JMO_DataAnswer answer = new JMO_DataAnswerImpl((Request) this.request.getMessage(), ResultCode.SUCCESS);

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

    //< MO-Data-Answer > ::=  	< Diameter Header: 8388733, PXY, 16777346 >
    // < Session-Id >
    // [ DRMP ]
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
    // [ OC-Supported-Features ]
    // [ OC-OLR ]
    // *[ Load ]
    // *[ Supported-Features ]
    // [ Failed-AVP ]
    // *[ Proxy-Info ]
    // *[ Route-Record ]
    // *[AVP]


    set.addAvp(reqSet.getAvp(Avp.CC_REQUEST_TYPE), reqSet.getAvp(Avp.CC_REQUEST_NUMBER), reqSet.getAvp(Avp.AUTH_APPLICATION_ID));



    this.serverT6aSession.sendMO_DataAnswer(answer);
    Utils.printMessage(log, super.stack.getDictionary(), answer.getMessage(), true);
    this.request = null;
    this.sentMoData = true;
  }

  @Override
  public Answer processRequest(Request request) {
    int code = request.getCommandCode();
    if (code != JMO_DataRequest.code) {
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
  public void doSendMO_DataRequestEvent(ServerT6aSession session, JMO_DataRequest request)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    if (this.receivedMoData) {
      fail("Received MO-Data-Request (ODR) more than once", null);
      return;
    }

    this.receivedMoData = true;
    this.request = request;
  }

  protected boolean isReceivedMoData() {
    return receivedMoData;
  }

  protected boolean isSentMoData() {
    return sentMoData;
  }


}
