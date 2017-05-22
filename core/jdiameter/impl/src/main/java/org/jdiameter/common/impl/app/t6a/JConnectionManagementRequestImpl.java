package org.jdiameter.common.impl.app.t6a;

import org.jdiameter.api.Message;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.t6a.events.JConnectionManagementRequest;
import org.jdiameter.common.impl.app.AppRequestEventImpl;


/*
 * Copyright (c) 2017. AT&T Intellectual Property. All rights reserved
 */

/**
 * Created by Adi Enzel on 13/5/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public class JConnectionManagementRequestImpl extends AppRequestEventImpl implements JConnectionManagementRequest {
  private static final long serialVersionUID = 1L;

  public JConnectionManagementRequestImpl(Message msg) {
    super(msg);
    msg.setRequest(true);
  }

  public JConnectionManagementRequestImpl(AppSession session, String destRealm, String destHost) {
    super(session.getSessions().get(0).createRequest(code, session.getSessionAppId(), destRealm, destHost));
  }

}
