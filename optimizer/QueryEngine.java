package optimizer;

import java.util.HashMap;

import model.incident.*;
import model.incidentree.ActiNode;
import model.incidentree.IncidentTreeNode;
import model.incidentree.IncidentTreeNode.NodeType;
import model.log.Log;

public class QueryEngine {
	public static QueryEngine queryEngine = new QueryEngine();
	public HashMap<Character, Operator> operators;
	public Log log;
	
	public QueryEngine(){
		addOperators();
	}

	private void addOperators() {
		operators = new HashMap<Character, Operator>();
		operators.put('.', new ConsOperator());
		operators.put(':', new SequOperator());
		operators.put('+', new ParaOperator());
		operators.put('|', new ExclOperator());
		
	}
	
	public boolean isOperator(char ch){
		if(operators.containsKey(ch))
			return true;
		return false;
	}

	public void query(Incident incident, Log log) {
		if(log == null)
			return;
		this.log = log;
		Thread t = new Thread(incident.tree.root);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
