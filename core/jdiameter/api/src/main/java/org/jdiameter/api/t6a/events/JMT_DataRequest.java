package org.jdiameter.api.t6a.events;

import org.jdiameter.api.app.AppRequestEvent;

/*
 * Copyright (c) 2017. AT&T Intellectual Property. All rights reserved
 */

/**
 * Created by Adi Enzel on 3/13/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public interface JMT_DataRequest extends AppRequestEvent {
  String _SHORT_NAME = "TDR";
  String _LONG_NAME = "MT-Data-Request";

  int code = 8388734;
}
