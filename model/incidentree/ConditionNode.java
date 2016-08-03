package model.incidentree;

import evaluation.QueryEngine;
import model.incident.Occurrence;
import model.incident.Operator;

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
		// TODO Auto-generated method stub
		return true;
	}

}
