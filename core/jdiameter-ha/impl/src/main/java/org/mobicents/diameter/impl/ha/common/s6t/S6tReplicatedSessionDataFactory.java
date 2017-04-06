package org.mobicents.diameter.impl.ha.common.s6t;

import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.s6t.ClientS6tSession;
import org.jdiameter.api.s6t.ServerS6tSession;
import org.jdiameter.common.api.app.IAppSessionDataFactory;
import org.jdiameter.common.api.app.s6t.IS6tSessionData;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.mobicents.cluster.MobicentsCluster;
import org.mobicents.diameter.impl.ha.client.s6t.ClientS6tSessionDataReplicatedImpl;
import org.mobicents.diameter.impl.ha.data.ReplicatedSessionDatasource;
import org.mobicents.diameter.impl.ha.server.s6t.ServerS6tSessionDataReplicatedImpl;

/**
 * Created by Adi Enzel on 3/12/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public class S6tReplicatedSessionDataFactory implements IAppSessionDataFactory<IS6tSessionData> {


  private ReplicatedSessionDatasource replicatedSessionDataSource;
  private MobicentsCluster mobicentsCluster;

  /**
   *
   * @param replicatedSessionDataSource ISessionDatasource
   */
  public S6tReplicatedSessionDataFactory(ISessionDatasource replicatedSessionDataSource) { // Is this ok?
    super();
    this.replicatedSessionDataSource = (ReplicatedSessionDatasource) replicatedSessionDataSource;
    this.mobicentsCluster = this.replicatedSessionDataSource.getMobicentsCluster();
  }


  /**
   *
   * @param clazz what class
   * @param sessionId the session id of the request
   * @return data container
   */
  @Override
  public IS6tSessionData getAppSessionData(Class<? extends AppSession> clazz, String sessionId) {
    IS6tSessionData data = null;
    if (clazz.equals(ClientS6tSession.class)) {
      return new ClientS6tSessionDataReplicatedImpl(sessionId, this.mobicentsCluster, this.replicatedSessionDataSource.getContainer());
    }
    else if (clazz.equals(ServerS6tSession.class)) {
      return new ServerS6tSessionDataReplicatedImpl(sessionId, this.mobicentsCluster, this.replicatedSessionDataSource.getContainer());
    }
    throw new IllegalArgumentException();
  }
}
