package org.rtens.cell.gui;

import java.awt.Dimension;
import java.util.*;

import javax.swing.JFrame;

import org.rtens.cell.*;
import org.rtens.cell.Messenger.Listener;

public class MessengerListener implements Listener {
	
	protected List<MessageInspector> inspectors = new LinkedList<MessageInspector>();
	private Map<MessageInspector, JFrame> frames = new HashMap<MessageInspector, JFrame>();
	private Cell root;
	
	public MessengerListener(Cell root) {
		this.root = root;
	}
	
	@Override
	public void done(Messenger m) {
		getMessengerLog(m).changed();
	}
	
	@Override
	public void started(Messenger m) {
		getMessengerLog(m).changed();
	}
	
	@Override
	public void cancelled(Messenger m) {
		getMessengerLog(m).changed();
	}
	
	@Override
	public void paused(Messenger m) {
		getMessengerLog(m).changed();
	}
	
	@Override
	public void unpaused(Messenger m) {
		getMessengerLog(m).changed();
	}
	
	@Override
	public void allPaused() {
		for (MessageInspector i : inspectors)
			i.repaint();
	}
	
	@Override
	public void allUnpaused() {
		for (MessageInspector i : inspectors)
			i.repaint();
	}
	
	synchronized private MessageLog getMessengerLog(Messenger m) {
		for (MessageInspector i : inspectors) {
			if (i.getRootLog().getSend().equals(m.getContext().getRoot().getSend())) {
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
