package org.jdiameter.common.impl.app.s6t;

import org.jdiameter.api.Message;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.s6t.events.JReportingInformationRequest;
import org.jdiameter.common.impl.app.AppRequestEventImpl;

/*
 * Copyright (c) 2017. AT&T Intellectual Property. All rights reserved
 */

/**
 * Created by Adi Enzel on 3/5/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public class JReportingInformationRequestImpl extends AppRequestEventImpl implements JReportingInformationRequest {
  private static final long serialVersionUID = 1L;

  public JReportingInformationRequestImpl(Message msg) {
    super(msg);
    msg.setRequest(true);
  }

  public JReportingInformationRequestImpl(AppSession session, String destRealm, String destHost) {
    super(session.getSessions().get(0).createRequest(code, session.getSessionAppId(), destRealm, destHost));
  }

}
