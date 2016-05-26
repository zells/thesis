package org.rtens.cell.test;

import java.net.InetSocketAddress;

import junit.framework.TestCase;

import org.rtens.cell.*;
import org.rtens.cell.connection.*;
import org.rtens.cell.test.TestReceive.MockCell;

public class TestServer extends TestCase {
	
	public void testSocketConnection() {
		MockCell serverRoot = new MockCell(null, "°");
		MockCell serverB = serverRoot.addChild("A").addChild("B").withReaction();
		
		Server server = new SocketServer(serverRoot, new InetSocketAddress("localhost", 4242));
		server.start();
		
		MockCell localRoot = new MockCell(null, "°");
		MockCell localB = localRoot.addChild("A").addChild("B");
		
		localB.addPeer(new SocketClient(localRoot, new Address("Socket", "localhost:4242", localB.getPath())));
		
		Result r = localRoot.send(new Send(new Path("A", "B"), new Path("Hallo")),
				new Context(null, new Path("°"), new Send(new Path(), new Path())));
		
		assertTrue(r.wasDelivered());
		assertEquals(1, serverB.received);
		assertEquals("Hallo", serverB.reaction.context.getMessage().getLast());
		
		server.stop();
		
		r = localRoot.send(new Send(new Path("A", "B"), new Path("Hallo")),
				new Context(null, new Path("°"), new Send(new Path(), new Path())));
		
		assertFalse(r.wasDelivered());
	}
}
