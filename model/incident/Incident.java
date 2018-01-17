package model.incident;

import java.util.List;

import evaluation.QueryEngine;
import model.incidentree.IncidentTree;
import model.log.Log;

public class Incident {
	public String incidentExpr;
	public IncidentTree tree;
	public IncidentTree optiTree;
	public List<Occurrence> occs;
	
	enum queryEle{
		ACTI, LPAREN, RPAREN, LBRACKET, RBRACKET, OP, ATTR
	};
	
	public Incident() {
		incidentExpr = "";
		tree = null;
		optiTree = null;
		occs = null;
	}
	
	public Incident(String query) {
		if(!checkQueryValidity(query)){
			System.err.println("Invalid query!");
			return;
		}
		incidentExpr = query;
		tree = new IncidentTree(query);
		optiTree = null;
	}
	
	//check parentheses, brackets
	private boolean checkQueryValidity(String query) {
		if(query == null || query.length() == 0)
			return false;
		int pcount = 0, bcount = 0;
		
		char[] chs = query.toCharArray();
		for(int i=0; i<chs.length; i++){
			if(chs[i] == '('){
				pcount++;
			}else if(chs[i] == ')'){
				if(pcount == 0)
					return false;
				pcount--;
			}else if(chs[i] == '['){
				bcount++;
			}else if(chs[i] == ']'){
				if(bcount == 0)
					return false;
				bcount--;
			}else if((chs[i] >= 'a' && chs[i] <= 'z') || (chs[i] >= 'A' && chs[i] <= 'Z')){
				continue;
			}else if(QueryEngine.queryEngine.isOperator(chs[i]+"")){
				continue;
			}
		}
		return pcount == 0 && bcount == 0;
	}

}
