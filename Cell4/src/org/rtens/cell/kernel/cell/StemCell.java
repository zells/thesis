package org.rtens.cell.kernel.cell;

import org.rtens.cell.*;

public class StemCell extends PathCell {
	
	public StemCell(Cell parent, String name) {
		super(parent, name);
	}
	
	@Override
	protected Path getCellPath() {
		return getCell().getStem();
	}
	
	@Override
	protected void setCellPath(Path path) {
		getCell().setStem(path);
	}
	
	@Override
	public void adopted(Context context) {
		Path path = context.getReceiver();
		Path shouldBe = new Path(path.subList(0, path.lastIndexOf("cell")).toArray(new String[0]));
		
		sendAndWait(new Send(new Path(shouldBe, new Path("cell", "Stem", "clear")), new Path("°")), context);
		
		for (String name : getCell().getStem()) {
			sendAndWait(new Send(new Path(shouldBe, new Path("cell", "Stem", "add")),
					new Path("°", "Prophet", "Literal", "String", name)), context);
		}
		
		super.adopted(context);
	}
}
