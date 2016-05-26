package org.rtens.cell.gui.browser;

import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;

import javax.swing.*;

import org.rtens.cell.*;
import org.rtens.cell.gui.*;

public class HostView extends JComponent implements ClickedPeerListener {
	
	private TreeView tree;
	private Browser browser;
	private LinkedList<Host> hosts = new LinkedList<Host>();
	
	private JTextArea title;
	
	private Point padding = new Point(10, 10);
	private Point margin = new Point(10, 10);
	
	private int titlePadding = 3;
	
	private Point grabbed;
	private boolean expanded = true;
	
	public LinkedList<ClickedPeerListener> clickedPeerListeners = new LinkedList<ClickedPeerListener>();
	
	public HostView(Browser b, Host host) {
		setLayout(new AbsoluteLayout());
		
		this.browser = b;
		
		title = new JTextArea("Host");
		title.setLocation(margin.x + titlePadding, margin.y);
		title.setEnabled(false);
		title.setDisabledTextColor(Color.black);
		add(title);
		
		Address rootAddress = new Address(host, new Path(browser.getRoot().getPath()));
		tree = new TreeView(browser, this, rootAddress);
		add(tree);
		tree.clickedPeerListeners.add(this);
		
		addHost(host);
		
		addTitleMouseListeners();
	}
	
	/**
	 * The address of the root is the empty address of the root.
	 */
	public Host getHost() {
		return hosts.getFirst();
	}
	
	public void addHost(Host host) {
		
		hosts.add(host);
		
		String titleString = "";
		for (Host h : hosts) {
			titleString += h + "\n";
		}
		
		title.setText(titleString.substring(0, titleString.length() - 1));
		
		tree.setLocation(margin.x + padding.x, margin.y + padding.y + title.getPreferredSize().height);
		
		revalidate();
		repaint();
	}
	
	@Override
	public void clickedOn(PeerView p) {
		for (ClickedPeerListener l : clickedPeerListeners)
			l.clickedOn(p);
	}
	
	private void addTitleMouseListeners() {
		title.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				grabbed = e.getPoint();
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				grabbed = null;
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					if (expanded) {
						collapse();
					} else {
						expand();
					}
				}
			}
		});
		
		title.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				Point trans = new Point(e.getX() - grabbed.x, e.getY() - grabbed.y);
				setLocation(getLocation().x + trans.x, getLocation().y + trans.y);
				
				revalidate();
				getRootPane().repaint();
			}
		});
	}
	
	/**
	 * Returns the view of the peer corresponding with given path.
	 * 
	 * The view is created if not existing.
	 */
	public PeerView getPeerView(Path path, boolean makeVisible) {
		if (!expanded && makeVisible)
			expand();
		
		path = new Path(path);
		path.removeFirst();
		return tree.getPeerView(path, makeVisible);
	}
	
	private void expand() {
		if (!expanded) {
			expanded = true;
			add(tree);
			
			revalidate();
			getRootPane().repaint();
		}
	}
	
	private void collapse() {
		if (expanded) {
			expanded = false;
			remove(tree);
			
			revalidate();
			getRootPane().repaint();
		}
	}
	
	public int getInnerWidth() {
		int width = tree.getPreferredSize().width + margin.x * 2;
		if (width < title.getPreferredSize().width + titlePadding * 2)
			width = title.getPreferredSize().width + titlePadding * 2;
		
		return width;
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(getInnerWidth() + margin.x * 2,
				title.getPreferredSize().height + tree.getPreferredSize().height
						+ (padding.y + margin.y) * 2);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		if (expanded) {
			g.drawRect(
					margin.x,
					margin.y + title.getPreferredSize().height,
					getInnerWidth(),
					tree.getPreferredSize().height + padding.y * 2);
		}
		
		g.drawRect(margin.x, margin.y, title.getPreferredSize().width + titlePadding * 2,
				title.getPreferredSize().height);
		
	}
	
	public TreeView getTree() {
		return tree;
	}
	
	public JTextArea getTitle() {
		return title;
	}
	
}
