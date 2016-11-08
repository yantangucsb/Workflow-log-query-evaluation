
package evaluation;

import model.incidentree.IncidentTree;
import model.incidentree.IncidentTreeNode;
import model.incidentree.IncidentTreeNode.NodeType;

public class AssociativeRule extends OperatorRule{
	//if left is true, the formula is (a.b).c, otherwise a.(b.c).
	boolean left = false;

	@Override
	public boolean checkQualified(IncidentTreeNode cur) {
		if(cur.type != NodeType.OP || (!cur.name.equals(".") && !cur.name.equals(":")))
			return false;
		if(cur.left != null && (cur.left.name.equals(".") || cur.left.name.equals(":"))){
			left = true;
			return true;
		}
		if(cur.right != null && (cur.right.name.equals(".") || cur.right.name.equals(":"))){
			left = false;
			return true;
		}
		return false;
	}

	@Override
	public void perform(IncidentTree it, IncidentTreeNode cur, String accessCode) {
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
		
		IncidentTreeNode newRoot = null;
		if(left){
			newRoot = node.left;
			node.left = newRoot.right;
			newRoot.right = node;
		}else{
			newRoot = node.right;
			node.right = newRoot.left;
			newRoot.left = node;
		}
		
		if(accessCode.length() == 0){
			it.root = newRoot;
		}else if(accessCode.charAt(accessCode.length()-1) == '0'){
			parent.left = newRoot;
		}else{
			parent.right = newRoot;
		}
		
	}

}
