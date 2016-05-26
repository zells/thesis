package org.rtens.cell.test;

import junit.framework.TestCase;

import org.rtens.cell.*;
import org.rtens.cell.connection.SocketClient;
import org.rtens.cell.glue.PathFormat;
import org.rtens.cell.test.TestReceive.*;

public class TestKernelCells extends TestCase {
	
	private MockCell root;
	private Cell strings;
	
	public void testAccess() {
		root.addChild("A").setStem(new Path("°", "B"));
		root.addChild("B");
		
		Result r = send("A.stem.cell");
		
		assertTrue(r.wasDelivered());
		assertEquals(new Path("°", "B", "cell"), r.getReceiver());
	}
	
	public void testReadName() {
		root.addChild("A");
		addStrings("A");
		
		Result r = send("A.cell.Name.stem.cell");
		
		assertTrue(r.wasDelivered());
		assertEquals("°.Prophet.Literal.String.A.cell", r.getResolvedReceiver().toString());
	}
	
	public void testReachCertainPeer() {
		MockCell a1 = (MockCell) root.addChild("X")
				.addChild("A").withReaction();
		
		MockCell root2 = new MockCell(null, "°");
		MockCell a2 = (MockCell) root2.addChild("X")
				.addChild("A").withReaction();
		
		root.addPeer(new MockPeer(root2, 2));
		
		Result r = send("X.A.cell.Peers.local:2");
		
		assertEquals("°.X.A.cell.Peers.local:2", r.getResolvedReceiver().toString());
		assertEquals(1, a2.reaction.executed);
		assertEquals(0, a1.reaction.executed);
		
		r = send("X.A.cell.Peers.local:0");
		
		assertEquals("°.X.A.cell.Peers.local:0", r.getResolvedReceiver().toString());
		assertEquals(1, a2.reaction.executed);
		assertEquals(1, a1.reaction.executed);
	}
	
	/**
	 * The cell "Children" contains the children of a cell as children with names "0", "1", ... and
	 * the name of each child as string as stem.
	 */
	public void testReadChildren() {
		Cell a = root.addChild("A");
		a.addChild("A");
		a.addChild("B");
		a.addChild("C");
		
		addStrings("A", "B", "C");
		
		Result r = send("A.cell.Children.0.stem.cell");
		assertTrue(r.wasDelivered());
		assertEquals("°.Prophet.Literal.String.A.cell", r.getResolvedReceiver().toString());
		
		r = send("A.cell.Children.1.stem.cell");
		assertTrue(r.wasDelivered());
		assertEquals("°.Prophet.Literal.String.B.cell", r.getResolvedReceiver().toString());
		
		r = send("A.cell.Children.2.stem.cell");
		assertTrue(r.wasDelivered());
		assertEquals("°.Prophet.Literal.String.C.cell", r.getResolvedReceiver().toString());
		
		r = send("A.cell.Children.3.stem.cell");
		assertFalse(r.wasDelivered());
		
		a.removeChild("A", null);
		a.removeChild("C", null);
		
		r = send("A.cell.Children.0.stem.cell");
		assertTrue(r.wasDelivered());
		assertEquals("°.Prophet.Literal.String.B.cell", r.getResolvedReceiver().toString());
		
		r = send("A.cell.Children.1.stem.cell");
		assertFalse(r.wasDelivered());
	}
	
	public void testReadChildrenFromCellWithPeers() {
		addStrings("A", "B");
		
		Cell a = root.addChild("A");
		
		MockCell root2 = new MockCell(null, "°");
		root2.addPeer(new MockPeer(root, 0));
		root.addPeer(new MockPeer(root2, 2));
		
		Cell a2 = root2.addChild("A");
		a.addPeer(new MockPeer(a2, 2));
		
		a2.addChild("B");
		
		Result r = send("A.cell.Peers.local:0.cell.Children.0.stem.cell");
		assertTrue(!r.wasDelivered());
		
		r = send("A.cell.Peers.local:2.cell.Children.0.stem.cell");
		assertTrue(r.wasDelivered());
		assertEquals("°.Prophet.Literal.String.B.cell", r.getResolvedReceiver().toString());
	}
	
