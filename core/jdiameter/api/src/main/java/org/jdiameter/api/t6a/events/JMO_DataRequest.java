package org.jdiameter.api.t6a.events;

import org.jdiameter.api.app.AppRequestEvent;

/**
 * Created by Adi Enzel on 3/13/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public interface JMO_DataRequest extends AppRequestEvent {
  String _SHORT_NAME = "ODR";
  String _LONG_NAME = "MO-Data-Request";

  int code = 8388733;
}
