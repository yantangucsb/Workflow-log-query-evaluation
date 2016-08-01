package model.incidentree;


public class OpNode extends IncidentTreeNode{

	public OpNode(String str) {
		super(str);
		type = NodeType.OP;
	}
}
