package org.mobicents.diameter.stack.functional.t6a.base;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.t6a.ClientT6aSession;
import org.jdiameter.api.t6a.ServerT6aSession;
import org.jdiameter.api.t6a.events.JConfigurationInformationAnswer;
import org.jdiameter.api.t6a.events.JConfigurationInformationRequest;
import org.jdiameter.common.impl.app.t6a.T6aSessionFactoryImpl;
import org.jdiameter.common.impl.app.t6a.JConfigurationInformationRequestImpl;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.t6a.AbstractServer;

import java.io.InputStream;

/**
 * Created by Adi Enzel on 16/03/2017.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public class ServerSendCIR extends AbstractServer {
  protected boolean receivedConfigurationInformation;
  protected boolean sentConfigurationInformation;

  public ServerSendCIR() {
  }

  @Override
  public void init(InputStream configStream, String clientID) throws Exception {
    try {
      super.init(configStream, clientID, ApplicationId.createByAuthAppId(10415, 16777346));
      T6aSessionFactoryImpl t6aSessionFactory = new T6aSessionFactoryImpl(this.sessionFactory);
      sessionFactory.registerAppFacory(ServerT6aSession.class, t6aSessionFactory);
      sessionFactory.registerAppFacory(ClientT6aSession.class, t6aSessionFactory);
      t6aSessionFactory.setServerSessionListener(this);
      this.serverT6aSession = sessionFactory.getNewAppSession(getApplicationId(), ServerT6aSession.class);
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

  public void sendConfigurationInformationRequest() throws Exception {

    JConfigurationInformationRequest request =
          new JConfigurationInformationRequestImpl(super.createRequest(this.serverT6aSession, JConfigurationInformationRequest.code));
    AvpSet reqSet = request.getMessage().getAvps();

    //TODO set values for Configuration-Information-Request
    reqSet.addAvp(Avp.USER_NAME, "8388719", false);
    AvpSet deregeReason = reqSet.addGroupedAvp(Avp.DEREGISTRATION_REASON, getApplicationId().getVendorId(), true, false);
    deregeReason.addAvp(Avp.REASON_CODE, 0, getApplicationId().getVendorId(), true, false, true);

    this.serverT6aSession.sendConfigurationInformationRequest(request);
    Utils.printMessage(log, super.stack.getDictionary(), request.getMessage(), true);

    this.sentConfigurationInformation = true;
  }

  @Override
  public void doSendConfigurationInformationAnswerEvent(ServerT6aSession session, JConfigurationInformationRequest request, JConfigurationInformationAnswer answer)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    if (this.receivedConfigurationInformation) {
      fail("T6a Received Configuration-Information-Answer (CIA) more than once", null);
      return;
    }

    this.receivedConfigurationInformation = true;
  }


  public boolean isReceivedConfigurationInformation() {
    return receivedConfigurationInformation;
  }

  public boolean isSentConfigurationInformation() {
    return sentConfigurationInformation;
  }
}
