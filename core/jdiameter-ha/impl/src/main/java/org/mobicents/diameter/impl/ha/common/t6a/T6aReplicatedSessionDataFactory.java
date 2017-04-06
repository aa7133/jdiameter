package org.mobicents.diameter.impl.ha.common.t6a;

import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.t6a.ClientT6aSession;
import org.jdiameter.api.t6a.ServerT6aSession;
import org.jdiameter.common.api.app.IAppSessionDataFactory;
import org.jdiameter.common.api.app.t6a.IT6aSessionData;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.mobicents.cluster.MobicentsCluster;
import org.mobicents.diameter.impl.ha.client.t6a.ClientT6aSessionDataReplicatedImpl;
import org.mobicents.diameter.impl.ha.data.ReplicatedSessionDatasource;
import org.mobicents.diameter.impl.ha.server.t6a.ServerT6aSessionDataReplicatedImpl;

/**
 * Created by Adi Enzel on 4/7/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public class T6aReplicatedSessionDataFactory  implements IAppSessionDataFactory<IT6aSessionData> {
  private ReplicatedSessionDatasource replicatedSessionDataSource;
  private MobicentsCluster mobicentsCluster;

  public T6aReplicatedSessionDataFactory(ISessionDatasource replicatedSessionDataSource) {
    super();
    this.replicatedSessionDataSource = (ReplicatedSessionDatasource) replicatedSessionDataSource;
    this.mobicentsCluster = this.replicatedSessionDataSource.getMobicentsCluster();
  }

  @Override
  public IT6aSessionData getAppSessionData(Class<? extends AppSession> clazz, String sessionId) {
    IT6aSessionData data = null;
    if (clazz.equals(ClientT6aSession.class)) {
      return new ClientT6aSessionDataReplicatedImpl(sessionId, this.mobicentsCluster, this.replicatedSessionDataSource.getContainer());
    }
    else if (clazz.equals(ServerT6aSession.class)) {
      return new ServerT6aSessionDataReplicatedImpl(sessionId, this.mobicentsCluster, this.replicatedSessionDataSource.getContainer());
    }
    throw new IllegalArgumentException();

  }
}
