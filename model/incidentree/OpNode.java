package model.incidentree;

import model.incident.Operator;
import optimizer.QueryEngine;


public class OpNode extends IncidentTreeNode{

	public OpNode(String str) {
		super(str);
		type = NodeType.OP;
	}

	@Override
	public void run() {
		System.err.println("[Debug: query thread] Operator Node " + name);
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
		
		Operator op = QueryEngine.queryEngine.operators.get(name.charAt(0));
		occs = op.execute(left.occs, right == null ? null: right.occs);
		
	}
}
