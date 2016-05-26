package org.rtens.cell.test;

import java.util.*;

import junit.framework.TestCase;

import org.rtens.cell.*;

/**
 * Symbols (Cells have globally unique letters, hosts numbers, root cell is omitted)
 * 
 * <pre>
 * A  			Cell with name "A" without response but with stem
 * 
 * A:			Cell which doesn't know its stem path
 * 
 * A:B			Cell A with stem B
 * 
 * A*			Cell with response
 * 
 * A*:B		Cell with stem B and response
 * 
 * A			Cell A with child B
 * B
 * 
 *   A			Cell A with children B, C and D
 *  /|\
 * B C D
 * 
 * A>123		Cell A which knows about its peers on hosts 1, 2 and 3
 * 
 * 1 A			A with child B on host 1
 *   B
 * 
 * ABC			Path of C of B of A
 * 
 * </pre>
 */
public class TestReceive extends TestCase {
	
	private MockCell root;
	
	public static MockConnections mockConnections;
	
	/**
	 * A receives from itself
	 * 
	 * <pre>
	 * A*
	 * </pre>
	 */
	public void testDirectly() {
		MockReaction ar = new MockReaction();
		Cell a = root.addChild("A").setReaction(ar);
		
		a.send(new Send(new Path(), new Path()), rootContext());
		
		assertEquals(1, ar.executed);
	}
	
	/**
	 * <pre>
	 * A B
	 * </pre>
	 */
	public void testRoot() {
		root.addChild("A");
		
		MockCell b = root.addChild("B").withReaction();
		
		root.send(new Send(new Path("A", "°", "B"), new Path()), rootContext());
		
		assertEquals(1, b.reaction.executed);
		assertEquals(new Path("°", "B"), b.parameters.resolvedStack.getLast());
	}
	
	/**
	 * <pre>
	 * A
	 * |\
	 * B C*
	 * </pre>
	 */
	public void testParent() {
		MockCell a = root.addChild("A");
		a.addChild("B");
		
		MockCell c = a.addChild("C").withReaction();
		
		root.send(new Send(new Path("A", "B", "parent", "C"), new Path()), rootContext());
		
		assertEquals(1, c.reaction.executed);
		assertEquals(new Path("°", "A", "C"), c.parameters.resolvedStack.getLast());
	}
	
	/**
	 * Send to A.B.stem.D and C.D should get it
	 * 
	 * <pre>
	 * A     C
	 * B:C   D*
	 * </pre>
	 */
	public void testStem() {
		root.addChild("A")
				.addChild("B").setStem(new Path("°", "C"));
		
		MockCell d = root.addChild("C")
				.addChild("D");
		
		send(new Path("A", "B", "stem", "D"));
		
		assertEquals(1, d.received);
		assertEquals(new Path("°", "C", "D"), d.parameters.resolvedStack.getLast());
	}
	
	/**
	 * Root sends to A.B.C
	 * 
	 * <pre>
	 * A
	 * B
	 * C*
	 * </pre>
	 */
	public void testChildren() {
		MockCell b = root.addChild("A").addChild("B");
		MockCell c = b.addChild("C").setReaction(new MockReaction());
		
		send(new Path("A", "B", "C"));
		
		assertEquals(1, c.received);
		assertEquals(new Path("°", "A", "B", "C"), c.parameters.resolvedStack.getLast());
	}
	
	/**
	 * Root1 sends to A which has response in other peer.
	 * 
	 * <pre>
	 * 1 A(2)      2 A*
	 * </pre>
	 */
	public void testPeer() {
		MockCell root1 = root;
		MockCell root2 = new MockCell(null, "°");
		
		MockCell a1 = root1.addChild("A");
		MockCell a2 = root2.addChild("A").withReaction();
		
		a1.addPeer(new MockPeer(a2, 2));
		
		send(new Path("A"));
		
		assertEquals(1, a2.reaction.executed);
	}
	
	/**
	 * The actual receiver is a peer of the peer of the sender
	 * 
	 * <pre>
	 * 1 A(2)      2 A(3)      3 A*
	 * </pre>
	 */
	public void testPeerOfPeer() {
		MockCell root2 = new MockCell(null, "°");
		MockCell root3 = new MockCell(null, "°");
		
		MockCell a1 = root.addChild("A");
		MockCell a2 = root2.addChild("A");
		MockCell a3 = root3.addChild("A").withReaction();
		
		a1.getPeers().add(new MockPeer(a2, 2));
		a2.getPeers().add(new MockPeer(a3, 3));
		
		send(new Path("A"));
		
		assertEquals(1, a3.reaction.executed);
	}
	
