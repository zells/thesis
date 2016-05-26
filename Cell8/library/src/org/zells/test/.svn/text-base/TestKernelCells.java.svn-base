package org.zells.test;

import org.zells.*;
import org.zells.library.Zells.Literal.StringCell;
import org.zells.peers.SocketPeer;

public class TestKernelCells extends LibraryTest {
	
	private String readString(String path) {
		return StringCell.get(p(path), root, root.getPath(), new DeliveryId());
	}
	
	public void testCellCellReceives() {
		root.addChild("A");
		
		send(p("°.A.cell"));
		
		assertTrue(res.wasDelivered());
		assertEquals(p("°.A.cell.##"), res.deliveredTo);
	}
	
	public void testReadName() {
		
		Cell a = root.addChild("A");
		
		send(p("°.A.cell.Name.cell"));
		assertTrue(res.wasDelivered());
		
		send(p("°.A.cell.Name.0.value"));
		assertTrue(res.wasDelivered());
		assertEquals(p("°.Zells.Literal.Character.A"), res.deliveredTo);
		
		a.setName("B");
		
		send(p("°.B.cell.Name.0.value"));
		assertTrue(res.wasDelivered());
		assertEquals(p("°.Zells.Literal.Character.B"), res.deliveredTo);
		
		a.setName("John");
		assertEquals("John", readString("°.John.cell.Name"));
	}
	
	public void testReadChildren() {
		Cell a = root.addChild("A");
		a.addChild("Anton");
		a.addChild("Bob");
		a.addChild("Carl");
		
		send(p("°.A.cell.Children.cell"));
		assertTrue(res.wasDelivered());
		
		send(p("°.A.cell.Children.0.cell"));
		assertTrue(res.wasDelivered());
		
		assertEquals("Anton", readString("°.A.cell.Children.0"));
		assertEquals("Bob", readString("°.A.cell.Children.1"));
		assertEquals("Carl", readString("°.A.cell.Children.2"));
		
		assertDelivery = false;
		send(p("°.A.cell.Children.3.cell"));
		assertFalse(res.wasDelivered());
		
		a.removeChild("Anton");
		a.removeChild("Carl");
		
		assertEquals("Bob", readString("°.A.cell.Children.0"));
		
		send(p("°.A.cell.Children.1.cell"));
		assertFalse(res.wasDelivered());
		
		a.addChild("Diana");
		
		assertEquals("Diana", readString("°.A.cell.Children.1"));
	}
	
	public void testReadStem() {
		Cell a = root.addChild("A");
		
		assertDelivery = false;
		send(p("°.A.cell.Stem.cell"));
		assertFalse(res.wasDelivered());
		
		a.setStem(new Path("My", "Stem"));
		
		send(p("°.A.cell.Stem.cell"));
		assertTrue(res.wasDelivered());
		
		assertEquals("My", readString("°.A.cell.Stem.0"));
		assertEquals("Stem", readString("°.A.cell.Stem.1"));
		
		send(p("°.A.cell.Stem.2.cell"));
		assertFalse(res.wasDelivered());
	}
	
	public void testReadReaction() {
		
		Reaction reaction = new Reaction();
		reaction.mailings.add(new Mailing(new Path("A", "B"), new Path("C", "D")));
		reaction.mailings.add(new Mailing(new Path("E", "F"), new Path("G", "H")));
		
		root.addChild("A").setReaction(reaction);
		root.addChild("B");
		
		assertDelivery = false;
		send(p("°.B.cell.Reaction.cell"));
		assertFalse(res.wasDelivered());
		
		assertDelivery = true;
		send(p("°.A.cell.Reaction.cell"));
		
		send(p("°.A.cell.Reaction.0.cell"));
		send(p("°.A.cell.Reaction.0.Receiver.cell"));
		send(p("°.A.cell.Reaction.0.Message.cell"));
		
		send(p("°.A.cell.Reaction.1.cell"));
		
		assertDelivery = false;
		send(p("°.A.cell.Reaction.2.cell"));
		
		assertReactionSend(p("0.Receiver.0"), "A");
		assertReactionSend(p("0.Receiver.1"), "B");
		assertReactionSend(p("0.Message.0"), "C");
		assertReactionSend(p("0.Message.1"), "D");
		
		assertReactionSend(p("1.Receiver.0"), "E");
		assertReactionSend(p("1.Receiver.1"), "F");
		assertReactionSend(p("1.Message.0"), "G");
		assertReactionSend(p("1.Message.1"), "H");
	}
	
