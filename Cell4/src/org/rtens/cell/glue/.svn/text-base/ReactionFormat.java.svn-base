package org.rtens.cell.glue;

import java.util.LinkedList;

import org.rtens.cell.*;

public class ReactionFormat {
	
	public static Reaction parse(String reactionString) throws Exception {
		Reaction reaction = new Reaction();
		
		AdvancedReader r = new AdvancedReader(reactionString);
		
		while (true) {
			Path receiver = null, messenger = null;
			
			r.skipWhites();
			
			if (r.peek() == -1)
				break;
			
			try {
				receiver = PathFormat.parse(r);
			} catch (Exception e) {
				throw new Exception("Could not parse receiver path at " + r.getPos() + " in line " + r.getLine() + ":"
						+ e.getMessage());
			}
			
			r.skipWhites();
			
			if (r.peek() == -1)
				throw new Exception("Premature end after receiver path at " + r.getPos() + " in line " + r.getLine());
			
			try {
				messenger = PathFormat.parse(r);
			} catch (Exception e) {
				throw new Exception("Could not parse message path at " + r.getPos() + " in line " + r.getLine() + ":"
						+ e.getMessage());
			}
			
			reaction.add(new Send(receiver, messenger));
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
		
		for (Send send : ((Reaction) reaction).getSends()) {
			receivers.add(PathFormat.format(send.getReceiver()));
			if (receivers.getLast().length() > longestReceiver)
				longestReceiver = receivers.getLast().length();
			messages.add(PathFormat.format(send.getMessage()));
		}
		
		StringBuilder sReaction = new StringBuilder();
		for (int i = 0; i < receivers.size(); i++) {
			sReaction.append(indent);
			sReaction.append(String.format("%-" + longestReceiver + "s    %s", receivers.get(i), messages.get(i)));
			sReaction.append("\n");
		}
		return sReaction.toString();
	}
	
}
