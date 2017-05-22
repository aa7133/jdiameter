package org.jdiameter.api.t6a.events;

import org.jdiameter.api.app.AppRequestEvent;

/*
 * Copyright (c) 2017. AT&T Intellectual Property. All rights reserved
 */

/**
 * Created by Adi Enzel on 3/2/17.
 *
 *  @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public interface JConfigurationInformationRequest extends AppRequestEvent {

  String _SHORT_NAME = "CIR";
  String _LONG_NAME = "Configuration-Information-Request";

  int code = 8388718;
}
