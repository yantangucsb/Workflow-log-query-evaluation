package evaluation;

import model.incidentree.IncidentTree;
import model.incidentree.IncidentTreeNode;
import model.incidentree.IncidentTreeNode.NodeType;

public class CommutativeRule extends OperatorRule{

	@Override
	public boolean checkQualified(IncidentTreeNode cur) {
		if(cur.type != NodeType.OP || (!cur.name.equals("+") && !cur.name.equals("|")))
			return false;
		return true;
	}

	@Override
	public void perform(IncidentTree it, IncidentTreeNode cur,
			String accessCode) {
//		System.out.println("[Debug] perform associative rule on Node: " + cur.name);
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
		
		IncidentTreeNode tmp = node.left;
		node.left = node.right;
		node.right = tmp;
		
	}

}
