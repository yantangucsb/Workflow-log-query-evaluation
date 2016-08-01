package model.incidentree;

public class ConditionNode extends IncidentTreeNode {
	String preCon, postCon;

	public ConditionNode(String str) {
		super(str);
		type = NodeType.COND;
		preCon = "";
		postCon = "";
	}
	
	public void setConditions(String str1, String str2){
		preCon = str1;
		postCon = str2;
	}

}
