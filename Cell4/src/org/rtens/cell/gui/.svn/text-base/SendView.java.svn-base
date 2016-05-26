package org.rtens.cell.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.rtens.cell.*;
import org.rtens.cell.glue.PathFormat;
import org.rtens.cell.gui.browser.*;

public class SendView extends JPanel {
	
	private Cell root;
	
	private JTextField receiverText;
	private JTextField messageText;
	private JButton sendButton;
	private JButton choseButton;
	
	private Browser browser;
	
	private int count = 0;
	
	private ClickedPeerListener receiverListener = new ClickedPeerListener() {
		
		@Override
		public void clickedOn(PeerView p) {
			browser.clickedPeerListeners.remove(receiverListener);
			browser.clickedPeerListeners.add(messageListener);
			receiverText.setText(PathFormat.format(p.getAddress()));
		}
	};
	
	private ClickedPeerListener messageListener = new ClickedPeerListener() {
		
		@Override
		public void clickedOn(PeerView p) {
			browser.clickedPeerListeners.remove(messageListener);
			messageText.setText(PathFormat.format(p.getAddress()));
			sendButton.requestFocus();
		}
	};
	
	public SendView(Cell root) {
		this.root = root;
		
		buildView();
		setListeners();
	}
	
	private void setListeners() {
		
		choseButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (browser != null) {
					browser.clickedPeerListeners.remove(messageListener);
					browser.clickedPeerListeners.remove(receiverListener);
					browser.clickedPeerListeners.add(receiverListener);
				}
			}
		});
		
		sendButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Path receiver = null, message = null;
				try {
					receiver = PathFormat.parse(receiverText.getText());
				} catch (Exception e) {
					JOptionPane.showMessageDialog(SendView.this, "Could not parse receiver: " + e.getMessage());
				}
				
				try {
					message = PathFormat.parse(messageText.getText());
				} catch (Exception e) {
					JOptionPane.showMessageDialog(SendView.this, "Could not parse message: " + e.getMessage());
				}
				
				if (receiver != null && message != null) {
					Context context = new Context(null, new Path("°"),
							new Send(new Path("°"), new Path(String.valueOf(count))));
					new Messenger(new Send(receiver, message), root, context).start();
					count++;
				}
				
				receiverText.setText("");
				messageText.setText("");
			}
		});
	}
	
	private void buildView() {
		setLayout(new BorderLayout());
		
		Box box = Box.createHorizontalBox();
		add(box);
		
		Box receiverBox = Box.createVerticalBox();
		box.add(receiverBox);
		receiverBox.add(new JLabel("Receiver"));
		receiverText = (JTextField) receiverBox.add(new JTextField(20));
		receiverText.setMaximumSize(
				new Dimension(Integer.MAX_VALUE, receiverText.getPreferredSize().height));
		
		Box messageBox = Box.createVerticalBox();
		box.add(messageBox);
		messageBox.add(new JLabel("Message"));
		messageText = (JTextField) messageBox.add(new JTextField(20));
		messageText.setMaximumSize(
				new Dimension(Integer.MAX_VALUE, messageText.getPreferredSize().height));
		
		sendButton = (JButton) box.add(new JButton("send"));
		sendButton.setMaximumSize(new Dimension(sendButton.getPreferredSize().width,
				messageBox.getPreferredSize().height));
		
		choseButton = (JButton) box.add(new JButton("chose"));
		choseButton.setMaximumSize(new Dimension(choseButton.getPreferredSize().width,
				messageBox.getPreferredSize().height));
		choseButton.setVisible(browser != null);
	}
	
	public void setBrowser(Browser b) {
		browser = b;
		choseButton.setVisible(b != null);
		revalidate();
		repaint();
	}
	
}
