package org.zells.test;

import java.util.*;

import org.zells.*;
import org.zells.peers.*;

public class TestSocketPeer extends DeliveryTest {
	
	private SocketServer s;
	private HashMap<String, String> params;
	
	public void testStartAndStopServer() throws InterruptedException {
		
		s.start(params);
		Thread.sleep(50L);
		
		assertEquals(new Address("Socket", "localhost:42"), s.getAddress());
		
		assertEquals(true, s.isRunning());
		
		s.stop();
		Thread.sleep(15L);
		
		assertFalse(s.isRunning());
	}
	
	public void testRoundtrip() throws InterruptedException {
		MockCell remoteRoot = root;
		MockCell localRoot = new MockCell(null, "°");
		
		MockCell aL = localRoot.addChild("A");
		MockCell abR = remoteRoot.addChild("A").addChild("B").withReaction();
		
		s.start(params);
		
		SocketPeer aLP = new SocketPeer(aL, new Address("Socket", "localhost:42"));
		
		LinkedList<Delivery> deliveryStack = new LinkedList<Delivery>();
		deliveryStack.add(new Delivery(new Path("°", "A"), new Path("B")));
		
		Result r = aLP.deliver(deliveryStack, new Path("M"), new DeliveryId(0));
		
		Thread.sleep(100L);
		
		assertEquals(1, abR.reaction.executed);
		assertTrue(r.wasDelivered());
	}
	
	@Override
	protected void tearDown() throws Exception {
		s.stop();
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		s = new SocketServer(root);
		
		params = new HashMap<String, String>();
		params.put("Hostname", "localhost");
		params.put("Port", "42");
	}
	
}
