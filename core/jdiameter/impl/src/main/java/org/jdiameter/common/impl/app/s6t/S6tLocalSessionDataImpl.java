package org.jdiameter.common.impl.app.s6t;

import org.jdiameter.api.Request;
import org.jdiameter.common.api.app.AppSessionDataLocalImpl;
import org.jdiameter.common.api.app.s6t.IS6tSessionData;
import org.jdiameter.common.api.app.s6t.S6tSessionState;

import java.io.Serializable;

/**
 * Created by odldev on 3/5/17.
 */
public class S6tLocalSessionDataImpl extends AppSessionDataLocalImpl implements IS6tSessionData {

  protected S6tSessionState state = S6tSessionState.IDLE;
  protected Request buffer;
  protected Serializable tsTimerId;

  @Override
  public void setS6tSessionState(S6tSessionState state) {
    this.state = state;
  }

  @Override
  public S6tSessionState getS6tSessionState() {
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
