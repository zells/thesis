package org.zells;

import java.io.Serializable;

public class Delivery implements Serializable {
	
	public Path role;
	public Path receiver;
	
	public Delivery(Path role, Path receiver) {
		this.role = role;
		this.receiver = receiver;
	}
	
	public Delivery(Delivery copy) {
		role = new Path(copy.role);
		receiver = new Path(copy.receiver);
	}
	
	@Override
	public String toString() {
		return role + "|" + receiver;
	}
	
	public void resolveNextReceiver() {
		role = new Path(role, new Path(receiver.removeFirst()));
	}
	
}
