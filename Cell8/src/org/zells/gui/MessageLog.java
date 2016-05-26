package org.zells.gui;

import java.util.*;

import javax.swing.event.*;
import javax.swing.tree.TreePath;

import org.zells.*;

public class MessageLog implements ChangeListener {
	
	private Messenger messenger;
	private DeliveryId eid;
	
	private MessageLog parent;
	private LinkedList<MessageLog> children = new LinkedList<MessageLog>();
	
	public List<ChangeListener> listeners = new LinkedList<ChangeListener>();
	
	public MessageLog(DeliveryId eid) {
		this.eid = eid;
	}
	
	public MessageLog(Messenger m) {
		messenger = m;
	}
	
	public DeliveryId getId() {
		DeliveryId did;
		if (messenger == null)
			did = eid;
		else did = messenger.getDeliveryId();
		
		if (did == null)
			throw new NullPointerException("Null: " + messenger);
		
		return did;
	}
	
	public LinkedList<MessageLog> getChildren() {
		return children;
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
	
	/**
	 * Finds the MessageLog with given ID within children of current log.
	 * 
	 * Creates children on the way that don't exist.
	 */
	public MessageLog findLog(Messenger m) {
		if (m.getDeliveryId().equals(getId())) return this;
		
		DeliveryId cid = m.getDeliveryId().subId(0, getId().size() + 1);
		
		MessageLog child = getChild(cid);
		if (child == null) {
			if (getId().size() + 1 == m.getDeliveryId().size())
				child = addChild(new MessageLog(m));
			else child = addChild(new MessageLog(cid));
		}
		
		return child.findLog(m);
	}
	
	private MessageLog addChild(MessageLog child) {
		children.add(child);
		child.listeners.add(this);
		child.parent = this;
		changed();
		return child;
	}
	
	private MessageLog getChild(DeliveryId cid) {
		for (MessageLog child : children) {
			if (child.getId().equals(cid))
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
