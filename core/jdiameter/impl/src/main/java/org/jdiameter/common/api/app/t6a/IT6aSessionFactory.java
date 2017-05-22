package org.jdiameter.common.api.app.t6a;

import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.t6a.ClientT6aSessionListener;
import org.jdiameter.api.t6a.ServerT6aSessionListener;
import org.jdiameter.common.api.app.IAppSessionFactory;

/*
 * Copyright (c) 2017. AT&T Intellectual Property. All rights reserved
 */

/**
 * Created by Adi Enzel on 3/13/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public interface IT6aSessionFactory extends IAppSessionFactory {

  /**
   * Get stack wide listener for sessions. In local mode it has similar effect
   * as setting this directly in app session. However clustered session use
   * this value when recreated!
   *
   * @return the serverSessionListener
   */
  ServerT6aSessionListener getServerSessionListener();

  /**
   * Set stack wide listener for sessions. In local mode it has similar effect
   * as setting this directly in app session. However clustered session use
   * this value when recreated!
   *
   * @param serverSessionListener
   *            the serverSessionListener to set
   */
  void setServerSessionListener(ServerT6aSessionListener serverSessionListener);

  /**
   * Get stack wide listener for sessions. In local mode it has similar effect
   * as setting this directly in app session. However clustered session use
   * this value when recreated!
   *
   * @return the clientSessionListener
   */
  ClientT6aSessionListener getClientSessionListener();

  /**
   * Set stack wide listener for sessions. In local mode it has similar effect
   * as setting this directly in app session. However clustered session use
   * this value when recreated!
   *
   * @param clientSessionListener
   *            the clientSessionListener to set
   */
  void setClientSessionListener(ClientT6aSessionListener clientSessionListener);

  /**
   * @return the messageFactory
   */
  IT6aMessageFactory getMessageFactory();

  /**
   * @param messageFactory
   *            the messageFactory to set
   */
  void setMessageFactory(IT6aMessageFactory messageFactory);

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
