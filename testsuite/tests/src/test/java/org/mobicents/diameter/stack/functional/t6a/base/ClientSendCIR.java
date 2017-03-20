package org.mobicents.diameter.stack.functional.t6a.base;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.t6a.ClientT6aSession;
import org.jdiameter.api.t6a.events.JConfigurationInformationAnswer;
import org.jdiameter.api.t6a.events.JConfigurationInformationRequest;
import org.jdiameter.common.impl.app.t6a.JConfigurationInformationRequestImpl;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.t6a.AbstractClient;

import static org.jdiameter.client.impl.helpers.Parameters.OwnDiameterURI;

/**
 * Created by Adi Enzel on 3/15/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public class ClientSendCIR extends AbstractClient {
  protected boolean receivedConfiguratinInfo;
  protected boolean sentConfigurationInfo;

  public ClientSendCIR() {

  }

  public void sendConfigurationInformationRequest()  throws Exception {
    JConfigurationInformationRequest request =
          new JConfigurationInformationRequestImpl(super.createRequest(this.clientT6aSession, JConfigurationInformationRequest.code));
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
   // userIdentifier.addAvp(Avp.MSISDN, BCDStringConverter.toBCD(str), getApplicationId().getVendorId(), true, false);
    //[ OC-Supported-Features ]
    //add group
    AvpSet ocSupportedFeature = reqSet.addGroupedAvp(Avp.OC_SUPPORTED_FEATURES, getApplicationId().getVendorId(), true, false);
    // add OC-Feature-Vector
    ocSupportedFeature.addAvp(Avp.OC_FEATURE_VECTOR, 28, getApplicationId().getVendorId(), true, false, false);
    //*[ Supported-Features ]
    //*[ Monitoring-Event-Configuration ]
    AvpSet monitoringEventConfig = reqSet.addGroupedAvp(Avp.MONITORING_EVENT_CONFIGURATION,getApplicationId().getVendorId(),true, false);
    monitoringEventConfig.addAvp(Avp.SCEF_REFERENCE_ID, 3456, getApplicationId().getVendorId(), true, false, false );
    monitoringEventConfig.addAvp(Avp.SCEF_ID, this.stack.getConfiguration().getStringValue(OwnDiameterURI.ordinal(), "aaa://12.12.12.12:3344"), getApplicationId().getVendorId(), true, false, false );
    monitoringEventConfig.addAvp(Avp.MONITORING_TYPE, 6 /*S6tMonitoringType.AVAILABILITY_AFTER_DDN_FAILURE */, getApplicationId().getVendorId(), true, false, false );

    monitoringEventConfig = reqSet.addGroupedAvp(Avp.MONITORING_EVENT_CONFIGURATION,getApplicationId().getVendorId(),true, false);
    monitoringEventConfig.addAvp(Avp.SCEF_REFERENCE_ID, 3457, getApplicationId().getVendorId(), true, false, false );
    monitoringEventConfig.addAvp(Avp.SCEF_ID,  this.getClientURI(), getApplicationId().getVendorId(), true, false, false );
    monitoringEventConfig.addAvp(Avp.MONITORING_TYPE, 2 /*S6tMonitoringType.LOCATION_REPORTING */, getApplicationId().getVendorId(), true, false, false );

    //[ CIR-Flags ]
    //*[ AESE-Communication-Pattern ]
    //*[ Proxy-Info ]
    //*[ Route-Record ]
    //*[AVP]

    this.clientT6aSession.sendConfigurationInformationRequest(request);
    Utils.printMessage(log, super.stack.getDictionary(), request.getMessage(), true);
    this.sentConfigurationInfo = true;

  }

  @Override
  public void doConfigurationInformationAnswerEvent(ClientT6aSession session, JConfigurationInformationRequest request, JConfigurationInformationAnswer answer)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    if (this.receivedConfiguratinInfo) {
      fail("T6a Received CIA more than once", null);
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
