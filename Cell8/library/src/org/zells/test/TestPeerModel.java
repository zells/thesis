package org.zells.test;

import java.util.List;

import org.zells.*;
import org.zells.gui.PeerModel;

public class TestPeerModel extends LibraryTest {
	
	private Cell a;
	private PeerModel model;
	
	public void testGetChildren() {
		a.addChild("A");
		a.addChild("B");
		
		List<String> children = model.getChildren();
		
		assertEquals(2, children.size());
		assertEquals("A", children.get(0));
	}
	
	public void testActive() {
		assertEquals(false, model.isActive());
		
		a.setActive(true);
		model.clearCache();
		
		assertEquals(true, model.isActive());
	}
	
	public void testGetStem() {
		assertEquals(null, model.getStem());
		
		a.setStem(p("My.Stem"));
		model.clearCache();
		
		assertEquals(a.getStem(), model.getStem());
	}
	
	public void testGetReaction() {
		assertNull(model.getReaction());
		
		Reaction reaction = new Reaction(m("John", "Hello"), m("Joe", "Bye"));
		a.setReaction(reaction);
		model.clearCache();
		
		assertEquals(reaction.mailings, model.getReaction().mailings);
	}
	
	public void testGetPeers() {
		Address ad1 = new Address("self", "me");
		Address ad2 = new Address("self", "you");
		a.addPeer(new MockPeer(a, ad1));
		a.addPeer(new MockPeer(a, ad2));
		
		List<Address> addresses = model.getPeers();
		
		assertEquals(2, addresses.size());
		assertEquals(ad1, addresses.get(0));
		assertEquals(ad2, addresses.get(1));
	}
	
	public void testSetReaction() {
		Reaction reaction = new Reaction(m("John", "Hello"), m("Joe", "Bye"));
		model.setReaction(reaction);
		assertEquals(reaction, a.getReaction());

		Reaction nReaction = new Reaction(m("Pete", "Whatup"));
		model.setReaction(nReaction);
		assertEquals(nReaction, a.getReaction());
	}
	
	public void testAddChild() {
		model.addChild("NewChild");
		assertNotNull(a.getChild("NewChild"));
	}
	
	public void testRemoveChild() {
		a.addChild("MyChild");
		model.removeChild("MyChild");
		assertNull(a.getChild("MyChild"));
	}
	
	public void testSetStem() {
		model.setStem(new Path("My", "Stem"));
		assertEquals(new Path("My", "Stem"), a.getStem());
		
		model.setStem(new Path("Other", "Stem"));
		assertEquals(new Path("Other", "Stem"), a.getStem());
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		a = root.addChild("A");
		model = PeerModel.getRootModel(root).getChildModel("A");
	}
}
