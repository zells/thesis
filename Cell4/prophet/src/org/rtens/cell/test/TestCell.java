package org.rtens.cell.test;

import org.rtens.cell.*;
import org.rtens.cell.test.TestReceive.MockCell;

public class TestCell extends LibraryTest {
	
	/**
	 * A.is X replies True if X.stem is in the stem-cell line of A
	 */
	public void testIs() {
		root.addChild(new MockCell(root, "A")).setStem(new Path("°", "B"));
		root.addChild(new MockCell(root, "B")).setStem(new Path("°", "C"));
		root.addChild(new MockCell(root, "C")).setStem(new Path("°", "D"));
		root.addChild(new MockCell(root, "D")).setStem(new Path("°", "Cell"));
		
		Cell x = root.addChild(new MockCell(root, "X")).setStem(new Path("°", "D"));
		Cell y = root.addChild(new MockCell(root, "Y")).setStem(new Path("°", "A"));
		Cell z = root.addChild(new MockCell(root, "Z")).setStem(new Path("°", "X"));
		
		send("°.A.is", "°.X");
		assertNotNull(x.getChild("reply"));
		assertEquals("True", x.getChild("reply").getStem().getLast());
		
		send("°.A.is", "°.Y");
		assertEquals("True", y.getChild("reply").getStem().getLast());
		
		send("°.A.is", "°.Z");
		assertEquals("False", z.getChild("reply").getStem().getLast());
	}
}
