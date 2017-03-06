package org.jdiameter.common.api.app.s6t;

import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.s6t.ClientS6tSessionListener;
import org.jdiameter.api.s6t.ServerS6tSessionListener;
import org.jdiameter.common.api.app.IAppSessionFactory;

/**
 * Created by Adi Enzel on 3/2/17.
 */
public interface IS6tSessionFactory extends IAppSessionFactory {

  /**
   * Get stack wide listener for sessions. In local mode it has similar effect
   * as setting this directly in app session. However clustered session use
   * this value when recreated!
   *
   * @return the serverSessionListener
   */
  ServerS6tSessionListener getServerSessionListener();

  /**
   * Set stack wide listener for sessions. In local mode it has similar effect
   * as setting this directly in app session. However clustered session use
   * this value when recreated!
   *
   * @param serverSessionListener
   *            the serverSessionListener to set
   */
  void setServerSessionListener(ServerS6tSessionListener serverSessionListener);

  /**
   * Get stack wide listener for sessions. In local mode it has similar effect
   * as setting this directly in app session. However clustered session use
   * this value when recreated!
   *
   * @return the clientSessionListener
   */
  ClientS6tSessionListener getClientSessionListener();

  /**
   * Set stack wide listener for sessions. In local mode it has similar effect
   * as setting this directly in app session. However clustered session use
   * this value when recreated!
   *
   * @param clientSessionListener
   *            the clientSessionListener to set
   */
  void setClientSessionListener(ClientS6tSessionListener clientSessionListener);

  /**
   * @return the messageFactory
   */
  IS6tMessageFactory getMessageFactory();

  /**
   * @param messageFactory
   *            the messageFactory to set
   */
  void setMessageFactory(IS6tMessageFactory messageFactory);

  /**
   * @return the stateListener
   */
  StateChangeListener<AppSession> getStateListener();

  /**
   * @param stateListener
   *            the stateListener to set
   */
  void setStateListener(StateChangeListener<AppSession> stateListener);
}
