package org.rtens.cell;

import java.io.Serializable;
import java.util.*;

public class Parameters implements Serializable {
	
	public int steps = 0;
	public Send send;
	
	public LinkedList<Path> resolvedStack = new LinkedList<Path>();
	public LinkedList<Path> receiverStack = new LinkedList<Path>();
	public LinkedList<Context> contextStack = new LinkedList<Context>();
	public HashSet<SearchEntry> searched = new HashSet<SearchEntry>();
	
	public LinkedList<Path> searchedStems = new LinkedList<Path>();
	public Address address;
	
	public Parameters() {}
	
	public Parameters(Parameters p) {
		resolvedStack = copyPathStack(p.resolvedStack);
		receiverStack = copyPathStack(p.receiverStack);
		contextStack = copyContextStack(p.contextStack);
		searched = new HashSet<SearchEntry>(p.searched);
		send = p.send;
		searchedStems.addAll(p.searchedStems);
		steps = p.steps;
		address = new Address(p.address);
	}
	
	private LinkedList<Path> copyPathStack(LinkedList<Path> copy) {
		LinkedList<Path> stack = new LinkedList<Path>();
		for (Path p : copy) {
			if (p == null)
				stack.add(null);
			else stack.add(new Path(p));
		}
		return stack;
	}
	
	private LinkedList<Context> copyContextStack(LinkedList<Context> copy) {
		LinkedList<Context> stack = new LinkedList<Context>();
		for (Context c : copy) {
			if (c == null)
				stack.add(null);
			else stack.add(new Context(c));
		}
		return stack;
	}
	
	@Override
	public String toString() {
		return steps + "\t" + address + "\t" + resolvedStack + "\t" + receiverStack + "\t" + contextStack + "\t" + send
				+ "\t" + searchedStems + "\t" + searched;
	}
	
	public void popStacks() {
		popStack(receiverStack);
		popStack(resolvedStack);
		popStack(contextStack);
	}
	
	private void popStack(LinkedList<?> stack) {
		if (stack.get(stack.size() - 2) == null)
			stack.remove(stack.size() - 2);
		else stack.removeLast();
	}
	
	public static class SearchEntry implements Serializable {
		public Address address;
		public Path path;
		
		public SearchEntry(Address address, Path path) {
			this.address = new Address(address);
			this.path = new Path(path);
		}
		
		@Override
		public boolean equals(Object o) {
			if (o == null || !(o instanceof SearchEntry)) return false;
			SearchEntry e = (SearchEntry) o;
			return address.equals(e.address) && path.equals(e.path);
		}
		
		@Override
		public int hashCode() {
			return address.hashCode();
		}
		
		@Override
		public String toString() {
			return "<" + address + " | " + path + ">";
		}
	}
	
}
