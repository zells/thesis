package org.zells.test;

import org.zells.*;

public class TestLibraryNumber extends LibraryTest {
	
	public void testEquals() {
		root.addChild("A").setStem(p("°.Zells.Literal.Number.1"));
		Cell b = root.addChild("B").setStem(p("°.Zells.Literal.Number.1"));
		
		send(p("A.equals"), p("°.B"));
		
		assertEquals(p("°.Zells.True"), b.getChild("response").getStem());
	}
	
	public void testSubtract() {
		root.addChild("A").setStem(p("°.Zells.Literal.Number.7"));
		root.addChild("B").setStem(p("°.Zells.Literal.Number.4"));
		root.addChild("C").setStem(p("°.Zells.Literal.Number.5"));
		
		send(p("A.subtract"), p("°.B"));
		assertEquals(p("°.Zells.Literal.Number.3"), send(p("B.response.value"), p("")).deliveredTo);
		
		send(p("A.add"), p("°.C"));
		assertEquals(p("°.Zells.Literal.Number.12"), send(p("C.response.value"), p("")).deliveredTo);
	}
}
