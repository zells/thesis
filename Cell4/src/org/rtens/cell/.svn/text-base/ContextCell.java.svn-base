package org.rtens.cell;

import java.util.*;

public class ContextCell extends Cell {
	
	private Map<Integer, LinkedList<Cell>> contextChildren = new HashMap<Integer, LinkedList<Cell>>();
	
	public ContextCell(Cell parent, String name) {
		super(parent, name);
		setActive(true);
		setStem(new Path("°", "Cell"));
	}
	
	public LinkedList<LinkedList<Cell>> getContextChildren() {
		LinkedList<LinkedList<Cell>> children = new LinkedList<LinkedList<Cell>>();
		for (LinkedList<Cell> c : contextChildren.values())
			children.add(c);
		
		return children;
	}
	
	private boolean hasChildrenOf(Context c) {
		return contextChildren.containsKey(c.getId());
	}
	
	@Override
	public Cell getChild(String name, Parameters p) {
		synchronized (this) {
			Path parent = new Path(p.resolvedStack.getLast());
			parent.removeLast();
			
			Context c = p.contextStack.getLast().getMessageContext(parent);
			
			if (c == null) {
				System.out.println("Receiver " + parent + " not found in "
						+ p.contextStack.getLast());
				return null;
			}
			
			if (!hasChildrenOf(c))
				return null;
			
			return getContextChild(c.getId(), name);
		}
	}
	
	@Override
	public LinkedList<Cell> getChildren() {
		return new LinkedList<Cell>();
	}
	
	@Override
	public Cell addChild(String name, Context context) {
		synchronized (this) {
			Context c = context.getMessageContext(getParent().getPath());
			
			if (c == null)
				return null;
			
			if (!hasChildrenOf(c))
				contextChildren.put(c.getId(), new LinkedList<Cell>());
			
			Cell child = new Cell(this, name);
			contextChildren.get(c.getId()).add(child);
			
			return child;
		}
	}
	
	@Override
	public boolean removeChild(String name, Context context) {
		synchronized (this) {
			Context c = context.getMessageContext(getParent().getPath());
			
			if (c == null || !hasChildrenOf(c))
				return false;
			
			for (Cell child : new LinkedList<Cell>(contextChildren.get(c.getId()))) {
				if (child.getName().equals(name))
					return contextChildren.get(c.getId()).remove(child);
			}
			return false;
		}
	}
	
	public Cell getContextChild(int i, String name) {
		for (Cell child : contextChildren.get(i))
			if (child.getName().equals(name))
				return child;
		
		return null;
	}
	
}
