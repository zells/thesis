package org.zells.library.Zells.Number;

import org.zells.*;

public abstract class Reaction extends NativeReaction {
	
	@Override
	public void execute(Cell receiver, Path role, Path message, DeliveryId eid) {
		
		Messenger m1 = new Messenger(receiver,
				new Mailing(new Path(role.subPath(-2), new Path("value")), new Path()), role, eid);
		Messenger m2 = new Messenger(receiver,
				new Mailing(new Path(message, new Path("value")), new Path()), role, eid);
		
		m1.start();
		m2.start();
		
		Path p1 = m1.waitForResult().deliveredTo;
		Path p2 = m2.waitForResult().deliveredTo;
		
		Path response = getResponse(Integer.parseInt(p1.getLast()), Integer.parseInt(p2.getLast()));
		
		new Messenger(receiver, new Mailing(new Path(role, new Path("message", "respond")), response), role, eid)
				.start();
	}
	
	abstract protected Path getResponse(int a, int b);
	
}
