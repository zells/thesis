package org.rtens.cell;

import java.io.Serializable;
import java.util.LinkedList;

public class Log extends LinkedList<Log.Entry> {
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (Entry e : this)
			s.append(e + "\n");
		
		return s.toString();
	}
	
	public static class Entry implements Serializable {
		public String description;
		public Address address;
		public Parameters parameters;
		
		public Entry(Address addr, String desc, Parameters p) {
			this(addr, desc);
			parameters = new Parameters(p);
		}
		
		public Entry(Address addr, String desc) {
			address = new Address(addr);
			description = desc;
		}
		
		@Override
		public String toString() {
			return address + "\t" + description + "\t" + parameters;
		}
	}
}
