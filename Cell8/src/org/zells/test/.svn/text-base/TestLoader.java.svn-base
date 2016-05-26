package org.zells.test;

import java.io.*;
import java.util.*;

import org.zells.*;
import org.zells.glue.*;

public class TestLoader extends CellTest {
	
	private File testFolder;
	private Cell root;
	private File rootFolder;
	
	public void testLoadingEmptyCell() throws Exception {
		write(new File(rootFolder, "A.cell"),
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?><cell/>");
		
		CellLoader loader = new CellLoader(testFolder);
		LinkedList<Cell> children = loader.getChildren(root);
		
		assertNotNull(children);
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
						+ "	<reaction>	 \n  first.\"receiver with spaces\" \t first.message   \n \t other.receiver \t other.message  \n last.\"Receiver.with.Dots\"  And.HisMessage \n\n  </reaction>"
						+ "</cell>");
		
		CellLoader loader = new CellLoader(testFolder);
		LinkedList<Cell> children = loader.getChildren(root);
		
		assertEquals(1, children.size());
		Cell a = children.getFirst();
		
		assertEquals("A", a.getName());
		assertEquals(new Path("My", "Stem", "Is.This"), a.getStem());
		
		assertNotNull(a.getReaction());
		Reaction reaction = (Reaction) a.getReaction();
		assertEquals(3, reaction.mailings.size());
		
		Mailing mailing = reaction.mailings.get(0);
		assertEquals(new Path("first", "receiver with spaces"), mailing.receiver);
		assertEquals(new Path("first", "message"), mailing.message);
		
		mailing = reaction.mailings.get(1);
		assertEquals(new Path("other", "receiver"), mailing.receiver);
		assertEquals(new Path("other", "message"), mailing.message);
		
		mailing = reaction.mailings.get(2);
		assertEquals(new Path("last", "Receiver.with.Dots"), mailing.receiver);
		assertEquals(new Path("And", "HisMessage"), mailing.message);
	}
	
	public void testLoadingCellWithPeers() throws Exception {
		write(
				new File(rootFolder, "A.cell"),
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
						"<cell>" +
						"	<peer><network>Test</network><host>42</host></peer>" +
						"	<peer><network>Test</network><host>21</host></peer>" +
						"</cell>");
		
		MockCellLoader loader = new MockCellLoader(testFolder);
		LinkedList<Cell> children = loader.getChildren(root);
		
		assertEquals(1, children.size());
		Cell a = children.getFirst();
		
		List<Peer> peers = a.getPeers();
		assertEquals(2, peers.size());
		
		assertEquals(MockPeer.class, peers.get(0).getClass());
		assertEquals("Test", ((MockPeer) peers.get(0)).getAddress().network);
		assertEquals("42", ((MockPeer) peers.get(0)).getAddress().host);
		assertEquals(MockPeer.class, peers.get(1).getClass());
		assertEquals("Test", ((MockPeer) peers.get(1)).getAddress().network);
		assertEquals("21", ((MockPeer) peers.get(1)).getAddress().host);
	}
	
	public void testNativeCell() throws Exception {
		File folder = new File(rootFolder, "A/B/C");
		folder.mkdirs();
		
		File file = new File(folder, "D.cell");
		String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><cell native=\"true\" />";
		
		write(file, content);
		
		Cell c = root.addChild("A")
				.addChild("B")
				.addChild("C");
		c.addChild("D");
		
		MockCellLoader loader = new MockCellLoader(testFolder);
		
		LinkedList<Cell> children = loader.getChildren(c);
		
		assertEquals(1, children.size());
		assertEquals("D", children.getFirst().getName());
		assertEquals("org.zells.library.A.B.C.DCell", loader.requestedClassName);
	}
	
