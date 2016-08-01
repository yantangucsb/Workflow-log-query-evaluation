package model.incidentree;

import optimizer.QueryEngine;
import model.incident.Operator;
import model.incidentree.IncidentTreeNode.NodeType;
import model.log.Log;

public class ActiNode extends IncidentTreeNode {

	public ActiNode(String str) {
		super(str);
		type = NodeType.ACTI;
		
	}

	@Override
	public void run() {
		System.err.println("[Debug: query thread] Acti Node " + name);
		this.occs = QueryEngine.queryEngine.log.filter(name);
		
	}
	
}
