package org.jdiameter.api.t6a.events;

import org.jdiameter.api.app.AppRequestEvent;

/**
 * Created by Adi Enzel on 3/13/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public interface JConnectionManagementRequest extends AppRequestEvent {
  String _SHORT_NAME = "CMR";
  String _LONG_NAME = "Connection-Management-Request";

  int code = 8388732;

}