	public void testReadPeers() {
		addStrings("net1:abc", "net1:xyz", "Socket:192.168.0.1");
		
		Cell a = root.addChild("A");
		
		a.addPeer(new FakePeer(new Address("net1", "abc", a.getPath())));
		a.addPeer(new FakePeer(new Address("net1", "xyz", a.getPath())));
		a.addPeer(new FakePeer(new Address("Socket", "192.168.0.1", a.getPath())));
		
		Result r = send("A.cell.Peers.cell.Children.0.stem.cell");
		assertTrue(r.wasDelivered());
		assertEquals("°.Prophet.Literal.String.net1:abc.cell", r.getResolvedReceiver().toString());
		
		r = send("A.cell.Peers.cell.Children.1.stem.cell");
		assertTrue(r.wasDelivered());
		assertEquals("°.Prophet.Literal.String.net1:xyz.cell", r.getResolvedReceiver().toString());
		
		r = send("A.cell.Peers.cell.Children.2.stem.cell");
		assertTrue(r.wasDelivered());
		assertEquals("°.Prophet.Literal.String.\"Socket:192.168.0.1\".cell", r.getResolvedReceiver().toString());
		
		r = send("A.cell.Peers.cell.Children.3.stem.cell");
		assertTrue(!r.wasDelivered());
	}
	
	public void testReadStem() {
		root.addChild("A").setStem(new Path("My", "Stem"));
		root.addChild("B");
		
		addStrings("My", "Stem");
		
		Result r = send("B.cell.Stem.cell");
		assertFalse(r.wasDelivered());
		
		r = send("A.cell.Stem.0.stem.cell");
		assertTrue(r.wasDelivered());
		assertEquals("°.Prophet.Literal.String.My.cell", r.getResolvedReceiver().toString());
		
		r = send("A.cell.Stem.1.stem.cell");
		assertTrue(r.wasDelivered());
		assertEquals("°.Prophet.Literal.String.Stem.cell", r.getResolvedReceiver().toString());
		
		r = send("A.cell.Stem.2.stem.cell");
		assertTrue(!r.wasDelivered());
	}
	
	public void testReadReaction() {
		addStrings("A", "B", "C", "D", "E", "F");
		
		Reaction reaction = new Reaction();
		reaction.add(new Send(new Path("A", "B"), new Path("C", "D")));
		reaction.add(new Send(new Path("C", "D"), new Path("E", "F")));
		
		root.addChild("A").setReaction(reaction);
		root.addChild("B");
		
		Result r = send("B.cell.Response.cell");
		assertFalse(r.wasDelivered());
		
		assertReactionSend("0.Receiver.0", "A");
		assertReactionSend("0.Receiver.1", "B");
		assertReactionSend("0.Message.0", "C");
		assertReactionSend("0.Message.1", "D");
		
		assertReactionSend("1.Receiver.0", "C");
		assertReactionSend("1.Receiver.1", "D");
		assertReactionSend("1.Message.0", "E");
		assertReactionSend("1.Message.1", "F");
		
		r = send("B.cell.Response.2.cell");
		assertFalse(r.wasDelivered());
	}
	
	private void assertReactionSend(String path, String name) {
		Result r;
		r = send("A.cell.Reaction." + path + ".stem.cell");
		assertTrue(path + " => " + name, r.wasDelivered());
		assertEquals("°.Prophet.Literal.String." + name + ".cell", r.getResolvedReceiver().toString());
	}
	
	/**
	 * Looks like to set most of the other properties, paths of stem cells are changes. Thus this is
	 * the central API. I guess we could make that really easy with "X.cell.Stem.set My.Stem" so no
	 * lists have to be created and stuff. This might be not the most coherent way with the stem
	 * being a Path which is a List but it's so easy and straight forward that we should give it a
	 * try.
	 * 
	 * At least now I know why this doesn't work: The message path is always interpreted relative to
	 * the sender but in this case it should be relative so the receiver so that means we have to
	 * work with lists which brings us way down in the List API. Ugly.
	 * 
	 * Screw that I'm just gonna mock a list ;)
	 */
	public void testSetStem() {
		Cell a = root.addChild("A").setStem(new Path("My", "Stem"));
		Cell b = root.addChild("B");
		
		Result r = send("A.cell.Stem.clear");
		
		assertTrue("Not received clear", r.wasDelivered());
		assertEquals(new Path(), a.getStem());
		
		r = send("A.cell.Stem.add", "°.Prophet.Literal.String.Other");
		assertTrue("Not received add 1", r.wasDelivered());
		assertEquals(new Path("Other"), a.getStem());
		
		r = send("A.cell.Stem.add", "°.Prophet.Literal.String.Stem");
		assertTrue("Not received add 2", r.wasDelivered());
		assertEquals(new Path("Other", "Stem"), a.getStem());
		
		assertNull(b.getStem());
		
		r = send("B.cell.cell.Children.add", "°.Prophet.Literal.String.Stem");
		assertTrue("Not received newStem", r.wasDelivered());
		assertNotNull(b.getStem());
	}
	
