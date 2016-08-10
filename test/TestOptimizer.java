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
	public void testOptimizerClone() {
		QueryEngine.queryEngine.log = log;
		Incident incident = new Incident("start.GetRefer");
		IncidentTree curTree = SerializationUtils.clone(incident.tree);
		System.out.println(curTree.toString());
	}
	
	@Test
	public void testAssociativeRule() {
		QueryEngine.queryEngine.log = log;
		Incident incident = new Incident("start.(GetRefer:GetReimburse)");
		Optimizer.generateOptimalTree(incident);
		System.out.println("---------------");
		Incident incident1 = new Incident("start.(GetRefer:(UpdateRefer.GetReimburse))");
		Optimizer.generateOptimalTree(incident1);
	}
	
	@Test
	public void testCommutativeRule() {
		QueryEngine.queryEngine.log = log;
		Incident incident = new Incident("start+GetRefer");
		Optimizer.generateOptimalTree(incident);
		System.out.println("---------------");
		Incident incident1 = new Incident("start.(GetRefer+UpdateRefer)");
		Optimizer.generateOptimalTree(incident1);
	}
	
	@Test
	public void testConditionRule() {
		QueryEngine.queryEngine.log = log;
		Incident incident = new Incident("[x=1](GetRefer.GetReimburse)[y=1]");
		Optimizer.generateOptimalTree(incident);
		
		System.out.println("---------------");
		Incident incident1 = new Incident("start.([x=1](GetRefer.UpdateRefer)[y=1])");
		Optimizer.generateOptimalTree(incident1);
	}
	
	@Test
	public void testDistributiveRule() {
		QueryEngine.queryEngine.log = log;
		Incident incident = new Incident("start.(GetRefer|GetReimburse)");
		Optimizer.generateOptimalTree(incident);
		
	}

}
