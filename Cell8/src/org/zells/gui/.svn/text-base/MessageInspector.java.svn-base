package org.zells.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

import org.zells.*;
import org.zells.gui.treeTable.JTreeTable;

public class MessageInspector extends JComponent {
	
	private MessageLog rootLog;
	private PeerModel root;
	
	private JTreeTable treeTable;
	private MessageLogTreeTableModel model;
	
	private JFrame analyzerFrame;
	
	private JButton pauseAllButton;
	private JButton unpauseAllButton;
	private JButton expandNotDeliveredButton;
	
	public MessageInspector(PeerModel rootModel) {
		this.root = rootModel;
		
		buildView();
		addListeners();
	}
	
	public MessageLog getRootLog() {
		return rootLog;
	}
	
	public MessageLog getMessageLog(Messenger m) {
		
		if (rootLog == null) {
			if (m.getDeliveryId().size() == 1)
				rootLog = new MessageLog(m);
			else rootLog = new MessageLog(m.getDeliveryId().subId(0, 1));
			
			model = new MessageLogTreeTableModel(rootLog);
			addTreeTable(model);
			
			revalidate();
			repaint();
		}
		
		return rootLog.findLog(m);
	}
	
	private void addTreeTable(MessageLogTreeTableModel model) {
		treeTable = new JTreeTable(model);
		treeTable.getTree().setShowsRootHandles(true);
		treeTable.getColumnModel().getColumn(3).setCellRenderer(new SendLogCellRenderer());
		
		add(new JScrollPane(treeTable));
		
		treeTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					if (treeTable.getSelectedRow() > -1) {
						MessageLog log = (MessageLog) treeTable.getValueAt(treeTable.getSelectedRow(), 0);
						if (log.getMessenger() != null && log.getMessenger().getResult() != null)
							showAnalyzer(log.getMessenger().getResult());
					}
				}
			}
		});
	}
	
	private void showAnalyzer(Result result) {
		analyzerFrame.getContentPane().removeAll();
		DeliveryAnalyzer ana = new DeliveryAnalyzer(root, result.log);
		ana.setPreferredSize(new Dimension(500, 600));
		analyzerFrame.add(ana);
		
		analyzerFrame.pack();
		analyzerFrame.setVisible(true);
	}
	
	private void buildView() {
		setLayout(new BorderLayout());
		
		pauseAllButton = new JButton("Pause All");
		unpauseAllButton = new JButton("Unpause All");
		pauseAllButton.setVisible(!Messenger.isPausedAll());
		unpauseAllButton.setVisible(Messenger.isPausedAll());
		
		expandNotDeliveredButton = new JButton("Expand Undones");
		
		Box buttons = Box.createHorizontalBox();
		add(buttons, BorderLayout.SOUTH);
		
		buttons.add(pauseAllButton);
		buttons.add(unpauseAllButton);
		buttons.add(Box.createHorizontalGlue());
		buttons.add(expandNotDeliveredButton);
		
		analyzerFrame = new JFrame("Delivery Analyzer");
	}
	
	private void addListeners() {
		
		pauseAllButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Messenger.pauseAll();
				pauseAllButton.setVisible(false);
				unpauseAllButton.setVisible(true);
			}
		});
		
		unpauseAllButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Messenger.resumeAll();
				pauseAllButton.setVisible(true);
				unpauseAllButton.setVisible(false);
			}
		});
		
		expandNotDeliveredButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				expandNotDones(rootLog);
			}
		});
	}
	
	private class SendLogCellRenderer extends DefaultTableCellRenderer {
		
		private MessageLog log;
		
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value,
					isSelected, hasFocus, row, column);
			log = (MessageLog) value;
			if (log == null) return null;
			
			Box box = Box.createHorizontalBox();
			
			if (log.getMessenger() != null) {
				Color color;
				String status;
				JButton b1, b2;
				
				if (log.getMessenger().isDone()) {
					status = "Delivered to " + log.getMessenger().getResult().deliveredTo;
					color = Color.green;
					b1 = null;
					b2 = null;
				} else if (log.getMessenger().isPaused()) {
					status = "Paused";
					color = Color.blue;
					b1 = null;
					b2 = null;
				} else if (log.getMessenger().isCancelled()) {
					status = "Cancelled";
					color = Color.red;
					b1 = null;
					b2 = null;
				} else if (log.getMessenger().isWaiting()) {
					status = "Waiting";
					color = Color.orange;
					b1 = new JButton("Pause");
					b2 = new JButton("Cancel");
				} else {
					status = "Sending";
					color = Color.black;
					b1 = new JButton("Pause");
					b2 = new JButton("Cancel");
				}
				
				JLabel label = new JLabel(status);
				label.setForeground(color);
				box.add(label);
				box.add(Box.createHorizontalGlue());
				
				if (b1 != null) {
					box.add(Box.createHorizontalStrut(10));
					box.add(b1);
				}
				
				if (b2 != null) {
					box.add(Box.createHorizontalStrut(5));
					box.add(b2);
				}
			} else {
				JLabel label = new JLabel("Unknown (" + log.getId() + ")");
				label.setForeground(Color.lightGray);
				box.add(label);
			}
			
			return box;
		}
	}
	
	private void expandNotDones(MessageLog log) {
		if (log.getMessenger() != null && !log.getMessenger().isDone()) {
			MessageLog xLog = log;
			while (xLog != null) {
				treeTable.getTree().expandPath(xLog.getPath());
				xLog = xLog.getParent();
			}
		}
		
		for (MessageLog child : log.getChildren())
			expandNotDones(child);
	}
	
}