	private void assertReactionSend(Path path, String name) {
		assertEquals(name, readString("°.A.cell.Reaction." + path));
	}
	
	public void testAddChild() {
		Cell a = root.addChild("A");
		
		send(p("°.A.cell.Children.add"), p("°.Zells.Literal.String.NewChild"));
		
		assertNotNull(a.getChild("NewChild"));
	}
	
	public void testAddChildWithSubCell() {
		Cell a = root.addChild("A");
		root.addChild("Name").setStem(p("°.Zells.Literal.String.SecondChild"));
		
		send(p("°.A.cell.Children.add"), p("°.Name"));
		
		assertTrue(res.wasDelivered());
		assertNotNull(a.getChild("SecondChild"));
	}
	
	public void testCreate() {
		Cell a = root.addChild("A");
		
		send(p("°.A.cell.create.NewChild"), p("°.B"));
		
		assertNotNull(a.getChild("NewChild"));
		assertEquals(p("°.B"), a.getChild("NewChild").getStem());
	}
	
	public void testRemoveChild() {
		
		Cell a = root.addChild("A");
		a.addChild("X");
		a.addChild("B");
		
		send(p("°.A.cell.Children.remove"), p("°.Zells.Literal.String.B"));
		
		assertTrue(res.wasDelivered());
		assertNull(a.getChild("B"));
		assertNotNull(a.getChild("X"));
	}
	
	public void testAddRemoveStem() {
		Cell a = root.addChild("A");
		
		send(p("°.A.cell.cell.Children.add"), p("°.Zells.Literal.String.Stem"));
		assertNotNull(a.getStem());
		
		send(p("°.A.cell.cell.Children.remove"), p("°.Zells.Literal.String.Stem"));
		assertTrue("Not received remove", res.wasDelivered());
		assertNull(a.getStem());
		
		assertDelivery = false;
		send(p("°.A.cell.Stem.cell"));
		assertFalse(res.wasDelivered());
	}
	
	public void testSetStem() {
		
		Cell a = root.addChild("A").setStem(new Path("My", "Stem"));
		
		send(p("°.A.cell.Stem.clear"));
		
		assertTrue("Not received clear", res.wasDelivered());
		assertEquals(new Path(), a.getStem());
		
		send(p("°.A.cell.Stem.add"), p("°.Zells.Literal.String.Other"));
		assertTrue("Not received add 1", res.wasDelivered());
		assertEquals(new Path("Other"), a.getStem());
		
		send(p("°.A.cell.Stem.add"), p("°.Zells.Literal.String.Stem"));
		assertTrue("Not received add 2", res.wasDelivered());
		assertEquals(new Path("Other", "Stem"), a.getStem());
	}
	
	//	public void testSetStemWithSetStem() {
	//		Cell a = root.addChild("A").setStem(new Path("My", "Stem"));
	//		
	//		send(p("°.A.cell.setStem"), p("°.New.StemCell"));
	//		assertEquals(p("°.New.StemCell"), a.getStem());
	//	}
	
	public void testSetName() {
		Cell a = root.addChild("A");
		
		setStem("°.A.cell.Name", "°.Zells.Literal.String.B");
		
		assertEquals("B", a.getName());
	}
	
	private void setStem(String path, String stem) {
		send(p(path + ".cell.cell.Children.add"), p("°.Zells.Literal.String.Stem"));
		send(p(path + ".cell.Stem.clear"));
		
		for (String s : p(stem)) {
			send(p(path + ".cell.Stem.add"), p("°.Zells.Literal.String." + s));
		}
	}
	
	public void testAddAndRemoveReaction() {
		Cell a = root.addChild("A");
		
		send(p("°.A.cell.cell.Children.add"), p("°.Zells.Literal.String.Reaction"));
		
		assertTrue(res.wasDelivered());
		assertNotNull(a.getReaction());
		
		send(p("°.A.cell.cell.Children.remove"), p("°.Zells.Literal.String.Reaction"));
		
		assertNull(a.getReaction());
	}
	
