package model.incidentree;

import java.util.List;

import evaluation.QueryEngine;
import model.incident.Occurrence;
import model.incident.Operator;


public class OpNode extends IncidentTreeNode{

	public OpNode(String str) {
		super(str);
		type = NodeType.OP;
	}

	public OpNode(IncidentTreeNode root) {
		super(root.name);
		type = NodeType.OP;
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
		for(List<Occurrence> li: occs.values()){
			this.size += li.size();
		}
//		System.err.println("[Debug: query thread] Operator Node " + name + " get occurrences " + occs.size());
		System.out.format("[RUN]%s\tnumActi:\t%d\n", this.name, this.size);
	}
}
