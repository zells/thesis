package org.zells.glue;

import java.io.*;
import java.util.LinkedList;

import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.*;
import org.zells.*;

/**
 * Format that is read/saved
 * 
 * <cell [native="true"]> [<stem>PATH</stem>] [<reaction [native="true"]>REACTION</reaction>]
 * {<peer> <network>NETWORK</network> <host>HOST</host> </peer>} </cell>
 */
public class CellLoader {
	
	private static final String suffix = ".cell";
	private static final String classNamePackage = "org.zells.library";
	
	private File rootFolder;
	
	public CellLoader(File rootFolder) {
		this.rootFolder = rootFolder;
	}
	
	public LinkedList<Cell> getChildren(Cell parent) {
		File childFolder = getFolder(parent);
		LinkedList<Cell> children = new LinkedList<Cell>();
		
		if (childFolder.exists()) {
			for (File childFile : childFolder.listFiles()) {
				if (childFile.isFile() && childFile.getName().endsWith(suffix)) {
					try {
						String name = childFile.getName();
						name = name.substring(0, name.length() - suffix.length());
						
						children.add(getChild(parent, name));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return children;
	}
	
	public Cell getChild(Cell parent, String name) throws Exception {
		Element eCell = new SAXBuilder().build(new File(getFolder(parent), name + suffix)).getRootElement();
		
		Cell child;
		if (eCell.getName().equals("execution")) {
			child = new Execution(parent, name, getMessage(eCell));
		} else if (eCell.getAttribute("native") != null && eCell.getAttributeValue("native").equals("true")) {
			child = getNativeCellInstance(getClassName(parent, name) + "Cell", parent, name);
		} else {
			child = new Cell(parent, name);
		}
		
		Path stem = getStem(eCell);
		if (stem != null) child.setStem(stem);
		
		NativeReaction reaction = getReaction(eCell, parent, name);
		if (reaction != null) child.setReaction(reaction);
		
		child
				.setActive(true)
				.setLoader(this);
		
		for (Peer peer : getPeers(eCell, child)) {
			child.addPeer(peer);
		}
		
		return child;
	}
	
	private Path getMessage(Element eCell) throws Exception {
		Element eMessage = eCell.getChild("message");
		if (eMessage != null)
			return PathFormat.parse(eMessage.getValue());
		else return null;
	}
	
	public Path getStem(Element eCell) throws Exception {
		Element eStem = eCell.getChild("stem");
		if (eStem != null)
			return PathFormat.parse(eStem.getValue());
		else return null;
	}
	
	public NativeReaction getReaction(Element eCell, Cell parent, String name) throws Exception {
		Element eReaction = eCell.getChild("reaction");
		
		if (eReaction != null) {
			if (eReaction.getAttribute("native") != null && eReaction.getAttributeValue("native").equals("true"))
				return getNativeReactionInstance(getClassName(parent, name) + "Reaction");
			else return ReactionFormat.parse(eReaction.getValue());
		}
		
		return null;
	}
	
	private LinkedList<Peer> getPeers(Element eCell, Cell child) {
		LinkedList<Peer> peers = new LinkedList<Peer>();
		for (Object o : eCell.getChildren("peer")) {
			try {
				Element ePeer = (Element) o;
				Address address = new Address(ePeer.getChildText("network"), ePeer.getChildText("host"));
				peers.add(getPeerInstance(child, address));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return peers;
	}
	
	protected Cell getNativeCellInstance(String className, Cell parent, String name) {
		try {
			Class<?> clas = Class.forName(className);
			return (Cell) clas.getConstructor(Cell.class, String.class).newInstance(parent, name);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected NativeReaction getNativeReactionInstance(String className) {
		try {
			Class<?> clas = Class.forName(className);
			return (NativeReaction) clas.getConstructor().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected Peer getPeerInstance(Cell child, Address address) throws Exception {
		return Peer.create(child, address);
	}
	
	private String getClassName(Cell parent, String name) {
		String className = classNamePackage;
		
		Path path = parent.getPath();
		for (int i = 1; i < path.size(); i++)
			className += "." + path.get(i);
		
		return className + "." + name;
	}
	
	private File getFolder(Cell cell) {
		if (cell == null) return rootFolder;
		File folder = rootFolder;
		
		for (String name : cell.getPath()) {
			folder = new File(folder, name);
		}
		
		return folder;
	}
	
	private File getFile(Cell cell) {
		return new File(getFolder(cell).getParentFile(), cell.getName() + suffix);
	}
	
	public void save(Cell cell) {
		Element eCell = new Element("cell");
		
		if (cell.getClass() == Execution.class) {
			eCell.setName("execution");
			eCell.addContent(new Element("message")
					.addContent(new Text(PathFormat.format(((Execution) cell).getMessage()))));
			// DONT SAVE EXECUTIONS
			return;
		} else if (cell.getClass() != Cell.class) {
			eCell.setAttribute("native", "true");
		}
		
		if (cell.getStem() != null)
			eCell.addContent(new Element("stem").addContent(new Text(PathFormat.format(cell.getStem()))));
		
		NativeReaction reaction = cell.getReaction();
		if (reaction != null) {
			Element eReaction = new Element("reaction");
			
			if (reaction instanceof Reaction) {
				eReaction.addContent(ReactionFormat.format((Reaction) reaction, "\t"));
			} else {
				eReaction.setAttribute("native", "true");
			}
			
			eCell.addContent(eReaction);
		}
		
		for (Peer peer : cell.getPeers()) {
			Element ePeer = new Element("peer");
			eCell.addContent(ePeer);
			
			ePeer.addContent(new Element("network").setText(peer.getAddress().network));
			ePeer.addContent(new Element("host").setText(peer.getAddress().host));
		}
		
		try {
			File file = getFile(cell);
			if (!file.getParentFile().exists())
				file.getParentFile().mkdirs();
			
			if (!file.exists())
				file.createNewFile();
			
			OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
			new XMLOutputter(Format.getPrettyFormat()).output(new Document(eCell), out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void delete(Cell cell) {
		File parentFolder = getFolder(cell.getParent());
		File file = new File(parentFolder, cell.getName() + suffix);
		File folder = new File(parentFolder, cell.getName());
		
		if (file.exists())
			file.delete();
		
		if (folder.exists())
			deleteFolderAndContent(folder);
	}
	
	private void deleteFolderAndContent(File folder) {
		for (File f : folder.listFiles()) {
			if (f.isDirectory())
				deleteFolderAndContent(f);
			else f.delete();
		}
		
		folder.delete();
	}
	
}
