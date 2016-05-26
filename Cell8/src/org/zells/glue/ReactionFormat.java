package org.zells.glue;

import java.util.LinkedList;

import org.zells.*;

public class ReactionFormat {
	
	public static Reaction parse(String reactionString) throws Exception {
		Reaction reaction = new Reaction();
		
		AdvancedReader r = new AdvancedReader(reactionString);
		
		while (true) {
			Path receiver = null, message = null;
			
			r.skipWhitesAndBreaks();
			
			if (r.peek() == -1)
				break;
			
			try {
				receiver = PathFormat.parse(r);
			} catch (Exception e) {
				throw new Exception("Could not parse receiver path at " + r.getPos() + " in line " + r.getLine() + ":"
						+ e.getMessage());
			}
			
			r.skipWhites();
			
			if (r.peek() == -1 || r.isLineBreak(r.peek()))
				throw new Exception("Premature end after receiver path at " + r.getPos() + " in line " + r.getLine());
			
			try {
				message = PathFormat.parse(r);
			} catch (Exception e) {
				throw new Exception("Could not parse message path at " + r.getPos() + " in line " + r.getLine()
							+ ":" + e.getMessage());
			}
			
			r.skipWhites();
			
			if (r.peek() != -1 && !r.isLineBreak(r.peek()))
				throw new Exception("Expected new line after message path at " + r.getPos() + " in line " + r.getLine());
			
			reaction.mailings.add(new Mailing(receiver, message));
		}
		
		return reaction;
	}
	
	public static String format(Reaction reaction) {
		return format(reaction, "");
	}
	
	public static String format(Reaction reaction, String indent) {
		LinkedList<String> receivers = new LinkedList<String>();
		LinkedList<String> messages = new LinkedList<String>();
		
		int longestReceiver = 1;
		int longestMessage = 1;
		
		for (Mailing mailing : ((Reaction) reaction).mailings) {
			receivers.add(PathFormat.format(mailing.receiver));
			
			if (mailing.message != null) {
				if (receivers.getLast().length() > longestReceiver)
					longestReceiver = receivers.getLast().length();
				messages.add(PathFormat.format(mailing.message));
			} else {
				messages.add(null);
			}
		}
		
		StringBuilder sReaction = new StringBuilder();
		for (int i = 0; i < receivers.size(); i++) {
			sReaction.append(indent);
			sReaction.append(String.format("%-" + longestReceiver + "s", receivers.get(i)));
			if (messages.get(i) != null)
				sReaction.append(String.format("    %-" + longestMessage + "s", messages.get(i)));
			sReaction.append("\n");
		}
		return sReaction.toString();
	}
	
}
