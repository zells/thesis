package org.rtens.cell.test;

import java.io.File;

import junit.framework.TestCase;

import org.rtens.cell.*;
import org.rtens.cell.glue.*;
import org.rtens.cell.test.TestReceive.MockConnections;

public abstract class LibraryTest extends TestCase {
	
	protected Cell root;
	
	protected Result send(String receiver) {
		return send(receiver, "°");
	}
	
	protected Result send(String receiver, String message) {
		try {
			return root.send(new Send(PathFormat.parse(receiver),
					PathFormat.parse(message)),
					TestReceive.rootContext());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	protected void setUp() throws Exception {
		root = new CellLoader(new File("prophet/library")).getChild(null, "°");
		root.getChildren();
		root.setLoader(null);
		root.getPeers().clear();
		
		TestReceive.mockConnections = new MockConnections(root);
		root.setConnections(TestReceive.mockConnections);
	}
}
