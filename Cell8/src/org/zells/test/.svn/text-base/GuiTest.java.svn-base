package org.zells.test;

import java.io.File;
import java.util.*;

import org.rtens.guiTest.TestGui;
import org.zells.*;
import org.zells.gui.*;
import org.zells.test.CellTest.*;

public class GuiTest extends TestGui {
	
	public static void main(String[] args) {
		new GuiTest();
	}
	
	private MockCell root;
	
	private Map<Address, MockCell> roots = new HashMap<Address, MockCell>();
	
	/**
	 * Uses actual Log of message send over more than one host
	 * 
	 * <pre>
	 * A      C
	 * B:°C   D*
	 * 
	 * -> A.B.D
	 * </pre>
	 */
	public void testDeliveryAnalyzer() {
		root.addChild("A").addChild("B").setStem(new Path("°", "C"));
		
		Address addr1 = new Address("Test", "1");
		MockCell root1 = new MockCell(null, "°");
		roots.put(addr1, root1);
		root1.addChild("C").addChild("D").withReaction();
		
		root.addPeer(new MockPeer(root1, addr1));
		
		Result r = root.send(new Mailing(new Path("A", "B", "D"), new Path("Hello")), root.getPath(), new DeliveryId());
		
		show(new DeliveryAnalyzer(new MockPeerModel(new Path("°"), root), r.log));
	}
	
