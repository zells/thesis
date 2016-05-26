package org.zells;

import java.util.*;

public class Reaction extends NativeReaction {
	
	public List<Mailing> mailings = new LinkedList<Mailing>();
	
	public Reaction(Mailing... mailings) {
		this.mailings.addAll(Arrays.asList(mailings));
	}
	
	@Override
	public void execute(Cell receiver, Path role, Path message, DeliveryId eid) {
		for (Mailing mailing : mailings) {
			send(receiver, role, prepare(mailing, role), eid);
		}
	}
	
	protected void send(Cell receiver, Path role, Mailing prepared, DeliveryId eid) {
		new Messenger(receiver, prepared, role, eid).start();
	}
	
	private Mailing prepare(Mailing mailing, Path role) {
		return new Mailing(
					globalize(mailing.receiver, role),
					globalize(mailing.message, role));
	}
	
	private Path globalize(Path path, Path role) {
		if (path.isEmpty() || !path.getFirst().equals("°"))
				path = new Path(role, path);
		
		return path;
	}
	
	@Override
	public String toString() {
		return mailings.toString();
	}
	
	@Override
	public int hashCode() {
		return mailings.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return mailings.equals(((Reaction) obj).mailings);
	}
	
}
