package model.incident;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import evaluation.CostModel;
import model.log.Activity;
import model.log.ProbModel;

public class ExclOperator extends Operator {
	public Map<Integer, List<Occurrence>> execute(Map<Integer, List<Occurrence>> occs1, Map<Integer, List<Occurrence>> occs2){
		Map<Integer, List<Occurrence>> res = new HashMap<Integer, List<Occurrence>>(occs1);
		for(int key: occs2.keySet()){
			if(!res.containsKey(key)){
				res.put(key, new ArrayList<Occurrence>());
			}
			res.get(key).addAll(occs2.get(key));
		}
		return res;
	}

	@Override
	public long getResultSize1(long c1, long c2) {
		return c1 + c2;
	}

	@Override
	public CostModel getResultSize2(CostModel a1, CostModel a2) {
		CostModel cur = new CostModel(a1.name);
		cur.aveStart = Math.min(a1.aveStart, a2.aveStart);
		cur.aveInterval = Math.max(Math.min(a1.aveInterval, a2.aveInterval)/2, 1);
		cur.count = a1.count + a2.count;
		cur.numStart = Math.max(a1.numStart, a2.numStart);
		return cur;
	}

	@Override
	public ProbModel estimate(ProbModel incidentHist1, ProbModel incidentHist2) {
		// TODO Auto-generated method stub
		return null;
	}
}
