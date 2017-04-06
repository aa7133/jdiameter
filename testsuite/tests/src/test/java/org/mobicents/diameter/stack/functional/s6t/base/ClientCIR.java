package org.mobicents.diameter.stack.functional.s6t.base;

import org.jdiameter.api.*;
import org.jdiameter.api.s6t.ClientS6tSession;
import org.jdiameter.api.s6t.events.JConfigurationInformationAnswer;
import org.jdiameter.api.s6t.events.JConfigurationInformationRequest;
import org.jdiameter.common.impl.app.s6t.JConfigurationInformationRequestImpl;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.s6t.AbstractClient;

import static org.jdiameter.client.impl.helpers.Parameters.OwnDiameterURI;

/**
 * Created by Adi Enzel on 3/6/17.
 */
public class ClientCIR extends AbstractClient{
  protected boolean receivedConfiguratinInfo;
  protected boolean sentConfigurationInfo;

  private BCDStringConverter bcd;

  public ClientCIR() {
    bcd = new BCDStringConverter();
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
    userIdentifier.addAvp(Avp.MSISDN, this.bcd.toBCD(str), getApplicationId().getVendorId(), true, false);
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
    monitoringEventConfig.addAvp(Avp.MONITORING_TYPE, MonitoringType.AVAILABILITY_AFTER_DDN_FAILURE, getApplicationId().getVendorId(), true, false, true);
    monitoringEventConfig.addAvp(Avp.MAXIMUM_NUMBER_OF_REPORTS, 200, getApplicationId().getVendorId(), true, false, true);

    monitoringEventConfig = reqSet.addGroupedAvp(Avp.MONITORING_EVENT_CONFIGURATION,getApplicationId().getVendorId(),true, false);
    monitoringEventConfig.addAvp(Avp.SCEF_REFERENCE_ID, 2, getApplicationId().getVendorId(), true, false);
    monitoringEventConfig.addAvp(Avp.SCEF_ID, "aaa://12.12.12.12:3344", getApplicationId().getVendorId(), true, false, true);
    monitoringEventConfig.addAvp(Avp.MONITORING_TYPE, MonitoringType.LOCATION_REPORTING, getApplicationId().getVendorId(), true, false);
    monitoringEventConfig.addAvp(Avp.MAXIMUM_NUMBER_OF_REPORTS, 1200, getApplicationId().getVendorId(), true, false);

    monitoringEventConfig = reqSet.addGroupedAvp(Avp.MONITORING_EVENT_CONFIGURATION,getApplicationId().getVendorId(),true, false);
    monitoringEventConfig.addAvp(Avp.SCEF_REFERENCE_ID, 3, getApplicationId().getVendorId(), true, false);
    monitoringEventConfig.addAvp(Avp.SCEF_ID, "aaa://120.120.125.125:3344", getApplicationId().getVendorId(), true, false, true);
    monitoringEventConfig.addAvp(Avp.MONITORING_TYPE, MonitoringType.CHANGE_OF_IMSI_IMEI_ASSOCIATION, getApplicationId().getVendorId(), true, false);
    monitoringEventConfig.addAvp(Avp.MAXIMUM_NUMBER_OF_REPORTS, 5000, getApplicationId().getVendorId(), true, false);

    monitoringEventConfig = reqSet.addGroupedAvp(Avp.MONITORING_EVENT_CONFIGURATION,getApplicationId().getVendorId(),true, false);
    monitoringEventConfig.addAvp(Avp.SCEF_REFERENCE_ID, 4, getApplicationId().getVendorId(), true, false);
    monitoringEventConfig.addAvp(Avp.SCEF_ID, this.stack.getConfiguration().getStringValue(OwnDiameterURI.ordinal(), "aaa://12.12.12.12:3344"), getApplicationId().getVendorId(), true, false, false );
    monitoringEventConfig.addAvp(Avp.MONITORING_TYPE, MonitoringType.LOSS_OF_CONNECTIVITY, getApplicationId().getVendorId(), true, false);
    monitoringEventConfig.addAvp(Avp.MAXIMUM_NUMBER_OF_REPORTS, 300, getApplicationId().getVendorId(), true, false);

    monitoringEventConfig = reqSet.addGroupedAvp(Avp.MONITORING_EVENT_CONFIGURATION,getApplicationId().getVendorId(),true, false);
    monitoringEventConfig.addAvp(Avp.SCEF_REFERENCE_ID, 5, getApplicationId().getVendorId(), true, false);
    monitoringEventConfig.addAvp(Avp.SCEF_ID, this.stack.getConfiguration().getStringValue(OwnDiameterURI.ordinal(), "aaa://12.12.12.12:3344"), getApplicationId().getVendorId(), true, false, false );
    monitoringEventConfig.addAvp(Avp.MONITORING_TYPE, MonitoringType.COMMUNICATION_FAILURE, getApplicationId().getVendorId(), true, false);
    monitoringEventConfig.addAvp(Avp.MAXIMUM_NUMBER_OF_REPORTS, 700, getApplicationId().getVendorId(), true, false);

    monitoringEventConfig = reqSet.addGroupedAvp(Avp.MONITORING_EVENT_CONFIGURATION,getApplicationId().getVendorId(),true, false);
    monitoringEventConfig.addAvp(Avp.SCEF_REFERENCE_ID, 6, getApplicationId().getVendorId(), true, false);
    monitoringEventConfig.addAvp(Avp.SCEF_ID, this.stack.getConfiguration().getStringValue(OwnDiameterURI.ordinal(), "aaa://12.12.12.12:3344"), getApplicationId().getVendorId(), true, false, false );
    monitoringEventConfig.addAvp(Avp.MONITORING_TYPE, MonitoringType.UE_REACHABILITY, getApplicationId().getVendorId(), true, false);
    monitoringEventConfig.addAvp(Avp.MAXIMUM_NUMBER_OF_REPORTS, 400, getApplicationId().getVendorId(), true, false);

    monitoringEventConfig = reqSet.addGroupedAvp(Avp.MONITORING_EVENT_CONFIGURATION, getApplicationId().getVendorId(),true, false);
    monitoringEventConfig.addAvp(Avp.SCEF_REFERENCE_ID, 7, getApplicationId().getVendorId(), true, false, true);
    monitoringEventConfig.addAvp(Avp.SCEF_ID, this.stack.getConfiguration().getStringValue(OwnDiameterURI.ordinal(), "aaa://12.12.12.12:3344"), getApplicationId().getVendorId(), true, false, false );
    monitoringEventConfig.addAvp(Avp.MONITORING_TYPE, MonitoringType.ROAMING_STATUS, getApplicationId().getVendorId(), true, false);
    monitoringEventConfig.addAvp(Avp.MAXIMUM_NUMBER_OF_REPORTS, 2000, getApplicationId().getVendorId(), true, false);

    monitoringEventConfig = reqSet.addGroupedAvp(Avp.MONITORING_EVENT_CONFIGURATION,getApplicationId().getVendorId(),true, false);
    monitoringEventConfig.addAvp(Avp.SCEF_REFERENCE_ID, 8, getApplicationId().getVendorId(), true, false);
    monitoringEventConfig.addAvp(Avp.SCEF_ID, this.stack.getConfiguration().getStringValue(OwnDiameterURI.ordinal(), "aaa://12.12.12.12:3344"), getApplicationId().getVendorId(), true, false, false );
    monitoringEventConfig.addAvp(Avp.MONITORING_TYPE, MonitoringType.NUMBER_OF_UES_PRESENT_IN_A_GEOGRAPHICAL_AREA, getApplicationId().getVendorId(), true, false);
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

  /**
   * This method is called when answer to a request was received (CIA) by the client that sent the CIR
   * and to handle all the parameters of the answer
   * @param session the S6t client session
   * @param request the original request that was sent by the application
   * @param answer The recived answer to the request
   * @throws InternalException exception
   * @throws IllegalDiameterStateException exception
   * @throws RouteException exception
   * @throws OverloadException exception
   */
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
          case Avp.USER_IDENTIFIER:
            break;
          case Avp.MONITORING_EVENT_REPORT: // grouped
            break;
          case Avp.MONITORING_EVENT_CONFIG_STATUS: //Grouped
            break;
          case Avp.AESE_COMMUNICATION_PATTERN_CONFIG_STATUS: // Grouped
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


  private class BCDStringConverter {

    private final String   BCD_STRING = "0123456789*#abc";
    private final char[]   BCD_CHAR   = BCD_STRING.toCharArray();

    public byte[] toBCD (String str) {
      int length = (str == null ? 0:str.length());
      int size = (length + 1)/2;
      byte[] buf = new byte[size];

      int evenLoc = 0;
      int oddLoc = 1;

      for (int i = 0; i < size; ++i) {
        char c = str.charAt(evenLoc);
        int n2 = convNible(c);
        int octet = 0;
        int n1 = 15;
        if (oddLoc < length) {
          c = str.charAt(oddLoc);
          n1 = convNible(c);
        }
        octet = (n1 << 4) + n2;
        buf[i] = (byte)(octet & 0xFF);
        evenLoc += 2;
        oddLoc +=2;
      }
      return buf;
    }

    private int convNible(char c) {

      int digit = Character.digit(c, 10); // convert to digit

      if (digit < 0 || digit > 9) {  // the E164 supports the charecters 'a', 'b', 'c', 'd', '#' and '*' the '*' and '#' are lower then normal digits order in ASCI
        switch (c) {
          case '*':
            digit = 10;
            break;
          case '#':
            digit = 11;
            break;
          case 'a':
            digit = 12;
            break;
          case 'b':
            digit = 13;
            break;
          case 'c':
            digit = 14;
            break;
          default:
            throw new NumberFormatException("character : " + c + " is bad " );
        }
      }
      return digit;
    }

    /**
     *
     * @param bcd  byte[] encoded BCD
     * @return  string if converted null if char empty.
     */
    public String toStringNumber (byte[] bcd) {
      if (bcd.length == 0 || bcd == null) {
        return null;
      }

      int size = bcd.length;
      // the string is twice as the byte array
      StringBuffer buf = new StringBuffer(2*size);
      for (int i = 0; i < size; ++i) {
        int octet = bcd[i];
        int n2 = (octet >> 4) & 0xF; // move the upper nible to its location and clean the upper bytes
        int n1 = octet & 0xF; // clean upper bytes of the second
        if (n1 == 15) { // if there is filler in the specific byte since it is not supose to be with filler byte
          throw new NumberFormatException("Illegal filler in octet n=" + i);
        }
        buf.append(BCD_CHAR[n1]);  // add the string in n1 location

        if (n2 == 15) { // only if this is the last
          if (i != size - 1) { // needs to be only on the last one if size is odd number
            throw new NumberFormatException("Illegal filler in octet n=" + i);
          }
          else {
            buf.append(BCD_CHAR[n2]);
          }
        }
      }

      return buf.toString();
    }


  }




}
