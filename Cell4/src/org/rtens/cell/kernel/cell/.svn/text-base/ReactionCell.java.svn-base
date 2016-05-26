package org.rtens.cell.kernel.cell;

import org.rtens.cell.*;

public class ReactionCell extends ListCell {
	
	public ReactionCell(Cell parent, String name) {
		super(parent, name);
	}
	
	private Reaction getCellReaction() {
		return (Reaction) getCell().getReaction();
	}
	
	@Override
	public void add(Path stem, Context contex) {
		Reaction r = getCellReaction();
		r.add(new Send(new Path(), new Path()));
		
		getCell().setReaction(r);
	}
	
	@Override
	public void remove(int index, Context contex) {
		Reaction r = getCellReaction();
		r.getSends().remove(index);
		
		getCell().setReaction(r);
	}
	
	@Override
	public void clear() {
		getCell().setReaction(new Reaction());
	}
	
	@Override
	protected Cell getCellOf(int i) {
		return new SendCell(this, String.valueOf(i), getCellReaction().getSends().get(i));
	}
	
	@Override
	protected int getSize() {
		if (!(getCell().getReaction() instanceof Reaction) || getCellReaction() == null) return 0;
		
		return getCellReaction().getSends().size();
	}
	
	@Override
	public void adopted(Context context) {
		
		send(new Path("cell", "Children", "add"),
					new Path("°", "Prophet", "Literal", "String", "Reaction"),
					context);
		send(new Path("Reaction", "clear"), new Path("°"), context);
		
		int sendIndex = 0;
		for (Send send : ((Reaction) getCell().getReaction()).getSends()) {
			send(new Path("Reaction", "add"), new Path("°", "Prophet", "Reflection", "Reaction", "Send"), context);
			
			for (String name : send.getReceiver())
				send(new Path("Reaction", String.valueOf(sendIndex), "Receiver", "add"),
							new Path("°", "Prophet", "Literal", "String", name),
							context);
			
			for (String name : send.getMessage())
				send(new Path("Reaction", String.valueOf(sendIndex), "Message", "add"),
							new Path("°", "Prophet", "Literal", "String", name),
							context);
			
			sendIndex++;
		}
		
		super.adopted(context);
	}
	
	private void send(Path receiver, Path message, Context c) {
		Path path = c.getReceiver();
		Path shouldBe = new Path(path.subList(0, path.lastIndexOf("cell")).toArray(new String[0]));
		shouldBe.add("cell");
		
		sendAndWait(new Send(new Path(shouldBe, receiver), message), c);
	}
	
}
