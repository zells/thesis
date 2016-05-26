package org.zells.gui;

import java.util.*;

import org.zells.*;
import org.zells.library.Zells.Literal.StringCell;

@SuppressWarnings("unchecked")
public class PeerModel {
	
	private Path path;
	
	private Map<String, Object> cache = new HashMap<String, Object>();
	
	private Cell root;
	
	protected PeerModel(Cell root, Path path) {
		this.root = root;
		this.path = path;
	}
	
	public static PeerModel getRootModel(Cell root) {
		return new PeerModel(root, new Path("°"));
	}
	
	public PeerModel getPeerModel(Address to) {
		return new PeerModel(root, new Path(path, new Path("cell", "Peers", to.toString())));
	}
	
	public PeerModel getChildModel(String name) {
		return new PeerModel(root, new Path(path, new Path(name)));
	}
	
	public void clearCache() {
		cache.clear();
	}
	
	public Path getPath() {
		return path;
	}
	
	private Path send(Path receiver, boolean deliveryExpected) {
		return send(receiver, new Path(), deliveryExpected);
	}
	
	private Path send(Path receiver, Path message, boolean deliveryExpected) {
		receiver.add(0, "cell");
		Result res = root.send(new Mailing(new Path(path, receiver), message), root.getPath(), new DeliveryId());
		if (deliveryExpected && !res.wasDelivered())
			System.err.println(res.log);
		
		return res.deliveredTo;
	}
	
	private Path readPath(Path of) throws Exception {
		Path nameStringPath;
		Path path = new Path();
		for (int i = 0; (nameStringPath = send(new Path(of, new Path(String.valueOf(i))), false)) != null; i++) {
			path.add(StringCell.get(nameStringPath.subPath(-1),
					root, root.getPath(), new DeliveryId()));
		}
		return path;
	}
	
	public List<String> getChildren() {
		if (!cache.containsKey("getChildren")) {
			LinkedList<String> names = new LinkedList<String>();
			Path namePath;
			for (int i = 0; (namePath = send(new Path("Children", String.valueOf(i), "cell"), false)) != null; i++) {
				names.add(StringCell.get(namePath.subPath(-2), root, root.getPath(), new DeliveryId()));
			}
			cache.put("getChildren", names);
		}
		return (List<String>) cache.get("getChildren");
	}
	
	public boolean isActive() {
		if (!cache.containsKey("isActive")) {
			Path active = send(new Path("Active", "value"), true);
			cache.put("isActive", active.getLast().equals("True"));
		}
		return (Boolean) cache.get("isActive");
	}
	
	public Path getStem() {
		if (!cache.containsKey("getStem")) {
			try {
				if (send(new Path("Stem", "cell"), false) == null) {
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
	
	public Reaction getReaction() {
		if (!cache.containsKey("getReaction")) {
			try {
				if (send(new Path("Reaction", "cell"), false) == null) {
					cache.put("getReaction", null);
				} else {
					Reaction r = new Reaction();
					for (int i = 0; send(new Path("Reaction", String.valueOf(i), "cell"), false) != null; i++) {
						r.mailings.add(new Mailing(readPath(new Path("Reaction", String.valueOf(i), "Receiver")),
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
	
	public List<Address> getPeers() {
		if (!cache.containsKey("getPeers")) {
			
			LinkedList<Address> addresses = new LinkedList<Address>();
			Path namePath;
			
			for (int i = 0; (namePath = send(new Path("Peers", "cell", "Children", String.valueOf(i), "cell"), false)) != null; i++) {
				String name = StringCell.get(namePath.subPath(-2), root, root.getPath(), new DeliveryId());
				try {
					addresses.add(Address.parse(name));
				} catch (Exception e) {
					System.err.println("Could not parse peer address: " + name + " from " + namePath + " @ " + path
							+ " -- " + new Path("Peers", "cell", "Children", String.valueOf(i), "cell"));
					break;
				}
			}
			cache.put("getPeers", addresses);
		}
		return (LinkedList<Address>) cache.get("getPeers");
	}
	
	public void setReaction(Reaction reaction) {
		send(new Path("cell", "Children", "add"), new Path("°", "Zells", "Literal", "String", "Reaction"), true);
		send(new Path("Reaction", "clear"), true);
		
		int i = 0;
		for (Mailing s : reaction.mailings) {
			send(new Path("Reaction", "add"), new Path("°"), true);
			
			for (String n : s.receiver) {
				send(new Path("Reaction", String.valueOf(i), "Receiver", "add"),
						new Path("°", "Zells", "Literal", "String", n), true);
			}
			
			for (String n : s.message) {
				send(new Path("Reaction", String.valueOf(i), "Message", "add"),
						new Path("°", "Zells", "Literal", "String", n), true);
			}
			
			i++;
		}
		
		clearCache();
	}
	
	public void addChild(String name) {
		send(new Path("Children", "add"), new Path("°", "Zells", "Literal", "String", name), true);
		clearCache();
	}
	
	public void removeChild(String name) {
		send(new Path("Children", "remove"), new Path("°", "Zells", "Literal", "String", name), true);
		clearCache();
	}
	
	public void setStem(Path stem) {
		send(new Path("cell", "Children", "add"), new Path("°", "Zells", "Literal", "String", "Stem"), true);
		send(new Path("Stem", "clear"), true);
		
		for (String n : stem) {
			send(new Path("Stem", "add"), new Path("°", "Zells", "Literal", "String", n), false);
		}
		clearCache();
	}
	
	//	public void setName(String name) {
	//		Auto-generated method stub
	//		changeNameInPath(name);
	//	}
	//		
	//	protected void changeNameInPath(String name) {
	//		int supposedIndex = path.size() - 1;
	//		while (supposedIndex > 3 && path.get(supposedIndex - 2).equals("cell"))
	//			supposedIndex -= 3;
	//		
	//		path.remove(supposedIndex);
	//		path.add(supposedIndex, name);
	//	}
	
}
