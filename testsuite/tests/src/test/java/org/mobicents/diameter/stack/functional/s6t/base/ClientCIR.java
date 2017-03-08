package org.mobicents.diameter.stack.functional.s6t.base;

import org.jdiameter.api.*;
import org.jdiameter.api.s6t.ClientS6tSession;
import org.jdiameter.api.s6t.events.JConfigurationInformationAnswer;
import org.jdiameter.api.s6t.events.JConfigurationInformationRequest;
import org.jdiameter.common.impl.app.s6t.JConfigurationInformationRequestImpl;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.s6t.AbstractClient;
import org.mobicents.diameter.stack.functional.s6t.BCDStringConverter;

/**
 * Created by Adi Enzel on 3/6/17.
 */
public class ClientCIR extends AbstractClient{
  protected boolean receivedConfiguratinInfo;
  protected boolean sentConfigurationInfo;

  public ClientCIR() {

  }

  public void sendConfigurationInformationRequest()  throws Exception {
    JConfigurationInformationRequest request =
            new JConfigurationInformationRequestImpl(super.createRequest(this.clientS6tSession, JConfigurationInformationRequest.code));
    AvpSet reqSet = request.getMessage().getAvps();

       //< Configuration-Information-Request > ::=	< Diameter Header: 8388718, REQ, PXY, 16777345 >
       //< Session-Id > set by SessionImpl.java super.createRequest(
       //[ DRMP ] ignored optional
       //{ Auth-Session-State }  set in AbstractClient allways 1
       //{ Origin-Host }
       //{ Origin-Realm }
       //[ Destination-Host ]
       //{ Destination-Realm } set by SessionImpl.java super.createRequest(
       //{ User-Identifier }
    // craete gruped
    AvpSet userIdentifier = reqSet.addGroupedAvp(Avp.USER_IDENTIFIER, getApplicationId().getVendorId(), true, false);
    //add to group
    userIdentifier.addAvp(Avp.EXTERNAL_IDENTIFIER, "kuku@stam.com", getApplicationId().getVendorId(), true, false, false);
    String str = "17325437654";
    //add to group
    userIdentifier.addAvp(Avp.MSISDN, BCDStringConverter.toBCD(str), getApplicationId().getVendorId(), true, false);
       //[ OC-Supported-Features ]
      //*[ Supported-Features ]
      //*[ Monitoring-Event-Configuration ]
       //[ CIR-Flags ]
      //*[ AESE-Communication-Pattern ]
      //*[ Proxy-Info ]
      //*[ Route-Record ]
      //*[AVP]

    this.clientS6tSession.sendConfigurationInformationRequest(request);
    Utils.printMessage(log, super.stack.getDictionary(), request.getMessage(), true);
    this.sentConfigurationInfo = true;

  }

  @Override
  public void doConfigurationInformationAnswerEvent(ClientS6tSession session, JConfigurationInformationRequest request, JConfigurationInformationAnswer answer)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    if (this.receivedConfiguratinInfo) {
      fail("S6t Received CIA more than once", null);
      return;
    }

    this.receivedConfiguratinInfo = true;
  }

  public boolean isReceivedConfigurationInfo() {
    return receivedConfiguratinInfo;
  }

  public boolean isSentConfigurationInfo() {
    return sentConfigurationInfo;
  }


}
