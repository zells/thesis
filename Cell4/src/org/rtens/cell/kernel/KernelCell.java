package org.rtens.cell.kernel;

import org.rtens.cell.*;

public class KernelCell extends Cell {
	
	public KernelCell(Cell parent, String name) {
		super(parent, name);
		
		setActive(true);
	}
	
	protected Cell getCell() {
		return ((KernelCell) getParent()).getCell();
	}
	
	protected boolean adoptIfNotOwn(Context context) {
		
		Path receiver = getCellPath(getPath());
		Path shouldBe = getCellPath(context.getReceiver());
		
		if (!receiver.equals(shouldBe)) {
			
			Path parentCell = new Path(shouldBe);
			String name = parentCell.removeLast();
			
			// Create child
			sendAndWait(new Send(new Path(parentCell, new Path("cell", "Children", "add")),
					new Path("°", "Prophet", "Literal", "String", name)), context);
			
			sendAndWait(new Send(new Path(shouldBe, new Path("cell", "cell", "Children", "add")),
					new Path("°", "Prophet", "Literal", "String", "Stem")), context);
			
			// Set stem
			for (String stemPathName : new Path(parentCell, new Path("stem", name))) {
				sendAndWait(new Send(new Path(shouldBe, new Path("cell", "Stem", "add")),
						new Path("°", "Prophet", "Literal", "String", stemPathName)), context);
			}
			
			return true;
		}
		
		return false;
	}
	
	public void adopted(Context context) {
		Path shouldBe = getCellPath(context.getReceiver());
		
		// Activate
		sendAndWait(new Send(new Path(shouldBe, new Path("cell", "Active", "cell", "Stem", "clear")),
				new Path("°")), context);
		
		for (String stemPathName : new Path("°", "Prophet", "True")) {
			sendAndWait(new Send(new Path(shouldBe, new Path("cell", "Active", "cell", "Stem", "add")),
					new Path("°", "Prophet", "Literal", "String", stemPathName)), context);
		}
		
		// forward original send 
		sendAndWait(new Send(context.getReceiver(), context.getMessage()), context);
	}
	
	protected Path getCellPath(Path path) {
		if (path.contains("cell"))
			return new Path(path.subList(0, path.indexOf("cell")).toArray(new String[0]));
		return new Path(path);
	}
	
	protected Result sendAndWait(Send send, Context context) {
		Messenger m = new Messenger(send, this, context);
		m.start();
		
		while (!m.isDone()) {
			try {
				Thread.sleep(10L);
			} catch (InterruptedException e) {}
		}
		
		return m.getResult();
	}
}
