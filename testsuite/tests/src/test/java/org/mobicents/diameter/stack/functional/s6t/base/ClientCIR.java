package org.mobicents.diameter.stack.functional.s6t.base;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.s6t.events.JConfigurationInformationRequest;
import org.jdiameter.common.impl.app.s6t.JConfigurationInformationRequestImpl;
import org.mobicents.diameter.stack.functional.s6t.AbstractClient;

/**
 * Created by odldev on 3/6/17.
 */
public class ClientCIR extends AbstractClient{
  protected boolean receivedConfiguratinInfo;
  protected boolean sentConfigurationInfo;

  public ClientCIR() {

  }

  public void sendConfigurationInfoRequest()  throws Exception {
    JConfigurationInformationRequest request = new JConfigurationInformationRequestImpl(super.createRequest(this.clientS6tSession, JConfigurationInformationRequest.code));
      AvpSet reqSet = request.getMessage().getAvps();

      //reqSet.addAvp(Avp.SERVER_NAME, );
  }

}
