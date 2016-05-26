package org.zells.library.Cell;

import org.zells.*;

public class respondReaction extends NativeReaction {
	
	@Override
	public void execute(Cell receiver, Path role, Path message, DeliveryId eid) {
		new Messenger(receiver,
				new Mailing(new Path(role.subPath(-2), new Path("cell", "create", "response")), message),
				role, eid).start();
	}
	
}
