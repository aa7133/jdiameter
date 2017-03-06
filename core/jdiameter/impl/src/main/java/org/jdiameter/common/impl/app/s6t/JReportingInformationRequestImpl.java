package org.jdiameter.common.impl.app.s6t;

import org.jdiameter.api.Message;
import org.jdiameter.api.s6t.events.JReportingInformationRequest;
import org.jdiameter.common.impl.app.AppRequestEventImpl;

/**
 * Created by Adi Enzel on 3/5/17.
 */
public class JReportingInformationRequestImpl extends AppRequestEventImpl implements JReportingInformationRequest {
  private static final long serialVersionUID = 1L;

  public JReportingInformationRequestImpl(Message msg) {
    super(msg);
    msg.setRequest(true);
  }
}
