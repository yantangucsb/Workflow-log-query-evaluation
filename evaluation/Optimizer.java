package evaluation;

import model.incident.Incident;
import model.incidentree.IncidentTree;
import model.incidentree.IncidentTreeNode;
import model.incidentree.IncidentTreeNode.NodeType;

public class Optimizer {

	public static void generateOptimalTree(Incident incident) {
		
	}
	
	public static double estimateCost(IncidentTree it){
		if(it == null)
			return 0;
		return estimate(it.root);
	}

	private static double estimate(IncidentTreeNode root) {
		if(root == null)
			return 0;
		if(root.getType() == NodeType.ACTI){
			return QueryEngine.queryEngine.log.actiStat.get(root.name).count;
		}else if(root.getType() == NodeType.COND){
			return estimate(root.left)/3.0;
		}else{
			return QueryEngine.queryEngine.operators.get(root.name).getCost1(estimate(root.left), estimate(root.right));
		}
	}

}
