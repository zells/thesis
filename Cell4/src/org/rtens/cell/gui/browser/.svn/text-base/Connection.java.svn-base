package org.rtens.cell.gui.browser;

import java.awt.*;

import javax.swing.JComponent;

import org.rtens.cell.gui.Browser;

public class Connection {
	
	public static void paint(Graphics g, Browser browser, PeerView from, PeerView to) {
		if (!from.isDisplayable()) return;
		
		Point fromCenter = getCenter(from, browser);
		
		Point toCenter = to.isDisplayable()
				? getCenter(to, browser)
				: getCenter(to.getHost().getTitle(), browser);
		
		double dist = fromCenter.distance(toCenter);
		Point dif = new Point(toCenter.x - fromCenter.x, toCenter.y - fromCenter.y);
		
		int transX = (int) (dif.x / dist * (from.getPreferredSize().width / 2 + 4));
		int transY = (int) (dif.y / dist * (from.getPreferredSize().width / 2 + 4));
		
		Point p1 = new Point(fromCenter.x + transX, fromCenter.y + transY);
		Point p2 = new Point(toCenter.x - transX, toCenter.y - transY);
		
		g.drawLine(p1.x, p1.y, p2.x, p2.y);
		g.fillArc(p2.x - 3, p2.y - 3, 6, 6, 0, 360);
	}
	
	private static Point getCenter(JComponent c, Browser browser) {
		Point cLoc = getLocationRelativeTo(c, browser);
		
		return new Point(cLoc.x + c.getPreferredSize().width / 2,
				cLoc.y + c.getPreferredSize().height / 2);
	}
	
	private static Point getLocationRelativeTo(JComponent relative, JComponent to) {
		Point loc = new Point();
		Container parent = relative;
		while (parent != to) {
			loc.translate(parent.getLocation().x, parent.getLocation().y);
			parent = parent.getParent();
		}
		
		return loc;
	}
}