	/**
	 * <pre>
	 * n/a -> A -> self -> °B -> °C
	 * 						  -> °D
	 * 
	 * - First send does not have Messenger
	 * - Last send is delayed 3 seconds
	 * </pre>
	 */
	public void testMessageInspector() throws InterruptedException {
		MockCell a = root.addChild("A").setReaction(new Reaction(
				new Mailing(new Path("°", "B"), new Path("°", "M")),
				new Mailing(new Path("°", "B"), new Path("°", "L"))));
		root.addChild("B").setReaction(new Reaction(
				new Mailing(new Path("°", "C"), new Path("°")),
				new Mailing(new Path("°", "D"), new Path())));
		root.addChild("C").withReaction();
		
		Messenger.listener = new MessengerListener(new MockPeerModel(new Path("°"), root));
		
		new Messenger(a, new Mailing(new Path("self"), new Path()), a.getPath(), new DeliveryId(9)).start();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(2000L);
				} catch (InterruptedException e) {}
				root.addChild("D").withReaction();
			}
		}).start();
		
	}
	
	/**
	 * <pre>
	 * - start/stop SocketServer
	 * </pre>
	 * 
	 * @throws Exception
	 */
	public void testMainView() throws Exception {
		show(new MainView(new File("test"), new String[0]));
	}
	
	public void testSendView() {
		root.addChild("A").setReaction(new NativeReaction() {
			
			@Override
			public void execute(Cell receiver, Path role, Path message, DeliveryId eid) {
				System.out.println("Received " + message + " @" + eid);
			}
		});
		show(new SendView(root));
	}
	
	/**
	 * <pre>
	 * - Double click on root: Host "mock:1" should appear, connection should be drawn between [local:0]°.A and [Test:1]
	 * - Double click on "Test:1": Root without model should appear
	 * - Double click on [Test:1]°: Connection should end in [Test:1]°.A
	 * - Drag host view, connection should adjust
	 * - Browser to °.A.B.C
	 * - °.A should have a stem symbol
	 * - °.B should have a reaction symbol
	 * - Add Child
	 * - Delete Cell
	 * - Add Stem
	 * - Set Stem
	 * - Remove Stem
	 * - Add Reaction
	 * - Set Reaction
	 * - Remove Reaction
	 * - TODO Add Peer via Drag'n'Drop
	 * - TODO Add Peer via Address
	 * - TODO Remove Peer
	 * - TODO Copy via DnD
	 * - TODO Deep Copy via DnD
	 * - TODO Move via DnD
	 * - TODO Real PeerModel
	 */
	public void testBrowserPeers() {
		Address addr1 = new Address("Test", "1");
		MockCell root1 = new MockCell(null, "°");
		roots.put(addr1, root1);
		MockCell a1 = root1.addChild("A");
		root1.addChild("P");
		MockCell b1 = root1.addChild("B");
		MockCell bc1 = b1.addChild("C");
		b1.addChild("D").setActive(false);
		
		Address addr2 = new Address("Test", "2");
		MockCell root2 = new MockCell(null, "°");
		roots.put(addr2, root2);
		MockCell b2 = root2.addChild("B");
		
		MockCell b = root.addChild("B").setStem(new Path("C"));
		b.addChild("C").addPeer(new MockPeer(bc1, addr1));
		root.addChild("A").addPeer(new MockPeer(a1, addr1)).setReaction(new Reaction());
		
		b.addPeer(new MockPeer(b2, addr2));
		b2.addPeer(new MockPeer(b1, addr1));
		
		show(new Browser(new MockPeerModel(root.getPath(), root)));
	}
	
	/**
	 * <pre>
	 * - Double click "localhost:0": Root should appear 
	 * - Double click on root: Children A, B, and C should expand side by side connected with lines 
	 * - Double click on C: Children A and D should expand, rest of tree should adapt 
	 * - Double click on D: X should expand - Collapsing/expanding root restores former state 
	 * - Collapsing cell with shift collapses all children
	 * - Expanding with shift expands all children
	 */
	public void testBrowserTree() {
		root.addChild("A").setStem(new Path("°", "B", "D"));
		root.addChild("B").setStem(new Path("°", "C")).setReaction(
				new Reaction(new Mailing(new Path("Hello", "You"), new Path("Wie.Gehts", "So"))));
		MockCell c = root.addChild("C");
		c.addChild("A");
		c.addChild("D").addChild("X").setReaction(new Reaction());
		
		show(new Browser(new MockPeerModel(root.getPath(), root)));
	}
	
	@Override
	protected void setUp() throws Exception {
		root = new MockCell(null, "°");
		roots.put(Server.localAddress, root);
	}
	
	@Override
	protected void tearDown() throws Exception {
		TestLoader.recursiveDelete(new File("test"));
	}
	
	class MockPeerModel extends PeerModel {
		
		private Cell cell;
		
		public MockPeerModel(Path path, Cell cell) {
			super(cell.getRoot(), path);
			this.cell = cell;
			if (cell == null) throw new NullPointerException();
		}
		
		@Override
		public PeerModel getPeerModel(Address to) {
			Path newPath = new Path(getPath(), new Path("cell", "Peers", to.toString()));
			return new MockPeerModel(newPath, resolve(roots.get(to), cell.getPath().subPath(1, 0)));
		}
		
		@Override
		public PeerModel getChildModel(String name) {
			return new MockPeerModel(new Path(getPath(), new Path(name)), cell.getChild(name));
		}
		
		private Cell resolve(Cell cell, Path path) {
			if (path.isEmpty()) return cell;
			String name = path.removeFirst();
			return resolve(cell.getChild(name), path);
		}
		
		@Override
		public List<String> getChildren() {
			List<String> children = new LinkedList<String>();
			for (Cell child : cell.getChildren())
				children.add(child.getName());
			return children;
		}
		
		@Override
		public List<Address> getPeers() {
			List<Address> addrs = new LinkedList<Address>();
			for (Peer peer : cell.getPeers())
				addrs.add(peer.getAddress());
			return addrs;
		}
		
		@Override
		public boolean isActive() {
			return cell.isActive();
		}
		
		@Override
		public Path getStem() {
			return cell.getStem();
		}
		
		@Override
		public Reaction getReaction() {
			NativeReaction r = cell.getReaction();
			if (r instanceof Reaction) return (Reaction) r;
			return null;
		}
		
		@Override
		public void setReaction(Reaction reaction) {
			cell.setReaction(reaction);
		}
		
		@Override
		public void addChild(String name) {
			cell.addChild(name);
		}
		
		@Override
		public void setStem(Path stem) {
			cell.setStem(stem);
		}
		
		@Override
		public void removeChild(String name) {
			cell.removeChild(name);
		}
		
	}
	
}
