package org.rtens.cell;

import java.util.*;

import org.rtens.cell.Parameters.SearchEntry;
import org.rtens.cell.connection.LocalClient;
import org.rtens.cell.glue.CellLoader;
import org.rtens.cell.kernel.*;

public class Cell extends Peer {
	
	private Cell parent;
	private String name;
	
	private LinkedList<Cell> children;
	private LinkedList<Peer> peers;
	
	private Path stem;
	private NativeReaction reaction;
	
	private boolean active = false;
	
	private KernelCell kernelCell;
	private ContextCell contextCell;
	
	protected CellLoader loader;
	protected Connections connections;
	
	public Cell(Cell parent, String name) {
		this.parent = parent;
		this.name = name;
	}
	
	public Cell(Cell copy) {
		name = copy.name;
		parent = copy.parent;
		
		if (copy.children != null)
			children = new LinkedList<Cell>(copy.children);
		
		if (copy.peers != null)
			peers = new LinkedList<Peer>(copy.peers);
		
		stem = copy.stem;
		reaction = copy.reaction;
		
		active = copy.active;
	}
	
	public Cell getParent() {
		return parent;
	}
	
	public Cell getRoot() {
		return (parent == null)
				? this
				: parent.getRoot();
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public NativeReaction getReaction() {
		return reaction;
	}
	
	public Cell setReaction(NativeReaction r) {
		synchronized (this) {
			reaction = r;
			save();
			return this;
		}
	}
	
	public Path getStem() {
		return stem;
	}
	
	public Cell setStem(Path stem) {
		synchronized (this) {
			this.stem = stem;
			save();
			return this;
		}
	}
	
	public boolean isActive() {
		return active;
	}
	
	public Cell setActive(boolean to) {
		synchronized (this) {
			active = to;
			return this;
		}
	}
	
	@Override
	public Address getAddress(Parameters p) {
		if (p == null)
			return new Address(LocalClient.host, getPath());
		
		return p.address;
	}
	
	public Path getPath() {
		Path path = (parent == null)
				? new Path()
				: parent.getPath();
		
		path.add(getName());
		return path;
	}
	
	public Connections getConnections() {
		if (connections == null)
			if (getParent() != null)
				connections = getParent().getConnections();
			else connections = new Connections(getRoot());
		
		return connections;
	}
	
	public Cell setConnections(Connections connections) {
		this.connections = connections;
		return this;
	}
	
	public Peer getKernel() {
		if (kernelCell == null) {
			kernelCell = new cellCell(this, "cell");
		}
		return kernelCell;
	}
	
	public ContextCell getContext() {
		if (contextCell == null) {
			contextCell = new ContextCell(this, "context");
		}
		
		return contextCell;
	}
	
	public Result send(Send send, Context context) {
		
		Parameters p = new Parameters();
		p.steps = getPath().size();
		p.send = send;
		p.receiverStack.add(new Path(send.getReceiver()));
		p.resolvedStack.add(new Path(context.getReceiver()));
		p.contextStack.add(new Context(context));
		p.address = getAddress(null);
		
		return deliver(p);
	}
	
	@Override
	public Result deliver(Parameters p) {
		
		Result res = deliverToChild(p);
		if (res.wasDelivered())
			return res;
		
		if (res.hasHeir()) {
			Peer peer = null;
			try {
				peer = getConnections().getPeer(res.heir);
			} catch (Exception e) {
				res.log.add(new Log.Entry(getAddress(p), "Error while loading peer for heir " + res.heir + ": "
						+ e.getMessage()));
				e.printStackTrace();
			}
			
			if (peer != null) {
				Result stemRes = peer.deliverToStem(res.heirsParams);
				stemRes.log.addAll(0, res.log);
				return stemRes;
			}
		}
		
		return res;
	}
	
	@Override
	public Result deliverToChild(Parameters p) {
		
		p.address.clear();
		p.address.addAll(getPath());
		
		Result res = new Result();
		res.log.add(new Log.Entry(getAddress(p), "dtc", p));
		
		if (removeParentRedundancy(p))
			res.log.add(new Log.Entry(getAddress(p), "skipped parents", p));
		
		boolean clearSearchedStems = p.receiverStack.getLast().isEmpty();
		while (p.receiverStack.size() > 1 && p.receiverStack.getLast().isEmpty()) {
			p.popStacks();
			res.log.add(new Log.Entry(getAddress(p), "pop", p));
		}
		
		if (getStem() != null)
			res.proposeHeir(getAddress(p), p);
		
		SearchEntry searchEntry = new SearchEntry(new Address(getAddress(p)), p.receiverStack.getLast());
		if (p.searched.contains(searchEntry)) {
			res.log.add(new Log.Entry(getAddress(p), "searched before"));
			return res;
		}
		p.searched.add(searchEntry);
		
		if (p.receiverStack.getLast().isEmpty()) {
			if (isActive() && getReaction() != null) {
				Context context = new Context(p.contextStack.getLast(), p.resolvedStack.getLast(), p.send);
				getReaction().execute(this, context);
				
				res.log.add(new Log.Entry(getAddress(p), "found receiver"));
				return res.deliveredTo(getAddress(p), p.resolvedStack.getLast());
			}
		} else {
			Peer nextReceiver = getNextReceiver(p);
			
			if (nextReceiver != null) {
				p.steps++;
				if (clearSearchedStems) p.searchedStems.clear();
				Result nextRes = nextReceiver.deliverToChild(p);
				nextRes.log.addAll(0, res.log);
				return nextRes;
			}
		}
		
		LinkedList<Result> negativeResults = new LinkedList<Result>();
		Cell peerCell = this;
		Parameters peerParameters = new Parameters(p);
		peerParameters.receiverStack.add(new Path());
		peerParameters.resolvedStack.add(getPath());
		peerParameters.contextStack.add(p.contextStack.getLast());
		
		while (usePeers(p) && peerCell != null) {
			res.log.add(new Log.Entry(peerCell.getAddress(peerParameters), "search peers", peerParameters));
			
			Path peerReceiver = new Path(peerParameters.receiverStack.getLast(), p.receiverStack.getLast());
			peerParameters.searched.add(new SearchEntry(peerCell.getAddress(peerParameters), peerReceiver));
			
			for (Peer peer : peerCell.getPeers()) {
				if (!p.searched.contains(new SearchEntry(peer.getAddress(null), peerReceiver))) {
					
					Result peerRes = peer.deliverToChild(new Parameters(peerParameters));
					if (peerRes.wasDelivered()) {
						peerRes.log.addAll(0, res.log);
						return peerRes;
					} else {
						negativeResults.add(peerRes);
						p.searched.addAll(peerRes.searched);
						res.log.addAll(peerRes.log);
					}
				}
			}
			
			peerCell = (Cell) peerCell.getParent();
			if (peerCell == null)
				break;
			
			peerParameters.address.removeLast();
			peerParameters.steps--;
			peerParameters.receiverStack.getLast().addFirst(
					peerParameters.resolvedStack.getLast().removeLast());
			peerParameters.searched = p.searched;
		}
		
		res.searched.addAll(p.searched);
		
		for (Result negRes : negativeResults) {
			if (negRes.hasHeir())
				res.proposeHeir(negRes.heir, negRes.heirsParams);
		}
		
		res.log.add(new Log.Entry(getAddress(p), "failed"));
		return res;
	}
	
	@Override
	public Result deliverToStem(Parameters p) {
		if (isActive() && getStem() != null && !p.searchedStems.contains(getStem())) {
			p.searchedStems.add(new Path(getStem()));
			p.searched.clear();
			
			p.receiverStack.add(new Path(getStem()));
			p.resolvedStack.add(new Path(p.resolvedStack.getLast()));
			p.contextStack.add(p.contextStack.getLast());
			
			Log.Entry entry = new Log.Entry(getAddress(p), "inherit", p);
			Result inheritRes = deliver(p);
			inheritRes.log.add(0, entry);
			return inheritRes;
		}
		
		Result res = new Result();
		
		if (!isActive())
			res.log.add(new Log.Entry(getAddress(p), "Can't inherit from inactive cell: " + getStem(), p));
		if (getStem() == null)
			res.log.add(new Log.Entry(getAddress(p), "No stem to inherit from (" + getPath() + ")", p));
		if (p.searchedStems.contains(getStem()))
			res.log.add(new Log.Entry(getAddress(p), "Stem already searched: " + getStem(), p));
		
		return res;
	}
	
	protected Peer getNextReceiver(Parameters p) {
		
		String name = p.receiverStack.getLast().getFirst();
		
		if (name.equals("°")) {
			return reduceRedundancy(p);
		} else if (name.equals("self")) {
			p.receiverStack.getLast().removeFirst();
			return this;
		} else if (name.equals("parent")) {
			if (!p.resolvedStack.getLast().isEmpty()) {
				if (p.resolvedStack.getLast().equals(getPath())) {
					p.resolvedStack.getLast().removeLast();
					p.receiverStack.getLast().removeFirst();
					return getParent();
				} else {
					p.resolvedStack.getLast().removeLast();
					
					p.receiverStack.getLast().removeFirst();
					p.receiverStack.getLast().addAll(0, p.resolvedStack.getLast());
					
					return this;
				}
			}
		} else if (name.equals("stem")) {
			if (isActive() && getStem() != null) {
				p.receiverStack.getLast().removeFirst();
				p.receiverStack.getLast().addAll(0, getStem());
				return this;
			}
			return null;
		} else if (name.equals("message")) {
			if (p.contextStack.getLast() == null) return null;
			Context messageContext = p.contextStack.getLast().getMessageContext(p.resolvedStack.getLast());
			if (!isActive() || messageContext == null) return null;
			
			p.receiverStack.getLast().removeFirst();
			Path receiver = new Path(p.receiverStack.getLast());
			p.receiverStack.getLast().clear();
			
			p.receiverStack.add(new Path(messageContext.getMessage()));
			p.receiverStack.getLast().addAll(receiver);
			
			p.resolvedStack.add(p.resolvedStack.size() - 1, null);
			p.contextStack.add(messageContext.getParent());
			
			return this;
		} else if (name.equals("context")) {
			if (!isActive()) return null;
			
			Context executionContext = p.contextStack.getLast().getMessageContext(p.resolvedStack.getLast());
			if (executionContext == null) return null;
			
			p.resolvedStack.getLast().add(p.receiverStack.getLast().removeFirst());
			return getContext();
		} else if (name.equals("cell")) {
			p.resolvedStack.getLast().add(p.receiverStack.getLast().removeFirst());
			return getKernel();
		} else if (isActive()) {
			Cell child = getChild(name, p);
			
			if (child != null) {
				p.resolvedStack.getLast().add(p.receiverStack.getLast().removeFirst());
				return child;
			}
		}
		
		return null;
	}
	
	protected boolean removeParentRedundancy(Parameters p) {
		Path rec = p.receiverStack.getLast();
		int numConsecParents = 0;
		int indexFirstParent = -1;
		String lastName = "";
		
		for (int i = 0; i < rec.size(); i++) {
			String name = rec.get(i);
			if (name.equals("stem")
					|| name.equals("message")
					|| name.equals("parent") && i == 0)
				break;
			
			if (name.equals("parent")) {
				if (indexFirstParent == -1) {
					indexFirstParent = i;
					numConsecParents = 1;
				} else if (lastName.equals("parent")) {
					numConsecParents++;
				} else {
					break;
				}
			}
			lastName = name;
		}
		
		if (indexFirstParent == numConsecParents) {
			for (int i = 0; i < numConsecParents * 2; i++) {
				rec.removeFirst();
				p.steps++;
			}
			return true;
		}
		return false;
	}
	
	private Peer reduceRedundancy(Parameters p) {
		
		Path path = getPath();
		Path reso = p.resolvedStack.getLast();
		Path rece = p.receiverStack.getLast();
		
		int i;
		for (i = 0; i < path.size() && i < reso.size() && i < rece.size(); i++)
			if (!path.get(i).equals(reso.get(i)) || !path.get(i).equals(rece.get(i))) break;
		
		Cell receiver = this;
		p.steps--;
		for (int j = 0; j < path.size() - i && receiver.getParent() != null; j++) {
			receiver = receiver.getParent();
			p.steps++;
		}
		
		for (int j = 0; j < i; j++) {
			p.receiverStack.getLast().removeFirst();
			p.steps++;
		}
		
		p.resolvedStack.getLast().clear();
		p.resolvedStack.getLast().addAll(receiver.getPath());
		
		return receiver;
	}
	
	public LinkedList<Cell> getChildren() {
		synchronized (this) {
			if (children == null) {
				reloadChildren();
			}
			return children;
		}
	}
	
	public void reloadChildren() {
		synchronized (this) {
			children = loadChildren();
		}
	}
	
	protected LinkedList<Cell> loadChildren() {
		if (loader != null)
			return loader.getChildren(this);
		return new LinkedList<Cell>();
	}
	
	protected void save() {
		if (loader != null)
			loader.save(this);
	}
	
	public Cell addChild(String name) {
		return addChild(name, null);
	}
	
	public Cell addChild(String name, Context context) {
		if (name.equals("context")) return contextCell;
		
		return addChild(new Cell(this, name));
	}
	
	public Cell addChild(Cell child) {
		synchronized (this) {
			child.setLoader(loader);
			child.save();
			
			getChildren().add(child);
			save();
			return child;
		}
	}
	
	public boolean removeChild(String name, Context contex) {
		synchronized (this) {
			for (int i = 0; i < getChildren().size(); i++) {
				if (getChildren().get(i).getName().equals(name)) {
					if (loader != null)
						loader.delete(getChildren().get(i));
					
					getChildren().remove(i);
					save();
					return true;
				}
			}
			return false;
		}
	}
	
	public Cell getChild(String name) {
		return getChild(name, null);
	}
	
	public Cell getChild(String name, Parameters p) {
		synchronized (this) {
			if (name.equals("context"))
				return getContext();
			
			for (Cell child : getChildren())
				if (child.getName().equals(name))
						return child;
			
			return null;
		}
	}
	
	public void addPeer(Peer peer) {
		synchronized (this) {
			getPeers().add(peer);
			save();
		}
	}
	
	public boolean removePeer(Address address) {
		synchronized (this) {
			for (int i = 0; i < getPeers().size(); i++) {
				if (getPeers().get(i).getAddress(null).equals(address)) {
					getPeers().remove(i);
					save();
					return true;
				}
			}
			
			return false;
		}
	}
	
	public LinkedList<Peer> getPeers() {
		if (peers == null)
			peers = new LinkedList<Peer>();
		return peers;
	}
	
	public Cell setLoader(CellLoader loader) {
		this.loader = loader;
		return this;
	}
	
	@Override
	public String toString() {
		return getPath().toString() + (getStem() != null ? " : " + getStem() : "");
	}
	
	public boolean usePeers(Parameters p) {
		return !p.resolvedStack.getLast().containsAll(new Path("cell", "Peers", getAddress(p).host.toString()));
	}
	
}