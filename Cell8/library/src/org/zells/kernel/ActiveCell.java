package org.zells.kernel;

import org.zells.*;

public class ActiveCell extends KernelCell {
	
	public ActiveCell(Cell parent) {
		super(parent, "Active");
	}
	
	@Override
	public Path getStem() {
		return new Path("°", "Zells", getCell().isActive() ? "True" : "False");
	}
	
	@Override
	public Cell setStem(Path stem) {
		Path value = getRoot().send(new Mailing(new Path(stem, new Path("value")), new Path()),
				getRoot().getPath(), new DeliveryId()).deliveredTo;
		
		if (value != null) {
			if (value.equals(new Path("°", "Zells", "True"))) {
				getCell().setActive(true);
			} else if (value.equals(new Path("°", "Zells", "False"))) {
				getCell().setActive(false);
			}
		}
		
		return super.setStem(stem);
	}
}