	/**
	 * The local cell has some of the children but not all
	 * 
	 * <pre>
	 * 1 A(2)    2 A
	 *   B         B
	 *             C*
	 * </pre>
	 */
	public void testPartialCell() {
		MockCell a1 = root.addChild("A");
		a1.addChild("B");
		
		MockCell root2 = new MockCell(null, "°");
		MockCell a2 = root2.addChild("A");
		MockCell c2 = a2.addChild("B").addChild("C").withReaction();
		
		a1.getPeers().add(new MockPeer(a2, 2));
		
		send(new Path("A", "B", "C"));
		
		assertEquals(1, c2.reaction.executed);
		assertEquals(new Path("°", "A", "B", "C"), c2.parameters.resolvedStack.getLast());
	}
	
	/**
	 * °@1(A) => A@1 should only receive once
	 * 
	 * <pre>
	 * 1 A(2)    2 A(3)    3 A(14)   4 A*
	 * </pre>
	 */
	public void testSearchEachPeerOnlyOnce() {
		root = new MockCell(null, "°");
		MockCell root2 = new MockCell(null, "°");
		MockCell root3 = new MockCell(null, "°");
		MockCell root4 = new MockCell(null, "°");
		
		MockCell a1 = root.addChild("A");
		Cell a2 = root2.addChild("A");
		Cell a3 = root3.addChild("A");
		MockCell a4 = root4.addChild("A").withReaction();
		
		a1.getPeers().add(new MockPeer(a2, 2));
		a2.getPeers().add(new MockPeer(a3, 3));
		a3.getPeers().add(new MockPeer(a1, 0));
		a3.getPeers().add(new MockPeer(a4, 4));
		
		send(new Path("A"));
		
		assertEquals(1, a1.received);
		assertEquals(1, a4.reaction.executed);
	}
	
	/**
	 * A doesn't have a response on its own but inherits B's
	 * 
	 * <pre>
	 * A:B  B*
	 * </pre>
	 */
	public void testInheritResponse() {
		root.addChild("A").setStem(new Path("°", "B"));
		
		MockCell b = root.addChild("B").withReaction();
		
		send(new Path("A"));
		
		assertEquals(1, b.received);
		assertEquals(1, b.reaction.executed);
		assertEquals(new Path("°", "A"), b.parameters.resolvedStack.getLast());
	}
	
	/**
	 * Root sends to A.C.parent should be A and not B
	 * 
	 * <pre>
	 * A*:B   B
	 *        C
	 * </pre>
	 */
	public void testParentOfInheritedCell() {
		MockCell a = root.addChild("A").setStem(new Path("°", "B")).withReaction();
		root.addChild("B").addChild("C");
		
		send(new Path("A", "C", "parent"));
		
		assertEquals(1, a.received);
		assertEquals(new Path("°", "A"), a.parameters.resolvedStack.getLast());
	}
	
	/**
	 * A sends message top B.E.F
	 * 
	 * <pre>
	 * 
	 * 1  ___°2__       2  °
	 *   A       C         C
	 *   B:°CDX  D         D
	 *           X         X
	 *           E         E
	 *                     F*
	 * </pre>
	 */
	public void testSearchPeersOfParentsOfInheritedCell() {
		root.addChild("A")
				.addChild("B").setStem(new Path("°", "C", "D", "X"));
		root.addChild("C")
				.addChild("D")
				.addChild("X")
				.addChild("E");
		
		MockCell root2 = new MockCell(null, "°");
		MockCell f2 = root2.addChild("C")
				.addChild("D")
				.addChild("X")
				.addChild("E")
				.addChild("F").withReaction();
		
		root.addPeer(new MockPeer(root2, 2));
		
		root.getChild("A").send(new Send(new Path("B", "E", "F"), new Path("Hello")),
				new Context(rootContext(), new Path("°", "A"), new Send(new Path(), new Path())));
		
		assertEquals(1, f2.received);
	}
	
	/**
	 * Three cells which for a inheritance circle are questioned for a non-existent child.
	 * 
	 * <pre>
	 * A:B  B:C  C:A
	 * </pre>
	 */
	public void testCircularInheritance() {
		root.addChild("A").setStem(new Path("°", "B"));
		root.addChild("B").setStem(new Path("°", "C"));
		root.addChild("C").setStem(new Path("°", "A"));
		
		send(new Path("A", "X"));
	}
	
