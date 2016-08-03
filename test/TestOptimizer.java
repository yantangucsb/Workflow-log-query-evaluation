package test;

import static org.junit.Assert.*;
import model.incident.Incident;
import model.log.Log;

import org.junit.Test;

import evaluation.Optimizer;
import evaluation.QueryEngine;

public class TestOptimizer extends Optimizer {
	Log log = new Log("data/tranlog.txt");

	@Test
	public void test() {
		QueryEngine.queryEngine.log = log;
		Incident incident = new Incident("start.GetRefer");
		System.out.println(Optimizer.estimateCost(incident.tree));
	}

}
