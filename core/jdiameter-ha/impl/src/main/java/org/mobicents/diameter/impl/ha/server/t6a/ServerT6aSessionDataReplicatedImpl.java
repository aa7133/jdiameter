package org.mobicents.diameter.impl.ha.server.t6a;

import org.jboss.cache.Fqn;
import org.jdiameter.api.t6a.ServerT6aSession;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.common.api.app.t6a.T6aSessionState;
import org.jdiameter.server.impl.app.t6a.IServerT6aSessionData;
import org.mobicents.cluster.MobicentsCluster;
import org.mobicents.diameter.impl.ha.common.t6a.T6aSessionDataReplicatedImpl;
import org.mobicents.diameter.impl.ha.data.ReplicatedSessionDatasource;

/**
 * Created by Adi Enzel on 4/7/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public class ServerT6aSessionDataReplicatedImpl extends T6aSessionDataReplicatedImpl implements IServerT6aSessionData {
  /**
   *
   * @param nodeFqn address of the node
   * @param mobicentsCluster the cluster
   * @param container container
   */
  public ServerT6aSessionDataReplicatedImpl(Fqn<?> nodeFqn, MobicentsCluster mobicentsCluster, IContainer container) {
    super(nodeFqn, mobicentsCluster, container);

    if (super.create()) {
      setAppSessionIface(this, ServerT6aSession.class);
      setT6aSessionState(T6aSessionState.IDLE);
    }
  }

  /**
   *
   * @param sessionId the session to save
   * @param mobicentsCluster the cluster
   * @param container container
   */
  public ServerT6aSessionDataReplicatedImpl(String sessionId, MobicentsCluster mobicentsCluster, IContainer container) {
    this(Fqn.fromRelativeElements(ReplicatedSessionDatasource.SESSIONS_FQN, sessionId), mobicentsCluster, container);
  }

}
