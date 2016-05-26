package org.rtens.cell.gui.browser;

import java.awt.*;
import java.awt.event.*;

import javax.swing.JTextArea;

import org.rtens.cell.*;
import org.rtens.cell.glue.*;
import org.rtens.cell.gui.Browser;

public class ReactionEditor extends JTextArea {
	
	private Reaction reaction;
	private Browser browser;
	private Path path;
	
	public ReactionEditor(Browser browser, Path path, Reaction reaction) {
		setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
		this.reaction = reaction;
		this.browser = browser;
		this.path = path;
		
		fill();
		
		setListeners();
	}
	
	public Reaction getReaction() throws Exception {
		parse();
		return reaction;
	}
	
	private void refresh() {
		try {
			parse();
			setForeground(Color.black);
			setToolTipText(null);
		} catch (Exception e) {
			setForeground(Color.red);
			setToolTipText(e.getMessage());
		}
	}
	
	private void fill() {
		if (reaction != null) {
			setText(ReactionFormat.format(reaction));
		} else {
			setText("");
		}
	}
	
	private void parse() throws Exception {
		reaction = ReactionFormat.parse(getText());
	}
	
	private void setListeners() {
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				refresh();
			}
		});
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				browser.clearSelections();
				
				if (e.isAltDown()) {
					try {
						Path selected = PathFormat.parse(getText().substring(getSelectionStart(), getSelectionEnd()));
						if (selected.isEmpty()) return;
						
						Result r = browser.getRoot().send(
								new Send(new Path(path, selected, new Path("cell")), new Path("°")),
								new Context(null, new Path("°"), new Send(new Path(), new Path())));
						
						if (r.wasDelivered()) {
							Address addr = new Address(r.getReceiver());
							addr.removeLast();
							
							browser.getPeerView(addr, true).select(Color.red);
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
	}
	
}
