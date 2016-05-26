package org.rtens.cell.test;

import java.io.File;
import java.util.*;

import org.rtens.cell.*;
import org.rtens.cell.glue.*;
import org.rtens.cell.gui.*;
import org.rtens.cell.gui.browser.*;
import org.rtens.cell.test.TestReceive.*;
import org.rtens.guiTest.TestGui;

public class GuiTest extends TestGui {
	
	public static void main(String[] args) {
		new GuiTest();
	}
	
	private MockCell root;
	
	private Map<Host, Cell> roots;
	
	/**
	 * The sending form provides two text fields for paths of receiver and message and a button to
	 * execute the send.
	 * 
	 * Send a message to "A.B" and it will be printed in the console.
	 * 
	 * @throws InterruptedException
	 */
	public void testSendingForm() {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				MockCell b = (MockCell) root.addChild("A").addChild("B").withReaction();
				
				while (b.received == 0)
					;
				
				System.out.println("Received: " + b.parameters.send.getMessage());
			}
		}).start();
		
		show(new SendView(root));
	}
	
	public void testSimpleBrowser() {
		root.addChild("A").setStem(new Path("°", "B", "D"));
		root.addChild("B").setStem(new Path("°", "C"));
		root.addChild("C").addChild("D").addChild("X").setReaction(new Reaction());
		show(new MockBrowser());
	}
	
	/**
	 * <pre>
	 * 1   °        2 °1   3 °
	 *   /   \        |      |
	 *   A     B      A      B
	 *  /|\    |\     |      |
	 * A B C2  A B3   C      B1
	 *    /|
	 *   A B
	 * </pre>
	 * 
	 * @throws Exception
	 */
	public void testBrowser() throws Exception {
		Cell a = root.addChild("A");
		Cell b = root.addChild("B");
		
		a.addChild("A");
		a.addChild("B");
		Cell ac = a.addChild("C");
		Cell aca = ac.addChild("A");
		ac.addChild("B");
		
		b.addChild("A");
		Cell bb = b.addChild("B").setActive(false);
		
		Cell root2 = new MockCell(null, "°");
		roots.put(new Host("local", "2"), root2);
		Cell ac2 = root2.addChild("A").addChild("C");
		
		ac.getPeers().add(new MockPeer(ac2, 2));
		root2.getPeers().add(new MockPeer(root, 0));
		
		Cell root3 = new Cell(null, "°");
		roots.put(new Host("local", "3"), root3);
		Cell bb3 = root3.addChild("B").addChild("B");
		
		bb.getPeers().add(new MockPeer(bb3, 3));
		bb3.getPeers().add(new MockPeer(bb, 0));
		aca.addPeer(new Peer() {
			
			@Override
			public Address getAddress(Parameters p) {
				return new Address("nirvana", "", new Path("°", "I", "Dont", "Exist"));
			}
			
			@Override
			public Result deliverToStem(Parameters p) {
				return null;
			}
			
			@Override
			public Result deliverToChild(Parameters p) {
				return null;
			}
			
			@Override
			public Result deliver(Parameters p) {
				return null;
			}
		});
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("Hostname", "localhost");
		params.put("Port", "4242");
		
		Server socketServer = Connections.getServer("Socket", root);
		socketServer.setParameters(params);
		socketServer.start();
		
		show(new MockBrowser());
	}
	
	public void testReactionEditor() {
		root.addChild("A");
		
		Reaction r = new Reaction();
		r.add(new Send(new Path("°", "A"), new Path("Hi")));
		
		Browser b = new MockBrowser();
		show(b);
		show(new ReactionEditor(b, new Path("°"), r));
	}
	
	/**
	 * <pre>
	 * 1  °23     2     °1      3   °1
	 *   A23 K      A       U      A     V
	 *   B2  P      B:°U    C      B     D:°K
	 *              C       D      C:°V
	 *                      P
	 * </pre>
	 */
	public void testDeliveryAnalizer() throws Exception {
		Cell root = new CellLoader(new File("prophet/library")).getChild(null, "°");
		root.getChildren();
		root.setLoader(null);
		
		MockCell root2 = new MockCell(null, "°");
		roots.put(new Host("local", "2"), root2);
		Cell root3 = new Cell(null, "°").setActive(true);
		roots.put(new Host("local", "3"), root3);
		
		root3.addPeer(new MockPeer(root, 0));
		root2.addPeer(new MockPeer(root, 0));
		
		root.addPeer(new MockPeer(root2, 2));
		root.addPeer(new MockPeer(root3, 3));
		
		Cell a1 = root.addChild("A").setActive(true);
		Cell b1 = a1.addChild("B").setActive(true);
		
		MockCell p1 = (MockCell) root.addChild(new MockCell(root, "K")).setActive(true)
				.addChild("P");
		p1.withReaction();
		
		Cell a2 = root2.addChild("A").setActive(true);
		Cell b2 = a2.addChild("B").setStem(new Path("°", "U")).setActive(true);
		b2.addChild("C").setActive(true);
		
		Reaction b2r = new Reaction();
		b2r.add(new Send(new Path("Hallo", "Hier"), new Path("Bin", "Ich")));
		b2r.add(new Send(new Path("Wie Gehts"), new Path("Denn", "So", "1.2.3.4\"5\"")));
		b2.setReaction(b2r);
		
		root2.addChild("U")
				.addChild("C")
				.addChild("D")
				.addChild("P").withReaction();
		
		Cell a3 = root3.addChild("A").setActive(true);
		Cell b3 = a3.addChild("B").setActive(true);
		b3.addChild("C").setStem(new Path("°", "V")).setActive(true);
		
		root3.addChild("V").setActive(true)
				.addChild("D").setStem(new Path("°", "K")).setActive(true);
		
		a1.getPeers().add(new MockPeer(a2, 2));
		a1.getPeers().add(new MockPeer(a3, 3));
		b1.getPeers().add(new MockPeer(b2, 2));
		
		Result r = root.send(new Send(new Path("A", "B", "C", "D", "P"), new Path()),
				TestReceive.rootContext());
		
		show(new DeliveryAnalyzer(root, r.log));
	}
	
	public void testMessageInspector() throws Exception {
		Cell root = new CellLoader(new File("prophet/library")).getChild(null, "°");
		root.getChildren();
		root.setLoader(null);
		root.getPeers().clear();
		
		final MockCell a = (MockCell) root.addChild(new MockCell(root, "A"));
		
		Messenger.listener = new MessengerListener(root);
		
		Cell s = a.addChild("S");
		new Messenger(new Send(new Path("°", "A", "B"), new Path("Hallo")), s,
				createContext(s("1.R", "1.M"), s("2.R", "2.M"), s("3.R", "3.M"))).start();
		
		new Messenger(new Send(new Path("°", "A", "C"), new Path("Hallo")), s,
				createContext(s("1.R", "1.M"), s("2.R2", "2.M2"), s("3.R", "3.M"))).start();
		
		new Messenger(new Send(new Path("°", "A", "B"), new Path("Hi")), s,
				createContext(s("1.R", "1.M"), s("2.R2", "2.M2"), s("3.R", "3.M"))).start();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(3000L);
				} catch (InterruptedException e) {}
				
				a.addChild("B").withReaction();
			}
		}).start();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(1000L);
				} catch (InterruptedException e) {}
				
				a.addChild("C").withReaction();
			}
		}).start();
	}
	
	private Context createContext(Send... sends) {
		Context c = null;
		for (Send send : sends) {
			c = new Context(c, send.getReceiver(), send);
		}
		return c;
	}
	
	private Send s(String receiver, String message) {
		try {
			return new Send(PathFormat.parse(receiver), PathFormat.parse(message));
		} catch (Exception e) {
			return null;
		}
	}
	
	private Cell getCell(Address address) {
		for (Host host : roots.keySet()) {
			if (host.equals(address.host)) {
				
				Path path = new Path(address);
				path.removeFirst();
				
				Cell c = roots.get(host);
				
				while (c != null && !path.isEmpty())
					c = c.getChild(path.removeFirst());
				
				return c;
			}
		}
		System.err.println("Could not find " + address);
		return null;
	}
	
	private class MockBrowser extends Browser {
		
		public MockBrowser() {
			super(root);
		}
		
		@Override
		public CellConnector getCellConnector(Address address) {
			return new MockCellConnector(root, address);
		}
		
	}
	
	private class MockCellConnector extends CellConnector {
		
		Cell cell;
		
		public MockCellConnector(Cell root, Address address) {
			super(root, address);
			cell = getCell(address);
			
			if (cell == null)
				cell = new NoCell(null, address.getLast());
		}
		
		private class NoCell extends Cell {
			
			public NoCell(Cell parent, String name) {
				super(parent, name);
			}
			
		}
		
		@Override
		public boolean isConnected() {
			return !(cell instanceof NoCell);
		}
		
		@Override
		public String getName() {
			return cell.getName();
		}
		
		@Override
		public void setName(String name) {
			cell.setName(name);
		}
		
		@Override
		public boolean isActive() {
			return cell.isActive();
		}
		
		@Override
		public void setActive(boolean to) {
			cell.setActive(to);
		}
		
		@Override
		public LinkedList<String> getChildren() {
			LinkedList<String> names = new LinkedList<String>();
			for (Peer child : cell.getChildren())
				names.add(child.getName());
			return names;
		}
		
		@Override
		public boolean hasChild(String name) {
			return cell.getChild(name) != null;
		}
		
		@Override
		public void addChild(String name) {
			cell.addChild(name);
		}
		
		@Override
		public void removeChild(String name) {
			cell.removeChild(name, null);
		}
		
		@Override
		public Path getStem() {
			return cell.getStem();
		}
		
		@Override
		public void setStem(Path stem) {
			cell.setStem(stem);
		}
		
		@Override
		public Reaction getReaction() {
			try {
				return (Reaction) cell.getReaction();
			} catch (Exception e) {
				return null;
			}
		}
		
		@Override
		public void setReaction(Reaction reaction) {
			cell.setReaction(reaction);
		}
		
		@Override
		public LinkedList<Address> getPeers() {
			LinkedList<Address> addresses = new LinkedList<Address>();
			for (Peer p : cell.getPeers())
				addresses.add(p.getAddress(null));
			return addresses;
		}
		
		@Override
		public void addPeer(Address address) {
			cell.addPeer(new MockPeer(getCell(address), Integer.parseInt(address.host.hostAddress)));
		}
		
		@Override
		public void removePeer(Address address) {
			cell.removePeer(address);
		}
		
	}
	
	@Override
	protected void setUp() throws Exception {
		root = new MockCell(null, "°");
		
		roots = new HashMap<Host, Cell>();
		roots.put(new Host("local", "0"), root);
	}
	
}
