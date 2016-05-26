package org.zells;

public class Messenger extends Thread {
	
	private Cell sender;
	private Path role;
	private Mailing mailing;
	private DeliveryId eid;
	private int number;
	
	private Result result;
	
	private static boolean pausedAll;

	private static int counter = 0;
	public static Listener listener;
	
	public Messenger(Cell sender, Mailing mailing, Path role, DeliveryId eid) {
		this.sender = sender;
		this.role = role;
		this.mailing = mailing;
		this.eid = eid;
		number = counter++;
	}
	
	public Cell getSender() {
		return sender;
	}
	
	public Mailing getMailing() {
		return mailing;
	}
	
	public DeliveryId getDeliveryId() {
		if (result == null || result.deliveryId == null)
			return new DeliveryId(eid, number);
		
		return result.deliveryId;
	}
	
	@Override
	public void run() {
		
		if (listener != null)
			listener.started(this);
		
		do {
			if (!pausedAll)
				result = sender.send(mailing, role, eid);
			
			try {
				sleep(100L);
			} catch (InterruptedException e) {}
		} while (!hasDelivered());
		
		if (listener != null)
			listener.done(this);
	}
	
	public boolean isDone() {
		return hasDelivered();
	}
	
	public boolean isPaused() {
		return pausedAll;
	}
	
	public boolean isCancelled() {
		return false;
	}
	
	public boolean isWaiting() {
		return result != null && !hasDelivered();
	}
	
	public boolean hasDelivered() {
		return result != null && result.wasDelivered();
	}
	
	public Result getResult() {
		return result;
	}
	
	public Result waitForResult() {
		while (!hasDelivered())
			try {
				Thread.sleep(50L);
			} catch (InterruptedException e) {}
		
		return result;
	}
	
	public static void pauseAll() {
		pausedAll = true;
		if (listener != null)
			listener.allPaused();
	}
	
	public static void resumeAll() {
		pausedAll = false;
		if (listener != null)
			listener.allResumed();
	}
	
	public static boolean isPausedAll() {
		return pausedAll;
	}
	
	@Override
	public String toString() {
		return "{" + mailing + " | " + role + " | " + getDeliveryId() + "}";
	}
	
	public interface Listener {
		
		public void started(Messenger m);
		
		public void paused(Messenger m);
		
		public void unpaused(Messenger m);
		
		public void cancelled(Messenger m);
		
		public void done(Messenger m);
		
		public void allPaused();
		
		public void allResumed();
		
	}
	
}