	/**
	 * A.C is sent a message
	 * 
	 * <pre>
	 * A:B  B
	 *      C*
	 * </pre>
	 */
	public void testInheritChild() {
		root.addChild("A").setStem(new Path("°", "B"));
		
		MockCell c = root.addChild("B").addChild("C").withReaction();
		
		send(new Path("A", "C"));
		
		assertEquals(1, c.received);
		assertEquals(new Path("°", "A", "C"), c.parameters.resolvedStack.getLast());
	}
	
	/**
	 * Double inheritance: Send message to A
	 * 
	 * <pre>
	 * A:B  B:C  C*
	 * </pre>
	 */
	public void testDoubleInheritance() {
		root.addChild("W")
				.addChild("A").setStem(new Path("°", "B"));
		root.addChild("B").setStem(new Path("°", "C"));
		
		MockCell c = root.addChild("C").withReaction();
		
		send(new Path("W", "A"));
		
		assertEquals(1, c.received);
		assertEquals(new Path("°", "W", "A"), c.parameters.resolvedStack.getLast());
	}
	
	/**
	 * Send message to A.E which is received by °.D.C.E
	 * 
	 * <pre>
	 * A:°BC  B:D  D
	 *             C
	 *             E*
	 * </pre>
	 */
	public void testNestedInheritance() {
		root.addChild("A").setStem(new Path("°", "B", "C"));
		root.addChild("B").setStem(new Path("°", "D"));
		MockCell e = root.addChild("D")
				.addChild("C")
				.addChild("E").withReaction();
		
		send(new Path("A", "E"));
		
		assertEquals(1, e.received);
		assertEquals(new Path("°", "A", "E"), e.parameters.resolvedStack.getLast());
	}
	
	/**
	 * Send message to A.X C is accessed in context °B and thus ^D should resolve to °BD
	 * 
	 * <pre>
	 *         ° ____
	 * 		 / |     \
	 * A:°BCE  B:°K   K
	 * 		   D      C:^D
	 * 		   E
	 *         X
	 * </pre>
	 */
	public void testNestedInheritanceWithParent() {
		root.addChild("A").setStem(new Path("°", "B", "C", "E"));
		MockCell x = root.addChild("B").setStem(new Path("°", "K"))
				.addChild("D")
				.addChild("E")
				.addChild("X").withReaction();
		root.addChild("K")
				.addChild("C").setStem(new Path("parent", "D"));
		
		send(new Path("A", "X"));
		
		assertEquals(1, x.received);
		assertEquals(new Path("°", "A", "X"), x.parameters.resolvedStack.getLast());
	}
	
	/**
	 * °@1(A.B.C.D.P) => A.B@2 should only receive once
	 * 
	 * <pre>
	 * 1     °        2   °        3   °(1)
	 *      /  \         /  \         /    \
	 *   A(23)  K       A    U       A      V
	 *   B(2)   P*      B:U  C       B      D:K
	 *                  C    D       C:V
	 *                       P
	 * </pre>
	 */
	@SuppressWarnings("unused")
	public void testStemInPartialCell() {
		MockCell root2 = new MockCell(null, "°");
		root2.setConnections(mockConnections);
		mockConnections.roots.put(new Host("local", "2"), root2);
		
		MockCell root3 = new MockCell(null, "°");
		root3.setConnections(mockConnections);
		mockConnections.roots.put(new Host("local", "3"), root3);
		
		root3.getPeers().add(new MockPeer(root, 0));
		
		Cell a1 = root.addChild("A");
		Cell b1 = a1.addChild("B");
		
		MockCell p1 = root.addChild("K")
				.addChild("P").withReaction();
		
		Cell a2 = root2.addChild("A");
		Cell b2 = a2.addChild("B").setStem(new Path("°", "U"));
		Cell c2 = b2.addChild("C");
		
		MockCell wrongP = root2.addChild("U")
				.addChild("C")
				.addChild("D")
				.addChild("P").withReaction();
		
		Cell a3 = root3.addChild("A");
		Cell b3 = a3.addChild("B");
		Cell c3 = b3.addChild("C").setStem(new Path("°", "V"));
		
		root3.addChild("V")
				.addChild("D").setStem(new Path("°", "K"));
		
		a1.getPeers().add(new MockPeer(a2, 2));
		a1.getPeers().add(new MockPeer(a3, 3));
		b1.getPeers().add(new MockPeer(b2, 2));
		
		Result r = send(new Path("°", "A", "B", "C", "D", "P"));
		
		assertTrue(r.wasDelivered());
		assertEquals(0, wrongP.received);
		assertEquals(1, p1.received);
		assertEquals(new Path("°", "A", "B", "C", "D", "P"), p1.parameters.resolvedStack.getLast());
	}
	
