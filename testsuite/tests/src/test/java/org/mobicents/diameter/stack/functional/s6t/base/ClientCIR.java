package org.mobicents.diameter.stack.functional.s6t.base;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.s6t.events.JConfigurationInformationRequest;
import org.jdiameter.common.impl.app.s6t.JConfigurationInformationRequestImpl;
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

  public void sendConfigurationInfoRequest()  throws Exception {
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
  }

}
