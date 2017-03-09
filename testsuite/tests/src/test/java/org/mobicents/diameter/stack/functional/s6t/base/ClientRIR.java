package org.mobicents.diameter.stack.functional.s6t.base;


import java.io.InputStream;

import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.s6t.ClientS6tSession;
import org.jdiameter.api.s6t.ServerS6tSession;
import org.jdiameter.api.s6t.events.JReportingInformationAnswer;
import org.jdiameter.api.s6t.events.JReportingInformationRequest;
import org.jdiameter.common.impl.app.s6t.JReportingInformationAnswerImpl;
import org.jdiameter.common.impl.app.s6t.S6tSessionFactoryImpl;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.s6t.AbstractClient;

/**
 * Created by Adi Enzel on 3/8/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public class ClientRIR extends AbstractClient {
  protected boolean receivedReportingInformation;
  protected boolean sentReportingInformation;

  protected JReportingInformationRequest request;

  /**
   *
   */
  public ClientRIR() {
  }

  @Override
  public void init(InputStream configStream, String clientID) throws Exception {
    try {
      super.init(configStream, clientID, ApplicationId.createByAuthAppId(10415, 16777345));
      S6tSessionFactoryImpl S6tSessionFactory = new S6tSessionFactoryImpl(this.sessionFactory);
      sessionFactory.registerAppFacory(ServerS6tSession.class, S6tSessionFactory);
      sessionFactory.registerAppFacory(ClientS6tSession.class, S6tSessionFactory);
      S6tSessionFactory.setClientSessionListener(this);
    }
    finally {
      try {
        configStream.close();
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public void sendReportingInformationAnswer() throws Exception {
    if (!receivedReportingInformation || request == null) {
      fail("Did not receive RTR or answer already sent.", null);
      throw new Exception("Did not receive RTR or answer already sent. Request: " + this.request);
    }
    JReportingInformationAnswer answer = new JReportingInformationAnswerImpl((Request) this.request.getMessage(), 2001);

    AvpSet reqSet = request.getMessage().getAvps();

    AvpSet set = answer.getMessage().getAvps();
    set.removeAvp(Avp.DESTINATION_HOST);
    set.removeAvp(Avp.DESTINATION_REALM);
    set.addAvp(reqSet.getAvp(Avp.CC_REQUEST_TYPE), reqSet.getAvp(Avp.CC_REQUEST_NUMBER), reqSet.getAvp(Avp.AUTH_APPLICATION_ID));

    // <Registration-Termination-Answer> ::= < Diameter Header: 304, PXY, 16777216 >
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
    // [ Associated-Identities ]
    // *[ Supported-Features ]
    // *[ AVP ]
    // *[ Failed-AVP ]
    this.clientS6tSession.sendReportingInformationAnswer(answer);
    Utils.printMessage(log, super.stack.getDictionary(), answer.getMessage(), true);
    this.request = null;
    this.sentReportingInformation = true;
  }

  @Override
  public void doReportingInformationRequestEvent(ClientS6tSession session, JReportingInformationRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    if (this.receivedReportingInformation) {
      fail("S6t Received Reporting-Information-Request(RIR) more than once", null);
      return;
    }
    this.receivedReportingInformation = true;
    this.request = request;
  }

  @Override
  public Answer processRequest(Request request) {
    int code = request.getCommandCode();
    if (code != JReportingInformationAnswer.code) {
      fail("S6t Received Request with code not used by S6t!. Code[" + request.getCommandCode() + "]", null);
      return null;
    }
    if (super.clientS6tSession != null) {
      // do fail?
      fail("Received Request in base listener, not in app specific!" + code, null);
    }
    else {
      try {
        super.clientS6tSession = this.sessionFactory.getNewAppSession(request.getSessionId(), getApplicationId(), ClientS6tSession.class, (Object) null);
        ((NetworkReqListener) this.clientS6tSession).processRequest(request);
      }
      catch (Exception e) {
        e.printStackTrace();
        fail(null, e);
      }
    }
    return null;
  }

  public boolean isReceivedReportingInformation() {
    return receivedReportingInformation;
  }

  public boolean isSentReportingInformation() {
    return sentReportingInformation;
  }

}
