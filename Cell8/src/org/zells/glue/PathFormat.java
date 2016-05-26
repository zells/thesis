package org.zells.glue;

import org.zells.Path;

public class PathFormat {
	
	public static Path parse(AdvancedReader r) throws Exception {
		
		Path p = new Path();
		String name = "";
		
		boolean inQuotes = false;
		boolean escaped = false;
		
		while (true) {
			int c = r.peek();
			
			if (inQuotes) {
				switch (c) {
				case '\\':
					if (escaped) {
						name += (char) c;
						escaped = false;
					} else {
						escaped = true;
					}
					break;
				case '"':
					if (escaped) {
						name += (char) c;
						escaped = false;
					} else {
						inQuotes = false;
					}
					break;
				case -1:
					throw new Exception("Unmatched quotes");
				default:
					name += (char) c;
					break;
				}
			} else {
				switch (c) {
				case '.':
					p.add(name);
					name = "";
					break;
				case '"':
					inQuotes = true;
					break;
				case ' ':
				case '\t':
				case '\n':
				case '\r':
				case -1:
					if (!name.isEmpty())
						p.add(name);
					return p;
				default:
					name += (char) c;
					break;
				}
			}
			
			r.read();
		}
	}
	
	public static Path parse(String sPath) throws Exception {
		return parse(new AdvancedReader(sPath));
	}
	
	public static String format(Path path) {
		String string = "";
		for (String name : new Path(path)) {
			if (name.contains(" ")
					|| name.contains("\n")
					|| name.contains("\r")
					|| name.contains("\t")
					|| name.contains(".")
					|| name.contains("\"")) {
				
				name = name.replace("\\", "\\\\");
				name = name.replace("\"", "\\\"");
				name = "\"" + name + "\"";
			}
			
			string += "." + name;
		}
		
		if (string.isEmpty()) return "";
		return string.substring(1);
	}

}
