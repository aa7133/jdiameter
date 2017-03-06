package org.jdiameter.common.impl.app.s6t;

import org.jdiameter.api.Message;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.s6t.events.JNIDDInformationRequest;
import org.jdiameter.common.impl.app.AppRequestEventImpl;

/**
 * Created by Adi Enzel on 3/5/17.
 */
public class JNIDDInformationRequestImpl extends AppRequestEventImpl implements JNIDDInformationRequest {
  private static final long serialVersionUID = 1L;

  public JNIDDInformationRequestImpl(Message msg) {
    super(msg);
    msg.setRequest(true);
  }

  public JNIDDInformationRequestImpl(AppSession session, String destRealm, String destHost) {
    super(session.getSessions().get(0).createRequest(code, session.getSessionAppId(), destRealm, destHost));
  }

}
