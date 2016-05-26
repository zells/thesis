package org.zells;

import java.util.*;

public class Path extends LinkedList<String> {

	public Path() {}
	
	public Path(String... names) {
		addAll(Arrays.asList(names));
	}
	
	public Path(Path... paths) {
		for (Path path : paths)
			addAll(path);
	}
	
	public Path subPath(int end) {
		return subPath(0, end);
	}
	
	public Path subPath(int start, int end) {
		if (end <= 0) end = size() + end;
		if (start < 0) start = size() + start;
		
		Path subPath = new Path();
		subPath.addAll(subList(start, end));
		return subPath;
	}
	
	@Override
	public String toString() {
		String s = super.toString();
		return s.substring(1, s.length()-1).replace(", ", ".");
	}
}
