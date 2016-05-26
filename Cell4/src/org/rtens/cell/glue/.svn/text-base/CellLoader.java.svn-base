package org.rtens.cell.glue;

import java.io.*;
import java.util.LinkedList;

import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.*;
import org.rtens.cell.*;

public class CellLoader {
	
	private static final String suffix = ".cell";
	private File rootFolder;
	
	/**
	 * The CellLoader is used to load children of a cell.
	 * 
	 * The children are found inside the folder which corresponds to the cell's path relative to the
	 * given root folder.
	 */
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
		
		File childFile = new File(getFolder(parent), name + suffix);
		Element eCell = new SAXBuilder().build(childFile).getRootElement();
		
		Cell child;
		if (eCell.getAttribute("native") != null && eCell.getAttributeValue("native").equals("true")) {
			child = getNativeInstance(getClassName(parent, name) + "Cell", parent, name);
		} else {
			child = new Cell(parent, name);
		}
		
		child.setStem(getStem(eCell))
				.setReaction(getReaction(eCell, parent, name))
				.setActive(true)
				.setLoader(this);
		
		for (Peer peer : getPeers(eCell, child)) {
			child.addPeer(peer);
		}
		
		return child;
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
	
	private File getFolder(Cell cell) {
		File folder = rootFolder;
		
		if (cell != null) {
			Path path = cell.getPath();
			
			for (int i = 0; i < path.size(); i++)
				folder = new File(folder, path.get(i));
		}
		
		return folder;
	}
	
	private File getFile(Cell cell) {
		return new File(getFolder(cell).getParentFile(), cell.getName() + suffix);
	}
	
	private LinkedList<Peer> getPeers(Element eCell, Cell child) {
		LinkedList<Peer> peers = new LinkedList<Peer>();
		for (Object o : eCell.getChildren("peer")) {
			Element ePeer = (Element) o;
			try {
				Address address = new Address(ePeer.getChildText("network"), ePeer.getChildText("host"),
						child.getPath());
				
				peers.add(getPeerInstance(address, child));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return peers;
	}
	
	protected Peer getPeerInstance(Address address, Cell child) throws Exception {
		return child.getConnections().getPeer(address);
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
			else return parseReaction(eReaction.getValue());
		}
		
		return null;
	}
	
	private Reaction parseReaction(String reactionString) throws Exception {
		return ReactionFormat.parse(reactionString);
	}
	
	private String getClassName(Cell parent, String name) {
		String className = "org.rtens.cell.library";
		
		Path path = parent.getPath();
		for (int i = 1; i < path.size(); i++)
			className += "." + path.get(i);
		
		return className + "." + name;
	}
	
	protected Cell getNativeInstance(String className, Cell parent, String name) {
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
	
	public void save(Cell cell) {
		Element eCell = new Element("cell");
		
		if (cell.getClass() != Cell.class)
			eCell.setAttribute("native", "true");
		
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
			
			ePeer.addContent(new Element("network").setText(peer.getAddress(null).host.network));
			ePeer.addContent(new Element("host").setText(peer.getAddress(null).host.hostAddress));
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
	
}
