package org.zells.test;

public class TestLibraryBoolean extends LibraryTest {
	
	public void testIfTrueFalse() {
		root.addChild("T").setStem(p("°.Zells.True"));
		root.addChild("F").setStem(p("°.Zells.False"));
		
		MockCell r = (MockCell) root.addChild(new MockCell(root, "R").withReaction());
		
		send(p("T.ifTrue"), p("°.R"));
		assertEquals(1, r.reaction.executed);
		
		send(p("T.ifFalse"), p("°.R"));
		assertEquals(1, r.reaction.executed);
		
		send(p("F.ifTrue"), p("°.R"));
		assertEquals(1, r.reaction.executed);
		
		send(p("F.ifFalse"), p("°.R"));
		assertEquals(2, r.reaction.executed);
		
	}
	
	public void testOr() {
		root.addChild("T").setStem(p("°.Zells.True"));
		root.addChild("F").setStem(p("°.Zells.False"));
		
		root.addChild("R1").setStem(p("°.Zells.True"));
		root.addChild("R2").setStem(p("°.Zells.False"));
		root.addChild("R3").setStem(p("°.Zells.True"));
		root.addChild("R4").setStem(p("°.Zells.False"));
		
		send(p("T.or"), p("°.R1"));
		send(p("T.or"), p("°.R2"));
		send(p("F.or"), p("°.R3"));
		send(p("F.or"), p("°.R4"));
		
		assertEquals(p("°.Zells.True"), send(p("R1.response.value"), p("")).deliveredTo);
		assertEquals(p("°.Zells.True"), send(p("R2.response.value"), p("")).deliveredTo);
		assertEquals(p("°.Zells.True"), send(p("R3.response.value"), p("")).deliveredTo);
		assertEquals(p("°.Zells.False"), send(p("R4.response.value"), p("")).deliveredTo);
	}
	
}
