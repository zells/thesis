package org.zells.gui.browser;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.zells.*;
import org.zells.glue.PathFormat;
import org.zells.gui.PeerModel;

public class PeerActionMenu extends JPopupMenu {
	
	private PeerView view;
	private MouseEvent event;
	
	public PeerActionMenu(MouseEvent event, PeerView peerView) {
		this.view = peerView;
		this.event = event;
		
		add(view.getHost().getAddresses() + view.getPath().toString());
		
		final PeerModel m = view.getModel();
		if (m != null)
			add(m.getPath().toString());
		
		add(new JSeparator()); //------------------------------------------------
		
		String expandCollapse = view.tree.controller.isExpanded() ? "Collapse" : "Expand";
		
		add(new JMenuItem(new AbstractAction(expandCollapse + " (double-click)") {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				view.tree.controller.toggleExpansion();
			}
		}));
		
		add(new JMenuItem(new AbstractAction(expandCollapse + " sub-tree (Shift + double-click)") {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				view.tree.controller.toggleExpansionWholeTree();
			}
		}));
		
		if (m != null) {
			add(new JSeparator()); //------------------------------------------------
			
			add(new JMenuItem(new AbstractAction("New Child") {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					newChild();
				}
			}));
			
			add(new JMenuItem(new AbstractAction("Delete") {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					delete();
				}
			}));
			add(new JSeparator()); //------------------------------------------------
			
			if (m.getStem() == null) {
				add(new JMenuItem(new AbstractAction("Add Stem") {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						changeStem(new Path());
					}
				}));
			} else {
				add(new JMenuItem(new AbstractAction("Change Stem") {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						changeStem(m.getStem());
					}
				}));
				add(new JMenuItem(new AbstractAction("Remove Stem") {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						removeStem();
					}
				}));
			}
			
			add(new JSeparator()); //------------------------------------------------
			
			if (m.getReaction() == null) {
				add(new JMenuItem(new AbstractAction("Add Reaction") {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						changeReaction(new Reaction());
					}
				}));
			} else {
				add(new JMenuItem(new AbstractAction("Change Reaction") {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						changeReaction(m.getReaction());
					}
				}));
				add(new JMenuItem(new AbstractAction("Remove Reaction") {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						removeReaction();
					}
				}));
			}
		}
	}
	
	protected void removeReaction() {
		view.getModel().setReaction(null);
		view.repaint();
	}
	
	protected void changeReaction(Reaction current) {
		
		if (view.getModel().getReaction() != null && !(view.getModel().getReaction() instanceof Reaction)) {
			JOptionPane.showMessageDialog(view,
					"The reaction of this cell is native. Can't edit a native reaction.");
			return;
		}
		
		final JFrame d = new JFrame();
		d.setLayout(new BorderLayout());
		d.setTitle("Reaction of " + view.getModel().getPath());
		
		final ReactionEditor editor = new ReactionEditor(current);
		JScrollPane sp = new JScrollPane(editor);
		sp.setPreferredSize(new Dimension(500, 200));
		d.add(sp);
		
		Box buttons = Box.createHorizontalBox();
		d.add(buttons, BorderLayout.SOUTH);
		
		buttons.add(new JButton(new AbstractAction("OK") {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					view.getModel().setReaction(editor.getReaction());
					d.setVisible(false);
					view.repaint();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(view, "Parsing Error: " + e.getMessage());
				}
			}
		}));
		
		buttons.add(new JButton(new AbstractAction("Cancel") {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				d.setVisible(false);
			}
		}));
		
		Point point = event.getPoint();
		point.translate(view.getLocationOnScreen().x, view.getLocationOnScreen().y);
		d.setLocation(point);
		
		d.pack();
		d.setVisible(true);
		
	}
	
	protected void removeStem() {
		view.getModel().setStem(null);
		view.repaint();
	}
	
	protected void changeStem(Path current) {
		String stem = JOptionPane.showInputDialog(view, "Name of new cell", PathFormat.format(current));
		
		if (stem != null) {
			try {
				view.getModel().setStem(PathFormat.parse(stem));
				view.repaint();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(view, "Could no set stem: " + e.getMessage());
			}
		}
	}
	
	protected void newChild() {
		String name = JOptionPane.showInputDialog(view, "Name of new cell");
		
		if (name != null && !name.isEmpty()) {
			view.getModel().addChild(name);
			view.tree.refreshChildren();
		}
	}
	
	protected void delete() {
		view.tree.parentTree.rootView.getModel().removeChild(view.getCellName());
		
		if (view.tree.parentTree != null)
			view.tree.parentTree.removeChild(view.tree);
	}
	
	protected void editName() {
		final JDialog d = new JDialog();
		d.getContentPane().setLayout(new BoxLayout(d.getContentPane(), BoxLayout.Y_AXIS));
		d.setTitle("Rename " + view.getPath());
		
		final JTextField input = new JTextField(view.getCellName());
		d.add(input);
		
		Box buttons = Box.createHorizontalBox();
		d.add(buttons);
		
		buttons.add(new JButton(new AbstractAction("OK") {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				d.setVisible(false);
			}
		}));
		
		buttons.add(new JButton(new AbstractAction("Cancel") {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				d.setVisible(false);
			}
		}));
		
		Point point = event.getPoint();
		point.translate(view.browser.getLocationOnScreen().x, view.browser.getLocationOnScreen().y);
		d.setLocation(point);
		
		d.pack();
		d.setVisible(true);
	}
	
}
