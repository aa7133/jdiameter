package org.jdiameter.common.impl.app.t6a;

import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.t6a.ClientT6aSession;
import org.jdiameter.api.t6a.ServerT6aSession;
import org.jdiameter.client.impl.app.t6a.ClientT6aSessionDataLocalImpl;
import org.jdiameter.common.api.app.IAppSessionDataFactory;
import org.jdiameter.common.api.app.t6a.IT6aSessionData;
import org.jdiameter.server.impl.app.t6a.ServerT6aSessionDataLocalImpl;


/*
 * Copyright (c) 2017. AT&T Intellectual Property. All rights reserved
 */

/**
 * Created by Adi Enzel on 3/13/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public class T6aLocalSessionDataFactory implements IAppSessionDataFactory<IT6aSessionData> {

  /*
   * (non-Javadoc)
   * @see org.jdiameter.common.api.app.IAppSessionDataFactory#getAppSessionData(java.lang.Class, java.lang.String)
   */
  @Override
  public IT6aSessionData getAppSessionData(Class<? extends AppSession> clazz, String sessionId) {
    if (clazz.equals(ClientT6aSession.class)) {
      ClientT6aSessionDataLocalImpl data = new ClientT6aSessionDataLocalImpl();
      data.setSessionId(sessionId);
      return data;
    }
    else if (clazz.equals(ServerT6aSession.class)) {
      ServerT6aSessionDataLocalImpl data = new ServerT6aSessionDataLocalImpl();
      data.setSessionId(sessionId);
      return data;
    }
    else {
      throw new IllegalArgumentException("Invalid Session Class: " + clazz.toString());
    }
  }

}
