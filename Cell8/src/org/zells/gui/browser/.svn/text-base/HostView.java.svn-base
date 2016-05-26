package org.zells.gui.browser;

import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;

import javax.swing.*;

import org.zells.*;
import org.zells.gui.*;

public class HostView extends JComponent {
	
	/*** Children ***/
	public JTextArea title;
	public TreeView tree;
	
	/*** Parents ***/
	private Browser browser;
	
	/*** Configuration ***/
	private Point padding = new Point(10, 10);
	private Point margin = new Point(10, 10);
	private int titlePadding = 3;
	
	/*** State ***/
	private LinkedList<Address> addresses = new LinkedList<Address>();
	public HostController controller;
	private Point grabbed;
	
	public HostView(Browser browser, Address addr) {
		this.browser = browser;
		controller = new HostController(this);
		
		setLayout(new AbsoluteLayout());
		
		tree = new TreeView(browser, this, null, "°");
		add(tree);
		
		title = new JTextArea();
		title.setLocation(margin.x + titlePadding, margin.y);
		title.setEnabled(false);
		title.setDisabledTextColor(Color.black);
		add(title);
		
		addAddress(addr);
		
		controller.setExpanded(false);
		
		addListeners();
	}

	public LinkedList<Address> getAddresses() {
		return addresses;
	}
	
	public JComponent getTitle() {
		return title;
	}
	
	private void addAddress(Address addr) {
		addresses.add(addr);
		
		String titleString = "";
		for (Address h : addresses) {
			titleString += h + "\n";
		}
		
		title.setText(titleString.substring(0, titleString.length() - 1));
		tree.setLocation(margin.x + padding.x, margin.y + padding.y + title.getPreferredSize().height);
	}
	
	public PeerView getPeerView(Path path, boolean createOnTheFly) {
		return tree.getPeerView(path.subPath(1, 0), createOnTheFly);
	}
	
	public int getInnerWidth() {
		int width = tree.getPreferredSize().width + margin.x * 2;
		if (width < title.getPreferredSize().width + titlePadding * 2)
			width = title.getPreferredSize().width + titlePadding * 2;
		
		return width;
	}
	
	@Override
	public Dimension getPreferredSize() {
		if (controller.isExpanded()) {
			return new Dimension(getInnerWidth() + margin.x * 2,
					title.getPreferredSize().height + tree.getPreferredSize().height
							+ (padding.y + margin.y) * 2);
		} else {
			return new Dimension(title.getPreferredSize().width + (titlePadding + margin.x) * 2,
					title.getPreferredSize().height + (padding.y + margin.y) * 2);
		}
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		// Title
		g.drawRect(margin.x, margin.y, title.getPreferredSize().width + titlePadding * 2,
				title.getPreferredSize().height);
		
		if (controller.isExpanded()) {
			// Box
			g.drawRect(
					margin.x,
					margin.y + title.getPreferredSize().height,
					getInnerWidth(),
					tree.getPreferredSize().height + padding.y * 2);
		}
		
	}
	
	public void refresh() {
		invalidate();
		browser.revalidate();
		browser.repaint();
	}
	
	private void addListeners() {
		title.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					controller.toggleExpansion();
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				grabbed = e.getPoint();
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				grabbed = null;
			}
		});
		
		title.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				Point trans = new Point(e.getX() - grabbed.x, e.getY() - grabbed.y);
				setLocation(getLocation().x + trans.x, getLocation().y + trans.y);
				refresh();
			}
		});
	}
	
}
