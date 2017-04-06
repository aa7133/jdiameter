package org.mobicents.diameter.impl.ha.client.t6a;

import org.jboss.cache.Fqn;
import org.jdiameter.api.t6a.ClientT6aSession;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.impl.app.t6a.IclientT6aSessionData;
import org.jdiameter.common.api.app.t6a.T6aSessionState;
import org.mobicents.cluster.MobicentsCluster;
import org.mobicents.diameter.impl.ha.common.t6a.T6aSessionDataReplicatedImpl;
import org.mobicents.diameter.impl.ha.data.ReplicatedSessionDatasource;

/**
 * Created by Adi Enzel on 4/7/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public class ClientT6aSessionDataReplicatedImpl extends T6aSessionDataReplicatedImpl implements IclientT6aSessionData {
  /**
   *
   * @param nodeFqn the node FQDN
   * @param mobicentsCluster cluster address
   * @param container the container
   */
  public ClientT6aSessionDataReplicatedImpl(Fqn<?> nodeFqn, MobicentsCluster mobicentsCluster, IContainer container) {
    super(nodeFqn, mobicentsCluster, container);

    if (super.create()) {
      setAppSessionIface(this, ClientT6aSession.class);
      setT6aSessionState(T6aSessionState.IDLE);
    }
  }

  /**
   *
   * @param sessionId the session id to save
   * @param mobicentsCluster which cluster
   * @param container container
   */
  public ClientT6aSessionDataReplicatedImpl(String sessionId, MobicentsCluster mobicentsCluster, IContainer container) {
    this(Fqn.fromRelativeElements(ReplicatedSessionDatasource.SESSIONS_FQN, sessionId), mobicentsCluster, container);
  }


}
