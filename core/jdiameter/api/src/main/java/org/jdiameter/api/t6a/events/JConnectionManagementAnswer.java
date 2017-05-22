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
public interface JConnectionManagementAnswer extends AppAnswerEvent {
  String _SHORT_NAME = "CMA";
  String _LONG_NAME = "Connection-Management-Answer";

  int code = 8388732;

}
