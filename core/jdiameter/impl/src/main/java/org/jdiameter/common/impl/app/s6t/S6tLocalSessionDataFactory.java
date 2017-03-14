package org.jdiameter.common.impl.app.s6t;

import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.s6t.ClientS6tSession;
import org.jdiameter.api.s6t.ServerS6tSession;
import org.jdiameter.client.impl.app.s6t.ClientS6tSessionDataLocalImpl;
import org.jdiameter.common.api.app.IAppSessionDataFactory;
import org.jdiameter.common.api.app.s6t.IS6tSessionData;
import org.jdiameter.server.impl.app.s6t.ServerS6tSessionDataLocalImpl;

/**
 * Created by Adi Enzel on 3/5/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public class S6tLocalSessionDataFactory implements IAppSessionDataFactory<IS6tSessionData> {

  /*
   * (non-Javadoc)
   * @see org.jdiameter.common.api.app.IAppSessionDataFactory#getAppSessionData(java.lang.Class, java.lang.String)
   */
  @Override
  public IS6tSessionData getAppSessionData(Class<? extends AppSession> clazz, String sessionId) {
    if (clazz.equals(ClientS6tSession.class)) {
      ClientS6tSessionDataLocalImpl data = new ClientS6tSessionDataLocalImpl();
      data.setSessionId(sessionId);
      return data;
    }
    else if (clazz.equals(ServerS6tSession.class)) {
      ServerS6tSessionDataLocalImpl data = new ServerS6tSessionDataLocalImpl();
      data.setSessionId(sessionId);
      return data;
    }
    else {
      throw new IllegalArgumentException("Invalid Session Class: " + clazz.toString());
    }
  }

}
