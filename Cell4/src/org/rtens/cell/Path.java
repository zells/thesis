package org.rtens.cell;

import java.util.*;
import org.rtens.cell.glue.PathFormat;

public class Path extends LinkedList<String> {
	
	public Path() {}
	
	public Path(String... names) {
		addAll(Arrays.asList(names));
	}
	
	public Path(Path... paths) {
		for (Collection<String> path : paths)
			addAll(path);
	}
	
	@Override
	public String toString() {
		return PathFormat.format(this);
	}
	
	public Path resolveRootAndSelf() {
		removeRange(0, lastIndexOf("°"));
		while (contains("self"))
			remove("self");
		return this;
	}
	
}
