package org.rtens.cell.gui.browser;

import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;

import javax.swing.*;

import org.rtens.cell.*;
import org.rtens.cell.glue.PathFormat;
import org.rtens.cell.gui.*;

public class PeerView extends JComponent {
	
	private TreeView tree;
	private Browser browser;
	
	private Address address;
	private CellConnector cell;
	
	private JLabel name;
	
	private boolean transparent;
	private Color color = Color.black;
	private Dimension size = new Dimension(40, 40);
	
	public LinkedList<ClickedPeerListener> clickedPeerListeners = new LinkedList<ClickedPeerListener>();
	
	public PeerView(Browser b, Address address, TreeView tree) {
		this(b, address, tree, b.getCellConnector(address));
	}
	
	public PeerView(Browser b, Address address, TreeView tree, CellConnector connector) {
		setLayout(new AbsoluteLayout());
		setPreferredSize(size);
		
		this.tree = tree;
		this.address = address;
		this.browser = b;
		
		cell = connector;
		
		name = new JLabel(address.getLast());
		add(name);
		
		if (name.getPreferredSize().width <= getPreferredSize().width) {
			name.setLocation((getPreferredSize().width - name.getPreferredSize().width) / 2,
						(getPreferredSize().height - name.getPreferredSize().height) / 2);
		} else {
			name.setLocation(2,
						(getPreferredSize().height - name.getPreferredSize().height) / 2);
		}
		
		addListeners();
		updateTooltip();
	}
	
	public Address getAddress() {
		return address;
	}
	
	public HostView getHost() {
		if (tree != null)
			return tree.getHost();
		
		return null;
	}
	
	public TreeView getTree() {
		return tree;
	}
	
	public CellConnector getCell() {
		return cell;
	}
	
	public void setTransparent(boolean to) {
		transparent = to;
	}
	
