package org.zells.test;

import org.zells.*;
import org.zells.library.Zells.Literal.StringCell;

public class TestLibraryLiteral extends LibraryTest {

	public void testString() {
		Cell hello = root.getChild("Zells").getChild("Literal").getChild("String").getChild("Hello");
		
		send(p("°.Zells.Literal.String.Hello.0.cell"));
		assertEquals(true, res.wasDelivered());

		assertEquals(new Path("°", "Zells", "Literal", "Character", "H"), hello.getChild("0").getStem());
		assertEquals("H", hello.getChild("0").getStem().getLast());
		assertEquals("e", hello.getChild("1").getStem().getLast());
		assertEquals("l", hello.getChild("2").getStem().getLast());
		assertEquals("l", hello.getChild("3").getStem().getLast());
		assertEquals("o", hello.getChild("4").getStem().getLast());
		assertNull(hello.getChild("5"));
	}
	
	public void testCharacter() {
		Cell a = root.getChild("Zells").getChild("Literal").getChild("Character").getChild("A");
		
		assertNotNull(a);
		assertEquals(new Path("°", "Zells", "Character"), a.getStem());
	}
	
	public void testReadString() {
		root.addChild("A").setStem(p("°.Zells.Literal.String.Hello"));
		
		assertEquals("Hello", StringCell.get(p("°.A"), root, p("°"), new DeliveryId()));
	}
	
	public void testCharacterValue() {
		send(p("°.Zells.Literal.Character.A.value"));
		
		assertEquals(true, res.wasDelivered());
		assertEquals(p("°.Zells.Literal.Character.A"), res.deliveredTo);
		
		root.addChild("A").setStem(p("°.Zells.Literal.Character.A"));
		send(p("°.A.value"));
		assertEquals(p("°.Zells.Literal.Character.A"), res.deliveredTo);
		
		send(p("°.A.value.cell"));
		assertEquals(p("°.A.value.cell.##"), res.deliveredTo);
	}
	
	public void testNumber() {
		Cell a = root.getChild("Zells").getChild("Literal").getChild("Number").getChild("1");
		
		assertNotNull(a);
		assertEquals(new Path("°", "Zells", "Number"), a.getStem());
		
		a = root.getChild("Zells").getChild("Literal").getChild("Number").getChild("A");
		assertNull(a);
		
		
		send(p("°.Zells.Literal.Number.42.value"));
		
		assertEquals(p("°.Zells.Literal.Number.42"), res.deliveredTo);
		
		root.addChild("A").setStem(p("°.Zells.Literal.Number.21"));
		send(p("°.A.value"));
		assertEquals(p("°.Zells.Literal.Number.21"), res.deliveredTo);
	}
	
}
