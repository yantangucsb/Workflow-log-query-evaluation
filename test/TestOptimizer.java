package test;

import static org.junit.Assert.*;
import model.incident.Incident;
import model.incidentree.IncidentTree;
import model.log.Log;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.Test;

import evaluation.Optimizer;
import evaluation.QueryEngine;

public class TestOptimizer extends Optimizer {
	Log log = new Log("data/tranlog.txt");

	@Test
	public void testCostModel() {
		QueryEngine.queryEngine.log = log;
		Incident incident = new Incident("start.GetRefer");
		System.out.println(Optimizer.estimateCost(incident.tree));
	}
	
	@Test
	public void testOptimizer() {
		QueryEngine.queryEngine.log = log;
		Incident incident = new Incident("start.GetRefer");
		IncidentTree curTree = SerializationUtils.clone(incident.tree);
		System.out.println(curTree.toString());
	}

}
