package org.zells;

import java.util.*;

import org.zells.glue.CellLoader;
import org.zells.kernel.cellCell;

public class Cell implements Deliverer {
	
	private Cell parent;
	private String name;
	private Path stem;
	
	private NativeReaction reaction;
	
	private List<Cell> children;
	
	private List<Peer> peers;
	
	private boolean active = false;
	
	protected KernelCell kernelCell;
	
	private Set<String> failedDeliveries = new HashSet<String>();
	
	private CellLoader loader;
	
	private int sendCount = 0;
	
	public Cell(Cell parent, String name) {
		this.parent = parent;
		this.name = name;
	}
	
	public Result send(Mailing mailing, Path role, DeliveryId eid) {
		LinkedList<Delivery> deliveryStack = new LinkedList<Delivery>();
		deliveryStack.add(new Delivery(role, new Path(mailing.receiver)));
		synchronized (this) {
			eid = new DeliveryId(eid, sendCount++);
		}
		
		return deliver(deliveryStack, mailing.message, eid);
	}
	
	@Override
	public Result deliver(LinkedList<Delivery> deliveryStack, Path message, DeliveryId eid) {
		
		Result res = new Result();
		
		while (deliveryStack.size() > 1 && deliveryStack.getFirst().receiver.isEmpty()) {
			res.log.add(new Result.Log.Entry(eid, getPath(), "<<<<<< pop"));
			deliveryStack.pop();
		}
		
		res.log.add(new Result.Log.Entry(eid, getPath(), "Deliver", deliveryStack, message));
		
		Delivery delivery = deliveryStack.getFirst();
		Deliverer nextCell = null;
		
		String searchedId = eid + ":" + delivery.receiver;
		if (failedDeliveries.contains(searchedId)) {
			res.log.add(new Result.Log.Entry(eid, getPath(), "########## Been there " + searchedId));
			return res;
		}
		
		if (delivery.receiver.isEmpty()) {
			if (reaction != null) {
				res.log.add(new Result.Log.Entry(eid, getPath(), "********** Delivered to " + delivery.role));
				
				receive(message, eid, delivery);
				
				return res.deliveredTo(delivery.role, eid);
			}
		} else {
			nextCell = getNextDeliverer(delivery.receiver.getFirst(), deliveryStack);
		}
		
		if (nextCell == null)
			failedDeliveries.add(searchedId);

		if (nextCell == null && usePeers(delivery.role)) {
			Result peerRes = tryPeers(deliveryStack, message, eid, res);
			if (peerRes.wasDelivered()) return peerRes;
		}
		
		boolean inherited = false;
		if (nextCell == null && getStem() != null) {
			res.log.add(new Result.Log.Entry(eid, getPath(), ">>>>>> Inherit from " + getStem()));
			
			inherited = true;
			deliveryStack.push(new Delivery(new Path(delivery.role), new Path(getStem())));
			nextCell = this;
		}
		
		if (nextCell != null) {
			Result nextRes = nextCell.deliver(deliveryStack, message, eid);
			if (nextRes.wasDelivered()) {
				
				if (inherited && nextRes.deliveredTo.size() > getPath().size()
						&& nextRes.deliveredTo.subPath(getPath().size()).equals(getPath())) {
					Path path = nextRes.deliveredTo.subPath(getPath().size(), -1);
					adopt(path, message, eid);
				}
				
				nextRes.log.addAll(0, res.log);
				return nextRes;
			} else {
				res.log.addAll(nextRes.log);
				return res;
			}
		}
		
		res.log.add(new Result.Log.Entry(eid, getPath(), "######## Failed"));
		return res;
	}
	
	protected void receive(Path message, DeliveryId eid, Delivery delivery) {
		String executionName = Execution.makeName(eid);
		if (delivery.role.equals(getPath()))
			addChild(new Execution(this, executionName, message));
		
		delivery.role.add(executionName);
		
		reaction.execute(this, delivery.role, message, eid);
	}
	
	protected void adopt(Path inherited, Path message, DeliveryId eid) {
		boolean inKernel = false;
		Cell cell = this;
		for (String name : inherited) {
			if (name.equals("cell")) {
				inKernel = true;
				cell = cell.getKernel();
			} else {
				cell = cell.addChild(new Cell(cell, name).setStem(new Path(cell.getPath(), new Path("stem", name))));
			}
		}
		
		if (inKernel) {
			cell.getReaction().execute(cell, cell.getPath(), message, eid);
		} else {
			cell.addChild(new Execution(cell, Execution.makeName(eid), message));
		}
	}
	
	protected Deliverer getNextDeliverer(String name, LinkedList<Delivery> deliveryStack) {
		Delivery delivery = deliveryStack.getFirst();
		
		if (name.equals("°")) {
			
			Path path = getPath();
			if (delivery.receiver.size() >= path.size() && path.equals(delivery.receiver.subPath(path.size()))) {
				delivery.receiver = delivery.receiver.subPath(path.size(), 0);
				delivery.role = path;
				return this;
			} else {
				delivery.receiver.removeFirst();
				delivery.role = new Path("°");
				return getRoot();
			}
			
		} else if (name.equals("parent")) {
			if (getPath().equals(delivery.role)) {
				Cell next = this;
				while (!delivery.receiver.isEmpty() && delivery.receiver.getFirst().equals("parent")) {
					delivery.receiver.removeFirst();
					delivery.role.removeLast();
					next = next.getParent();
				}
				return next;
			} else {
				delivery.receiver.removeFirst();
				delivery.receiver.addAll(0, delivery.role.subPath(-1));
				return this;
			}
			
		} else if (name.equals("self")) {
			delivery.receiver.removeFirst();
			return this;
			
		} else if (name.equals("stem")) {
			delivery.receiver.removeFirst();
			deliveryStack.push(new Delivery(new Path(delivery.role), new Path(getStem())));
			return this;
			
		} else if (name.equals("cell")) {
			delivery.resolveNextReceiver();
			return getKernel();
			
		} else {
			Cell child = getChild(name);
			
			if (child != null) {
				delivery.resolveNextReceiver();
				return child;
			}
		}
		
		return null;
	}
	
