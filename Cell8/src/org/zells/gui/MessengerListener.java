package org.zells.gui;

import java.awt.Dimension;
import java.util.*;

import javax.swing.JFrame;

import org.zells.*;
import org.zells.Messenger.Listener;

public class MessengerListener implements Listener {
	
	protected List<MessageInspector> inspectors = new LinkedList<MessageInspector>();
	private Map<MessageInspector, JFrame> frames = new HashMap<MessageInspector, JFrame>();
	private PeerModel root;
	
	public MessengerListener(PeerModel rootModel) {
		this.root = rootModel;
	}
	
	@Override
	public void done(Messenger m) {
		getMessageLog(m).changed();
	}
	
	@Override
	public void started(Messenger m) {
		getMessageLog(m).changed();
	}
	
	@Override
	public void cancelled(Messenger m) {
		getMessageLog(m).changed();
	}
	
	@Override
	public void paused(Messenger m) {
		getMessageLog(m).changed();
	}
	
	@Override
	public void unpaused(Messenger m) {
		getMessageLog(m).changed();
	}
	
	@Override
	public void allPaused() {
		for (MessageInspector i : inspectors)
			i.repaint();
	}
	
	@Override
	public void allResumed() {
		for (MessageInspector i : inspectors)
			i.repaint();
	}
	
	synchronized private MessageLog getMessageLog(Messenger m) {
		for (MessageInspector i : inspectors) {
			if (i.getRootLog().getId().equals(m.getDeliveryId().subId(0, 1))) {
				frames.get(i).setVisible(true);
				return i.getMessageLog(m);
			}
		}
		
		MessageInspector i = new MessageInspector(root);
		inspectors.add(i);

		JFrame f = new JFrame("Message Inspector");
		f.add(i);
		i.setPreferredSize(new Dimension(600, 400));
		f.pack();
		f.setVisible(true);
		frames.put(i, f);
		
		return i.getMessageLog(m);
	}
}
