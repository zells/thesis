package org.zells.library.Test;

import org.zells.*;

public class ConsoleReaction extends NativeReaction {

	@Override
	public void execute(Cell receiver, Path role, Path message, DeliveryId eid) {
		Mailing mailing = new Mailing(new Path(message, new Path("value")), new Path());
		Messenger m = new Messenger(receiver, mailing, role, eid);
		m.start();
		
		System.out.println("############### " + m.waitForResult().deliveredTo);
	}
	
}
