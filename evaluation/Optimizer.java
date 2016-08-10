package evaluation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.lang3.SerializationUtils;

import model.incident.Incident;
import model.incidentree.*;
import model.incidentree.IncidentTreeNode.NodeType;

public class Optimizer {

	public static void generateOptimalTree(Incident incident) {
		incident.optiTree = incident.tree;
		IncidentTree curTree = SerializationUtils.clone(incident.tree);
		Queue<IncidentTree> frontier = new LinkedList<IncidentTree>();
		frontier.add(curTree);
		Set<String> visited = new HashSet<String>();
		while(!frontier.isEmpty()){
			IncidentTree front = frontier.remove();
			if(estimateCost(front) < estimateCost(incident.optiTree)){
				incident.optiTree = front;
			}
			System.out.println(front.toString());
			visited.add(front.toString());
			List<IncidentTree> li = new ArrayList<IncidentTree>();
			generate(front, front.root, li, "");
			for(IncidentTree it: li){
				if(!visited.contains(it.toString())){
					frontier.add(it);
					visited.add(it.toString());
				}
			}
		}
	}
	
	private static void generate(IncidentTree curTree, IncidentTreeNode cur,
			List<IncidentTree> li, String accessCode) {
		if(cur == null)
			return;
		for(OperatorRule oprule: QueryEngine.queryEngine.rules){
			if(oprule.checkQualified(cur)){
				IncidentTree tmp = SerializationUtils.clone(curTree);
				oprule.perform(tmp, cur, accessCode);
				li.add(tmp);
			}
		}
		generate(curTree, cur.left, li, accessCode+"0");
		generate(curTree, cur.right, li, accessCode+"1");
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
			if(QueryEngine.queryEngine.log.actiStat.containsKey(root.name))
				return QueryEngine.queryEngine.log.actiStat.get(root.name).count;
			System.err.println("Current log doesn't have the activity: " + root.name);
			return 0;
		}else if(root.getType() == NodeType.COND){
			return estimate(root.left)/3.0;
		}else{
			return QueryEngine.queryEngine.operators.get(root.name).getCost1(estimate(root.left), estimate(root.right));
		}
	}

}
