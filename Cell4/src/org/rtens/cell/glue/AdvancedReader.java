package org.rtens.cell.glue;

import java.io.*;
import java.util.LinkedList;

public class AdvancedReader {
	
	private StringReader reader;
	
	private LinkedList<Integer> buffer = new LinkedList<Integer>();
	
	private int line = 1;
	private int pos = 1;
	
	public AdvancedReader(String s) {
		reader = new StringReader(s);
	}
	
	public int read() throws IOException {
		int c;
		
		if (buffer.isEmpty())
			c = reader.read();
		else c = buffer.removeFirst();
		
		if (c == '\n') {
			line++;
			pos = 1;
		} else if (c != '\r') {
			pos++;
		}
		
		return c;
	}
	
	public int peek() throws IOException {
		if (buffer.isEmpty())
			buffer.add(reader.read());
		
		return buffer.getFirst();
	}
	
	public void skipWhites() throws IOException {
		while (isWhite(peek()))
			read();
	}
	
	private static boolean isWhite(int c) {
		return c == ' ' || c == '\t' || c == '\r' || c == '\n';
	}
	
	public int getLine() {
		return line;
	}
	
	public int getPos() {
		return pos;
	}
	
}
