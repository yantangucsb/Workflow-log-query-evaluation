package test;

import static org.junit.Assert.*;
import model.incident.Incident;
import model.incidentree.IncidentTree;
import model.log.Log;
import optimizer.QueryEngine;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestQueryEngineLogic {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}
	
	@Test
	public void test() {
		Incident incident = new Incident("(([x=1]a.b)+c)[y=1]");
		Log log = new Log("tranlog.txt");
		QueryEngine.queryEngine.query(incident, log);
	}

}
