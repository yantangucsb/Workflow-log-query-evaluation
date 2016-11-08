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
import model.log.Activity;

public class Optimizer {
	static double filterRate = 10.0;
	static double epsilon = 1e-3;
	static int limitedSearchSpace = Integer.MAX_VALUE;
	static Set<String> visited;

	public static void generateOptimalTree(Incident incident, int model) {
		incident.optiTree = incident.tree;
		IncidentTree curTree = SerializationUtils.clone(incident.tree);
		Queue<IncidentTree> frontier = new LinkedList<IncidentTree>();
		frontier.add(curTree);
		visited = new HashSet<String>();
		double optiCost = estimateCost(incident.optiTree, model);
		int count = 0;
		while(!frontier.isEmpty()){
			IncidentTree front = frontier.remove();
			count++;
			if(count == limitedSearchSpace)
				break;
			double curCost = estimateCost(front, model);
			if(curCost < optiCost){
				optiCost = curCost;
				incident.optiTree = front;
				if(optiCost < epsilon)
					break;
			}
//			System.out.println("[Debug] Frontier: " + front.toString() + "\nCost: " + curCost);
			
			visited.add(front.toString());
			List<IncidentTree> li = new ArrayList<IncidentTree>();
			generate(front, front.root, li, "");
			for(IncidentTree it: li){
				frontier.add(it);
				visited.add(it.toString());
			}
//			System.out.println("Runtime: " + testRuntime(front));
		}
//		System.out.println(incident.optiTree.toString());
	}
	
	private static long testRuntime(IncidentTree front) {
		long t1 = System.currentTimeMillis();
		Thread t = new Thread(front.root);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long t2 = System.currentTimeMillis();
		return t2 - t1;
	}

	private static void generate(IncidentTree curTree, IncidentTreeNode cur,
			List<IncidentTree> li, String accessCode) {
		if(cur == null)
			return;
		for(OperatorRule oprule: QueryEngine.queryEngine.rules){
			if(oprule.checkQualified(cur)){
				IncidentTree tmp = SerializationUtils.clone(curTree);
				oprule.perform(tmp, cur, accessCode);
//				System.out.println("[Debug] Perform: " + tmp.toString());
				if(!visited.contains(tmp.toString())){
					li.add(tmp);
				}
				
			}
		}
		generate(curTree, cur.left, li, accessCode+"0");
		generate(curTree, cur.right, li, accessCode+"1");
	}


	public static double estimateCost(IncidentTree it, int model){
		if(it == null)
			return 0;
		return estimate(it.root, model).cost;
	}

	//normalized cost with log size -- 11/4/2016
	private static CostModel estimate(IncidentTreeNode root, int model) {
		long logSize = QueryEngine.queryEngine.log.size();
		CostModel curModel = null;
		
		if(root == null)
			return null;
		else if(root.getType() == NodeType.ACTI){
//			System.err.println("[Debug] estimate: Node name --" + root.name);
			if(QueryEngine.queryEngine.log.actiStat.containsKey(root.name)){
				curModel = new CostModel(QueryEngine.queryEngine.log.actiStat.get(root.name));
				curModel.cost = 1;
			}else
//			System.err.println("Current log doesn't have the activity: " + root.name);
				curModel = new CostModel(root.name);
		}else if(root.getType() == NodeType.COND){
			CostModel costLeft = estimate(root.left, model);
			if(costLeft == null || costLeft.count == 0){
				curModel = costLeft;
			}else{
				//condition node only reduce the count
				costLeft.count /= filterRate;
				costLeft.cost = costLeft.cost + costLeft.count/logSize;
				curModel = costLeft;
			}
		}else{
			CostModel costLeft = estimate(root.left, model);
			CostModel costRight = estimate(root.right, model);
			double curCost = Math.max(costLeft.cost, costRight.cost);
			int len = costLeft.len + costRight.len;
			double normLeftSize = (double)costLeft.count/logSize;
			double normRightSize = (double)costRight.count/logSize;
			switch(root.name){
			//binary search: normLeftSize*(Math.log(normRightSize)/Math.log(2))
			//linear scan: normLeftSize*normRightSize
			case ".": curCost += normLeftSize == 0 || normRightSize == 0? 0: normLeftSize*costRight.count; break;
			case ":": curCost += normLeftSize*costRight.count; break;
			case "|": curCost += normLeftSize + normLeftSize; 
				      len = Math.max(costLeft.len, costRight.len); break;
			case "+": curCost += normLeftSize * costRight.count * len; break;
			default: break;
			}
			
			if(model == 1){
				long curSize = QueryEngine.queryEngine.operators.get(root.name).getResultSize1(costLeft.count, costRight.count);
				curModel = new CostModel(costLeft.name);
				curModel.count = curSize;
			}else
				curModel = QueryEngine.queryEngine.operators.get(root.name).getResultSize2(costLeft, costRight);
			
			curModel.cost = curCost;
			curModel.len = len;
			
		}
//		System.out.println("[Node " + root.name + " -- Estimated Cost:" + curModel.cost + " Count: " + curModel.count);
		return curModel;
	}

}
