package model.incidentree;

import java.util.List;

import evaluation.QueryEngine;
import model.incident.Occurrence;
import model.incident.Operator;
import model.incidentree.IncidentTreeNode.NodeType;
import model.log.Log;

public class ActiNode extends IncidentTreeNode {

	public ActiNode(String str) {
		super(str);
		type = NodeType.ACTI;
		
	}

	public ActiNode(IncidentTreeNode root) {
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

	@Override
	public void run() {
//		System.err.println("[Debug: query thread] Acti Node " + name);
		this.occs = QueryEngine.queryEngine.log.filter(name);
		
		//for optimizer performance analysis
		//should be commented when testing efficiency
		for(List<Occurrence> li: occs.values()){
			this.size += li.size();
		}
//		System.err.println("[Debug: query thread] Acti Node " + name + " get occurrences " + occs.size());
//		System.out.format("[RUN]%s\tnumActi:\t%d\n", this.name, this.size);
	}
	
}
