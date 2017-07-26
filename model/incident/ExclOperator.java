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
		if(occs1 == null){
			return occs2;
		}else if(occs2 == null){
			return occs1;
		}
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
		ProbModel curHist = new ProbModel();
		
		// If one operand is empty, no results are generated
		if(incidentHist1.numActi == 0 || incidentHist2.numActi == 0)
			return curHist;
		
		// constraint on wid_1 == wid_2
		//double factor = 1.0/QueryEngine.queryEngine.log.numWorkflow;
		//double factor = 1.0/Math.min(incidentHist1.numInst, incidentHist2.numInst);
		double factor = 0.0;
		//factor method 2
		/*for(int x: incidentHist1.instDict.keySet()){
			if(incidentHist2.instDict.containsKey(x)){
				double prob_inst = incidentHist1.instDict.get(x)*incidentHist2.instDict.get(x);
				curHist.instDict.put(x, prob_inst);
				factor += incidentHist1.instDict.get(x)*incidentHist2.instDict.get(x);
			}
		}
		*/
		//factor method 1
		factor = 1.0/Math.min(incidentHist1.numInst, incidentHist2.numInst);

		//experimenting with numbers
		/*for(int s: incidentHist1.startHist.keySet()){
			double probStart1 = incidentHist1.startHist.get(s);
			double probStart2 = 0;
			for(int d: incidentHist1.durHist.keySet()){
				if(!incidentHist2.startHist.containsKey(s+d)){
					continue;
				}
				double prob_d = 1.0*incidentHist1.durHist.get(d)/incidentHist1.numActi*incidentHist2.startHist.get(s+d);
				probStart2 += prob_d;
				for(int d2: incidentHist2.durHist.keySet()){
					int probDur = (int) (1.0*probStart1*prob_d*incidentHist2.durHist.get(d2)/incidentHist2.numActi*factor);
					if(curHist.durHist.containsKey(d+d2)){
						curHist.durHist.put(d+d2, curHist.durHist.get(d+d2)+probDur);
					}else{
						curHist.durHist.put(d+d2, probDur);
					}
				}
			}
			
			int curprobStart = (int) (probStart1*probStart2*factor);
			curHist.startHist.put(s, curprobStart);
			curHist.numActi += curprobStart;
		}*/
		
		//update startHist/durHist
		double totalprobStart = 0;
		double totalprobDur = 0;
		for(int s = Math.min(incidentHist1.minStart, incidentHist2.minStart); s < Math.max(incidentHist1.maxStart, incidentHist2.maxStart); s++){
			double prob_s = 0;
			if(incidentHist1.startHist.containsKey(s))
				prob_s += incidentHist1.startHist.get(s);
			if(incidentHist2.startHist.containsKey(s))
				prob_s += incidentHist2.startHist.get(s);
			prob_s *= factor;
			if(prob_s > 1e-7){
				curHist.startHist.put(s, prob_s);
				totalprobStart += prob_s;
				curHist.minStart = Math.min(curHist.minStart, s);
				curHist.maxStart = Math.max(curHist.maxStart, s);
			}
		}
		
		for(int d = Math.min(incidentHist1.minDur, incidentHist2.minDur); d < Math.max(incidentHist1.maxDur, incidentHist2.maxDur); d++){
			double prob_d = 0;
			if(incidentHist1.durHist.containsKey(d))
				prob_d += incidentHist1.startHist.get(d);
			if(incidentHist2.durHist.containsKey(d))
				prob_d += incidentHist2.startHist.get(d);
			prob_d *= factor;
			if(prob_d > 1e-7){
				curHist.durHist.put(d, prob_d);
				totalprobDur += prob_d;
				curHist.minDur = Math.min(d, curHist.minDur);
				curHist.maxDur = Math.min(d, curHist.maxDur);
			}
		}
			

		//update numActi
		curHist.numActi = (int)Math.round(totalprobStart*incidentHist1.numActi*incidentHist2.numActi);
				
		//normalize
		for(int s: curHist.startHist.keySet()){
			curHist.startHist.put(s, curHist.startHist.get(s)/totalprobStart);
		}
		
		for(int s: curHist.durHist.keySet()){
			curHist.durHist.put(s, curHist.durHist.get(s)/totalprobDur);
		}
		
		//factor update 2
		//for(int x: curHist.instDict.keySet()){
		//	curHist.instDict.put(x, curHist.instDict.get(x)/factor);
		//}
		
		//factor update 1
		curHist.numInst = Math.min(incidentHist1.numInst, incidentHist2.numInst);
		
		return curHist;
	}
}
