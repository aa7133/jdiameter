package org.mobicents.diameter.stack.functional.s6t.base;

import org.jdiameter.api.*;
import org.jdiameter.api.s6t.ClientS6tSession;
import org.jdiameter.api.s6t.events.JConfigurationInformationAnswer;
import org.jdiameter.api.s6t.events.JConfigurationInformationRequest;
import org.jdiameter.common.impl.app.s6t.JConfigurationInformationRequestImpl;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.s6t.AbstractClient;
import org.mobicents.diameter.stack.functional.s6t.BCDStringConverter;
import org.mobicents.diameter.stack.functional.s6t.S6tMonitoringType;
import sun.font.TrueTypeFont;

import static org.jdiameter.client.impl.helpers.Parameters.OwnDiameterURI;

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
    //add group
    AvpSet ocSupportedFeature = reqSet.addGroupedAvp(Avp.OC_SUPPORTED_FEATURES, getApplicationId().getVendorId(), true, false);
    // add OC-Feature-Vector
    ocSupportedFeature.addAvp(Avp.OC_FEATURE_VECTOR, 28, getApplicationId().getVendorId(), true, false, true);
      //*[ Supported-Features ]
      //*[ Monitoring-Event-Configuration ]
    AvpSet monitoringEventConfig = reqSet.addGroupedAvp(Avp.MONITORING_EVENT_CONFIGURATION,getApplicationId().getVendorId(),true, false);
    monitoringEventConfig.addAvp(Avp.SCEF_REFERENCE_ID, 1, getApplicationId().getVendorId(), true, false, true);
    monitoringEventConfig.addAvp(Avp.SCEF_ID, this.stack.getConfiguration().getStringValue(OwnDiameterURI.ordinal(), "aaa://12.12.12.12:3344"), getApplicationId().getVendorId(), true, false, false);
    monitoringEventConfig.addAvp(Avp.MONITORING_TYPE, S6tMonitoringType.AVAILABILITY_AFTER_DDN_FAILURE, getApplicationId().getVendorId(), true, false, true);
    monitoringEventConfig.addAvp(Avp.MAXIMUM_NUMBER_OF_REPORTS, 200, getApplicationId().getVendorId(), true, false, true);

    monitoringEventConfig = reqSet.addGroupedAvp(Avp.MONITORING_EVENT_CONFIGURATION,getApplicationId().getVendorId(),true, false);
    monitoringEventConfig.addAvp(Avp.SCEF_REFERENCE_ID, 2, getApplicationId().getVendorId(), true, false);
    monitoringEventConfig.addAvp(Avp.SCEF_ID, "aaa://12.12.12.12:3344", getApplicationId().getVendorId(), true, false, true);
    monitoringEventConfig.addAvp(Avp.MONITORING_TYPE, S6tMonitoringType.LOCATION_REPORTING, getApplicationId().getVendorId(), true, false);
    monitoringEventConfig.addAvp(Avp.MAXIMUM_NUMBER_OF_REPORTS, 1200, getApplicationId().getVendorId(), true, false);

    monitoringEventConfig = reqSet.addGroupedAvp(Avp.MONITORING_EVENT_CONFIGURATION,getApplicationId().getVendorId(),true, false);
    monitoringEventConfig.addAvp(Avp.SCEF_REFERENCE_ID, 3, getApplicationId().getVendorId(), true, false);
    monitoringEventConfig.addAvp(Avp.SCEF_ID, "aaa://120.120.125.125:3344", getApplicationId().getVendorId(), true, false, true);
    monitoringEventConfig.addAvp(Avp.MONITORING_TYPE, S6tMonitoringType.CHANGE_OF_IMSI_IMEI_ASSOCIATION, getApplicationId().getVendorId(), true, false);
    monitoringEventConfig.addAvp(Avp.MAXIMUM_NUMBER_OF_REPORTS, 5000, getApplicationId().getVendorId(), true, false);

    monitoringEventConfig = reqSet.addGroupedAvp(Avp.MONITORING_EVENT_CONFIGURATION,getApplicationId().getVendorId(),true, false);
    monitoringEventConfig.addAvp(Avp.SCEF_REFERENCE_ID, 4, getApplicationId().getVendorId(), true, false);
    monitoringEventConfig.addAvp(Avp.SCEF_ID, this.stack.getConfiguration().getStringValue(OwnDiameterURI.ordinal(), "aaa://12.12.12.12:3344"), getApplicationId().getVendorId(), true, false, false );
    monitoringEventConfig.addAvp(Avp.MONITORING_TYPE, S6tMonitoringType.LOSS_OF_CONNECTIVITY, getApplicationId().getVendorId(), true, false);
    monitoringEventConfig.addAvp(Avp.MAXIMUM_NUMBER_OF_REPORTS, 300, getApplicationId().getVendorId(), true, false);

    monitoringEventConfig = reqSet.addGroupedAvp(Avp.MONITORING_EVENT_CONFIGURATION,getApplicationId().getVendorId(),true, false);
    monitoringEventConfig.addAvp(Avp.SCEF_REFERENCE_ID, 5, getApplicationId().getVendorId(), true, false);
    monitoringEventConfig.addAvp(Avp.SCEF_ID, this.stack.getConfiguration().getStringValue(OwnDiameterURI.ordinal(), "aaa://12.12.12.12:3344"), getApplicationId().getVendorId(), true, false, false );
    monitoringEventConfig.addAvp(Avp.MONITORING_TYPE, S6tMonitoringType.COMMUNICATION_FAILURE, getApplicationId().getVendorId(), true, false);
    monitoringEventConfig.addAvp(Avp.MAXIMUM_NUMBER_OF_REPORTS, 700, getApplicationId().getVendorId(), true, false);

    monitoringEventConfig = reqSet.addGroupedAvp(Avp.MONITORING_EVENT_CONFIGURATION,getApplicationId().getVendorId(),true, false);
    monitoringEventConfig.addAvp(Avp.SCEF_REFERENCE_ID, 6, getApplicationId().getVendorId(), true, false);
    monitoringEventConfig.addAvp(Avp.SCEF_ID, this.stack.getConfiguration().getStringValue(OwnDiameterURI.ordinal(), "aaa://12.12.12.12:3344"), getApplicationId().getVendorId(), true, false, false );
    monitoringEventConfig.addAvp(Avp.MONITORING_TYPE, S6tMonitoringType.UE_REACHABILITY, getApplicationId().getVendorId(), true, false);
    monitoringEventConfig.addAvp(Avp.MAXIMUM_NUMBER_OF_REPORTS, 400, getApplicationId().getVendorId(), true, false);

    monitoringEventConfig = reqSet.addGroupedAvp(Avp.MONITORING_EVENT_CONFIGURATION, getApplicationId().getVendorId(),true, false);
    monitoringEventConfig.addAvp(Avp.SCEF_REFERENCE_ID, 7, getApplicationId().getVendorId(), true, false, true);
    monitoringEventConfig.addAvp(Avp.SCEF_ID, this.stack.getConfiguration().getStringValue(OwnDiameterURI.ordinal(), "aaa://12.12.12.12:3344"), getApplicationId().getVendorId(), true, false, false );
    monitoringEventConfig.addAvp(Avp.MONITORING_TYPE, S6tMonitoringType.ROAMING_STATUS, getApplicationId().getVendorId(), true, false);
    monitoringEventConfig.addAvp(Avp.MAXIMUM_NUMBER_OF_REPORTS, 2000, getApplicationId().getVendorId(), true, false);

    monitoringEventConfig = reqSet.addGroupedAvp(Avp.MONITORING_EVENT_CONFIGURATION,getApplicationId().getVendorId(),true, false);
    monitoringEventConfig.addAvp(Avp.SCEF_REFERENCE_ID, 8, getApplicationId().getVendorId(), true, false);
    monitoringEventConfig.addAvp(Avp.SCEF_ID, this.stack.getConfiguration().getStringValue(OwnDiameterURI.ordinal(), "aaa://12.12.12.12:3344"), getApplicationId().getVendorId(), true, false, false );
    monitoringEventConfig.addAvp(Avp.MONITORING_TYPE, S6tMonitoringType.NUMBER_OF_UES_PRESENT_IN_A_GEOGRAPHICAL_AREA, getApplicationId().getVendorId(), true, false);
    monitoringEventConfig.addAvp(Avp.MAXIMUM_NUMBER_OF_REPORTS, 10000, getApplicationId().getVendorId(), true, false);

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

    StringBuffer str = new StringBuffer("");
    boolean session_id = false;
    boolean auth_sessin_state = false;
    boolean orig_host = false;
    boolean orig_relm = false;
    try {
      for (Avp a : answer.getMessage().getAvps()) {
        switch (a.getCode()) {
          case Avp.SESSION_ID:
            session_id = true;
            str.append("SESSION_ID : ").append(a.getUTF8String()).append("\n");
            break;
          case Avp.DRMP:
            break;
          case Avp.RESULT_CODE:
            break;
          case Avp.EXPERIMENTAL_RESULT:
            break;
          case Avp.AUTH_SESSION_STATE:
            auth_sessin_state = true;
            break;
          case Avp.ORIGIN_HOST:
            orig_host = true;
            break;
          case Avp.ORIGIN_REALM:
            orig_relm = true;
          case Avp.OC_SUPPORTED_FEATURES:
            break;
          case Avp.OC_OLR:
            break;
          case Avp.SUPPORTED_FEATURES: // grouped
            break;
          case Avp.USER_IDENTIFIER:
            break;
          case Avp.MONITORING_EVENT_REPORT: // grouped
            break;
          case Avp.MONITORING_EVENT_CONFIG_STATUS: //Grouped
            break;
          case Avp.AESE_COMMUNICATION_PATTEREN_CONFIG_STATUS: // Grouped
            break;
          case Avp.SUPPORTED_SERVICES: // Grouped
            break;
          case Avp.S6T_HSS_CAUSE:
            break;
          case Avp.FAILED_AVP: // Grouped
            break;
          case Avp.PROXY_INFO: // Grouped
            break;
          case Avp.ROUTE_RECORD: // Grouped
            break;
          default: // got Extra AVP'S
            break;
        }

      }
    } catch (AvpDataException e) {
      e.printStackTrace();
    }

    log.info(str.toString());

    this.receivedConfiguratinInfo = true;
  }

  public boolean isReceivedConfigurationInfo() {
    return receivedConfiguratinInfo;
  }

  public boolean isSentConfigurationInfo() {
    return sentConfigurationInfo;
  }


}
