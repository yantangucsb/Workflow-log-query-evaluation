package model.incidentree;

import java.util.*;

import evaluation.QueryEngine;
import model.incident.Occurrence;
import model.incident.Operator;
import model.incidentree.IncidentTreeNode.NodeType;

public class ConditionNode extends IncidentTreeNode {
	public String preCon, postCon;
	public Map<String, String> preMap, postMap;

	public ConditionNode(String str) {
		super(str);
		type = NodeType.COND;
		preCon = "";
		postCon = "";
		preMap = new HashMap<String, String>();
		postMap = new HashMap<String, String>();
	}
	
	public ConditionNode(IncidentTreeNode root) {
		super(root.name);
		type = root.type;
		if(root.left != null){
			if(root.left.type == NodeType.OP)
				this.left = new OpNode(root.left);
			else if(root.left.type == NodeType.ACTI)
				this.left = new ActiNode(root.left);
			else
				this.left = new ConditionNode(root.left);
		}
		if(root.right != null){
			if(root.right.type == NodeType.OP)
				this.right = new OpNode(root.right);
			else if(root.right.type == NodeType.ACTI)
				this.right = new ActiNode(root.right);
			else
				this.right = new ConditionNode(root.right);
		}
	}

	public void setConditions(String str1, String str2){
		preCon = str1;
		postCon = str2;
		if(str1.length() > 0){
			String[] strs = preCon.split(",");
			for(String s: strs){
				String[] pair = s.split("=");
				preMap.put(pair[0], pair[1]);
			}
		}
		if(str2.length() > 0){
			String[] strs = postCon.split(",");
			for(String s: strs){
				String[] pair = s.split("=");
				postMap.put(pair[0], pair[1]);
			}
		}
	}

	@Override
	public void run() {
//		System.err.println("[Debug: query thread] Condition Node " + preCon + " " + postCon);
		Thread t1 = null;
		if(left != null){
			t1 = new Thread(left);
			t1.start();
		}
		
		try {
			t1.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.occs = new HashMap<Integer, List<Occurrence>>();
		for(int key: left.occs.keySet()){
			List<Occurrence> li = left.occs.get(key);
			for(Occurrence occ: li){
				if(checkCondition(occ)){
					if(!occs.containsKey(occ.wid)){
						occs.put(key, new ArrayList<Occurrence>());
					}
					occs.get(key).add(occ);
				}
			}
		
		}
		for(List<Occurrence> li: occs.values()){
			this.size += li.size();
		}
//		System.out.format("[RUN]%s\tnumActi:\t%d\n", this.name, this.size);
	}

	private boolean checkCondition(Occurrence occ) {
//		System.err.println("[Debug: cur condition node post map size] " + this.postMap.size());
//		System.err.println("[Debug: post Map] " + occ.postMap);
		for(Map.Entry<String, String> pair: preMap.entrySet()){
			if(!occ.getPreMap().containsKey(pair.getKey()) || !occ.getPreMap().get(pair.getKey()).equals(pair.getValue())){
				return false;
			}
		}
		for(Map.Entry<String, String> pair: postMap.entrySet()){
			if(!occ.getPostMap().containsKey(pair.getKey()) || !occ.getPostMap().get(pair.getKey()).equals(pair.getValue())){
				return false;
			}
//			if(occ.preMap.containsKey(pair.getKey()) && occ.preMap.get(pair.getKey()) == pair.getValue())
//				return false;
		}
		
		// Based on def of postcondition
		// If it is effect of the occ not the postsnapshot, then uncomment the following
		return checkEffect(occ);
	}

	private boolean checkEffect(Occurrence occ) {
		for(String str: postMap.keySet()){
			boolean found = false;
			for(int i=occ.size()-1; i>=0; i--){
				if(occ.get(i).attWrite.containsKey(str)){
					if(!found)
						found = true;
					if(occ.get(i).attWrite.get(str).equals(postMap.get(str))){
						break;
					}else{
						return false;
					}
				}
			}
			if(!found)
				return false;
		}
		return true;
		
	}

}
