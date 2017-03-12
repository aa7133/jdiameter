package org.mobicents.diameter.impl.ha.client.s6t;

import org.jboss.cache.Fqn;
import org.jdiameter.api.s6t.ClientS6tSession;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.impl.app.s6t.IClientS6tSessionData;
import org.jdiameter.common.api.app.s6t.S6tSessionState;
import org.mobicents.cluster.MobicentsCluster;
import org.mobicents.diameter.impl.ha.common.s6t.S6tSessionDataReplicatedImpl;
import org.mobicents.diameter.impl.ha.data.ReplicatedSessionDatasource;

/**
 * Created by Adi Enzel on 3/12/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public class ClientS6tSessionDataReplicatedImpl extends S6tSessionDataReplicatedImpl implements IClientS6tSessionData {
  /**
   *
   * @param nodeFqn
   * @param mobicentsCluster
   * @param container
   */
  public ClientS6tSessionDataReplicatedImpl(Fqn<?> nodeFqn, MobicentsCluster mobicentsCluster, IContainer container) {
    super(nodeFqn, mobicentsCluster, container);

    if (super.create()) {
      setAppSessionIface(this, ClientS6tSession.class);
      setS6tSessionState(S6tSessionState.IDLE);
    }
  }

  /**
   *
   * @param sessionId
   * @param mobicentsCluster
   * @param container
   */
  public ClientS6tSessionDataReplicatedImpl(String sessionId, MobicentsCluster mobicentsCluster, IContainer container) {
    this(Fqn.fromRelativeElements(ReplicatedSessionDatasource.SESSIONS_FQN, sessionId), mobicentsCluster, container);
  }

}
