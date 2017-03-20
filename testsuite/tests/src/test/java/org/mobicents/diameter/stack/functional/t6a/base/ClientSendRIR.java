package org.mobicents.diameter.stack.functional.t6a.base;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.t6a.ClientT6aSession;
import org.jdiameter.api.t6a.events.JReportingInformationAnswer;
import org.jdiameter.api.t6a.events.JReportingInformationRequest;
import org.jdiameter.common.impl.app.t6a.JReportingInformationRequestImpl;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.t6a.AbstractClient;

import static org.jdiameter.client.impl.helpers.Parameters.OwnDiameterURI;

/**
 * Created by Adi Enzel on 3/19/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public class ClientSendRIR extends AbstractClient {
  private boolean receivedRiportingInformation;
  private boolean sentReportingInformation;

  public ClientSendRIR() {
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


  public void sendReportingInformationRequest() throws Exception {
    JReportingInformationRequest request = new JReportingInformationRequestImpl(super.createRequest(this.clientT6aSession, JReportingInformationRequest.code));
    AvpSet reqSet = request.getMessage().getAvps();

    //< Reporting-Information-Request > ::=	< Diameter Header: 8388719, PXY, 16777346 >
    //< Session-Id >
    //[ DRMP ]
    //{ Auth-Session-State }
    //{ Origin-Host }
    //{ Origin-Realm }
    //[ Destination-Host ]
    //{ Destination-Realm }
    //[ OC-Supported-Features ]
    AvpSet ocSupportedFeature = reqSet.addGroupedAvp(Avp.OC_SUPPORTED_FEATURES, getApplicationId().getVendorId(), true, false);
    // add OC-Feature-Vector
    ocSupportedFeature.addAvp(Avp.OC_FEATURE_VECTOR, 28, getApplicationId().getVendorId(), true, false, true);

    //*[ Supported-Features ]
    //*[ Monitoring-Event-Report ]
    AvpSet monitoringEventReport = reqSet.addGroupedAvp(Avp.MONITORING_EVENT_REPORT, getApplicationId().getVendorId(), true, false);
    //Monitoring-Event-Report ::= <AVP header: 3123 10415>
    //    { SCEF-Reference-ID }
    monitoringEventReport.addAvp(Avp.SCEF_REFERENCE_ID, 1,getApplicationId().getVendorId(), true, false);
    //    [ SCEF-ID ]
    monitoringEventReport.addAvp(Avp.SCEF_ID, this.stack.getConfiguration().getStringValue(OwnDiameterURI.ordinal(), "aaa://12.12.12.12:3344"), getApplicationId().getVendorId(), true, false, false);
    //    [ Monitoring-Type ]
    monitoringEventReport.addAvp(Avp.MONITORING_TYPE, MonitoringType.AVAILABILITY_AFTER_DDN_FAILURE, getApplicationId().getVendorId(), true, false, true);

    monitoringEventReport = reqSet.addGroupedAvp(Avp.MONITORING_EVENT_REPORT, getApplicationId().getVendorId(), true, false);
    monitoringEventReport.addAvp(Avp.SCEF_REFERENCE_ID, 2,getApplicationId().getVendorId(), true, false);
    monitoringEventReport.addAvp(Avp.SCEF_ID, "aaa://172.123.124.125:5566", getApplicationId().getVendorId(), true, false, false);
    monitoringEventReport.addAvp(Avp.MONITORING_TYPE, MonitoringType.CHANGE_OF_IMSI_IMEI_ASSOCIATION, getApplicationId().getVendorId(), true, false, true);
    //    [ Reachability-Information ]
    //    [ EPS-Location-Information ]
    //    [ Communication-Failure-Information ]
    //    *[ Number-Of-UE-Per-Location-Report ]
    //    *[AVP]

    //*[ Failed-AVP ]
    //*[ Proxy-Info ]
    //*[ Route-Record ]
    //*[AVP]

    this.clientT6aSession.sendReportingInformationRequest(request);
    Utils.printMessage(log, super.stack.getDictionary(), request.getMessage(), true);
    this.sentReportingInformation = true;
  }

  @Override
  public void doReportingInformationAnswerEvent(ClientT6aSession session, JReportingInformationRequest request, JReportingInformationAnswer answer)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    if (this.receivedRiportingInformation) {
      fail("T6a Received Reporting-Information-Answer (RIA) more than once", null);
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
            str.append("\tDRMP : ").append(a.getUTF8String()).append("\n");
            break;
          case Avp.RESULT_CODE:
            str.append("\tRESULT_CODE : ").append(a.getUTF8String()).append("\n");
            break;
          case Avp.EXPERIMENTAL_RESULT:
            str.append("\tEXPERIMENTAL_RESULT : ").append(a.getUTF8String()).append("\n");
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
    }  catch (AvpDataException e) {
      e.printStackTrace();
    }

    this.receivedRiportingInformation = true;

  }


  /**
   * Getter
   * @return
   */
  public boolean isReceivedRiportingInformation() {
    return receivedRiportingInformation;
  }

  /**
   * Getter
   * @return
   */
  public boolean isSentReportingInformation() {
    return sentReportingInformation;
  }


}
