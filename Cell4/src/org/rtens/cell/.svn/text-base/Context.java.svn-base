package org.rtens.cell;

import java.io.Serializable;
import java.util.Date;

public class Context implements Serializable {
	
	private Context parent;
	private Path receiver;
	private Send send;
	private int id = -1;
	
	public Context(Context parent, Path receiver, Send send) {
		this.parent = parent;
		this.receiver = receiver;
		this.send = send;
		
		id = (int) ((((int) new Date().getTime()) << 8) + Math.random() * 0xFF);
	}
	
	public Context(Context copy) {
		receiver = copy.receiver;
		send = new Send(new Path(copy.send.getReceiver()), (copy.send.getMessage()));
		id = copy.id;
		
		if (copy.parent != null)
			parent = new Context(copy.parent);
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int i) {
		id = i;
	}
	
	public Context getParent() {
		return parent;
	}
	
	public Context getRoot() {
		if (parent == null)
			return this;
		return parent.getRoot();
	}
	
	public Path getReceiver() {
		return receiver;
	}
	
	public Path getMessage() {
		return send.getMessage();
	}
	
	public Send getSend() {
		return send;
	}
	
	public Context getMessageContext(Path receiver) {
		if (this.receiver.equals(receiver)) {
			return this;
		} else if (parent != null) {
			return parent.getMessageContext(receiver);
		} else {
			return null;
		}
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		
		Context c = this;
		while (c != null) {
			s.insert(0, "(" + Integer.toHexString(c.id) + " " + c.receiver + " " + c.send + ")");
			c = c.parent;
		}
		
		return "<" + s + ">";
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Context)) return false;
		
		Context c = (Context) o;
		if (parent != null && !parent.equals(c.parent)) return false;
		return c.receiver.equals(receiver) && c.send.equals(send) && c.id == id;
	}
	
	@Override
	public int hashCode() {
		return send.hashCode() + id;
	}
	
}
