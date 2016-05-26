package org.rtens.cell.test;

import java.util.LinkedList;

import junit.framework.TestCase;

import org.rtens.cell.*;
import org.rtens.cell.connection.SocketClient;
import org.rtens.cell.gui.browser.CellConnector;
import org.rtens.cell.test.TestKernelCells.FakePeer;
import org.rtens.cell.test.TestReceive.*;

public class TestCellConnector extends TestCase {
	
	private MockCell root;
	private Cell strings;
	private CellConnector c;
	
	public void testSimpleGetName() {
		root.addChild("A");
		
		addStrings("A");
		
		assertEquals("A", c.getName());
	}
	
	/**
	 * <pre>
	 * 1 °       2 °      3 °(1)
	 *   A         A(3)     A
	 *   B(2)      B        B
	 *   C                  C
	 *                      D <-
	 * </pre>
	 */
	public void testTrickyGetName() {
		CellConnector c = new CellConnector(root, new Address("local", "3", new Path("°", "A", "B", "C", "D")));
		
		Cell b1 = root.addChild("A")
				.addChild("B");
		b1.addChild("C");
		
		Cell root2 = new MockCell(null, "°");
		Cell a2 = root2.addChild("A");
		Cell b2 = a2.addChild("B");
		
		Cell root3 = new MockCell(null, "°");
		Cell a3 = root3.addChild("A");
		a3.addChild("B")
				.addChild("C")
				.addChild("D");
		
		b1.addPeer(new MockPeer(b2, 2));
		a2.addPeer(new MockPeer(a3, 3));
		root3.addPeer(new MockPeer(root, 0));
		
		addStrings("D");
		
		assertEquals("D", c.getName());
	}
	
	public void testIsActive() {
		CellConnector c2 = new CellConnector(root, new Address("local", "0", new Path("°", "B")));
		
		Cell a = root.addChild("A");
		root.addChild("B").setActive(false);
		
		assertEquals(true, c.isActive());
		assertEquals(false, c2.isActive());
		
		a.setActive(false);
		
		c.clearCache();
		assertEquals(false, c.isActive());
	}
	
	public void testGetChildren() {
		addStrings("A", "B", "C");
		
		Cell a = root.addChild("A");
		a.addChild("A");
		a.addChild("B");
		a.addChild("C");
		
		LinkedList<String> children = c.getChildren();
		
		assertEquals(3, children.size());
		assertEquals("A", children.get(0));
		assertEquals("B", children.get(1));
		assertEquals("C", children.get(2));
		
		a.removeChild("A", null);
		a.removeChild("C", null);
		assertEquals(3, c.getChildren().size());
		
		c.clearCache();
		assertEquals(1, c.getChildren().size());
	}
	
	public void testGetPeers() {
		addStrings("net1:abc", "net1:xyz", "Socket:192.168.0.1");
		
		Cell a = root.addChild("A");
		
		Address addr1 = new Address("net1", "abc", a.getPath());
		Address addr2 = new Address("net1", "xyz", a.getPath());
		Address addr3 = new Address("Socket", "192.168.0.1", a.getPath());
		
		a.addPeer(new FakePeer(addr1));
		a.addPeer(new FakePeer(addr2));
		a.addPeer(new FakePeer(addr3));
		
		LinkedList<Address> peerAddresses = c.getPeers();
		
		assertEquals(3, peerAddresses.size());
		assertTrue(peerAddresses.contains(addr1));
		assertTrue(peerAddresses.contains(addr2));
		assertTrue(peerAddresses.contains(addr3));
	}
	
	public void testGetStem() {
		addStrings("My", "Stem");
		root.addChild("A").setStem(new Path("My", "Stem"));
		
		assertEquals(new Path("My", "Stem"), c.getStem());
	}
	
	public void testGetReaction() {
		addStrings("A", "B", "C", "D", "E", "F");
		
		Reaction reaction = new Reaction();
		reaction.add(new Send(new Path("A", "B"), new Path("C", "D")));
		reaction.add(new Send(new Path("C", "D"), new Path("E", "F")));
		
		root.addChild("A").setReaction(reaction);
		root.addChild("B");
		
		assertEquals(reaction, c.getReaction());
		
		c = new CellConnector(root, new Address("local", "0", new Path("°", "B")));
		
		assertNull(root.getChild("B").getReaction());
		assertNull(c.getReaction());
	}
	
	public void testSetStem() {
		Cell a = root.addChild("A").setStem(new Path("My", "Stem"));
		Cell b = root.addChild("B");
		
		c.setStem(new Path("New", "StemPath"));
		
		assertEquals(new Path("New", "StemPath"), a.getStem());
		
		c = new CellConnector(root, new Address("local", "0", new Path("°", "B")));
		
		c.setStem(new Path("MyB", "StemPath"));
		
		assertEquals(new Path("MyB", "StemPath"), b.getStem());
	}
	
	public void testSetActive() {
		Cell a = root.addChild("A");
		a.setActive(false);
		
		assertFalse(a.isActive());
		
		c.setActive(true);
		assertTrue(a.isActive());
		
		c.setActive(false);
		assertFalse(a.isActive());
	}
	
	public void testSetName() {
		Cell a = root.addChild("A");
		
		c.setName("NewName");
		
		assertEquals("NewName", a.getName());
	}
	
	public void testAddChild() {
		Cell a = root.addChild("A");
		
		c.addChild("MyChild");
		
		assertNotNull(a.getChild("MyChild"));
	}
	
	public void testDelete() {
		root.addChild("A").addChild("B");
		
		c.removeChild("B");
		
		assertNull(root.getChild("A").getChild("B"));
	}
	
	public void testAddAndRemovePeer() {
		Cell a = root.addChild("A");
		
		Address addr = new Address("Socket", "localhost:80", a.getPath());
		
		c.addPeer(addr);
		
		assertEquals(1, a.getPeers().size());
		assertTrue(a.getPeers().getFirst() instanceof SocketClient);
		
		SocketClient client = (SocketClient) a.getPeers().getFirst();
		assertEquals("localhost:80", client.getAddress(null).host.hostAddress);
		
		c.removePeer(addr);
		
		assertEquals(0, a.getPeers().size());
	}
	
	public void testSetReaction() {
		Cell a = root.addChild("A");
		
		Reaction reaction = new Reaction();
		reaction.add(new Send(new Path("My", "Receiver"), new Path("Your", "Message")));
		reaction.add(new Send(new Path("Whats", "Up"), new Path("Your", "Mom")));
		
		c.setReaction(reaction);
		
		assertEquals(reaction, a.getReaction());
	}
	
	private void addStrings(String... s) {
		for (String string : s)
			strings.addChild(string);
	}
	
	@Override
	protected void setUp() throws Exception {
		root = new MockCell(null, "°");
		
		Cell prophet = root.addChild("Prophet");
		
		strings = prophet
				.addChild("Literal")
				.addChild("String");
		
		prophet.addChild("True");
		prophet.addChild("False");
		
		c = new CellConnector(root, new Address("local", "0", new Path("°", "A")));
	}
	
}
