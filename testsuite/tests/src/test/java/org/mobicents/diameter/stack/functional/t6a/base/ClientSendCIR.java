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

/*
 * Copyright (c) 2017. AT&T Intellectual Property. All rights reserved
 */

/**
 * Created by Adi Enzel on 3/15/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public class ClientSendCIR extends AbstractClient {
  protected boolean receivedConfiguratinInfo;
  protected boolean sentConfigurationInfo;

  private BCDStringConverter bcd;

  public ClientSendCIR() {
    bcd = new BCDStringConverter();
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
   // userIdentifier.addAvp(Avp.MSISDN, this.bcd.toBCD(str), getApplicationId().getVendorId(), true, false);
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
