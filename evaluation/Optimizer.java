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
import model.log.ProbModel;

public class Optimizer {
	static double filterRate = 10.0;
	static double epsilon = 1e-3;
	static int limitedSearchSpace = Integer.MAX_VALUE;
	static Set<String> visited;

	/*
	 * Used to do optimizer runtime evaluation
	 * @Param: model is a label of cost model
	 * model == 1, the cost model with assumption that no repeated activities in one instance
	 * model == 2, average of occurrence of activities is assumed
	 * model == 3, probability model built on histogram
	 */
	public static Incident[] generateOptimalTree(Incident incident, int model) {
		double[] estiCost = {0.0, 0.0};
		Incident[] rewrite = new Incident[2];
		rewrite[0] = new Incident();
		rewrite[1] = new Incident();
		incident.optiTree = incident.tree;
		IncidentTree curTree = SerializationUtils.clone(incident.tree);
		Queue<IncidentTree> frontier = new LinkedList<IncidentTree>();
		frontier.add(curTree);
		visited = new HashSet<String>();
		int count = 0;
		while(!frontier.isEmpty()){
			IncidentTree front = frontier.remove();
			if(visited.contains(front.toString())){
				continue;
			}
			count++;
			if(count == limitedSearchSpace)
				break;
			double curCost = estimateCost(front, model);
			if(estiCost[0] < 1e-7 || curCost < estiCost[0]){
				estiCost[0] = curCost;
				rewrite[0].tree = front;
				//incident.optiTree = front;
				//if(estiCost[0] < epsilon)
					//break;
			}
			
//			System.out.printf("[Debug] Frontier: %s\nCost: %.2f\n", front.toString(), curCost);
			visited.add(front.toString());
			List<IncidentTree> li = new ArrayList<IncidentTree>();
			generate(front, front.root, li, "");
			for(IncidentTree it: li){
				frontier.add(it);
			}
			double realRuntime = testRuntime(front);
			if(estiCost[1] < 1e-7 || realRuntime > estiCost[1]){
				estiCost[1] = realRuntime;
				rewrite[1].tree = front;
			}
//			System.out.printf("Runtime: %.3f\n", testRuntime(front));
		}
		return rewrite;
			//System.out.println("Best matched.");
		//return false;
//		System.out.println(incident.optiTree.toString());
/*		for(String str: rewrite){
			System.out.println(str);
		}
		for(double x: estiCost){
			System.out.printf("%.2f,", x);
		}
		System.out.println();
		for(double x: realCost){
			System.out.printf("%.2f,", x);
		}
		System.out.println();*/
	}
	
	// used to evaluate optimizer accuracy
	public static double generateOptimalTree_performance(Incident incident, int model) {
		List<Double> estiCost = new ArrayList<Double>();
		List<Double> realCost = new ArrayList<Double>();
		List<String> rewrite = new ArrayList<String>();
		incident.optiTree = incident.tree;
		IncidentTree curTree = SerializationUtils.clone(incident.tree);
		Queue<IncidentTree> frontier = new LinkedList<IncidentTree>();
		frontier.add(curTree);
		visited = new HashSet<String>();
		double optiCost = estimateCost(incident.optiTree, model);
		int bestEsti = 0;
		int bestReal = 0;
		int count = 0;
		while(!frontier.isEmpty()){
			IncidentTree front = frontier.remove();
			if(visited.contains(front.toString())){
				continue;
			}
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
//			System.out.printf("[Debug] Frontier: %s\nCost: %.2f\n", front.toString(), curCost);
			estiCost.add(curCost);
			rewrite.add(front.toString());
			
			visited.add(front.toString());
			List<IncidentTree> li = new ArrayList<IncidentTree>();
			generate(front, front.root, li, "");
			for(IncidentTree it: li){
				frontier.add(it);
			}
			double realRuntime = testRuntime(front);
			realCost.add(realRuntime);
			if(curCost < estiCost.get(bestEsti)) {
				bestEsti = estiCost.size()-1;
			}
			if(realRuntime < realCost.get(bestReal)) {
				bestReal = realCost.size()-1;
			}
//			System.out.printf("Runtime: %.3f\n", testRuntime(front));
		}
		
			//System.out.println("Best matched.");
		//return false;
//		System.out.println(incident.optiTree.toString());
		for(String str: rewrite){
			System.out.println(str);
		}
		for(double x: estiCost){
			System.out.printf("%.2f,", x);
		}
		System.out.println();
		for(double x: realCost){
			System.out.printf("%.2f,", x);
		}
		System.out.println();
		
		return Math.abs(realCost.get(bestReal) - realCost.get(bestEsti))/realCost.get(bestReal);
	}
	
