package org.zells;

public class Mailing {
	
	public Path receiver;
	public Path message;
	
	public Mailing(Path receiver, Path message) {
		this.receiver = receiver;
		this.message = message;
	}
	
	@Override
	public String toString() {
		return "<" + receiver + " " + message + ">";
	}
	
	@Override
	public int hashCode() {
		return receiver.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return receiver.equals(((Mailing)obj).receiver) && message.equals(((Mailing)obj).message);
	}

}
