package org.zells;

import java.util.*;

public class DeliveryId extends LinkedList<Integer> {
	
	public DeliveryId(Integer... ints) {
		addAll(Arrays.asList(ints));
	}
	
	public DeliveryId(DeliveryId copy) {
		addAll(copy);
	}
	
	public DeliveryId(DeliveryId copy, int id) {
		this(copy);
		add(id);
	}
	
	public DeliveryId subId(int end) {
		return subId(0, end);
	}
	
	public DeliveryId subId(int start, int end) {
		if (end <= 0) end = size() + end;
		if (start < 0) start = size() + start;
		
		DeliveryId subId = new DeliveryId();
		subId.addAll(subList(start, end));
		return subId;
	}
	
	@Override
	public String toString() {
		String s = super.toString();
		return s.substring(1, s.length() - 1).replace(", ", "#");
	}
	
}
