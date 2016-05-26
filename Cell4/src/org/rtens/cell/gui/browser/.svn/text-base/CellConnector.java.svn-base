package org.rtens.cell.gui.browser;

import java.util.*;

import javax.swing.*;

import org.rtens.cell.*;

public class CellConnector {
	
	private Cell root;
	private Address address;
	
	private Map<String, Object> cache = new HashMap<String, Object>();
	
	private Path peerPath;
	
	public CellConnector(Cell root, Address address) {
		this.root = root;
		this.address = address;
		peerPath = new Path("cell", "Peers", address.host.toString(), "cell");
	}
	
	private void send(Path peerPath, Path receiver, Path message, boolean showError) throws Exception {
		Send send = new Send(new Path(address, peerPath, receiver), message);
		Result r = root.send(send, new Context(null, root.getPath(), new Send(new Path(), new Path())));
		if (!r.wasDelivered()) {
			Exception e = new Exception("Could not deliver " + send.getMessage() + " to " + send.getReceiver());
			
			if (showError) {
				JOptionPane.showMessageDialog(null, e.getMessage());
				System.err.println(e);
				System.err.println(r.log);
			}
			throw e;
		}
	}
	
	private void send(Path receiver, Path message, boolean showError) throws Exception {
		send(peerPath, receiver, message, showError);
	}
	
	private Path resolve(Path path, boolean trowException) throws Exception {
		final Path receiver = new Path(address, peerPath, path, new Path("cell"));
		
		Result r = root.send(new Send(receiver, new Path()),
				new Context(null, root.getPath(), new Send(new Path(), new Path())));
		
		if (!r.wasDelivered()) {
			if (trowException) {
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						JOptionPane.showMessageDialog(null, "Could not deliver to " + receiver);
					}
				});
				Exception e = new Exception("Could not connect to " + receiver);
				
				System.err.println("----------------------------- resolve " + receiver
						+ " ------------------------------");
				System.err.println(r.log);
				e.printStackTrace();
				
