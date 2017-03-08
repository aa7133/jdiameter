package org.mobicents.diameter.stack.functional.s6t.base;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.s6t.ServerS6tSession;
import org.jdiameter.api.s6t.events.JNIDDInformationAnswer;
import org.jdiameter.api.s6t.events.JNIDDInformationRequest;
import org.jdiameter.common.impl.app.s6t.JNIDDInformationAnswerImpl;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.s6t.AbstractServer;

/**
 * Created by Adi Enzel on 3/8/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public class ServerNIR extends AbstractServer{

  protected boolean receivedNIDDInformation;
  protected boolean sentNIDDInformation;

  protected JNIDDInformationRequest request;

  /**
   *
   */
  public ServerNIR() {
  }

  public void sendNIDDInformationAnswer() throws Exception {
    if (!receivedNIDDInformation || request == null) {
      fail("S6t - Did not receive NIDD-Information-Request (NIR) or answer already sent.", null);
      throw new Exception("S6t Did not receive NIDD-Information-Request (NIR) or answer already sent. Request: " + this.request);
    }

    JNIDDInformationAnswer answer = new JNIDDInformationAnswerImpl((Request) this.request.getMessage(), 2001);

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
    this.serverS6tSession.sendNIDDInformationAnswer(answer);
    Utils.printMessage(log, super.stack.getDictionary(), answer.getMessage(), true);
    this.request = null;
    this.sentNIDDInformation = true;
  }

  @Override
  public Answer processRequest(Request request) {
    int code = request.getCommandCode();
    if (code != JNIDDInformationRequest.code) {
      fail("S6t - Received Request with code not used by S6t!. Code[" + request.getCommandCode() + "]", null);
      return null;
    }
    if (super.serverS6tSession != null) {
      // do fail?
      fail("S6t - Received Request in base listener, not in app specific!" + code, null);
    }
    else {
      try {
        super.serverS6tSession = this.sessionFactory.getNewAppSession(request.getSessionId(), getApplicationId(), ServerS6tSession.class, (Object) null);
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
  public void doNIDDInformationRequestEvent(ServerS6tSession session, JNIDDInformationRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    if (this.receivedNIDDInformation) {
      fail("S6t - Received NIDD-Information-Request (NIR) more than once", null);
      return;
    }

    this.receivedNIDDInformation = true;
    this.request = request;
  }

  public boolean isReceivedNIDDInformation() {
    return receivedNIDDInformation;
  }

  public boolean isSentNIDDInformation() {
    return sentNIDDInformation;
  }

}
