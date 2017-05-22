package org.jdiameter.common.impl.app.t6a;

import org.jdiameter.api.Request;
import org.jdiameter.common.api.app.AppSessionDataLocalImpl;
import org.jdiameter.common.api.app.t6a.IT6aSessionData;
import org.jdiameter.common.api.app.t6a.T6aSessionState;

import java.io.Serializable;

/*
 * Copyright (c) 2017. AT&T Intellectual Property. All rights reserved
 */

/**
 * Created by Adi Enzel on 3/13/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public class T6aLocalSessionDataImpl extends AppSessionDataLocalImpl implements IT6aSessionData {

  protected T6aSessionState state = T6aSessionState.IDLE;
  protected Request buffer;
  protected Serializable tsTimerId;

  @Override
  public void setT6aSessionState(T6aSessionState state) {
    this.state = state;
  }

  @Override
  public T6aSessionState getT6aSessionState() {
    return this.state;
  }

  @Override
  public Serializable getTsTimerId() {
    return this.tsTimerId;
  }

  @Override
  public void setTsTimerId(Serializable tid) {
    this.tsTimerId = tid;
  }

  @Override
  public void setBuffer(Request buffer) {
    this.buffer = buffer;
  }

  @Override
  public Request getBuffer() {
    return this.buffer;
  }



}