	//calculated using time complexity
	private static double testRuntime(IncidentTree front) {
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
		return calcCost(front.root);
	}

	private static double calcCost(IncidentTreeNode root) {
		double norm = QueryEngine.queryEngine.log.size();
		if(root == null){
			return 0;
		}else if(root.type == NodeType.COND){
			return calcCost(root.left) + root.left.size/norm;
		}else if(root.type == NodeType.ACTI){
			return 0;
		}else{
			double cost = calcCost(root.left) + calcCost(root.right);
			switch(root.name){
			case ".": cost += 1.0*(root.left.size + root.right.size)/norm; break;
			case ":": cost += 1.0*root.left.size * root.right.size/norm; break;
			case "|": cost += 1.0*(root.left.size + root.right.size)/norm; break;
			case "+": cost += 1.0*root.left.size * root.right.size/norm; break;
			default: break;
			}
			return cost;
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
		if(model == 3){
			return estimate3(it.root, 3).cost;
		}
		return estimate(it.root, model).cost;
	}

	private static ProbModel estimate3(IncidentTreeNode root, int model) {
		long logSize = QueryEngine.queryEngine.log.size();
		
		ProbModel curModel = null;
		if(root == null)
			return null;
		else if(root.getType() == NodeType.ACTI){
			if(!QueryEngine.queryEngine.log.probInfo.containsKey(root.name)){
//				System.err.println("Current log doesn't have the activity: " + root.name);
				curModel = new ProbModel();
			}else 
				curModel = QueryEngine.queryEngine.log.probInfo.get(root.name);
		}else if(root.getType() == NodeType.COND){
			ProbModel probLeft = estimate3(root.left, model);
			if(probLeft == null || probLeft.numActi == 0){
				curModel = probLeft;
			}else{
				//condition node filter on a fixted rate
				
				//Caution: the histograms should change along the numActi
				probLeft.numActi /= filterRate;
				probLeft.cost = probLeft.cost + probLeft.numActi/logSize;
				curModel = probLeft;
			}
		}else{
			ProbModel costLeft = estimate3(root.left, model);
			ProbModel costRight = estimate3(root.right, model);
			double curCost = costLeft.cost + costRight.cost;
			double normLeftSize = (double)costLeft.numActi/logSize;
			double normRightSize = (double)costRight.numActi/logSize;
			switch(root.name){
			//binary search: normLeftSize*(Math.log(normRightSize)/Math.log(2))
			//linear scan: normLeftSize*normRightSize
			case ".": curCost += normLeftSize + normRightSize; break;
			case ":": curCost += normLeftSize*costRight.numActi; break;
			case "|": curCost += normLeftSize + normRightSize; break;
			case "+": curCost += normLeftSize * costRight.numActi; break;
			default: break;
			}
			
			curModel = QueryEngine.queryEngine.operators.get(root.name).estimate(costLeft, costRight);
			curModel.cost = curCost;
		}
		//System.out.format("%s\tnumActi:\t%d\tcost:\t%f\n", root.name, curModel.numActi, curModel.cost);
		return curModel;
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
			//measure wall clock time
			//double curCost = Math.max(costLeft.cost, costRight.cost);
			//measure machine time
			double curCost = costLeft.cost + costRight.cost;
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
