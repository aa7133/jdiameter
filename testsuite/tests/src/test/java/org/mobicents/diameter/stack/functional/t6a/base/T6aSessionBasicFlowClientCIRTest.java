package org.mobicents.diameter.stack.functional.t6a.base;


import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jdiameter.api.DisconnectCause;
import org.jdiameter.api.Mode;
import org.jdiameter.api.Peer;
import org.jdiameter.api.PeerState;
import org.jdiameter.api.PeerTable;
import org.jdiameter.api.Stack;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

/**
 * Created by Adi Enzel on 3/16/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public class T6aSessionBasicFlowClientCIRTest {
  private ClientSendCIR clientNode;
  private ServerRecvCIR serverNode1;
  private URI clientConfigURI;
  private URI serverNode1ConfigURI;


  /**
   *
   * @param clientConfigUrl
   * @param serverNode1ConfigURL
   * @throws Exception
   */
  public T6aSessionBasicFlowClientCIRTest(String clientConfigUrl, String serverNode1ConfigURL) throws Exception {
    super();
    this.clientConfigURI = new URI(clientConfigUrl);
    this.serverNode1ConfigURI = new URI(serverNode1ConfigURL);
  }

  @Before
  public void setUp() throws Exception {
    try {
      this.clientNode = new ClientSendCIR();
      this.serverNode1 = new ServerRecvCIR();

      this.serverNode1.init(new FileInputStream(new File(this.serverNode1ConfigURI)), "SERVER1");
      this.serverNode1.start();

      this.clientNode.init(new FileInputStream(new File(this.clientConfigURI)), "CLIENT");
      this.clientNode.start(Mode.ANY_PEER, 10, TimeUnit.SECONDS);
      Stack stack = this.clientNode.getStack();
      List<Peer> peers = stack.unwrap(PeerTable.class).getPeerTable();
      if (peers.size() == 1) {
        // ok
      }
      else if (peers.size() > 1) {
        // works better with replicated, since disconnected peers are also listed
        boolean foundConnected = false;
        for (Peer p : peers) {
          if (p.getState(PeerState.class).equals(PeerState.OKAY)) {
            if (foundConnected) {
              throw new Exception("Wrong number of connected peers: " + peers);
            }
            foundConnected = true;
          }
        }
      }
      else {
        throw new Exception("Wrong number of connected peers: " + peers);
      }
    }
    catch (Throwable e) {
      e.printStackTrace();
    }
  }

  @After
  public void tearDown() {
    if (this.serverNode1 != null) {
      try {
        this.serverNode1.stop(DisconnectCause.REBOOTING);
      }
      catch (Exception e) {

      }
      this.serverNode1 = null;
    }

    if (this.clientNode != null) {
      try {
        this.clientNode.stop(DisconnectCause.REBOOTING);
      }
      catch (Exception e) {

      }
      this.clientNode = null;
    }
  }

  @Test
  public void testConfigurationInformation() throws Exception {
    try {
      // pain of parameter tests :) ?
      clientNode.sendConfigurationInformationRequest();
      waitForMessage();

      serverNode1.sendConfigurationInformationAnswer();
      waitForMessage();
    }
    catch (Exception e) {
      e.printStackTrace();
      fail(e.toString());
    }

    if (!serverNode1.isReceivedConfigurationInfo()) {
      StringBuilder sb = new StringBuilder("Did not receive LIR! ");
      sb.append("Server ER:\n").append(serverNode1.createErrorReport(this.serverNode1.getErrors()));

      fail(sb.toString());
    }
    if (!clientNode.isReceivedConfigurationInfo()) {
      StringBuilder sb = new StringBuilder("Did not receive LIA! ");
      sb.append("Client ER:\n").append(clientNode.createErrorReport(this.clientNode.getErrors()));

      fail(sb.toString());
    }

    if (!clientNode.isPassed()) {
      StringBuilder sb = new StringBuilder();
      sb.append("Client ER:\n").append(clientNode.createErrorReport(this.clientNode.getErrors()));

      fail(sb.toString());
    }

    if (!serverNode1.isPassed()) {
      StringBuilder sb = new StringBuilder();
      sb.append("Server ER:\n").append(serverNode1.createErrorReport(this.serverNode1.getErrors()));

      fail(sb.toString());
    }
  }

  @Parameters
  public static Collection<Object[]> data() {

    String client = "configurations/functional-t6a/config-client.xml";
    String server1 = "configurations/functional-t6a/config-server-node1.xml";

    Class<T6aSessionBasicFlowClientCIRTest> t = T6aSessionBasicFlowClientCIRTest.class;
    client = t.getClassLoader().getResource(client).toString();
    server1 = t.getClassLoader().getResource(server1).toString();

    return Arrays.asList(new Object[][] { { client, server1 }/*, { replicatedClient, replicatedServer1 } */});
  }

  private void waitForMessage() {
    try {
      Thread.sleep(2000);
    }
    catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