	/**
	 * B sends message to C which should arrive on host 2
	 * 
	 * <pre>
	 * 1 °(2)       2 °
	 *   A            A
	 *   B            B
	 *                C*
	 * </pre>
	 */
	public void testStartBelowConnectedCell() {
		Cell a1 = root.addChild("A");
		Cell b1 = a1.addChild("B");
		
		MockCell root2 = new MockCell(null, "°");
		MockCell c = root2.addChild("A")
				.addChild("B")
				.addChild("C").withReaction();
		
		root.getPeers().add(new MockPeer(root2, 2));
		
		b1.send(new Send(new Path("C"), new Path()),
				new Context(rootContext(), new Path("°", "B"), new Send(new Path(), new Path())));
		
		assertEquals(1, c.received);
	}
	
	/**
	 * Send to A.B.C.stem.F
	 * 
	 * <pre>
	 * 1 °(2)    2   ___°_______
	 *   A          A     D:°E  E
	 *   B:X        B           F*
	 *              C:°D
	 * </pre>
	 */
	public void testLongestPathIsNotBestHeir() {
		root.addChild("A")
				.addChild("B").setStem(new Path("°", "X"));
		
		MockCell root2 = new MockCell(null, "°");
		mockConnections.roots.put(new Host("local", "2"), root2);
		root2.addChild("A")
				.addChild("B")
				.addChild("C").setStem(new Path("°", "D"));
		
		root.getPeers().add(new MockPeer(root2, 2));
		
		root2.addChild("D").setStem(new Path("°", "E"));
		
		MockCell f = root2.addChild("E")
				.addChild("F").withReaction();
		
		send(new Path("A", "B", "C", "stem", "F"));
		
		assertEquals(1, f.received);
		assertEquals(new Path("°", "D", "F"), f.parameters.resolvedStack.getLast());
	}
	
	/**
	 * Log should be complete representation of search.
	 * 
	 * root on host 1 send message to A.B.C.D.E
	 * 
	 * <pre>
	 * 1 A       2 A(3)    3 A
	 *   B(2)      B         F
	 *   C         C         E
	 *             D:°AF
	 * </pre>
	 */
	public void testLog() {
		MockCell b1 = root.addChild("A")
				.addChild("B");
		b1.addChild("C");
		
		MockCell root2 = new MockCell(null, "°");
		mockConnections.roots.put(new Host("local", "2"), root2);
		MockCell a2 = root2.addChild("A");
		MockCell b2 = a2.addChild("B");
		b2.addChild("C")
				.addChild("D").setStem(new Path("°", "A", "F"));
		
		MockCell root3 = new MockCell(null, "°");
		mockConnections.roots.put(new Host("local", "3"), root3);
		MockCell a3 = root3.addChild("A");
		MockCell e3 = (MockCell) a3.addChild("F")
				.addChild("E").withReaction();
		
		b1.addPeer(new MockPeer(b2, 2));
		a2.addPeer(new MockPeer(a3, 3));
		
		Result res = send(new Path("A", "B", "C", "D", "E"));
		
		assertEquals(1, e3.received);
		
		LinkedList<String> addresses = new LinkedList<String>();
		for (Log.Entry e : res.log)
			if (e.description.equals("dtc"))
				addresses.add(e.address.toString());
		
		LinkedList<String> expected = new LinkedList<String>();
		expected.add("[local:0]°.A.B.C");
		expected.add("[local:2]°.A.B");
		expected.add("[local:2]°.A.B.C.D");
		expected.add("[local:3]°.A");
		expected.add("[local:2]°.A.B.C.D");
		expected.add("[local:2]°.A");
		expected.add("[local:3]°.A");
		expected.add("[local:3]°.A.F");
		expected.add("[local:3]°.A.F.E");
		
		int pos = 0;
		for (String exp : expected) {
			while (pos < addresses.size() && !addresses.get(pos).equals(exp))
				pos++;
			
			if (pos == addresses.size())
				fail("Did not find " + exp);
		}
	}
	
