package org.rtens.cell.gui;

import javax.swing.event.*;

import org.rtens.cell.gui.treeTable.*;

public class MessageLogTreeTableModel extends AbstractTreeTableModel implements ChangeListener {
	
	public MessageLogTreeTableModel(MessageLog root) {
		super(root);
		root.listeners.add(this);
	}
	
	@Override
	public boolean isCellEditable(Object node, int column) {
		return true;
	}
	
	@Override
	public int getColumnCount() {
		return 4;
	}
	
	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "Sender";
		case 1:
			return "Receiver";
		case 2:
			return "Message";
		case 3:
			return "Status";
		default:
			return "none";
		}
	}
	
	@Override
	public Class<?> getColumnClass(int column) {
		switch (column) {
		case 0:
			return TreeTableModel.class;
		case 1:
			return String.class;
		case 2:
			return String.class;
		case 3:
			return MessageLog.class;
		default:
			return null;
		}
	}
	
	@Override
	public Object getValueAt(Object node, int column) {
		MessageLog log = ((MessageLog) node);
		switch (column) {
		case 0:
			return node;
		case 1:
			return log.getSend().getReceiver();
		case 2:
			return log.getSend().getMessage();
		case 3:
			return node;
		default:
			return node;
		}
	}
	
	@Override
	public Object getChild(Object parent, int index) {
		return ((MessageLog) parent).getChildren().get(index);
	}
	
	@Override
	public int getChildCount(Object node) {
		return ((MessageLog) node).getChildren().size();
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		try {
			if (e != null) {
				MessageLog changed = (MessageLog) e.getSource();
				fireTreeStructureChanged(this, changed.getPath().getPath(), null, null);
			} else {
				fireTreeStructureChanged(this, ((MessageLog) getRoot()).getPath().getPath(), null, null);
			}
		} catch (Exception ex) {}
	}
	
}
