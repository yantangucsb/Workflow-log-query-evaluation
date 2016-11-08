package test;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;

import model.incident.Incident;
import model.incident.Occurrence;
import model.incidentree.IncidentTree;
import model.log.Log;

import org.junit.BeforeClass;
import org.junit.Test;

import evaluation.QueryEngine;

public class TestQueryEngineLogic {
	Log log = new Log("data/output_07.txt");

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}
	
	@Test
	public void testQueryExecution() {
		QueryEngine.queryEngine.log = log;
		Incident incident = new Incident("(([x=1]a.b)+c)[y=1]");
		QueryEngine.queryEngine.query(incident);

	}
	
	@Test
	//test cases for hospital refer application
	public void testQueryResults(){
		QueryEngine.queryEngine.log = log;
		Incident incident = new Incident("start.GetRefer");
		List<Long> res = QueryEngine.queryEngine.query(incident);
		System.out.println(res.toString());
		
		Incident incident1 = new Incident("UpdateRefer:GetReimburse");
		List<Long> res1 = QueryEngine.queryEngine.query(incident1);
		System.out.println(res1.toString());
		
		Incident incident2 = new Incident("start.GetRefer[balance=2000]");
		List<Long> res2 = QueryEngine.queryEngine.query(incident2);
		System.out.println(res2.toString());
		
		Incident incident3 = new Incident("[balance=1000]GetReimburse[reimburse=1000]");
		List<Long> res3 = QueryEngine.queryEngine.query(incident3);
		System.out.println(res3.toString());
	}
	
	@Test
	public void testTraumaData(){
		QueryEngine.queryEngine.log = log;
		String q1 = "ICU[los=1]";
		String q2 = "MAINDATA[admdate=20070104]";
		String q3 = "ICU:INJDETS";
		Incident incident = new Incident(q3);
		List<Long> res = QueryEngine.queryEngine.query(incident);
		Collections.sort(res);
//		List<Occurrence> res = QueryEngine.queryEngine.queryOcc(incident, log);
		System.out.println(res.toString());
		System.out.println(res.size());
	}

}
