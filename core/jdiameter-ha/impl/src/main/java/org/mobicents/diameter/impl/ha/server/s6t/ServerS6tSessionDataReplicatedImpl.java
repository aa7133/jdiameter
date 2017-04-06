package org.mobicents.diameter.impl.ha.server.s6t;

import org.jboss.cache.Fqn;
import org.jdiameter.api.s6t.ServerS6tSession;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.common.api.app.s6t.S6tSessionState;
import org.jdiameter.server.impl.app.s6t.IServerS6tSessionData;
import org.mobicents.cluster.MobicentsCluster;
import org.mobicents.diameter.impl.ha.common.s6t.S6tSessionDataReplicatedImpl;
import org.mobicents.diameter.impl.ha.data.ReplicatedSessionDatasource;

/**
 * Created by Adi Enzel on 3/12/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public class ServerS6tSessionDataReplicatedImpl extends S6tSessionDataReplicatedImpl implements IServerS6tSessionData {

  /**
   *
   * @param nodeFqn address FQDN
   * @param mobicentsCluster cluster
   * @param container container
   */
  public ServerS6tSessionDataReplicatedImpl(Fqn<?> nodeFqn, MobicentsCluster mobicentsCluster, IContainer container) {
    super(nodeFqn, mobicentsCluster, container);

    if (super.create()) {
      setAppSessionIface(this, ServerS6tSession.class);
      setS6tSessionState(S6tSessionState.IDLE);
    }
  }

  /**
   *
   * @param sessionId the session to save
   * @param mobicentsCluster cluster
   * @param container container
   */
  public ServerS6tSessionDataReplicatedImpl(String sessionId, MobicentsCluster mobicentsCluster, IContainer container) {
    this(Fqn.fromRelativeElements(ReplicatedSessionDatasource.SESSIONS_FQN, sessionId), mobicentsCluster, container);
  }
}
