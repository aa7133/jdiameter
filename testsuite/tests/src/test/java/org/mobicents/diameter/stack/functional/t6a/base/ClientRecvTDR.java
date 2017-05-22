package org.mobicents.diameter.stack.functional.t6a.base;


import org.jdiameter.api.*;
import org.jdiameter.api.t6a.ClientT6aSession;
import org.jdiameter.api.t6a.ServerT6aSession;
import org.jdiameter.api.t6a.events.JMT_DataAnswer;
import org.jdiameter.api.t6a.events.JMT_DataRequest;
import org.jdiameter.common.impl.app.t6a.JMT_DataAnswerImpl;
import org.jdiameter.common.impl.app.t6a.T6aSessionFactoryImpl;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.t6a.AbstractClient;

import java.io.InputStream;

/*
 * Copyright (c) 2017. AT&T Intellectual Property. All rights reserved
 */

/**
 * Created by Adi Enzel on 16/03/2017.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public class ClientRecvTDR extends AbstractClient {
  private boolean receivedMTData;
  private boolean sentMTData;

  protected JMT_DataRequest request;

  protected ClientRecvTDR() {
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

  protected void sendMTDataAnswer() throws Exception {
    if (!receivedMTData || request == null) {
      fail("T6a Did not receive MT-Data-Request (TDR) or answer already sent.", null);
      throw new Exception("T6a Did not receive MT-Data-Request (TDR) or answer already sent. Request: " + this.request);
    }
    JMT_DataAnswer answer = new JMT_DataAnswerImpl((Request) this.request.getMessage(), ResultCode.SUCCESS);

    AvpSet reqSet = request.getMessage().getAvps();

    AvpSet set = answer.getMessage().getAvps();
    set.removeAvp(Avp.DESTINATION_HOST);
    set.removeAvp(Avp.DESTINATION_REALM);
    set.addAvp(reqSet.getAvp(Avp.CC_REQUEST_TYPE), reqSet.getAvp(Avp.CC_REQUEST_NUMBER), reqSet.getAvp(Avp.AUTH_APPLICATION_ID));


    //< MT-Data-Answer > ::=  	< Diameter Header: 8388734, PXY, 16777346 >
    // < Session-Id >
    // [ DRMP ]
    // [ Result-Code ]
    if (set.getAvp(Avp.RESULT_CODE) == null) {
      set.addAvp(Avp.RESULT_CODE, ResultCode.SUCCESS);
    }
    // [ Experimental-Result ]
    // { Auth-Session-State }
    if (set.getAvp(Avp.AUTH_SESSION_STATE) == null) {
      set.addAvp(Avp.AUTH_SESSION_STATE, 1);
    }
    // { Origin-Host }
    // { Origin-Realm }
    // [ OC-Supported-Features ]
    // [ OC-OLR ]
    // *[ Load ]
    // [ Requested-Retransmission-Time ]
    // *[ Supported-Features ]
    // [ Failed-AVP ]
    // *[ Proxy-Info ]
    // *[ Route-Record ]
    // *[AVP]

    this.clientT6aSession.sendMTDataAnswer(answer);
    Utils.printMessage(log, super.stack.getDictionary(), answer.getMessage(), true);
    this.request = null;
    this.sentMTData = true;
  }


  @Override
  public void doMT_DataRequestEvent(ClientT6aSession session, JMT_DataRequest request)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    if (this.receivedMTData) {
      fail("T6a Received MT-Data-Request (TDR) more than once", null);
      return;
    }
    this.receivedMTData = true;
    this.request = request;
  }

  @Override
  public Answer processRequest(Request request) {
    int code = request.getCommandCode();
    if (code != JMT_DataRequest.code) {
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

  protected boolean isReceivedMTData() {
    return receivedMTData;
  }

  protected boolean isSentMTData() {
    return sentMTData;
  }

}