	/**
	 * Send message to A.B.C.D => A@2 should only receive once
	 * 
	 * <pre>
	 * 
	 * 1 A23   2 A    3 A
	 *   B2      B      B
	 *           C      C
	 *                  D*
	 * </pre>
	 */
	public void testDontSearchInCellsWhosePeersHaveBeenSearched() {
		MockCell a1 = root.addChild("A");
		MockCell b1 = a1.addChild("B");
		
		MockCell root2 = new MockCell(null, "°");
		MockCell a2 = root2.addChild("A");
		MockCell b2 = (MockCell) a2.addChild("B");
		b2.addChild("C");
		
		MockCell root3 = new MockCell(null, "°");
		MockCell a3 = root3.addChild("A");
		a3.addChild("B")
				.addChild("C")
				.addChild("D").withReaction();
		
		a1.addPeer(new MockPeer(a2, 2));
		a1.addPeer(new MockPeer(a3, 3));
		b1.addPeer(new MockPeer(b2, 2));
		
		send(new Path("A", "B", "C", "D"));
		
		assertEquals(1, b2.received);
	}
	
	/**
	 * -> A.B.D.E.parent
	 * 
	 * <pre>
	 * A    C
	 * B:C  D*
	 *      E
	 * </pre>
	 */
	public void testParentOfChildOfInheritedCell() {
		root.addChild("A")
				.addChild("B").setStem(new Path("°", "C"));
		root.addChild("C")
				.addChild("D").withReaction()
				.addChild("E");
		
		Result r = send(new Path("°", "A", "B", "D", "E", "parent"));
		
		assertEquals(true, r.wasDelivered());
	}
	
	/**
	 * An inactive cell can't receive itself, can't inherit and can't deliver to any other child
	 * than "cell". But peers of the cell should still be discovered.
	 */
	public void testInactiveCell() {
	// TODO Make test
	}
	
	private Result send(Path receiver) {
		return root.send(new Send(receiver, new Path()), rootContext());
	}
	
	static Context rootContext() {
		return new Context(null, new Path("°"), new Send(new Path("°"), new Path("°", "God")));
	}
	
	static class MockPeer extends Peer {
		
		private Cell connectedWith;
		private int host;
		
		public MockPeer(Cell cell, int host) {
			connectedWith = cell;
			this.host = host;
		}
		
		private void adaptParameters(Parameters p) {
			p.address = new Address("local", String.valueOf(host));
		}
		
		@Override
		public Result deliver(Parameters p) {
			adaptParameters(p);
			return connectedWith.deliver(p);
		}
		
		@Override
		public Result deliverToChild(Parameters p) {
			adaptParameters(p);
			return connectedWith.deliverToChild(p);
		}
		
		@Override
		public Result deliverToStem(Parameters p) {
			adaptParameters(p);
			return connectedWith.deliverToStem(p);
		}
		
		@Override
		public String toString() {
			return host + "@" + connectedWith.toString();
		}
		
		@Override
		public Address getAddress(Parameters p) {
			return new Address("local", String.valueOf(host), connectedWith.getPath());
		}
		
	}
	
	static class MockCell extends Cell {
		
		public int received = 0;
		public Parameters parameters;
		public MockReaction reaction;
		
		public MockCell(Cell parent, String name) {
			super(parent, name);
			setActive(true);
		}
		
		@Override
		public MockCell addChild(String name) {
			return (MockCell) addChild(new MockCell(this, name));
		}
		
		public MockCell setReaction(MockReaction r) {
			super.setReaction(r);
			reaction = r;
			return this;
		}
		
		@Override
		public MockCell setStem(Path stem) {
			return (MockCell) super.setStem(stem);
		}
		
		public MockCell withReaction() {
			return setReaction(new MockReaction());
		}
		
		@Override
		public Result deliverToChild(Parameters p) {
			received++;
			parameters = p;
			
			return super.deliverToChild(p);
		}
		
	}
	
	public static class MockReaction implements NativeReaction {
		
		public int executed = 0;
		public Context context;
		
		@Override
		public void execute(Cell cell, Context context) {
			executed++;
			this.context = context;
		}
		
	}
	
	public static class MockConnections extends Connections {
		
		public Map<Host, Cell> roots = new HashMap<Host, Cell>();
		
		public MockConnections(Cell root) {
			super(root);
			roots.put(root.getAddress(null).host, root);
		}
		
		@Override
		public Peer getPeer(Address address) throws Exception {
			if (roots.containsKey(address.host)) {
				Path path = new Path(address);
				path.removeFirst();
				Cell peer = roots.get(address.host);
				while (!path.isEmpty())
					peer = peer.getChild(path.removeFirst());
				
				return peer;
			}
			
			return super.getPeer(address);
		}
		
	}
	
	@Override
	protected void setUp() throws Exception {
		root = new MockCell(null, "°");
		mockConnections = new MockConnections(root);
		root.setConnections(mockConnections);
	}
}
