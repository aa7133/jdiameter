package org.mobicents.diameter.stack.functional.t6a.base;


import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.ResultCode;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.t6a.ClientT6aSession;
import org.jdiameter.api.t6a.ServerT6aSession;
import org.jdiameter.api.t6a.events.JConfigurationInformationAnswer;
import org.jdiameter.api.t6a.events.JConfigurationInformationRequest;
import org.jdiameter.common.impl.app.t6a.JConfigurationInformationAnswerImpl;
import org.jdiameter.common.impl.app.t6a.T6aSessionFactoryImpl;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.t6a.AbstractClient;

import java.io.InputStream;

/**
 * Created by Adi Enzel on 16/03/2017.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public class ClientRecvCIR extends AbstractClient {
  protected boolean receivedConfigurationInformation;
  protected boolean sentConfigurationInformation;

  protected JConfigurationInformationRequest request;

  public ClientRecvCIR() {

  }

  @Override
  public void init(InputStream configStream, String clientID) throws Exception {
    try {
      super.init(configStream, clientID, ApplicationId.createByAuthAppId(10415, 16777346));
      T6aSessionFactoryImpl t6aSessionFactory = new T6aSessionFactoryImpl(this.sessionFactory);
      sessionFactory.registerAppFacory(ServerT6aSession.class, t6aSessionFactory);
      sessionFactory.registerAppFacory(ClientT6aSession.class, t6aSessionFactory);
      t6aSessionFactory.setClientSessionListener(this);
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

  public void sendConfigurationInformationAnswer() throws Exception {
    if (!receivedConfigurationInformation || request == null) {
      fail("T6a Did not receive CIR or answer already sent.", null);
      throw new Exception("T6a Did not receive CIR or answer already sent. Request: " + this.request);
    }
    JConfigurationInformationAnswer answer = new JConfigurationInformationAnswerImpl((Request) this.request.getMessage(), ResultCode.SUCCESS);

    AvpSet reqSet = request.getMessage().getAvps();

    AvpSet set = answer.getMessage().getAvps();
    set.removeAvp(Avp.DESTINATION_HOST);
    set.removeAvp(Avp.DESTINATION_REALM);
    set.addAvp(reqSet.getAvp(Avp.CC_REQUEST_TYPE), reqSet.getAvp(Avp.CC_REQUEST_NUMBER), reqSet.getAvp(Avp.AUTH_APPLICATION_ID));

    // <Configuration-Information-Answer> ::= < Diameter Header: , PXY, 16777346 >
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
    this.clientT6aSession.sendConfigurationInformationAnswer(answer);
    Utils.printMessage(log, super.stack.getDictionary(), answer.getMessage(), true);
    this.request = null;
    this.sentConfigurationInformation = true;
  }


  @Override
  public void doConfigurationInformationRequestEvent(ClientT6aSession session, JConfigurationInformationRequest request)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    if (this.receivedConfigurationInformation) {
      fail("T6a Received Configuration-Information-Request(CIR) more than once", null);
      return;
    }
    this.receivedConfigurationInformation = true;
    this.request = request;
  }

  @Override
  public Answer processRequest(Request request) {
    int code = request.getCommandCode();
    if (code != JConfigurationInformationRequest.code) {
      fail("T6a Received Request with code not used by T6a!. Code[" + request.getCommandCode() + "]", null);
      return null;
    }
    if (super.clientT6aSession != null) {
      // do fail?
      fail("T6a Received Request in base listener, not in app specific!" + code, null);
    }
    else {
      try {
        super.clientT6aSession = this.sessionFactory.getNewAppSession(request.getSessionId(), getApplicationId(), ClientT6aSession.class, (Object) null);
        ((NetworkReqListener) this.clientT6aSession).processRequest(request);
      }
      catch (Exception e) {
        e.printStackTrace();
        fail(null, e);
      }
    }
    return null;
  }

  public boolean isReceivedConfigurationInformation() {
    return receivedConfigurationInformation;
  }

  public boolean isSentConfigurationInformation() {
    return sentConfigurationInformation;
  }

}
