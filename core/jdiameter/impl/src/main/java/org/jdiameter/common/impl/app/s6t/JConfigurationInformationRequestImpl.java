package org.jdiameter.common.impl.app.s6t;

import org.jdiameter.api.Message;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.s6t.events.JConfigurationInformationRequest;
import org.jdiameter.common.impl.app.AppRequestEventImpl;

/**
 * Created by Adi Enzel on 3/5/17.
 */
public class JConfigurationInformationRequestImpl extends AppRequestEventImpl implements JConfigurationInformationRequest {
  private static final long serialVersionUID = 1L;

  public JConfigurationInformationRequestImpl(Message msg) {
    super(msg);
    msg.setRequest(true);
  }

  public JConfigurationInformationRequestImpl(AppSession session, String destRealm, String destHost) {
    super(session.getSessions().get(0).createRequest(code, session.getSessionAppId(), destRealm, destHost));
  }

}
