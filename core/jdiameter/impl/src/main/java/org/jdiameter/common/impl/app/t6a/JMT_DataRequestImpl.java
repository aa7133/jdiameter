package org.jdiameter.common.impl.app.t6a;

import org.jdiameter.api.Message;
import org.jdiameter.api.app.AppSession;

import org.jdiameter.api.t6a.events.JMT_DataRequest;
import org.jdiameter.common.impl.app.AppRequestEventImpl;

/*
 * Copyright (c) 2017. AT&T Intellectual Property. All rights reserved
 */

/**
 * Created by Adi Enzel on 13/5/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public class JMT_DataRequestImpl extends AppRequestEventImpl implements JMT_DataRequest {
  private static final long serialVersionUID = 1L;

  public JMT_DataRequestImpl(Message msg) {
    super(msg);
    msg.setRequest(true);
  }

  public JMT_DataRequestImpl(AppSession session, String destRealm, String destHost) {
    super(session.getSessions().get(0).createRequest(code, session.getSessionAppId(), destRealm, destHost));
  }

}
