package test;

import static org.junit.Assert.*;

import java.util.List;

import model.incident.Incident;
import model.incident.Occurrence;
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
	public void testProbModel_synthetic(){
		QueryEngine.queryEngine.log = log;
		//System.out.println(log.toString());
		//Incident incident = new Incident("GetRefer.CheckIn");
		//Incident incident = new Incident("MAINDATA.VITALS");
		String[] qs = {"GetRefer.CheckIn", "start.GetRefer", "start.CheckIn", "CheckIn.SeeDoctor", "CheckIn.PayTreatment", "start.GetRefer.CheckIn"};
		for(String str: log.probInfo.keySet()){
			System.out.println(str + log.probInfo.get(str).toString());
		}
		for(int i=0; i<qs.length; i++){
			System.out.println("Test case " + i + ":");
			Incident incident = new Incident(qs[i]);
			
			System.out.println("Estimated cost: " + Optimizer.estimateCost(incident.tree, 3));
			List<Occurrence> res = QueryEngine.queryEngine.queryOcc(incident, log);
			System.out.println("Result: " + res.size() + "," + res.toString());
			
		}
		
	}
	
	@Test
	public void testProbModel_trauma(){
		QueryEngine.queryEngine.log = log;
		//System.out.println(log.toString());
		//Incident incident = new Incident("GetRefer.CheckIn");
		//Incident incident = new Incident("MAINDATA.VITALS");
		String[] qs = {"ICU.INJDETS", "TRANSFER.TRA", "ICU.HOSPREV", "PRECONDS.HOSPREV",
				"MTOS.QAISSUE", "INJDETS.PROTECT", "FLDDETAI.INJDETS", "VITALS.FLDDETAI",
				"TREATMEN.LAB", "MORTDETS.ORGANS"};
		String[] qs2 = {"ICU:INJDETS", "TRANSFER:TRA", "ICU:HOSPREV", "PRECONDS:HOSPREV",
				"MTOS:QAISSUE", "INJDETS:PROTECT", "FLDDETAI:INJDETS", "VITALS:FLDDETAI",
				"TREATMEN:LAB", "MORTDETS:ORGANS"};
		String[] qs3 = {"ICU|INJDETS", "TRANSFER|TRA", "ICU|HOSPREV", "PRECONDS|HOSPREV",
				"MTOS|QAISSUE", "INJDETS|PROTECT", "FLDDETAI|INJDETS", "VITALS|FLDDETAI",
				"TREATMEN|LAB", "MORTDETS|ORGANS"};
		String[] qs4 = {"ICU+INJDETS", "TRANSFER+TRA", "ICU+HOSPREV", "PRECONDS+HOSPREV",
				"MTOS+QAISSUE", "INJDETS+PROTECT", "FLDDETAI+INJDETS", "VITALS+FLDDETAI",
				"TREATMEN+LAB", "MORTDETS+ORGANS"};
		//System.out.println(QueryEngine.queryEngine.log.probInfo.get("MORTDETS"));
		//System.out.println(QueryEngine.queryEngine.log.probInfo.get("ORGANS"));
		String[] qs1 = {"BURNS.HEMO.TREATMEN",
				"LAB.LAB.DIAGS",
				"EMERG.POSTHOSP.INJDIAG",
				"PRECONDS.INJMECH.ADM_INPT",
				"HEMO.READMIT.STEP",
				"POSTHOSP.RADIOLOG.NARRATIV",
				"QAISSUE.CULTURE.POSTHOSP",
				"PRECONDS.TTDETLS.CULTURE",
				"ICU.CONSULT.CONSULT",
				"OPRM.INJMECH.LAB"};
		for(int i=0; i<qs4.length; i++){
			System.out.println("Test case " + i + ":");
			Incident incident = new Incident(qs4[i]);
			
			System.out.println("Estimated cost: " + Optimizer.estimateCost(incident.tree, 3));
			List<Occurrence> res = QueryEngine.queryEngine.queryOcc(incident, log);
			System.out.println("Result: " + res.size());
			//System.out.println("Result: " + res.size() + "," + res.toString());
		}
		
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
