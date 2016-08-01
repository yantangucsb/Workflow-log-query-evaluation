package model.incidentree;

import model.incidentree.IncidentTreeNode.NodeType;

public class ActiNode extends IncidentTreeNode {

	public ActiNode(String str) {
		super(str);
		type = NodeType.ACTI;
		
	}
	
	public void retrieveOcc(){
		
	}
	
}
