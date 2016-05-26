package org.rtens.cell;

import java.util.*;

import org.rtens.cell.glue.ReactionFormat;

public class Reaction implements NativeReaction {
	
	private LinkedList<Send> sends = new LinkedList<Send>();
	
	@Override
	public void execute(Cell cell, Context context) {
		for (Send send : sends) {
			new Messenger(send, cell, context).start();
		}
	}
	
	public void add(Send send) {
		sends.add(send);
	}
	
	public LinkedList<Send> getSends() {
		return sends;
	}
	
	@Override
	public boolean equals(Object o) {
		return o != null && o instanceof Reaction && ((Reaction) o).sends.equals(sends);
	}
	
	@Override
	public int hashCode() {
		return sends.hashCode();
	}
	
	@Override
	public String toString() {
		return ReactionFormat.format(this);
	}
	
}