	public void testRemoveStem() {
		Cell a = root.addChild("A").setStem(new Path("My", "Stem"));
		
		Result r = send("A.cell.cell.Children.remove", "°.Prophet.Literal.String.Stem");
		assertTrue("Not received remove", r.wasDelivered());
		assertNull(a.getStem());
		
		r = send("A.cell.Stem.cell");
		assertFalse(r.wasDelivered());
	}
	
	/**
	 * So to set a cell active you change the stem of the "Active" cell.
	 */
	public void testSetActive() {
		Cell a = root.addChild("A");
		a.setActive(false);
		
		Result r = send("A.cell.Active.cell.Stem.clear");
		assertTrue("Not received clear", r.wasDelivered());
		
		r = send("A.cell.Active.cell.Stem.add", "°.Prophet.Literal.String.°");
		assertTrue("Not received add", r.wasDelivered());
		r = send("A.cell.Active.cell.Stem.add", "°.Prophet.Literal.String.Prophet");
		assertTrue("Not received add", r.wasDelivered());
		r = send("A.cell.Active.cell.Stem.add", "°.Prophet.Literal.String.True");
		assertTrue("Not received add", r.wasDelivered());
		
		assertEquals(true, a.isActive());
		
		send("A.cell.Active.cell.Stem.clear");
		send("A.cell.Active.cell.Stem.add", "°.Prophet.Literal.String.°");
		send("A.cell.Active.cell.Stem.add", "°.Prophet.Literal.String.Prophet");
		send("A.cell.Active.cell.Stem.add", "°.Prophet.Literal.String.False");
		
		assertEquals(false, a.isActive());
	}
	
	/**
	 * <pre>
	 * Example: Setting Name of X to °.Prophet.Literal.String.MyName
	 * 
	 * X.cell.Name.cell.Stem.clear	°
	 * X.cell.Name.cell.Stem.add	°.Prophet.Literal.String.°
	 * X.cell.Name.cell.Stem.add	°.Prophet.Literal.String.Prophet
	 * X.cell.Name.cell.Stem.add	°.Prophet.Literal.String.Literal
	 * X.cell.Name.cell.Stem.add	°.Prophet.Literal.String.String
	 * X.cell.Name.cell.Stem.add	°.Prophet.Literal.String.MyName
	 */
	public void testSetName() {
		Cell a = root.addChild("A");
		addStrings("B");
		
		Result r = send("A.cell.Name.cell.Stem.clear");
		assertTrue(r.wasDelivered());
		
		r = send("A.cell.Name.cell.Stem.add", "°.Prophet.Literal.String.°");
		r = send("A.cell.Name.cell.Stem.add", "°.Prophet.Literal.String.Prophet");
		r = send("A.cell.Name.cell.Stem.add", "°.Prophet.Literal.String.Literal");
		r = send("A.cell.Name.cell.Stem.add", "°.Prophet.Literal.String.String");
		r = send("A.cell.Name.cell.Stem.add", "°.Prophet.Literal.String.B");
		
		assertEquals("B", a.getName());
	}
	
	/**
	 * <pre>
	 * 
	 * X.cell.Children.add	°.Prophet.String.Literal.NewChild
	 */
	public void testAddChild() {
		Cell a = root.addChild("A");
		
		Result r = send("A.cell.Children.add", "°.Prophet.String.Literal.NewChild");
		
		assertTrue(r.wasDelivered());
		assertNotNull(a.getChild("NewChild"));
	}
	
