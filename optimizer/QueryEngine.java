package optimizer;

import java.util.HashMap;

import model.incident.*;
import model.log.Log;

public class QueryEngine {
	public static QueryEngine queryEngine = new QueryEngine();
	public HashMap<Character, Operator> operators;
	
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
		// TODO Auto-generated method stub
		
	}
}
