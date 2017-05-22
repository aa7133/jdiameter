package org.jdiameter.common.api.app.s6t;

import org.jdiameter.common.api.app.IAppSessionState;

/*
 * Copyright (c) 2017. AT&T Intellectual Property. All rights reserved
 */

/**
 * Created by Adi Enzel on 3/5/17.
 *
 *  @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public enum S6tSessionState implements IAppSessionState<S6tSessionState> {
  IDLE(0),
  OPEN(1),
  DISCONNECTED(2),
  TIMEDOUT(3);

  private final int value;

  S6tSessionState(int val) {
    this.value = val;
  }

  @Override
  public final int getValue() {
    return value;
  }

  @Override
  public final S6tSessionState fromInt(int val) throws IllegalArgumentException {
    switch (val) {
      case 0:
        return IDLE;
      case 1:
        return OPEN;
      case 2:
        return DISCONNECTED;
      case 3:
        return TIMEDOUT;
      default:
        throw new IllegalArgumentException();
    }
  }

}
