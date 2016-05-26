package org.zells.gui;

import java.awt.Dimension;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import org.zells.*;
import org.zells.glue.CellLoader;

public class MainView extends JFrame {
	
	public static void main(String[] args) {
		try {
			File cellFolder = (args.length == 0 ? new File("library/cells") : new File(args[0]));
			
			if (!cellFolder.exists())
				cellFolder.mkdir();
			
			new MainView(cellFolder, args);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private Cell rootCell;
	private File rootFolder;
	
	private SendView sendView;
	private PeerModel rootModel;
	
	public MainView(File cellFolder, String[] args) throws Exception {
		setFolder(cellFolder);
		
		rootModel = PeerModel.getRootModel(rootCell);
		
		buildView();
		buildMenu();
		
		Messenger.listener = new MessengerListener(rootModel);
		
		if (args.length == 2) {
			Map<String, String> params = new HashMap<String, String>();
			params.put("Hostname", "localhost");
			params.put("Port", args[1]);
			
			Server.getServer("Socket", rootCell).start(params);
		}
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		pack();
		setVisible(true);
	}
	
	public void setFolder(File folder) {
		try {
			setTitle("Cell - " + folder.getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		rootFolder = folder;
		CellLoader loader = new CellLoader(rootFolder);
		try {
			rootCell = loader.getChild(null, "°");
		} catch (Exception e) {
			rootCell = new Cell(null, "°");
			rootCell.setStem(new Path("°", "Cell"));
			rootCell.setLoader(loader);
			rootCell.setActive(true);
			
			rootCell.addChild("Cell")
					.setStem(new Path("°", "Cell"))
					.setReaction(new Reaction())
					.setActive(true);
		}
	}
	
	private void buildView() {
		sendView = new SendView(rootCell);
		add(sendView);
		
		setMinimumSize(new Dimension(sendView.getPreferredSize().width / 2, sendView.getPreferredSize().height + 50));
	}
	
	private void buildMenu() {
		JMenuBar bar = new JMenuBar();
		setJMenuBar(bar);
		
		JMenu fileMenu = new JMenu("File");
		bar.add(fileMenu);
		
		fileMenu.add(new JMenuItem(new AbstractAction("Change Folder") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser(rootFolder);
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = fc.showOpenDialog(MainView.this);
				
				if (returnVal == JFileChooser.APPROVE_OPTION)
					setFolder(fc.getSelectedFile());
			}
		}));
		
		fileMenu.add(new JMenuItem(new AbstractAction("Reload") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				rootCell.reloadChildren();
			}
		}));
		
		fileMenu.add(new JMenuItem(new AbstractAction("Exit") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		}));
		
		JMenu serverMenu = new JMenu("Server");
		bar.add(serverMenu);
		
		for (Server s : Server.getServers(rootCell)) {
			serverMenu.add(new ServerMenu(s));
		}
		
		JMenu viewMenu = new JMenu("View");
		bar.add(viewMenu);
		
		viewMenu.add(new JMenuItem(new AbstractAction("New browser") {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Browser browser = new Browser(rootModel);
				
				sendView.setBrowser(browser);
				
				JFrame f = new JFrame("Cell Browser - " + rootFolder);
				f.add(new JScrollPane(browser));
				f.setSize(500, 400);
				f.setVisible(true);
				
				f.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent arg0) {
						sendView.setBrowser(null);
					}
				});
			}
		}));
	}
	
	private class ServerMenu extends JMenu implements ChangeListener {
		
		private Server server;
		
		private JMenuItem start;
		private JMenuItem stop;
		private JMenuItem showLog;
		
		private JFrame logFrame;
		private JTextArea logTextArea;
		private JScrollPane logScrollPane;
		
		private JDialog paramDialog;
		
		public ServerMenu(Server s) {
			super(s.getAddress().network);
			
			this.server = s;
			
			s.listeners.add(this);
			
			buildView();
			buildMenu();
		}
		
		protected void start() {
			paramDialog.setVisible(true);
		}
		
		protected void start(Map<String, String> params) {
			try {
				server.start(params);
				paramDialog.setVisible(false);
				
				showLog();
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(paramDialog, "Could not start server: " + e.getMessage());
			}
		}
		
		protected void stop() {
			server.stop();
		}
		
		protected void showLog() {
			logFrame.setVisible(true);
		}
		
		private void buildView() {
			paramDialog = new JDialog(MainView.this, "Server Parameters", true);
			
			paramDialog.getContentPane().setLayout(new BoxLayout(paramDialog.getContentPane(), BoxLayout.Y_AXIS));
			
			final Map<String, JTextField> fields = new LinkedHashMap<String, JTextField>();
			
			for (String paramName : server.getParameters().keySet()) {
				paramDialog.add(Box.createVerticalStrut(5));
				Box box = Box.createHorizontalBox();
				paramDialog.add(box);
				
				box.add(new JLabel(paramName + ":"));
				box.add(Box.createHorizontalStrut(10));
				
				JTextField field = new JTextField(15);
				field.setText(server.getParameters().get(paramName));
				field.setMaximumSize(new Dimension(field.getMaximumSize().width, field.getPreferredSize().height));
				box.add(field);
				
				fields.put(paramName, field);
			}
			
			Box box = Box.createHorizontalBox();
			paramDialog.add(Box.createVerticalStrut(10));
			paramDialog.add(box);
			
			box.add(Box.createHorizontalGlue());
			box.add(new JButton(new AbstractAction("Start Server") {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					Map<String, String> values = new HashMap<String, String>();
					for (String paramName : fields.keySet()) {
						values.put(paramName, fields.get(paramName).getText());
					}
					
					start(values);
				}
			}));
			
			box.add(Box.createHorizontalStrut(5));
			
			box.add(new JButton(new AbstractAction("Cancel") {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					paramDialog.setVisible(false);
				}
			}));
			
			paramDialog.pack();
			
			logTextArea = new JTextArea();
			
			logFrame = new JFrame("Log of " + server.getAddress().network);
			logScrollPane = new JScrollPane(logTextArea);
			logFrame.add(logScrollPane);
			logFrame.setSize(400, 200);
			
			server.logger = new Server.Logger() {
				
				@Override
				public void log(Server server, String message) {
					logTextArea.append(message + "\n");
					
					logTextArea.selectAll();
					int x = logTextArea.getSelectionEnd();
					logTextArea.select(x, x);
				}
			};
		}
		
		private void buildMenu() {
			start = new JMenuItem(new AbstractAction("Start") {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					start();
				}
			});
			add(start);
			
			stop = new JMenuItem(new AbstractAction("Stop") {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					stop();
				}
			});
			add(stop);
			stop.setEnabled(false);
			
			showLog = new JMenuItem(new AbstractAction("Show log") {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					showLog();
				}
			});
			add(showLog);
		}
		
		@Override
		public void stateChanged(ChangeEvent isAlwaysNull) {
			stop.setEnabled(server.isRunning());
			start.setEnabled(!server.isRunning());
		}
		
	}
	
}
