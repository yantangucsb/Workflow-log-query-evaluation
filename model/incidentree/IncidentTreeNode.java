package model.incidentree;

import java.util.List;

import model.incident.Occurrence;

public abstract class IncidentTreeNode {
	public enum NodeType {
		ACTI, OP, COND
	};
	
	NodeType type;
	String name;
	IncidentTreeNode left, right;
	List<Occurrence> occ;
	
	public IncidentTreeNode(String str){
		name = str;
		left = null;
		right = null;
	}
}
