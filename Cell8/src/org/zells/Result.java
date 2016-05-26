package org.zells;

import java.io.Serializable;
import java.util.*;

import org.zells.test.DeliveryTest;

public class Result implements Serializable {
	
	private static int count = 0;
	
	public DeliveryId deliveryId;
	public Path deliveredTo;
	public Log log = new Log()
	{
		@Override
		public boolean add(Entry e) {
			e.stepCount = count++;
			if (DeliveryTest.debug)
				System.out.println(e);
			return super.add(e);
		};
	};
	
	public Result deliveredTo(Path role, DeliveryId id) {
		deliveredTo = role;
		deliveryId = id;
		return this;
	}
	
	public boolean wasDelivered() {
		return deliveredTo != null;
	}
	
	public static class Log extends LinkedList<Log.Entry> {
		
		@Override
		public String toString() {
			StringBuilder s = new StringBuilder("Path\tDescription\tRole\tReceiver\tDeliveries\tMessage\tEID\n");
			for (Entry e : this)
				s.append(e + "\n");
			
			return s.toString() + "\n";
		}
		
		public static class Entry implements Serializable {
			
			public Path path;
			public String description;
			public List<Delivery> deliveryStack = new LinkedList<Delivery>();
			public Path message;
			public DeliveryId eid;
			public int stepCount;
			
			public Entry(DeliveryId eid, Path p, String descr, List<Delivery> deliveryStack, Path message) {
				this(eid, p, descr);
				
				for (Delivery d : deliveryStack) {
					this.deliveryStack.add(new Delivery(d));
				}
				
				this.message = new Path(message);
			}
			
			public Entry(DeliveryId eid, Path p, String descr) {
				this.eid = new DeliveryId(eid);
				path = p;
				description = descr;
			}
			
			@Override
			public String toString() {
				Delivery d = (deliveryStack.isEmpty() ? new Delivery(new Path(), new Path()) : deliveryStack.get(0));
				
				return path
						+ "\t" + description
						+ "\t" + d.role
						+ "\t" + d.receiver
						+ "\t" + (deliveryStack.size() > 1 ? deliveryStack.subList(1, deliveryStack.size()) : "[]")
						+ "\t" + message
						+ "\t" + eid
						+ "\t" + stepCount;
			}
		}
	}
	
}
