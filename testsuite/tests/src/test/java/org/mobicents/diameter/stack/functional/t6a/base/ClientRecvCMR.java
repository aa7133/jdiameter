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
import org.jdiameter.api.t6a.events.JConnectionManagementAnswer;
import org.jdiameter.api.t6a.events.JConnectionManagementRequest;
import org.jdiameter.common.impl.app.t6a.JConnectionManagementAnswerImpl;
import org.jdiameter.common.impl.app.t6a.T6aSessionFactoryImpl;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.t6a.AbstractClient;

import java.io.InputStream;

/**
 * Created by Adi Enzel on 16/03/2017.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public class ClientRecvCMR extends AbstractClient {
  protected boolean receivedConnectionManagement;
  protected boolean sentConnectionManagement;

  protected JConnectionManagementRequest request;

  public ClientRecvCMR() {
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

  public void sendConnectionManagementAnswer() throws Exception {
    if (!receivedConnectionManagement || request == null) {
      fail("T6a Did not receive Connection-Management-Request (CMR) or answer already sent.", null);
      throw new Exception("T6a Did not receive Connection-Management-Request (CMR) or answer already sent. Request: " + this.request);
    }
    JConnectionManagementAnswer answer = new JConnectionManagementAnswerImpl((Request) this.request.getMessage(), ResultCode.SUCCESS);

    AvpSet reqSet = request.getMessage().getAvps();

    AvpSet set = answer.getMessage().getAvps();
    set.removeAvp(Avp.DESTINATION_HOST);
    set.removeAvp(Avp.DESTINATION_REALM);
    set.addAvp(reqSet.getAvp(Avp.CC_REQUEST_TYPE), reqSet.getAvp(Avp.CC_REQUEST_NUMBER), reqSet.getAvp(Avp.AUTH_APPLICATION_ID));

    //< Connection-Management-Answer > ::=  	< Diameter Header: 8388732, PXY, 16777346 >
    //< Session-Id >
    //[ DRMP ]
    //[ Result-Code ]
    //[ Experimental-Result ]
    //{ Auth-Session-State }
    if (set.getAvp(Avp.AUTH_SESSION_STATE) == null) {
      set.addAvp(Avp.AUTH_SESSION_STATE, 1);
    }
    //{ Origin-Host }
    //{ Origin-Realm }
    //[ OC-Supported-Features ]
    //[ OC-OLR ]
    //*[ Load ]
    //*[ Supported-Features ]
    //[ PDN-Connection-Charging-Id ]
    //[ Extended-PCO ]
    //*[ Failed-AVP ]
    //*[ Proxy-Info ]
    //*[ Route-Record ]
    //*[AVP]

    this.clientT6aSession.sendConnectionManagementAnswer(answer);
    Utils.printMessage(log, super.stack.getDictionary(), answer.getMessage(), true);
    this.request = null;
    this.sentConnectionManagement = true;
  }


  @Override
  public void doConnectionManagementRequestEvent(ClientT6aSession session, JConnectionManagementRequest request)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    if (this.receivedConnectionManagement) {
      fail("T6a Received Connection-Management-Request (CMR) more than once", null);
      return;
    }
    this.receivedConnectionManagement = true;
    this.request = request;
  }

  @Override
  public Answer processRequest(Request request) {
    int code = request.getCommandCode();
    if (code != JConnectionManagementRequest.code) {
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

  public boolean isReceivedConnectionManagement() {
    return receivedConnectionManagement;
  }

  public boolean isSentConnectionManagement() {
    return sentConnectionManagement;
  }

}
