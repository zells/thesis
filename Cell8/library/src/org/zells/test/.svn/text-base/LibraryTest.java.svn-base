package org.zells.test;

import java.io.File;

import org.zells.glue.CellLoader;

public class LibraryTest extends CellTest {
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		root = new CellLoader(new File("library/cells")).getChild(null, "°");
		root.getChildren();
		root.setLoader(null);
		root.getPeers().clear();
	}
	
}
