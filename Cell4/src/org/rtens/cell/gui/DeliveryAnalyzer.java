package org.rtens.cell.gui;

import java.awt.Color;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.AbstractTableModel;

import org.rtens.cell.*;

public class DeliveryAnalyzer extends JSplitPane {
	
	private Browser browser;
	private JTable table;
	private Log log;
	
	public DeliveryAnalyzer(Cell root, Log l) {
		super(JSplitPane.VERTICAL_SPLIT);
		this.log = l;
		
		setResizeWeight(0.0);
		
		browser = new Browser(root);
		table = new JTable(new LogAdapterModel(l));
		
		setTopComponent(new JScrollPane(browser));
		setBottomComponent(new JScrollPane(table));
		
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				browser.clearSelections();
				for (int i = 0; i < table.getSelectedRow(); i++) {
					browser.getPeerView(log.get(i).address, true).select(Color.orange);
				}
				browser.getPeerView(log.get(table.getSelectedRow()).address, true).select(Color.red);
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
				return "Address";
			case 1:
				return "Log";
			default:
				return Parameters.class.getDeclaredFields()[c - 2].getName();
			}
		}
		
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return true;
		}
		
		@Override
		public int getColumnCount() {
			return Parameters.class.getDeclaredFields().length;
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
					return entry.address;
				case 1:
					return entry.description;
				default:
					if (entry.parameters != null) {
						return Parameters.class.getDeclaredFields()[c - 2].get(entry.parameters);
					} else {
						return "-";
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}
}
