package org.jdiameter.api.t6a.events;

import org.jdiameter.api.app.AppAnswerEvent;

/*
 * Copyright (c) 2017. AT&T Intellectual Property. All rights reserved
 */

/**
 * Created by Adi Enzel on 3/13/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public interface JMO_DataAnswer extends AppAnswerEvent {
  String _SHORT_NAME = "ODA";
  String _LONG_NAME = "MO-Data-Answer";

  int code = 8388733;
}
