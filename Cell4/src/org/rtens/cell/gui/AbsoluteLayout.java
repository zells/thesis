package org.rtens.cell.gui;

import java.awt.*;

public class AbsoluteLayout implements LayoutManager {
	
	public void addLayoutComponent(final String s, final Component component) {
	// Nothing
	}
	
	public void removeLayoutComponent(final Component component) {
	// Nothing
	}
	
	public Dimension preferredLayoutSize(final Container container) {
		int width = 0;
		int height = 0;
		
		for (Component component : container.getComponents()) {
			
			int maxX = component.getLocation().x + component.getPreferredSize().width;
			if (maxX > width)
				width = maxX;
			
			int maxY = component.getLocation().y + component.getPreferredSize().height;
			if (maxY > height)
				height = maxY;
			
		}
		return new Dimension(width, height);
	}
	
	public Dimension minimumLayoutSize(final Container container) {
		return preferredLayoutSize(container);
	}
	
	public void layoutContainer(final Container container) {
		for (Component c : container.getComponents()) {
			Dimension size = c.getPreferredSize();
			size.height++;
			size.width++;
			
			c.setBounds(new Rectangle(c.getLocation(), size));
		}
	}
	
	public Dimension maximumLayoutSize(final Container container) {
		return new Dimension(0x7fffffff, 0x7fffffff);
	}
	
	public float getLayoutAlignmentX(final Container container) {
		return 0.0F;
	}
	
	public float getLayoutAlignmentY(final Container container) {
		return 0.0F;
	}
	
	public void invalidateLayout(final Container container) {
		System.out.println("Invalidate");
	}
}