	public void testSave() throws Exception {
		
		Cell test = root.addChild("test");
		
		Reaction reaction = new Reaction();
		reaction.mailings.add(new Mailing(new Path("My", "Receiver"), new Path("My", "Message")));
		reaction.mailings.add(new Mailing(new Path("Other", "Receiver"), new Path("Other", "Message.Dot")));
		reaction.mailings.add(new Mailing(new Path("Third", "Receiver"), new Path("Third", "Message")));
		test.setReaction(reaction);
		
		test.setStem(new Path("°", "My Stem"));
		
		test.getPeers().add(new MockPeer(test, new Address("Test", "42")));
		test.getPeers().add(new MockPeer(test, new Address("Test", "21")));
		
		CellLoader loader = new CellLoader(testFolder);
		loader.save(test);
		
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
						"<cell>\r\n" +
						"  <stem>°.\"My Stem\"</stem>\r\n" +
						"  <reaction>My.Receiver       My.Message\r\n" +
						"	Other.Receiver    Other.\"Message.Dot\"\r\n" +
						"	Third.Receiver    Third.Message</reaction>\r\n" +
						"  <peer>\r\n" +
						"    <network>Test</network>\r\n" +
						"    <host>42</host>\r\n" +
						"  </peer>\r\n" +
						"  <peer>\r\n" +
						"    <network>Test</network>\r\n" +
						"    <host>21</host>\r\n" +
						"  </peer>\r\n" +
						"</cell>\r\n",
				read(new File(rootFolder, "test.cell")));
	}
	
	public void testReactionFormat() throws Exception {
		Reaction r = new Reaction();
		r.mailings.add(new Mailing(new Path("First", "Me"), new Path("Then", "You")));
		r.mailings.add(new Mailing(new Path("Hello", "You"), new Path("Should", "Be", "Easy")));
		r.mailings.add(new Mailing(new Path("Something", "With Spaces"), new Path("In Message", "As \"Well\"")));
		
		String formatted = ReactionFormat.format(r);
		
		assertEquals(r, ReactionFormat.parse(formatted));
		
		formatted =
				"First.Me   Then.You   \r\n  \t \r\n" +
						"Hello.You					Should.Be.Easy\r\n" +
						"    Something.\"With Spaces\"	\"In Message\".\"As \\\"Well\\\"\"";
		assertEquals(r, ReactionFormat.parse(formatted));
	}
	
	public void testAddAndRemoveChildren() throws Exception {
		write(new File(testFolder, "°.cell"), "<cell/>");
		root = new CellLoader(testFolder).getChild(null, "°");
		
		root.addChild("T");
		root.addChild("Z").addChild("U");
		
		assertTrue(new File(rootFolder, "T.cell").exists());
		assertTrue(new File(rootFolder, "Z.cell").exists());
		assertTrue(new File(rootFolder, "Z").exists());
		assertTrue(new File(rootFolder, "Z/U.cell").exists());
		
		root.removeChild("Z");
		assertFalse(new File(rootFolder, "Z").exists());
		assertFalse(new File(rootFolder, "Z.cell").exists());
		
		root.removeChild("T");
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
		
		Cell c = root
				.addChild("A").setActive(true)
				.addChild("B").setActive(true)
				.addChild("C").setActive(true);
		
		MockCellLoader loader = new MockCellLoader(testFolder);
		
		LinkedList<Cell> children = loader.getChildren(c);
		
		assertEquals(1, children.size());
		assertEquals("D", children.getFirst().getName());
		assertEquals("org.zells.library.A.B.C.DCell", loader.requestedClassName);
		
		Cell d = children.getFirst();
		assertEquals(new Path("My", "Stem"), d.getStem());
		assertTrue(d.isActive());
		assertEquals(1, ((Reaction) d.getReaction()).mailings.size());
		
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

	public void testSaveExecutionCell() throws Exception {
		
		Cell test = root.addChild(new Execution(root, "#Test", new Path("My", "Message")));
		test.setStem(new Path("°", "My Stem"));
		
		CellLoader loader = new CellLoader(testFolder);
		loader.save(test);
		
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
						"<execution>\r\n" +
						"  <message>My.Message</message>\r\n" +
						"  <stem>°.\"My Stem\"</stem>\r\n" +
						"</execution>\r\n",
				read(new File(rootFolder, "#Test.cell")));
	}
	
	public void testLoadExecutionCell() throws Exception {
		write(
				new File(rootFolder, "A.cell"),
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
						"<execution>" +
						"	<message>°.The.Message</message>" +
						"	<stem>My.Stem.</stem>>" +
						"</execution>");
		
		MockCellLoader loader = new MockCellLoader(testFolder);
		LinkedList<Cell> children = loader.getChildren(root);
		
		assertEquals(1, children.size());
		Cell a = children.getFirst();
		
		assertTrue(a instanceof Execution);
		assertEquals(new Path("°", "The", "Message"), ((Execution)a).getMessage());
		assertEquals(new Path("My", "Stem"), a.getStem());
	}
	
	@Override
	protected void setUp() throws Exception {
		testFolder = new File("test");
		rootFolder = new File(testFolder, "°");
		rootFolder.mkdirs();
		root = new Cell(null, "°");
		root.setActive(true);
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
	protected void tearDown() throws Exception {
		recursiveDelete(testFolder);
	}
	
	public static void recursiveDelete(File folder) {
		if (!folder.exists()) return;
		for (File f : folder.listFiles()) {
			if (f.isDirectory())
				recursiveDelete(f);
			else f.delete();
		}
		
		folder.delete();
	}
	
	private class MockCellLoader extends CellLoader {
		
		public String requestedClassName;
		
		public MockCellLoader(File rootFolder) {
			super(rootFolder);
		}
		
		@Override
		protected Cell getNativeCellInstance(String className, Cell parent, String name) {
			requestedClassName = className;
			return new MockCell(parent, name);
		}
		
		@Override
		protected Reaction getNativeReactionInstance(String className) {
			requestedClassName = className;
			return null;
		}
		
		@Override
		protected Peer getPeerInstance(Cell child, Address address) throws Exception {
			return new MockPeer(child, address);
		}
	}
}
