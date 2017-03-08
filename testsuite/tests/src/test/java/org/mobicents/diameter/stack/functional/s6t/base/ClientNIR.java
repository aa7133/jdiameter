package org.mobicents.diameter.stack.functional.s6t.base;


import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.s6t.ClientS6tSession;
import org.jdiameter.api.s6t.events.JNIDDInformationAnswer;
import org.jdiameter.api.s6t.events.JNIDDInformationRequest;
import org.jdiameter.common.impl.app.s6t.JNIDDInformationRequestImpl;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.s6t.AbstractClient;

/**
 * Created by Adi Enzel on 3/8/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public class ClientNIR extends AbstractClient  {
  protected boolean receivedNIDDInformation;
  protected boolean sentNIDDInformation;

  /**
   *
   */
  public ClientNIR() {
  }

  public void sendNIDDInformation() throws Exception {
    JNIDDInformationRequest request = new JNIDDInformationRequestImpl(super.createRequest(this.clientS6tSession, JNIDDInformationRequest.code));
    AvpSet reqSet = request.getMessage().getAvps();

    //TODO fix paramters
    reqSet.addAvp(Avp.SERVER_NAME, "ala", getApplicationId().getVendorId(), true, false, false);
    // <Location-Info-Request> ::= < Diameter Header: 302, REQ, PXY,
    // 16777216 >
    // < Session-Id >
    // { Vendor-Specific-Application-Id }
    // { Auth-Session-State }
    // { Origin-Host }
    // { Origin-Realm }
    // [ Destination-Host ]
    // { Destination-Realm }
    // [ Originating-Request ]
    // *[ Supported-Features ]
    // { Public-Identity }
    AvpSet userIdentity = reqSet.addGroupedAvp(Avp.USER_IDENTITY, getApplicationId().getVendorId(), true, false);
    // User-Identity ::= <AVP header: 700 10415>
    // [Public-Identity]
    userIdentity.addAvp(Avp.PUBLIC_IDENTITY, "tralalalal user", getApplicationId().getVendorId(), true, false, false);
    // [MSISDN]
    // *[AVP]
    // [ User-Authorization-Type ]
    // *[ AVP ]
    // *[ Proxy-Info ]
    this.clientS6tSession.sendNIDDInformationRequest(request);
    Utils.printMessage(log, super.stack.getDictionary(), request.getMessage(), true);
    this.sentNIDDInformation = true;
  }

  @Override
  public void doNIDDInformationAnswerEvent(ClientS6tSession session, JNIDDInformationRequest request, JNIDDInformationAnswer answer) throws InternalException,
  IllegalDiameterStateException, RouteException, OverloadException {
    if (this.receivedNIDDInformation) {
      fail("S6t - Received NIDD-Information-Answer (NIA) more than once", null);
      return;
    }

    this.receivedNIDDInformation = true;
  }

  public boolean isReceivedNIDDInformation() {
    return receivedNIDDInformation;
  }

  public boolean isSentNIDDInformation() {
    return sentNIDDInformation;
  }

}