	public void testSetReaction() {
		Cell a = root.addChild("A");
		
		send(p("°.A.cell.cell.Children.add"), p("°.Zells.Literal.String.Reaction"));
		assertTrue(res.wasDelivered());
		assertNotNull(a.getReaction());
		
		Reaction reaction = (Reaction) a.getReaction();
		
		send(p("°.A.cell.Reaction.add"), p("°"));
		assertTrue(res.wasDelivered());
		assertEquals(1, reaction.mailings.size());
		
		send(p("°.A.cell.Reaction.0.Receiver.add"), p("°.Zells.Literal.String.My"));
		send(p("°.A.cell.Reaction.0.Receiver.add"), p("°.Zells.Literal.String.Receiver"));
		
		send(p("°.A.cell.Reaction.0.Message.add"), p("°.Zells.Literal.String.Your"));
		send(p("°.A.cell.Reaction.0.Message.add"), p("°.Zells.Literal.String.Message"));
		
		assertEquals(new Path("My", "Receiver"), reaction.mailings.get(0).receiver);
		assertEquals(new Path("Your", "Message"), reaction.mailings.get(0).message);
		
		send(p("°.A.cell.Reaction.clear"), p("°"));
		assertEquals(0, ((Reaction) a.getReaction()).mailings.size());
	}
	
	public void testSetActive() {
		Cell a = root.addChild("A");
		a.setActive(false);
		
		setStem("°.A.cell.Active", "°.Zells.True");
		assertEquals(true, a.isActive());
		
		setStem("°.A.cell.Active", "°.Zells.False");
		assertEquals(false, a.isActive());
	}
	
	public void testReachCertainPeer() {
		Cell a = root.addChild("A");
		
		MockCell root1 = new MockCell(null, "°");
		MockCell a1 = root1.addChild("A").withReaction();
		
		a.addPeer(new MockPeer(a1, new Address("test", "1")));
		
		send(p("°.A"));
		assertEquals(1, a1.reaction.executed);
		
		send(p("°.A.cell.Peers.test:1"));
		assertEquals(2, a1.reaction.executed);
		
		send(p("°.A.cell.Peers.test:1.cell.Peers.local:0"));
		assertEquals(3, a1.reaction.executed);
		
		assertDelivery = false;
		send(p("°.A.cell.Peers.local:0"));
		assertEquals(3, a1.reaction.executed);
	}
	
	public void testReadPeers() {
		Cell a = root.addChild("A");
		a.addPeer(new MockPeer(a, new Address("Socket", "localhost:42")));
		a.addPeer(new MockPeer(a, new Address("nothing", "nowhere")));
		
		send(p("°.A.cell.Peers.cell"));
		
		assertEquals("Socket:localhost:42", readString("°.A.cell.Peers.cell.Children.0"));
		assertEquals("nothing:nowhere", readString("°.A.cell.Peers.cell.Children.1"));
		assertDelivery = false;
		send(p("°.A.cell.Peers.cell.Children.3.cell"));
		assertFalse(res.wasDelivered());
	}
	
	public void testAddPeer() {
		Cell a = root.addChild("A");
		
		send(p("°.A.cell.Peers.cell.Children.add"), p("°.Zells.Literal.String.Socket:localhost:42"));
		
		assertEquals(1, a.getPeers().size());
		
		SocketPeer p = (SocketPeer) a.getPeers().get(0);
		assertEquals("localhost", p.hostname);
		assertEquals(42, p.port);
	}
		
	public void testRemovePeer() {
		Cell a = root.addChild("A");
		a.addPeer(new MockPeer(a, new Address("test", "1")));
		a.addPeer(new MockPeer(a, new Address("Socket", "localhost:42")));
		a.addPeer(new MockPeer(a, new Address("nothing", "nowhere")));
		
		assertEquals(3, a.getPeers().size());
		
		Result r = send(p("°.A.cell.Peers.cell.Children.remove"), p("°.Zells.Literal.String.Socket:localhost:42"));
		assertTrue(r.wasDelivered());
		
		assertEquals(2, a.getPeers().size());
	}
	
}
