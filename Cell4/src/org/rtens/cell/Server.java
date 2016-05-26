package org.rtens.cell;

import java.util.*;

import javax.swing.event.*;

public abstract class Server implements Runnable {
	
	protected Cell root;
	
	private Logger logger;
	
	private Thread thread;
	
	public LinkedList<ChangeListener> listeners = new LinkedList<ChangeListener>();
	
	public static final char DELIVER = 0;
	public static final char DELIVER_TO_CHILD = 1;
	public static final char DELIVER_TO_STEM = 2;
	
	public Server(Cell root) {
		this.root = root;
	}
	
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	protected void log(String message) {
		if (logger != null)
			logger.log("[" + new Date() + "] " + message);
	}
	
	public void start() {
		if (thread != null && thread.isAlive()) {
			log("Can't start server. Is already running.");
		} else {
			thread = new Thread(this);
			thread.start();
			
			for (ChangeListener l : listeners)
				l.stateChanged(null);
		}
	}
	
	public boolean isRunning() {
		return thread != null && thread.isAlive();
	}
	
	public void stop() {
		close();
		thread = null;
		
		for (ChangeListener l : listeners)
			l.stateChanged(null);
	}
	
	@Override
	public abstract void run();
	
	public abstract List<String> getParameterNames();
	
	public abstract void setParameters(Map<String, String> params) throws Exception;
	
	public abstract Host getHost();
	
	public abstract String getNetwork();
	
	protected abstract void close();
	
	public interface Logger {
		public void log(String message);
	}
	
	protected Cell resolveCell(Path path, Parameters p) {
		path = new Path(path);
		path.removeFirst();
		Cell cell = root;
		
		Parameters params = new Parameters(p);
		
		while (cell != null && !path.isEmpty()) {
			params.resolvedStack.getLast().clear();
			params.resolvedStack.getLast().addAll(cell.getPath());
			
			cell = cell.getChild(path.removeFirst(), params);
		}
		
		if (cell == null)
			throw new RuntimeException("Could not resolve peer cell " + path + " --- " + p);
		
		return cell;
	}
	
}
