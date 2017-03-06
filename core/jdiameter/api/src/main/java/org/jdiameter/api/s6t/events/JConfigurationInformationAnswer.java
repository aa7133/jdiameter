package org.jdiameter.api.s6t.events;

import org.jdiameter.api.app.AppAnswerEvent;

/**
 * Created by Adi Enzel on 3/2/17.
 *  @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public interface JConfigurationInformationAnswer extends AppAnswerEvent {
  String _SHORT_NAME = "CIA";
  String _LONG_NAME = "Configuration-Information-Answer";

  int code = 8388718;

}
