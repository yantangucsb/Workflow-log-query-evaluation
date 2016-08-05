package model.incidentree;

import java.util.HashMap;
import java.util.Map;

import evaluation.QueryEngine;
import model.incident.Occurrence;
import model.incident.Operator;

public class ConditionNode extends IncidentTreeNode {
	String preCon, postCon;
	Map<String, String> preMap, postMap;

	public ConditionNode(String str) {
		super(str);
		type = NodeType.COND;
		preCon = "";
		postCon = "";
		preMap = new HashMap<String, String>();
		postMap = new HashMap<String, String>();
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
		System.err.println("[Debug: query thread] Condition Node " + preCon + " " + postCon);
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
		
		for(Occurrence occ: left.occs){
			if(checkCondition(occ))
				occs.add(occ);
		}
	}

	private boolean checkCondition(Occurrence occ) {
//		System.err.println("[Debug: cur condition node post map size] " + this.postMap.size());
//		System.err.println("[Debug: post Map] " + occ.postMap);
		for(Map.Entry<String, String> pair: preMap.entrySet()){
			if(!occ.preMap.containsKey(pair.getKey()) || !occ.preMap.get(pair.getKey()).equals(pair.getValue())){
				return false;
			}
		}
		for(Map.Entry<String, String> pair: postMap.entrySet()){
			if(!occ.postMap.containsKey(pair.getKey()) || !occ.postMap.get(pair.getKey()).equals(pair.getValue())){
				return false;
			}
		}
		return true;
	}

}
