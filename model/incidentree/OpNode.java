package model.incidentree;

import evaluation.QueryEngine;
import model.incident.Operator;


public class OpNode extends IncidentTreeNode{

	public OpNode(String str) {
		super(str);
		type = NodeType.OP;
	}

	@Override
	public void run() {
//		System.err.println("[Debug: query thread] Operator Node " + name);
		Thread t1 = null, t2 = null;
		if(left != null){
			t1 = new Thread(left);
			t1.start();
		}
		if(right != null){
			t2 = new Thread(right);
			t2.start();
		}
		try {
			t1.join();
			t2.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Operator op = QueryEngine.queryEngine.operators.get(name);
		occs = op.execute(left.occs, right == null ? null: right.occs);
		
//		System.err.println("[Debug: query thread] Operator Node " + name + " get occurrences " + occs.size());
	}
}
