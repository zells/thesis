package org.zells.gui;

import java.awt.Color;
import java.util.Stack;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.AbstractTableModel;

import org.zells.*;
import org.zells.Result.Log;
import org.zells.Result.Log.Entry;
import org.zells.gui.browser.PeerView;

public class DeliveryAnalyzer extends JSplitPane {
	
	private Browser browser;
	private JTable table;
	private Log log;
	
	public DeliveryAnalyzer(PeerModel rootModel, Log l) {
		super(JSplitPane.VERTICAL_SPLIT);
		this.log = l;
		
		setResizeWeight(0.0);
		
		browser = new Browser(rootModel);
		table = new JTable(new LogAdapterModel(l));
		
		setTopComponent(new JScrollPane(browser));
		setBottomComponent(new JScrollPane(table));
		
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				browser.deselectAll();
				
				Stack<Address> addrStack = new Stack<Address>();
				addrStack.push(Server.localAddress);
				for (int i = 0; i <= table.getSelectedRow(); i++) {
					Entry entry = log.get(i);
					
					if (entry.description.startsWith("->"))
						try {
							addrStack.push(Address.parse(entry.description.substring(3)));
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					else if (entry.description.equals("<-"))
						addrStack.pop();
					
					PeerView pv = browser.getHostView(addrStack.peek(), false).getPeerView(entry.path, true);
					pv.select(i < table.getSelectedRow() ? Color.orange : Color.red);
					pv.makeVisible();
				}
			}
		});
	}
	
	private static class LogAdapterModel extends AbstractTableModel {
		
		private Log log;
		
		public LogAdapterModel(Log log) {
			this.log = log;
		}
		
		@Override
		public String getColumnName(int c) {
			switch (c) {
			case 0:
				return "Path";
			case 1:
				return "Description";
			case 2:
				return "Role";
			case 3:
				return "Receiver";
			case 4:
				return "Stack";
			default:
				return null;
			}
		}
		
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return true;
		}
		
		@Override
		public int getColumnCount() {
			return 5;
		}
		
		@Override
		public int getRowCount() {
			return log.size();
		}
		
		@Override
		public Object getValueAt(int r, int c) {
			Log.Entry entry = log.get(r);
			try {
				switch (c) {
				
				case 0:
					return entry.path;
				case 1:
					return entry.description;
				case 2:
					return entry.deliveryStack.isEmpty() ? "" : entry.deliveryStack.get(0).role;
				case 3:
					return entry.deliveryStack.isEmpty() ? "" : entry.deliveryStack.get(0).receiver;
				case 4:
					return entry.deliveryStack.isEmpty() ? "" :
							entry.deliveryStack.subList(1, entry.deliveryStack.size());
				default:
					return null;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}
}