				throw e;
			} else {
				return null;
			}
		}
		
		Path resolved = new Path(r.getReceiver());
		resolved.removeLast();
		
		return resolved;
	}
	
	public void clearCache() {
		cache.clear();
	}
	
	public boolean isConnected() {
		if (!cache.containsKey("isConnected")) {
			try {
				cache.put("isConnected", resolve(new Path("Name"), false) != null);
			} catch (Exception e) {
				return false;
			}
		}
		
		return (Boolean) cache.get("isConnected");
	}
	
	public String getName() {
		if (!cache.containsKey("getName")) {
			Path namePath;
			try {
				namePath = resolve(new Path("Name", "stem"), true);
				cache.put("getName", namePath.getLast());
			} catch (Exception e) {
				return "n/a";
			}
		}
		return (String) cache.get("getName");
	}
	
	public boolean isActive() {
		if (!cache.containsKey("isActive")) {
			Path activeStem;
			try {
				activeStem = resolve(new Path("Active", "stem"), true);
				cache.put("isActive", activeStem.getLast().equals("True"));
			} catch (Exception e) {
				return false;
			}
		}
		return (Boolean) cache.get("isActive");
	}
	
	@SuppressWarnings("unchecked")
	public LinkedList<String> getChildren() {
		
		if (!cache.containsKey("getChildren")) {
			LinkedList<String> names = new LinkedList<String>();
			try {
				Path nameStringPath;
				for (int i = 0; (nameStringPath = resolve(new Path("Children", String.valueOf(i), "stem"), false)) != null; i++) {
					names.add(nameStringPath.getLast());
				}
			} catch (Exception e) {
				return names;
			}
			cache.put("getChildren", names);
		}
		return (LinkedList<String>) cache.get("getChildren");
	}
	
	public boolean hasChild(String name) {
		for (String childName : getChildren())
			if (childName.equals(name))
				return true;
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public LinkedList<Address> getPeers() {
		if (!cache.containsKey("getPeers")) {
			LinkedList<Address> addresses = new LinkedList<Address>();
			try {
				int i = 0;
				while (true) {
					Path nameStringPath = resolve(new Path("Peers", "cell", "Children", String.valueOf(i), "stem"),
							false);
					if (nameStringPath == null) break;
					
					addresses.add(new Address(Host.parse(nameStringPath.getLast()), address));
					
					i++;
				}
			} catch (Exception e) {
				return addresses;
			}
			cache.put("getPeers", addresses);
		}
		return (LinkedList<Address>) cache.get("getPeers");
	}
	
	public Path getStem() {
		if (!cache.containsKey("getStem")) {
			try {
				if (resolve(new Path("Stem"), false) == null) {
					cache.put("getStem", null);
				} else {
					cache.put("getStem", readPath(new Path("Stem")));
				}
			} catch (Exception e) {
				return null;
			}
		}
		return (Path) cache.get("getStem");
	}
	
	private Path readPath(Path of) throws Exception {
		Path nameStringPath;
		Path path = new Path();
		for (int i = 0; (nameStringPath = resolve(new Path(of, new Path(String.valueOf(i), "stem")), false)) != null; i++) {
			path.add(nameStringPath.getLast());
		}
		return path;
	}
	
	public Reaction getReaction() {
		if (!cache.containsKey("getReaction")) {
			try {
				if (resolve(new Path("Reaction"), false) == null) {
					cache.put("getReaction", null);
				} else {
					Reaction r = new Reaction();
					for (int i = 0; resolve(new Path("Reaction", String.valueOf(i)), false) != null; i++) {
						r.add(new Send(readPath(new Path("Reaction", String.valueOf(i), "Receiver")),
								readPath(new Path("Reaction", String.valueOf(i), "Message"))));
					}
					cache.put("getReaction", r);
				}
			} catch (Exception e) {
				return null;
			}
		}
		return (Reaction) cache.get("getReaction");
	}
	
	public void setActive(boolean to) {
		cache.remove("isActive");
		try {
			send(new Path("Active", "cell", "Stem", "clear"), new Path("°"), true);
			
			send(new Path("Active", "cell", "Stem", "add"), new Path("°", "Prophet", "Literal", "String", "°"), true);
			send(new Path("Active", "cell", "Stem", "add"), new Path("°", "Prophet", "Literal", "String", "Prophet"),
					true);
			send(new Path("Active", "cell", "Stem", "add"), new Path("°", "Prophet", "Literal", "String", (to ? "True"
					: "False")), true);
		} catch (Exception e) {}
	}
	
	public void setName(String name) {
		cache.remove("getName");
		try {
			send(new Path("Name", "cell", "Stem", "clear"), new Path("°"), true);
			
			send(new Path("Name", "cell", "Stem", "add"), new Path("°", "Prophet", "Literal", "String", "°"), true);
			send(new Path("Name", "cell", "Stem", "add"), new Path("°", "Prophet", "Literal", "String", "Prophet"),
					true);
			send(new Path("Name", "cell", "Stem", "add"), new Path("°", "Prophet", "Literal", "String", "Literal"),
					true);
			send(new Path("Name", "cell", "Stem", "add"), new Path("°", "Prophet", "Literal", "String", "String"), true);
			send(new Path("Name", "cell", "Stem", "add"), new Path("°", "Prophet", "Literal", "String", name), true);
		} catch (Exception e) {}
	}
	
	public void addChild(String name) {
		cache.remove("getChildren");
		try {
			send(new Path("Children", "add"), new Path("°", "Prophet", "Literal", "String", name), true);
		} catch (Exception e) {}
	}
	
	public void removeChild(String name) {
		try {
			send(new Path("Children", "remove"), new Path("°", "Prophet", "Literal", "String", name), true);
		} catch (Exception e) {}
	}
	
	public void addPeer(Address address) {
		cache.remove("getPeers");
		try {
			send(new Path("Peers", "cell", "Children", "add"),
					new Path("°", "Prophet", "Literal", "String", address.host.toString()),
					true);
		} catch (Exception e) {}
	}
	
	public void removePeer(Address address) {
		cache.remove("getPeers");
		try {
			send(new Path("Peers", "cell", "Children", "remove"),
					new Path("°", "Prophet", "Literal", "String", address.host.toString()),
					true);
		} catch (Exception e) {}
	}
	
	public void setStem(Path stem) {
		cache.remove("getStem");
		try {
			if (stem == null) {
				send(new Path("cell", "Children", "remove"),
						new Path("°", "Prophet", "Literal", "String", "Stem"),
						true);
			} else {
				try {
					send(new Path("Stem", "clear"), new Path("°"), false);
				} catch (Exception e) {
					send(new Path("cell", "Children", "add"),
							new Path("°", "Prophet", "Literal", "String", "Stem"),
							true);
				}
				
				for (String name : stem)
					send(new Path("Stem", "add"), new Path("°", "Ptophet", "Literal", "String", name), true);
			}
		} catch (Exception e) {}
	}
	
	public void setReaction(Reaction reaction) {
		cache.remove("getReaction");
		try {
			if (reaction == null) {
				send(new Path("cell", "Children", "remove"),
						new Path("°", "Prophet", "Literal", "String", "Reaction"),
						true);
			} else {
				try {
					send(new Path("Reaction", "clear"), new Path("°"), false);
				} catch (Exception e) {
					send(new Path("cell", "Children", "add"),
							new Path("°", "Prophet", "Literal", "String", "Reaction"),
							true);
				}
				
				int sendIndex = 0;
				for (Send send : reaction.getSends()) {
					send(new Path("Reaction", "add"), new Path("°", "Prophet", "Reflection", "Reaction", "Send"), true);
					
					for (String name : send.getReceiver())
						send(new Path("Reaction", String.valueOf(sendIndex), "Receiver", "add"),
								new Path("°", "Prophet", "Literal", "String", name),
								true);
					
					for (String name : send.getMessage())
						send(new Path("Reaction", String.valueOf(sendIndex), "Message", "add"),
								new Path("°", "Prophet", "Literal", "String", name),
								true);
					
					sendIndex++;
				}
			}
		} catch (Exception e) {}
	}
}
