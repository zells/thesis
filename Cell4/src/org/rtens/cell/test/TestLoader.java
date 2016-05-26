package org.rtens.cell.test;

import java.io.*;
import java.util.*;

import junit.framework.TestCase;

import org.rtens.cell.*;
import org.rtens.cell.connection.SocketClient;
import org.rtens.cell.glue.*;

public class TestLoader extends TestCase {
	
	private File testFolder;
	private File rootFolder;
	private Cell rootCell;
	
	public void testLoadingEmptyCell() throws Exception {
		write(new File(rootFolder, "A.cell"),
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?><cell/>");
		
		CellLoader loader = new CellLoader(testFolder);
		LinkedList<Cell> children = loader.getChildren(rootCell);
		
		assertEquals(1, children.size());
		Cell a = children.getFirst();
		assertEquals("A", a.getName());
	}
	
	public void testLoadingCellWithStemAndReaction() throws Exception {
		write(
				new File(rootFolder, "A.cell"),
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<cell>"
						+ "	<stem>My.Stem.\"Is.This\"</stem>"
						+ "	<reaction>	 \n  first.\"receiver with spaces\" \t\n first.message    other.receiver \t other.message     </reaction>"
						+ "</cell>");
		
		CellLoader loader = new CellLoader(testFolder);
		LinkedList<Cell> children = loader.getChildren(rootCell);
		
		assertEquals(1, children.size());
		Cell a = children.getFirst();
		
		assertEquals("A", a.getName());
		assertEquals(new Path("My", "Stem", "Is.This"), a.getStem());
		
		Reaction reaction = (Reaction) a.getReaction();
		assertEquals(2, reaction.getSends().size());
		
		Send send = reaction.getSends().get(0);
		assertEquals(new Path("first", "receiver with spaces"), send.getReceiver());
		assertEquals(new Path("first", "message"), send.getMessage());
		
		send = reaction.getSends().get(1);
		assertEquals(new Path("other", "receiver"), send.getReceiver());
		assertEquals(new Path("other", "message"), send.getMessage());
	}
	
	public void testLoadingCellWithPeers() throws Exception {
		write(
				new File(rootFolder, "A.cell"),
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
						"<cell>" +
						"	<peer><network>Test</network><host>42</host></peer>" +
						"	<peer><network>Socket</network><host>example.com:42</host></peer>" +
						"</cell>");
		
		MockCellLoader loader = new MockCellLoader(testFolder);
		LinkedList<Cell> children = loader.getChildren(rootCell);
		
		assertEquals(1, children.size());
		Cell a = children.getFirst();
		
		LinkedList<Peer> peers = a.getPeers();
		assertEquals(2, peers.size());
		
		assertEquals(MockPeer.class, peers.getFirst().getClass());
		assertEquals(42, ((MockPeer) peers.getFirst()).param);
		assertEquals(SocketClient.class, peers.get(1).getClass());
	}
	
	public void testNativeCell() throws Exception {
		File folder = new File(rootFolder, "A/B/C");
		folder.mkdirs();
		
		File file = new File(folder, "D.cell");
		String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><cell native=\"true\" />";
		
		write(file, content);
		
		Cell c = rootCell.addChild("A")
				.addChild("B")
				.addChild("C");
		c.addChild("D");
		
		MockCellLoader loader = new MockCellLoader(testFolder);
		
		LinkedList<Cell> children = loader.getChildren(c);
		
		assertEquals(1, children.size());
		assertEquals("D", children.getFirst().getName());
		assertEquals("org.rtens.cell.library.A.B.C.DCell", loader.requestedClassName);
	}
	
	public void testSave() throws Exception {
		
		Cell test = rootCell.addChild("test");
		
		Reaction reaction = new Reaction();
		reaction.add(new Send(new Path("My", "Receiver"), new Path("My", "Message")));
		reaction.add(new Send(new Path("Other", "Receiver"), new Path("Other", "Message")));
		reaction.add(new Send(new Path("Third", "Receiver"), new Path("Third", "Message")));
		test.setReaction(reaction);
		
		test.setStem(new Path("°", "MyStem"));
		
		test.getPeers().add(new MockPeer(new Address("Test", "42", test.getPath())));
		test.getPeers().add(new SocketClient(rootCell, new Address("Socket", "example.com:80", test.getPath())));
		
		CellLoader loader = new CellLoader(testFolder);
		loader.save(test);
		
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
						"<cell>\r\n" +
						"  <stem>°.MyStem</stem>\r\n" +
						"  <reaction>My.Receiver       My.Message\r\n" +
						"	Other.Receiver    Other.Message\r\n" +
						"	Third.Receiver    Third.Message</reaction>\r\n" +
						"  <peer>\r\n" +
						"    <network>Test</network>\r\n" +
						"    <host>42</host>\r\n" +
						"  </peer>\r\n" +
						"  <peer>\r\n" +
						"    <network>Socket</network>\r\n" +
						"    <host>example.com:80</host>\r\n" +
						"  </peer>\r\n" +
						"</cell>\r\n",
				read(new File(rootFolder, "test.cell")));
	}
	
