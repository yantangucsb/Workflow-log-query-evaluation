package test;

import static org.junit.Assert.*;

import java.util.List;

import model.incident.Incident;
import model.incidentree.IncidentTree;
import model.log.Log;

import org.junit.BeforeClass;
import org.junit.Test;

import evaluation.QueryEngine;

public class TestQueryEngineLogic {
	Log log = new Log("data/tranlog.txt");

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}
	
	@Test
	public void testQueryExecution() {
		Incident incident = new Incident("(([x=1]a.b)+c)[y=1]");
		QueryEngine.queryEngine.query(incident, log);

	}
	
	@Test
	public void testQueryResults(){
		Incident incident = new Incident("start.GetRefer");
		List<Long> res = QueryEngine.queryEngine.query(incident, log);
		System.out.println(res.toString());
		
		Incident incident1 = new Incident("UpdateRefer:GetReimburse");
		List<Long> res1 = QueryEngine.queryEngine.query(incident1, log);
		System.out.println(res1.toString());
		
		Incident incident2 = new Incident("start.GetRefer[balance=2000]");
		List<Long> res2 = QueryEngine.queryEngine.query(incident2, log);
		System.out.println(res2.toString());
		
		Incident incident3 = new Incident("[balance=1000]GetReimburse[reimburse=1000]");
		List<Long> res3 = QueryEngine.queryEngine.query(incident3, log);
		System.out.println(res3.toString());
	}

}