	private void addListeners() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					if (e.isAltDown()) {
						new Actions(e).editReaction();
					} else {
						if (tree != null) {
							if (tree.isExpanded()) {
								if (e.isShiftDown()) {
									tree.collapseWholeTree();
								} else {
									tree.collapse();
								}
							} else {
								if (e.isShiftDown()) {
									tree.expandWholeTree();
								} else {
									tree.expand();
								}
							}
						}
					}
				} else if (e.getClickCount() == 1) {
					for (ClickedPeerListener l : clickedPeerListeners)
						l.clickedOn(PeerView.this);
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				new Dragger(browser, PeerView.this, e.getPoint());
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					new Actions(e).getMenu().show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
	}
	
	public void delete() {
		if (tree != null) {
			for (TreeView child : new LinkedList<TreeView>(tree.getChildren())) {
				child.getRoot().delete();
			}
			
			tree.getParentTree().getRoot().getCell().removeChild(getCell().getName());
			
			if (tree.getParentTree() != null)
				tree.getParentTree().removeChild(tree);
		}
	}
	
	public void select(Color color) {
		this.color = color;
		
		revalidate();
		browser.repaint();
	}
	
	public void unselect() {
		select(Color.black);
	}
	
	public void updateTooltip() {
		setToolTipText(address + (cell.isConnected() ? " : " + cell.getStem() : ""));
	}
	
	@Override
	public void paint(Graphics g) {
		
		if (transparent)
			((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		
		Color fill;
		if (!cell.isConnected())
			fill = new Color(0xEE, 0xEE, 0xEE);
		else if (!cell.isActive())
			fill = Color.lightGray;
		else fill = Color.white;
		
		g.setColor(fill);
		g.fillArc(0, 0, size.width, size.height, 0, 360);
		
		g.setColor(color);
		g.drawArc(0, 0, size.width, size.height, 0, 360);
		
		if (cell.getReaction() != null) {
			g.drawArc(size.width - 5, size.height - 5, 5, 5, 0, 360);
		}
		
		if (cell.getStem() != null) {
			g.drawLine(size.width - 5, 0, size.width, 0);
			g.drawLine(size.width, 0, size.width, 5);
			g.drawLine(size.width, 5, size.width - 5, 0);
		}
		
		if (tree != null) {
			g.drawString((tree.isExpanded() ? "-" : "+"), 0, getPreferredSize().height);
		}
		
		super.paint(g);
	}
	
	@Override
	public String toString() {
		return getAddress().toString();
	}
	
	private class Actions {
		
		private MouseEvent event;
		
		public Actions(MouseEvent e) {
			this.event = e;
		}
		
		public JPopupMenu getMenu() {
			final JPopupMenu menu = new JPopupMenu();
			menu.add(getAddress().toString());
			
			menu.add(new JSeparator());
			
			if (!tree.isExpanded()) {
				menu.add(new JMenuItem(new AbstractAction("Expand (double-click)") {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						expand();
					}
				}));
				
				menu.add(new JMenuItem(new AbstractAction("Expand sub-tree (Shift + double-click)") {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						expandSubTree();
					}
				}));
			} else {
				
				menu.add(new JMenuItem(new AbstractAction("Collapse (double-click)") {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						collapse();
					}
				}));
				
				menu.add(new JMenuItem(new AbstractAction("Collapse sub-tree (Shift + double-click)") {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						collapseSubTree();
					}
				}));
			}
			
			menu.add(new JSeparator());
			
			if (cell.isActive()) {
				menu.add(new JMenuItem(new AbstractAction("Deactivate") {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						deactivate();
					}
				}));
			} else {
				menu.add(new JMenuItem(new AbstractAction("Activate") {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						activate();
					}
				}));
			}
			
			menu.add(new JSeparator());
			
			menu.add(new JMenuItem(new AbstractAction("New child") {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					newChild();
				}
			}));
			
			menu.add(new JMenuItem(new AbstractAction("Delete") {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					delete();
				}
			}));
			
			menu.add(new JSeparator());
			
			if (cell.getReaction() == null) {
				menu.add(new JMenuItem(new AbstractAction("Set Reaction (Alt + double-click)") {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						addReaction();
					}
				}));
			} else {
				menu.add(new JMenuItem(new AbstractAction("Edit Reaction") {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						editReaction();
					}
				}));
				
				menu.add(new JMenuItem(new AbstractAction("Remove Reaction") {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						removeReaction();
					}
				}));
			}
			
			menu.add(new JSeparator());
			
			if (cell.getStem() == null) {
				menu.add(new JMenuItem(new AbstractAction("Set Stem") {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						addStem();
					}
				}));
			} else {
				menu.add(new JMenuItem(new AbstractAction("Edit Stem") {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						editStem();
					}
				}));
				menu.add(new JMenuItem(new AbstractAction("Remove Stem") {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						removeStem();
					}
				}));
			}
			
			menu.add(new JSeparator());
			
			menu.add(new JMenuItem(new AbstractAction("New Peer") {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					newPeer();
				}
			}));
			
			if (cell.isConnected() && cell.getPeers().size() > 0) {
				JMenu peerMenu = new JMenu("Remove Peer");
				menu.add(peerMenu);
				
				final class PeerDeleter extends AbstractAction {
					private Address delete;
					
					public PeerDeleter(Address addr) {
						super(addr.toString());
						this.delete = addr;
					}
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						removePeer(delete);
					}
				}
				
				for (Address addr : cell.getPeers()) {
					peerMenu.add(new PeerDeleter(addr));
				}
			}
			
			return menu;
		}
		
		protected void activate() {
			cell.setActive(true);
			
			revalidate();
			browser.repaint();
		}
		
		protected void deactivate() {
			cell.setActive(false);
			
			revalidate();
			browser.repaint();
		}
		
		protected void newPeer() {
			String hostId = JOptionPane.showInputDialog(browser, "Enter Host ID of new peer.");
			
			if (hostId != null) {
				try {
					Address peerAddr = new Address(Host.parse(hostId), getAddress());
					cell.addPeer(peerAddr);
					
					revalidate();
					browser.repaint();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(browser, "Parsing error: " + e.getMessage());
				}
			}
		}
		
		protected void removePeer(Address delete) {
			cell.removePeer(delete);
			
			revalidate();
			browser.repaint();
		}
		
		protected void removeStem() {
			cell.setStem(null);
			
			revalidate();
			browser.repaint();
		}
		
		protected void removeReaction() {
			cell.setReaction(null);
			
			revalidate();
			browser.repaint();
		}
		
		protected void editStem() {
			editStem(cell.getStem());
		}
		
		protected void addStem() {
			editStem(null);
		}
		
		private void editStem(Path stem) {
			
			final JDialog d = new JDialog();
			d.getContentPane().setLayout(new BoxLayout(d.getContentPane(), BoxLayout.Y_AXIS));
			d.setTitle("Stem cell path of " + address);
			
			final JTextField input = (stem == null ? new JTextField(20) : new JTextField(PathFormat.format(stem)));
			d.add(input);
			
			Box buttons = Box.createHorizontalBox();
			d.add(buttons);
			
			final ClickedPeerListener peerClickListener = new ClickedPeerListener() {
				
				@Override
				public void clickedOn(PeerView p) {
					input.setText(PathFormat.format(p.getAddress()));
					d.requestFocus();
				}
				
			};
			browser.clickedPeerListeners.add(peerClickListener);
			
			buttons.add(new JButton(new AbstractAction("OK") {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					String path = input.getText();
					if (!path.isEmpty()) {
						try {
							cell.setStem(PathFormat.parse(path));
							updateTooltip();
							
							d.setVisible(false);
							browser.clickedPeerListeners.remove(peerClickListener);
							
							revalidate();
							browser.repaint();
						} catch (Exception e) {
							JOptionPane.showMessageDialog(browser, "Parsing error: " + e.getMessage());
						}
					}
				}
			}));
			
			buttons.add(new JButton(new AbstractAction("Cancel") {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					d.setVisible(false);
					browser.clickedPeerListeners.remove(peerClickListener);
				}
			}));
			
			Point point = event.getPoint();
			point.translate(browser.getLocationOnScreen().x, browser.getLocationOnScreen().y);
			d.setLocation(point);
			
			d.pack();
			d.setVisible(true);
		}
		
		protected void editReaction() {
			if (cell.getReaction() != null && !(cell.getReaction() instanceof Reaction)) {
				JOptionPane.showMessageDialog(browser,
						"The reaction of this cell is native. Can't edit a native reaction.");
				return;
			}
			
			final JFrame d = new JFrame();
			d.setLayout(new BorderLayout());
			d.setTitle("Reaction of " + address);
			
			final ReactionEditor editor = new ReactionEditor(browser, getAddress(), cell.getReaction());
			JScrollPane sp = new JScrollPane(editor);
			sp.setPreferredSize(new Dimension(500, 200));
			d.add(sp);
			
			Box buttons = Box.createHorizontalBox();
			d.add(buttons, BorderLayout.SOUTH);
			
			buttons.add(new JButton(new AbstractAction("OK") {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					try {
						cell.setReaction(editor.getReaction());
						d.setVisible(false);
					} catch (Exception e) {
						JOptionPane.showMessageDialog(browser, "Parsing Error: " + e.getMessage());
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
			point.translate(browser.getLocationOnScreen().x, browser.getLocationOnScreen().y);
			d.setLocation(point);
			
			d.pack();
			d.setVisible(true);
			
			revalidate();
			browser.repaint();
		}
		
		protected void addReaction() {
			editReaction();
		}
		
		protected void expand() {
			tree.expand();
		}
		
		protected void expandSubTree() {
			tree.expandWholeTree();
		}
		
		protected void collapse() {
			tree.collapse();
		}
		
		protected void collapseSubTree() {
			tree.collapseWholeTree();
		}
		
		protected void delete() {
			PeerView.this.delete();
		}
		
		protected void newChild() {
			if (!cell.isConnected()) return;
			
			String name = JOptionPane.showInputDialog(browser, "Name of new cell");
			
			if (name != null && !name.isEmpty()) {
				cell.addChild(name);
				TreeView child = tree.addChild(name);
				child.getRoot().getCell().setStem(new Path("°", "Cell"));
				child.getRoot().updateTooltip();
				
				revalidate();
				browser.repaint();
			}
		}
	}
}