	public void testReactionFormat() throws Exception {
		Reaction r = new Reaction();
		r.add(new Send(new Path("Hello", "You"), new Path("Should", "Be", "Easy")));
		r.add(new Send(new Path("Something", "With Spaces"), new Path("In Message", "As \"Well\"")));
		
		String formatted = ReactionFormat.format(r);
		
		assertEquals(r, ReactionFormat.parse(formatted));
		
		formatted =
				"Hello.You					Should.Be.Easy\t" +
						"Something.\"With Spaces\"	\"In Message\".\"As \\\"Well\\\"\"";
		assertEquals(r, ReactionFormat.parse(formatted));
	}
	
	public void testAddAndRemoveChildren() throws Exception {
		write(new File(testFolder, "°.cell"), "<cell/>");
		rootCell = new CellLoader(testFolder).getChild(null, "°");
		
		rootCell.addChild("T");
		rootCell.addChild("Z").addChild("U");
		
		assertTrue(new File(rootFolder, "T.cell").exists());
		assertTrue(new File(rootFolder, "Z.cell").exists());
		assertTrue(new File(rootFolder, "Z").exists());
		assertTrue(new File(rootFolder, "Z/U.cell").exists());
		
		rootCell.removeChild("Z", null);
		assertFalse(new File(rootFolder, "Z").exists());
		assertFalse(new File(rootFolder, "Z.cell").exists());
		
		rootCell.removeChild("T", null);
		assertFalse(new File(rootFolder, "T.cell").exists());
	}
	
	public void testNativeCellsWithStemAndReaction() throws Exception {
		File folder = new File(rootFolder, "A/B/C");
		folder.mkdirs();
		
		File dFile = new File(folder, "D.cell");
		write(dFile, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<cell native=\"true\">"
				+ "	 <stem>My.Stem</stem>"
				+ "	 <reaction>first.receiver first.message</reaction>"
				+ "</cell>");
		
		File dFolder = new File(rootFolder, "A/B/C/D");
		dFolder.mkdir();
		write(new File(dFolder, "E.cell"), "<?xml version=\"1.0\" encoding=\"UTF-8\"?><cell />");
		
		Cell c = rootCell.addChild("A")
				.addChild("B")
				.addChild("C");
		
		MockCellLoader loader = new MockCellLoader(testFolder);
		
		LinkedList<Cell> children = loader.getChildren(c);
		
		assertEquals(1, children.size());
		assertEquals("D", children.getFirst().getName());
		assertEquals("org.rtens.cell.library.A.B.C.DCell", loader.requestedClassName);
		
		Cell d = children.getFirst();
		assertEquals(new Path("My", "Stem"), d.getStem());
		assertTrue(d.isActive());
		assertEquals(1, ((Reaction) d.getReaction()).getSends().size());
		
		assertNotNull(d.getChild("E"));
		
		d.setStem(new Path("Other"));
		d.setReaction(null);
		
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
						"<cell native=\"true\">\r\n" +
						"  <stem>Other</stem>\r\n" +
						"</cell>\r\n",
				read(dFile));
	}
	
	private class MockCellLoader extends CellLoader {
		
		public String requestedClassName;
		
		public MockCellLoader(File rootFolder) {
			super(rootFolder);
		}
		
		@Override
		protected Cell getNativeInstance(String className, Cell parent, String name) {
			requestedClassName = className;
			return new TestCell(parent, name);
		}
		
		@Override
		protected Reaction getNativeReactionInstance(String className) {
			requestedClassName = className;
			return null;
		}
		
		@Override
		protected Peer getPeerInstance(Address address, Cell parent) throws Exception {
			if (address.host.network.equals("Test")) {
				return new MockPeer(address);
			} else if (address.host.network.equals("Socket")) {
				return new SocketClient(rootCell, address);
			}
			return null;
		}
	}
	
	private static class TestCell extends Cell {
		
		public TestCell(Cell parent, String name) {
			super(parent, name);
		}
		
	}
	
	private static class MockPeer extends Peer {
		
		public int param;
		private Address address;
		
		public MockPeer(Address address) {
			this.address = address;
			param = Integer.parseInt(address.host.hostAddress);
		}
		
		@Override
		public Result deliver(Parameters p) {
			return null;
		}
		
		@Override
		public Result deliverToChild(Parameters p) {
			return null;
		}
		
		@Override
		public Result deliverToStem(Parameters p) {
			return null;
		}
		
		@Override
		public Address getAddress(Parameters p) {
			return address;
		}
		
	}
	
	private void write(File file, String content) throws Exception {
		OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(file), "UTF8");
		w.write(content);
		w.close();
	}
	
	private String read(File file) throws Exception {
		Scanner s = new Scanner(new InputStreamReader(new FileInputStream(file), "UTF8"))
				.useDelimiter("\\Z");
		String content = s.next();
		s.close();
		return content;
	}
	
	@Override
	protected void setUp() throws Exception {
		testFolder = new File("test");
		rootFolder = new File(testFolder, "°");
		rootFolder.mkdirs();
		rootCell = new Cell(null, "°");
	}
	
	@Override
	protected void tearDown() throws Exception {
		recursiveDelete(testFolder);
	}
	
	private void recursiveDelete(File folder) {
		for (File f : folder.listFiles()) {
			if (f.isDirectory())
				recursiveDelete(f);
			else f.delete();
		}
		
		folder.delete();
	}
	
}