	private Result tryPeers(LinkedList<Delivery> deliveryStack, Path message, DeliveryId eid, Result res) {
		Cell peerCell = this;
		Path peerReceiver = new Path();
		
		while (peerCell != null) {
			LinkedList<Delivery> peerDeliveryStack = new LinkedList<Delivery>();
			peerDeliveryStack.addAll(deliveryStack);
			peerDeliveryStack.push(new Delivery(peerCell.getPath(), peerReceiver));
			
			res.log.add(new Result.Log.Entry(eid, peerCell.getPath(), "Try peers", peerDeliveryStack, message));
			for (Peer peer : peerCell.getPeers()) {
				Result peerRes = peer.deliver(peerDeliveryStack, message, eid);
				res.log.add(new Result.Log.Entry(eid, peerCell.getPath(), "-> " + peer.getAddress()));
				if (peerRes.wasDelivered()) {
					peerRes.log.addAll(0, res.log);
					return peerRes;
				} else {
					res.log.addAll(peerRes.log);
					res.log.add(new Result.Log.Entry(eid, peerCell.getPath(), "<-"));
				}
			}
			
			peerReceiver.add(0, peerCell.name);
			peerCell = peerCell.parent;
		}
		
		return res;
	}
	
	private boolean usePeers(Path role) {
		//return delivery.role.size() <= 3 || !delivery.role.subPath(-3, -1).equals(new Path("cell", "Peers"));
		return !role.containsAll(new Path("cell", "Peers"));
	}
	
	public String getName() {
		return name;
	}
	
	public Cell setName(String name) {
		synchronized (this) {
			this.name = name;
			save();
			return this;
		}
	}
	
	public Cell getParent() {
		return parent;
	}
	
	public Cell getRoot() {
		if (parent == null)
				return this;
		return parent.getRoot();
	}
	
	public Path getPath() {
		if (parent == null)
				return new Path(name);
		Path path = parent.getPath();
		path.add(name);
		return path;
	}
	
	public Cell setStem(Path stem) {
		synchronized (this) {
			this.stem = stem;
			save();
			return this;
		}
	}
	
	public Path getStem() {
		synchronized (this) {
			return stem;
		}
	}
	
	public Cell setReaction(NativeReaction reaction) {
		synchronized (this) {
			this.reaction = reaction;
			save();
			return this;
		}
	}
	
	public NativeReaction getReaction() {
		synchronized (this) {
			return reaction;
		}
	}
	
	public Cell setActive(boolean to) {
		synchronized (this) {
			active = to;
			save();
			return this;
		}
	}
	
	public boolean isActive() {
		synchronized (this) {
			return active;
		}
	}
	
	public void setLoader(CellLoader loader) {
		this.loader = loader;
		save();
	}
	
	private void save() {
		synchronized (this) {
			if (loader != null)
				loader.save(this);
		}
	}
	
	protected List<Cell> loadChildren() {
		synchronized (this) {
			if (loader != null)
				return loader.getChildren(this);
			return new LinkedList<Cell>();
		}
	}
	
	public void reloadChildren() {
		synchronized (this) {
			children = loadChildren();
		}
	}
	
	public List<Cell> getChildren() {
		synchronized (this) {
			if (children == null)
				children = loadChildren();
			return children;
		}
	}
	
	public Cell getChild(String name) {
		synchronized (this) {
			for (Cell child : getChildren())
				if (child.name.equals(name))
					return child;
			
			return null;
		}
	}
	
	public Cell addChild(String name) {
		return addChild(new Cell(this, name));
	}
	
	public Cell addChild(Cell child) {
		synchronized (this) {
			Cell alreadyThere = getChild(child.getName());
			if (alreadyThere != null)
				return alreadyThere;
			
			child.setLoader(loader);
			child.save();
			
			getChildren().add(child);
			save();
			failedDeliveries.clear();
			return child;
		}
	}
	
	public Cell removeChild(String name) {
		synchronized (this) {
			Cell child = getChild(name);
			if (child == null) return this;
			
			getChildren().remove(child);
			if (loader != null)
				loader.delete(child);
			save();
			return this;
		}
	}
	
	public List<Peer> getPeers() {
		synchronized (this) {
			if (peers == null)
				peers = new LinkedList<Peer>();
			return peers;
		}
	}
	
	public Cell addPeer(Peer peer) {
		synchronized (this) {
			getPeers().add(peer);
			save();
			return this;
		}
	}

	public boolean removePeer(Address address) {
		synchronized (this) {
			for (int i = 0; i < getPeers().size(); i++) {
				if (getPeers().get(i).getAddress().equals(address)) {
					getPeers().remove(i);
					save();
					return true;
				}
			}
			
			return false;
		}
	}
	
	public Cell getKernel() {
		synchronized (this) {
			if (kernelCell == null)
				kernelCell = new cellCell(this);
			return kernelCell;
		}
	}
	
	@Override
	public String toString() {
		return getPath().toString();
	}
	
}
