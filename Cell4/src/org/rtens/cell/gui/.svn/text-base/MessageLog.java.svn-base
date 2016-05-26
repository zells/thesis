package org.rtens.cell.gui;

import java.util.*;

import javax.swing.event.*;
import javax.swing.tree.TreePath;

import org.rtens.cell.*;

public class MessageLog implements ChangeListener {
	
	private Messenger messenger;
	private Send send;
	
	private MessageLog parent;
	private LinkedList<MessageLog> children = new LinkedList<MessageLog>();
	
	public List<ChangeListener> listeners = new LinkedList<ChangeListener>();
	
	public MessageLog(Send s) {
		send = s;
	}
	
	public MessageLog(Messenger m) {
		this(m.getSend());
		messenger = m;
	}
	
	public LinkedList<MessageLog> getChildren() {
		return children;
	}
	
	public Send getSend() {
		return send;
	}
	
	public Messenger getMessenger() {
		return messenger;
	}
	
	public void changed() {
		stateChanged(new ChangeEvent(this));
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		for (ChangeListener l : listeners)
			l.stateChanged(e);
	}
	
	@Override
	public String toString() {
		if (messenger != null)
			return messenger.getSender().getPath().toString();
		return "n/a";
	}
	
	public MessageLog findLog(LinkedList<Send> sends, Messenger m) {
		
		if (sends.isEmpty()) {
			MessageLog child = getChild(m.getSend());
			
			if (child == null) {
				child = new MessageLog(m);
				addChild(child);
			}
			
			return child;
		} else {
			Send send = sends.removeFirst();
			MessageLog child = getChild(send);
			
			if (child == null) {
				child = new MessageLog(send);
				addChild(child);
			}
			
			return child.findLog(sends, m);
		}
	}
	
	private void addChild(MessageLog child) {
		children.add(child);
		child.listeners.add(this);
		child.parent = this;
		changed();
	}
	
	private MessageLog getChild(Send send) {
		for (MessageLog child : children) {
			if (child.getSend().equals(send))
				return child;
		}
		return null;
	}
	
	public MessageLog getParent() {
		return parent;
	}

	public TreePath getPath() {
		LinkedList<Object> path = new LinkedList<Object>();
		
		MessageLog log = this;
		while (log != null) {
			path.addFirst(log);
			log = log.getParent();
		}
		
		return new TreePath(path.toArray());
	}
	
}
