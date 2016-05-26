package org.rtens.cell;

import java.io.Serializable;
import java.util.HashSet;

import org.rtens.cell.Parameters.SearchEntry;

public class Result implements Serializable {
	
	public Address receivedAddress;
	
	public Path resolvedReceiver;
	
	public Address heir;
	
	public Parameters heirsParams;
	
	public HashSet<SearchEntry> searched = new HashSet<SearchEntry>();
	
	public Log log = new Log();
	
	//	{
	//		@Override
	//		public boolean add(Entry e) {
	//			System.out.println(e);
	//			return super.add(e);
	//		};
	//	};
	
	public boolean wasDelivered() {
		return receivedAddress != null;
	}
	
	public Address getReceiver() {
		return receivedAddress;
	}
	
	public Path getResolvedReceiver() {
		return resolvedReceiver;
	}
	
	public Result proposeHeir(Address address, Parameters p) {
		if (heir == null || p.steps > heirsParams.steps) {
			heir = address;
			heirsParams = new Parameters(p);
		}
		return this;
	}
	
	public Result deliveredTo(Address address, Path path) {
		receivedAddress = address;
		resolvedReceiver = path;
		return this;
	}
	
	public boolean hasHeir() {
		return heir != null;
	}
	
	@Override
	public String toString() {
		String s = wasDelivered() ? "Delivered: " + receivedAddress : "Not Delivered";
		
		if (hasHeir())
			s += "  ----- Heir: " + heir + "(" + heirsParams.steps + ")";
		
		return s;
	}
	
}
