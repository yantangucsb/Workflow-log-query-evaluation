package evaluation;

import model.incidentree.ConditionNode;
import model.incidentree.IncidentTree;
import model.incidentree.IncidentTreeNode;
import model.incidentree.IncidentTreeNode.NodeType;

public class ConditionRule extends OperatorRule {

	@Override
	public boolean checkQualified(IncidentTreeNode cur) {
		if(cur.type != NodeType.COND)
			return false;
		if(cur.left != null &&(cur.left.type == NodeType.OP && !cur.left.name.equals("+")))
			return true;
		return false;
	}

	@Override
	public void perform(IncidentTree it, IncidentTreeNode cur,
			String accessCode) {
		IncidentTreeNode parent = it.root, node = it.root;
		for(char ch: accessCode.toCharArray()){
			if(ch == '0'){
				parent = node;
				node = node.left;
			}else{
				parent = node;
				node = node.right;
			}
		}
		
		IncidentTreeNode newRoot = node.left;
		ConditionNode leftCond = new ConditionNode(node.name);
		leftCond.setConditions(((ConditionNode)node).preCon, "");
		ConditionNode rightCond = new ConditionNode(node.name);
		rightCond.setConditions("", ((ConditionNode)node).postCon);
		leftCond.left = newRoot.left;
		rightCond.left = newRoot.right;
		newRoot.left = leftCond;
		newRoot.right = rightCond;
		if(accessCode.length() == 0){
			it.root = newRoot;
		}else if(accessCode.charAt(accessCode.length()-1) == '0'){
			parent.left = newRoot;
		}else{
			parent.right = newRoot;
		}
		
	}

}