	public void testRemoveChild() {
		Cell a = root.addChild("A");
		a.addChild("X");
		a.addChild("B");
		
		Result r = send("A.cell.Children.remove", "°.Prophet.Literal.String.B");
		
		assertTrue(r.wasDelivered());
		assertNull(a.getChild("B"));
		assertNotNull(a.getChild("X"));
	}
	
	public void testAddPeer() {
		Cell a = root.addChild("A");
		
		Result r = send("A.cell.Peers.cell.Children.add", "°.Prophet.Literal.String.Socket:localhost:42");
		
		assertTrue(r.wasDelivered());
		
		assertEquals(1, a.getPeers().size());
		assertTrue(a.getPeers().getFirst() instanceof SocketClient);
		
		SocketClient client = (SocketClient) a.getPeers().getFirst();
		assertEquals("localhost:42", client.getAddress(null).host.hostAddress);
	}
	
	public void testRemovePeer() {
		Cell a = root.addChild("A");
		a.addPeer(new SocketClient(root, new Address("Socket", "localhost:42", a.getPath())));
		
		Result r = send("A.cell.Peers.cell.Children.remove", "°.Prophet.Literal.String.Socket:localhost:42");
		
		assertTrue(r.wasDelivered());
		assertEquals(0, a.getPeers().size());
	}
	
	public void testAddAndRemoveReaction() {
		Cell a = root.addChild("A");
		
		Result r = send("A.cell.cell.Children.add", "°.Prophet.Literal.String.Reaction");
		
		assertTrue(r.wasDelivered());
		assertNotNull(a.getReaction());
		
		r = send("A.cell.cell.Children.remove", "°.Prophet.Literal.String.Reaction");
		
		assertNull(a.getReaction());
	}
	
	public void testSetReaction() {
		Cell a = root.addChild("A");
		
		Result r = send("A.cell.cell.Children.add", "°.Prophet.Literal.String.Reaction");
		assertTrue(r.wasDelivered());
		assertNotNull(a.getReaction());
		
		Reaction reaction = (Reaction) a.getReaction();
		
		r = send("A.cell.Reaction.add", "°");
		assertTrue(r.wasDelivered());
		assertEquals(1, reaction.getSends().size());
		
		send("A.cell.Reaction.0.Receiver.add", "°.Prophet.Literal.String.My");
		send("A.cell.Reaction.0.Receiver.add", "°.Prophet.Literal.String.Receiver");
		
		send("A.cell.Reaction.0.Message.add", "°.Prophet.Literal.String.Your");
		send("A.cell.Reaction.0.Message.add", "°.Prophet.Literal.String.Message");
		
		assertEquals(new Path("My", "Receiver"), reaction.getSends().getFirst().getReceiver());
		assertEquals(new Path("Your", "Message"), reaction.getSends().getFirst().getMessage());
		
		send("A.cell.Reaction.clear", "°");
		assertEquals(0, ((Reaction) a.getReaction()).getSends().size());
	}
	
	public void testSetStemDirectly() {
		Cell a = root.addChild("A");
		a.addChild("B").setStem(new Path("°", "S"));
		root.addChild("S");
		
		send("°.A.cell.setStem", "°.A.B.stem");
		
		assertEquals(new Path("°", "S"), a.getStem());
	}
	
	private void addStrings(String... s) {
		for (String string : s)
			strings.addChild(string);
	}
	
	static class FakePeer extends Peer {
		
		private Address address;
		
		public FakePeer(Address addr) {
			this.address = addr;
		}
		
		@Override
		public Result deliver(Parameters p) {
			return new Result();
		}
		
		@Override
		public Result deliverToChild(Parameters p) {
			return new Result();
		}
		
		@Override
		public Result deliverToStem(Parameters p) {
			return new Result();
		}
		
		@Override
		public Address getAddress(Parameters p) {
			return address;
		}
		
	}
	
	private Result send(String receiver) {
		return send(receiver, "°");
	}
	
	private Result send(String receiver, String message) {
		try {
			return root.send(new Send(PathFormat.parse(receiver),
					PathFormat.parse(message)),
					TestReceive.rootContext());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	protected void setUp() throws Exception {
		root = new MockCell(null, "°");
		strings = root.addChild("Prophet")
				.addChild("Literal")
				.addChild("String");
		
		TestReceive.mockConnections = new MockConnections(root);
		root.setConnections(TestReceive.mockConnections);
	}
	
}
