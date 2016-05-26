package org.zells.kernel;

import java.util.*;

import org.zells.*;

public class SendCell extends KernelCell {
	
	private Mailing mailing;
	
	public SendCell(Cell parent, String name, Mailing s) {
		super(parent, name);
		this.mailing = s;
	}
	
	@Override
	protected List<Cell> loadChildren() {
		LinkedList<Cell> children = new LinkedList<Cell>();
		
		children.add(new PathCell(this, "Receiver") {
			
			@Override
			protected Path getCellPath() {
				return mailing.receiver;
			}
			
			@Override
			protected void setCellPath(Path path) {
				mailing.receiver = path;
				getCell().setReaction(getCell().getReaction());
			}
			
		});
		
		children.add(new PathCell(this, "Message") {
			
			@Override
			protected Path getCellPath() {
				return mailing.message;
			}
			
			@Override
			protected void setCellPath(Path path) {
				mailing.message = path;
				getCell().setReaction(getCell().getReaction());
			}
			
		});
		
		return children;
	}
	
}
