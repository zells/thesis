package org.rtens.cell;

import java.io.Serializable;

public class Send implements Serializable {
	
	private Path receiver;
	private Path message;
	
	public Send(Path receiver, Path message) {
		this.receiver = receiver;
		this.message = message;
	}
	
	public Path getReceiver() {
		return receiver;
	}
	
	public Path getMessage() {
		return message;
	}
	
	@Override
	public String toString() {
		return "{" + receiver + " " + message + "}";
	}
	
	@Override
	public boolean equals(Object o) {
		return o != null && o instanceof Send && ((Send) o).receiver.equals(receiver)
				&& ((Send) o).message.equals(message);
	}
	
	@Override
	public int hashCode() {
		return receiver.hashCode() + message.hashCode();
	}
	
	public void setReceiver(Path r) {
		receiver = r;
	}
	
	public void setMessage(Path m) {
		message = m;
	}
	
}
