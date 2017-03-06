package org.jdiameter.common.api.app.s6t;

import org.jdiameter.api.Request;
import org.jdiameter.common.api.app.IAppSessionData;

import java.io.Serializable;

/**
 * Created by Adi Enzel on 3/2/17.
 *  @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public interface IS6tSessionData extends IAppSessionData {

  void setS6tSessionState(S6tSessionState state);
  S6tSessionState getS6tSessionState();

  Serializable getTsTimerId();
  void setTsTimerId(Serializable tid);

  void setBuffer(Request buffer);
  Request getBuffer();

}
