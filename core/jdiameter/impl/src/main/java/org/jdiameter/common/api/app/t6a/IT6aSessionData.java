package org.jdiameter.common.api.app.t6a;

import org.jdiameter.api.Request;
import org.jdiameter.common.api.app.IAppSessionData;

import java.io.Serializable;

/*
 * Copyright (c) 2017. AT&T Intellectual Property. All rights reserved
 */

/**
 * Created by Adi Enzel on 3/13/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public interface IT6aSessionData extends IAppSessionData {

  void setT6aSessionState(T6aSessionState state);
  T6aSessionState getT6aSessionState();

  Serializable getTsTimerId();
  void setTsTimerId(Serializable tid);

  void setBuffer(Request buffer);
  Request getBuffer();

}
