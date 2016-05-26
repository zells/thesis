package org.zells.gui.browser;

import java.awt.*;

import javax.swing.JComponent;

import org.zells.Address;
import org.zells.gui.*;

public class Connection extends JComponent {
	
	private PeerView from;
	private Address to;
	
	private PeerView toView;
	private Browser browser;
	
	public Connection(PeerView from, Address to) {
		this.from = from;
		this.to = to;
		
		setLayout(new AbsoluteLayout());
	}
	
	public void setBrowser(Browser browser) {
		this.browser = browser;
		
		setPreferredSize(browser.getPreferredSize());
		
		toView = browser.getHostView(to, true)
				.getPeerView(from.getPath(), true);
		toView.setModel(from.getModel().getPeerModel(to));
	}
	
	@Override
	public void paint(Graphics g) {
		
		setPreferredSize(browser.getPreferredSize());

		Point fromCenter = getCenter(from);
		
		Point toCenter = toView.isShowing()
				? getCenter(toView)
				: getCenter(toView.getHost().getTitle());
		
		double dist = fromCenter.distance(toCenter);
		Point dif = new Point(toCenter.x - fromCenter.x, toCenter.y - fromCenter.y);
		
		int transX = (int) (dif.x / dist * (from.getPreferredSize().width / 2 + 4));
		int transY = (int) (dif.y / dist * (from.getPreferredSize().width / 2 + 4));
		
		Point p1 = new Point(fromCenter.x + transX, fromCenter.y + transY);
		Point p2 = new Point(toCenter.x - transX, toCenter.y - transY);
		
		g.drawLine(p1.x, p1.y, p2.x, p2.y);
		g.fillArc(p2.x - 3, p2.y - 3, 6, 6, 0, 360);
		
		super.paint(g);
	}
	
	private Point getCenter(JComponent c) {
		Point cLoc = getLocationRelativeTo(c, browser);
		
		return new Point(cLoc.x + c.getPreferredSize().width / 2,
				cLoc.y + c.getPreferredSize().height / 2);
	}
	
	private Point getLocationRelativeTo(JComponent relative, JComponent to) {
		Point loc = new Point();
		Container parent = relative;
		while (parent != to) {
			loc.translate(parent.getLocation().x, parent.getLocation().y);
			parent = parent.getParent();
		}
		
		return loc;
	}
	
}
