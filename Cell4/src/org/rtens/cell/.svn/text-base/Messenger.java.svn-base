package org.rtens.cell;

public class Messenger extends Thread {
	
	private Cell sender;
	private Context context;
	private Send send;
	
	private Result result;
	
	public static Listener listener;
	
	private static boolean globalPaused = false;
	private boolean paused = false;
	private boolean stopped = false;
	
	public Messenger(Send send, Cell sender, Context context) {
		this.sender = sender;
		this.context = context;
		this.send = new Send(new Path(context.getReceiver(), send.getReceiver()).resolveRootAndSelf(),
				new Path(context.getReceiver(), send.getMessage()).resolveRootAndSelf());
	}
	
	@Override
	public void run() {
		
		if (listener != null)
			listener.started(this);
		
		while (true) {
			synchronized (this) {
				while (paused || globalPaused)
					try {
						Thread.sleep(50L);
					} catch (InterruptedException e) {}
				
				if (stopped) break;
				
				if (globalPaused && listener != null)
						listener.paused(this);
				
				result = sender.send(send, context);
				
				if (!result.wasDelivered()) {
					try {
						Thread.sleep(100L);
					} catch (InterruptedException e) {}
				} else {
					if (listener != null)
						listener.done(this);
					break;
				}
			}
		}
	}
	
	public void cancel() {
		stopped = true;
		if (listener != null)
			listener.cancelled(this);
	}
	
	public void pause() {
		paused = true;
		if (listener != null)
			listener.paused(this);
	}
	
	public void unpause() {
		paused = false;
		if (listener != null)
			listener.unpaused(this);
	}
	
	public static boolean isPausedAll() {
		return globalPaused;
	}
	
	public static void pauseAll() {
		globalPaused = true;
		if (listener != null)
			listener.allPaused();
	}
	
	public static void unpauseAll() {
		globalPaused = false;
		if (listener != null)
			listener.allUnpaused();
	}
	
	public boolean isPaused() {
		return globalPaused || paused;
	}
	
	public boolean isCancelled() {
		return stopped;
	}
	
	public boolean isDone() {
		return result != null && result.wasDelivered();
	}
	
	public boolean isWaiting() {
		return result != null && !result.wasDelivered();
	}
	
	public Cell getSender() {
		return sender;
	}
	
	public Send getSend() {
		return send;
	}
	
	public Context getContext() {
		return context;
	}
	
	public Result getResult() {
		return result;
	}
	
	@Override
	public String toString() {
		return "<" + sender + ", " + send + ", " + context + ">";
	}
	
	public interface Listener {
		
		public void started(Messenger m);
		
		public void paused(Messenger m);
		
		public void unpaused(Messenger m);
		
		public void cancelled(Messenger m);
		
		public void done(Messenger m);
		
		public void allPaused();
		
		public void allUnpaused();
		
	}
	
}
