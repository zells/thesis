package org.rtens.cell;

public abstract class LibraryReaction implements NativeReaction {
	
	protected Result sendAndWait(Path receiver, Path message, Cell sender, Context context) {
		Messenger m = new Messenger(new Send(receiver, message), sender, context);
		m.start();
		
		int i = 0;
		while (!m.isDone()) {
			try {
				Thread.sleep(10L);
			} catch (InterruptedException e) {}
			i++;
			if (i > 100) {
				if (!m.isPaused())
					System.err.println("Waiting for " + new Send(receiver, message) + " in " + sender);
				i = 0;
			}
		}
		
		return m.getResult();
	}
}
