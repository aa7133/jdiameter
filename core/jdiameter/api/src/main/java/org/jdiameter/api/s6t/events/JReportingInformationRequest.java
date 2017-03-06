package org.jdiameter.api.s6t.events;

import org.jdiameter.api.app.AppRequestEvent;

/**
 * Created by Adi Enzel on 3/2/17.
 *  @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public interface JReportingInformationRequest extends AppRequestEvent {
  String _SHORT_NAME = "RIR";
  String _LONG_NAME = "Reporting-Information-Request";

  int code = 8388719;

}
