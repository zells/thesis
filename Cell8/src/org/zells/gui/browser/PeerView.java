package org.zells.gui.browser;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

import org.zells.*;
import org.zells.gui.*;

public class PeerView extends JComponent {
	
	/*** Children ***/
	private JLabel label;
	private List<Connection> connections = new LinkedList<Connection>();
	
	/*** Parents ***/
	public TreeView tree;
	public Browser browser;
	
	/*** State ***/
	private String name;
	private PeerModel model;
	
	/*** Configuration ***/
	private Color color = Color.black;
	private Dimension size = new Dimension(40, 40);
	
	public PeerView(Browser b, TreeView tree, String name) {
		this.tree = tree;
		this.browser = b;
		
		setLayout(new AbsoluteLayout());
		setPreferredSize(size);

		label = new JLabel();
		setCellName(name);
		add(label);
		
		setListeners();
	}
	
	private Browser getBrowser() {
		return browser;
	}
	
	public Path getPath() {
		Path path = new Path(name);
		if (tree != null && tree.parentTree != null)
			path.addAll(0, tree.parentTree.rootView.getPath());
		return path;
	}
	
	public PeerModel getModel() {
		return model;
	}
	
	public HostView getHost() {
		if (tree != null)
			return tree.getHost();
		
		return null;
	}
	
	public void setModel(PeerModel to) {
		model = to;
		
		for (Address addr : getModel().getPeers()) {
			Connection c = new Connection(this, addr);
			c.setVisible(isVisible());
			connections.add(c);
			getBrowser().addConnection(c);
		}
	}
	
	public String getCellName() {
		return name;
	}
	
	private void setCellName(String name) {
		if (this.name != null && getModel() == null) return;
		
		this.name = name;
		label.setText(name);
		
		if (label.getPreferredSize().width <= getPreferredSize().width) {
			label.setLocation((getPreferredSize().width - label.getPreferredSize().width) / 2,
					(getPreferredSize().height - label.getPreferredSize().height) / 2);
		} else {
			label.setLocation(2,
					(getPreferredSize().height - label.getPreferredSize().height) / 2);
		}
	}
	
	@Override
	public void setVisible(boolean aFlag) {
		for (Connection c : connections)
			c.setVisible(aFlag);
		
		super.setVisible(aFlag);
	}
	
	private void setListeners() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					if (e.isAltDown()) {
						//						new Actions(e).editReaction();
					} else {
						if (e.isShiftDown()) {
							tree.controller.toggleExpansionWholeTree();
						} else {
							tree.controller.toggleExpansion();
						}
					}
				} else if (e.getClickCount() == 1) {
					//					for (ClickedPeerListener l : clickedPeerListeners)
					//						l.clickedOn(PeerView.this);
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
			//				new Dragger(browser, PeerView.this, e.getPoint());
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					new PeerActionMenu(e, PeerView.this).show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
	}

	
	public void select(Color color) {
		this.color = color;
		tree.refresh();
	}
	
	public void deselect() {
		select(Color.black);
	}
	
	@Override
	public void paint(Graphics g) {
		
		if (getModel() == null)
			g.setColor(new Color(0xEE, 0xEE, 0xEE));
		else if (!getModel().isActive())
			g.setColor(Color.lightGray);
		else g.setColor(Color.white);
		
		g.fillArc(0, 0, size.width, size.height, 0, 360);
		
		g.setColor(color);
		g.drawArc(0, 0, size.width, size.height, 0, 360);
		
		if (getModel() != null) {
			if (getModel().getStem() != null) {
				g.drawLine(size.width - 5, 0, size.width, 0);
				g.drawLine(size.width, 0, size.width, 5);
				g.drawLine(size.width, 5, size.width - 5, 0);
			}
			
			if (getModel().getReaction() != null) {
				g.drawArc(size.width - 5, size.height - 5, 5, 5, 0, 360);
			}
		}
		
		super.paint(g);
	}

	public void makeVisible() {
		if (tree != null)
			tree.makeVisible();
	}
}
