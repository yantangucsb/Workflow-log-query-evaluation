package model.incidentree;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import optimizer.QueryEngine;
import model.incident.Occurrence;
import model.incidentree.IncidentTreeNode.NodeType;
import model.log.Log;

public class IncidentTree {
	IncidentTreeNode root = null;

	//@param: query: valid query expression
	public IncidentTree(String query) {
		if(query == null || query.length() == 0)
			return;
		root = buildTree(query);
		
	}

	private IncidentTreeNode buildTree(String query) {
		if(query == null || query.length() == 0)
			return null;
		IncidentTreeNode cur = null;
		int level = 0;
		char[] chs = query.toCharArray();
		int i=0;
		for(; i<chs.length; i++){
			if(chs[i] == '(' || chs[i] == '['){
				level++;
			}else if(chs[i] == ')' || chs[i] == ']'){
				level--;
			}else if((chs[i] >= 'a' && chs[i] <= 'z') || (chs[i] >= 'A' && chs[i] <= 'Z')){
				continue;
			}else if(QueryEngine.queryEngine.isOperator(chs[i])){
				if(level == 0){
					break;
				}
			}
		}
		if(i == chs.length){
//			System.out.println("[Debug: query containing only activities] " + query);
			int l = 0, r= chs.length-1;
			while(l < r && chs[l] == '(' && chs[r] == ')'){
				l++;
				r--;
			}
			if(l > r){
				return null;
			}
			if(l != 0){
				return buildTree(query.substring(l, r+1));
			}
			String leftCond = "", rightCond = "";
			String curStr = query.substring(l, r+1);
//			System.out.println("[Debug]: " + curStr);
			if(chs[l] == '['){
				String pattern = "^\\[([^\\[\\]]+)\\]";
				Pattern paToMatch = Pattern.compile(pattern);
				Matcher m = paToMatch.matcher(curStr);
				if(m.find()){
					leftCond = curStr.substring(m.start(1), m.end(1));
					curStr = curStr.substring(m.end(0));
//					System.out.println("[Debug: remove precondition] " + curStr);
				}
			}
			if(chs[r] == ']'){
				String pattern = "\\[([^\\[\\]]+)\\]$";
				Pattern paToMatch = Pattern.compile(pattern);
				Matcher m = paToMatch.matcher(curStr);
				if(m.find()){
					rightCond = curStr.substring(m.start(1), m.end(1));
					curStr = curStr.substring(0, m.start(0));
				}
			}
			if(leftCond.equals("") && rightCond.equals("")){
				cur = new ActiNode(curStr);
				return cur;
			}else{
				cur = new ConditionNode("[condition]");
				((ConditionNode)cur).preCon = leftCond;
				((ConditionNode)cur).postCon = rightCond;
//				System.out.println("[debug]: " + curStr);
				cur.left = buildTree(curStr);
				return cur;
			}
		}
		cur = new OpNode(query.substring(i, i+1));
		cur.left = buildTree(query.substring(0, i));
		cur.right = buildTree(query.substring(i+1));
		
		return cur;
	}

	public List<Occurrence> execute(Log log) {
		// TODO Auto-generated method stub
		return null;
	}

	public String toString() {
		if(root == null)
			return "";
		Queue<IncidentTreeNode> qu = new LinkedList<IncidentTreeNode>();
		StringBuilder sb = new StringBuilder();
		qu.add(root);
		while(!qu.isEmpty()){
			IncidentTreeNode cur = qu.remove();
			if(cur == null){
				sb.append("null,");
				continue;
			}
			sb.append('[');
			sb.append(cur.type);
			sb.append(',');
			if(cur.type == NodeType.COND){
				sb.append('\"');
				sb.append(((ConditionNode)cur).preCon);
				sb.append('\"');
				sb.append(',');
				sb.append('\"');
				sb.append(((ConditionNode)cur).postCon);
				sb.append('\"');
			}else
				sb.append(cur.name);
			sb.append("],");
			
			qu.add(cur.left);
			qu.add(cur.right);
		}
		return sb.toString();
	}

}
