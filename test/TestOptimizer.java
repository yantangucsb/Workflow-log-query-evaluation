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
	Log log = new Log("data/output_07.txt");
//	Log log = new Log("data/tranlog.txt");

	@Test
	public void testCostModel() {
		QueryEngine.queryEngine.log = log;
		System.out.println(QueryEngine.queryEngine.log.statInfo());
		Incident incident = new Incident("start.GetRefer.SeeDoctor");
		System.out.println(Optimizer.estimateCost(incident.tree, 1));
		
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
		Incident incident = new Incident("start.GetRefer:GetReimburse");
		Optimizer.generateOptimalTree(incident, 1);
		System.out.println("---------------");
		Incident incident1 = new Incident("start:GetRefer.UpdateRefer.GetReimburse");
		Optimizer.generateOptimalTree(incident1, 1);
	}
	
	@Test
	public void testCommutativeRule() {
		QueryEngine.queryEngine.log = log;
		Incident incident = new Incident("start+GetRefer");
		Optimizer.generateOptimalTree(incident, 1);
		System.out.println("---------------");
		Incident incident1 = new Incident("start.(GetRefer+UpdateRefer)");
		Optimizer.generateOptimalTree(incident1, 1);
	}
	
	@Test
	public void testConditionRule() {
		QueryEngine.queryEngine.log = log;
		Incident incident = new Incident("[x=1](GetRefer.GetReimburse)[y=1]");
		Optimizer.generateOptimalTree(incident, 1);
		
		System.out.println("---------------");
		Incident incident1 = new Incident("start.([x=1](GetRefer.UpdateRefer)[y=1])");
		Optimizer.generateOptimalTree(incident1, 1);
	}
	
	@Test
	public void testDistributiveRule() {
		QueryEngine.queryEngine.log = log;
		Incident incident = new Incident("a.b.(c|d)");
		Optimizer.generateOptimalTree(incident, 1);
		System.out.println(incident.optiTree.toString());
	}
	
	@Test
	public void testOptimizer(){
		QueryEngine.queryEngine.log = log;
		String[] qs = {"CULTURE+RADIOLOG.INJMECH+OPRM:TREATMEN",
				"VITALS|INJMECH+QAISSUE|TTDETLS|PROTECT",
				"CULTURE+MTOS:EMERG:MAINDATA.QAISSUE",
				"TRANSPRT|NARRATIV:PERHIST+BURNS:ICU",
				"TRANSFER+LAB:GENMECH|NARRATIV:RADIOLOG",
				"TRANSPRT|STEP.CULTURE:INJDETS.STEP",
				"EMERG+TRA+LAB+HEMO:GENMECH",
				"TRANSPRT.CULTURE.MAINDATA+INJDETS.STEP",
				"GENMECH+MTOS:CONSULT:CULTURE+WARD",
				"HOSPREV|ORGANS:MAINDATA+TRA+TRANSFER"};
/*		for(String q: qs){
			System.out.println(q);
			Incident incident = new Incident(q);
			System.out.println(incident.tree.toString());
			Optimizer.generateOptimalTree(incident, 2);
			System.out.println(incident.optiTree.toString());
		}*/
		Incident incident = new Incident(qs[9]);
		System.out.println(incident.tree.toString());
		Optimizer.generateOptimalTree(incident, 2);
		System.out.println(incident.optiTree.toString());
	}

}
