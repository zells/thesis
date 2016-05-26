package org.rtens.cell.kernel.cell;

import org.rtens.cell.*;
import org.rtens.cell.kernel.KernelCell;

public class SendCell extends KernelCell {
	
	private Send send;
	
	public SendCell(Cell parent, String name, Send s) {
		super(parent, name);
		this.send = s;
		
		addChild(new PathCell(this, "Receiver") {
			
			@Override
			protected Path getCellPath() {
				return send.getReceiver();
			}
			
			@Override
			protected void setCellPath(Path path) {
				send.setReceiver(path);
				getCell().setReaction(getCell().getReaction());
			}
			
		});
		addChild(new PathCell(this, "Message") {
			
			@Override
			protected Path getCellPath() {
				return send.getMessage();
			}
			
			@Override
			protected void setCellPath(Path path) {
				send.setMessage(path);
				getCell().setReaction(getCell().getReaction());
			}
			
		});
		
		setActive(true);
	}
	
}
