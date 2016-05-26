package org.rtens.cell.connection;

import java.util.*;

import org.rtens.cell.*;

public class LocalServer extends Server {
	
	public LocalServer(Cell root) {
		super(root);
	}
	
	@Override
	protected void close() {}
	
	@Override
	public Host getHost() {
		return LocalClient.host;
	}
	
	@Override
	public List<String> getParameterNames() {
		return new LinkedList<String>();
	}
	
	@Override
	public void run() {}
	
	@Override
	public void setParameters(Map<String, String> params) throws Exception {}
	
	public Result deliver(Path path, Parameters p) {
		return resolveCell(path, p).deliver(p);
	}
	
	public Result deliverToChild(Path path, Parameters p) {
		return resolveCell(path, p).deliverToChild(p);
	}
	
	public Result deliverToStem(Path path, Parameters p) {
		return resolveCell(path, p).deliverToStem(p);
	}
	
	@Override
	public String getNetwork() {
		return LocalClient.host.network;
	}
	
}
