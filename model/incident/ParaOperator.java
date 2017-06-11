package model.incident;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import evaluation.CostModel;
import evaluation.QueryEngine;
import model.log.Activity;
import model.log.LogRecord;
import model.log.ProbModel;

public class ParaOperator extends Operator {
	public Map<Integer, List<Occurrence>> execute(Map<Integer, List<Occurrence>> occs1, Map<Integer, List<Occurrence>> occs2){
		Map<Integer, List<Occurrence>> res = new HashMap<Integer, List<Occurrence>>();
		
		if(occs1.size() == 0 || occs2.size() == 0){
			return res;
		}
		
		for(int key: occs1.keySet()){
			if(!occs2.containsKey(key))
				continue;
			List<Occurrence> li1 = occs1.get(key);
			List<Occurrence> li2 = occs2.get(key);
			for(Occurrence occ1: li1){
				for(Occurrence occ2: li2){
					if(!hasOverlap(occ1, occ2)){
						if(!res.containsKey(key)){
							res.put(key, new ArrayList<Occurrence>());
						}
						res.get(key).add(merge(occ1, occ2));
					}
				}
			}
		}
		
		return res;
	}

	private boolean hasOverlap(Occurrence occ1, Occurrence occ2) {
		HashSet<Integer> lsns = new HashSet<Integer>();
		for(int l: occ1.seq){
			lsns.add(l);
		}
		for(int l: occ2.seq){
			if(lsns.contains(l)){
				return true;
			}
		}
		return false;
	}

	
	/*
	 * Tradeoff: Do we need to sort the records currently?
	 * current version of merge
	 */
	private Occurrence merge(Occurrence occ1, Occurrence occ2) {
/*		Occurrence occ = new Occurrence(occ1.wid);
		int i=0, j=0;
		while(i<occ1.size() || j<occ2.size()){
			if(i == occ1.size()){
				occ.add(occ2.get(j));
				j++;
			}else if(j == occ2.size()){
				occ.add(occ1.get(i));
				i++;
			}else if(occ1.get(i).lsn < occ2.get(j).lsn){
				occ.add(occ1.get(i));
				i++;
			}else{
				occ.add(occ2.get(j));
				j++;
			}
		}
		
		//set the pre post map
		//preMap is the preSnapshot for the first log record
		//postMap is calculated from the preSnapshot for the last log record
		if(occ.size() != 0){
			occ.setTimeInterval(occ.get(0).islsn, occ.get(occ.size()-1).islsn);
			occ.setPreMap(occ.get(0).preSnapshot);
			LogRecord last = occ.get(occ.size()-1);
//			Map<String, String> tmp = new HashMap<String, String>(last.preSnapshot);
//			last.updatePostSnapshot(tmp);
			
			if(last.lsn >= QueryEngine.queryEngine.log.records.size()){
				occ.setPostMap(QueryEngine.queryEngine.log.snapshots.get(last.wid));
			}else{
				LogRecord afterLast = QueryEngine.queryEngine.log.records.get((int) (last.lsn));
				occ.setPostMap(afterLast.preSnapshot);
			}
		}*/
		return new Occurrence(occ1, occ2);
	}

	@Override
	public long getResultSize1(long c1, long c2) {
		return Math.min(c1, c2);
	}

	@Override
	public CostModel getResultSize2(CostModel a1, CostModel a2) {
		if(a1.count == 0 || a2.count == 0 || a1.numStart == 0 || a2.numStart == 0){
			return new CostModel(a1.name);
		}
		
		CostModel cur = new CostModel(a1.name);
		cur.aveStart = Math.min(a1.aveStart, a2.aveStart);
		cur.aveInterval = Math.max(Math.min(a1.aveInterval, a2.aveInterval)/2, 1);
		cur.count = a1.count * a2.count * Math.min(a2.numStart, a1.numStart);
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
		double curprobStart1 = 0, curprobStart2 = 0;
		for(int s=Math.max(incidentHist1.maxStart, incidentHist2.maxStart); s >= Math.min(incidentHist1.minStart, incidentHist2.minStart); s--){
			double prob_s = 0, acc_prob1 = 0, acc_prob2 = 0;
			if(incidentHist1.startHist.containsKey(s)){
				prob_s += incidentHist1.startHist.get(s) * curprobStart2;
				acc_prob1 = incidentHist1.startHist.get(s);
			}
			if(incidentHist2.startHist.containsKey(s)){
				acc_prob2 = incidentHist2.startHist.get(s);
				prob_s += acc_prob2 * curprobStart1;
			}
			prob_s *= factor;
			if(prob_s > 1e-7){
				curHist.startHist.put(s, prob_s);
				totalprobStart += prob_s;
				curHist.minStart = Math.min(curHist.minStart, s);
				curHist.maxStart = Math.max(curHist.maxStart, s);
			}
			curprobStart1 += acc_prob1;
			curprobStart2 += acc_prob2;
		}
		
		for(int s1: incidentHist1.startHist.keySet()){
			for(int s2: incidentHist2.startHist.keySet()){
				for(int d1: incidentHist1.durHist.keySet()){
					for(int d2: incidentHist2.durHist.keySet()){
						double prob_d = incidentHist1.startHist.get(s1)*incidentHist2.startHist.get(s2)
								*incidentHist1.durHist.get(d1)*incidentHist2.durHist.get(d2)*factor;
						if(prob_d > 1e-7){
							int d = Math.max(s1+d1, s2+d2) - Math.min(s1, s2);
							curHist.durHist.put(d, prob_d);
							totalprobDur += prob_d;
							curHist.minDur = Math.min(d, curHist.minDur);
							curHist.maxDur = Math.min(d, curHist.maxDur);
						}
					}
				}
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
		
		//factor update 2 corresponding to the for-loop calculation of factor
		//for(int x: curHist.instDict.keySet()){
		//	curHist.instDict.put(x, curHist.instDict.get(x)/factor);
		//}
		
		//factor update 1
		curHist.numInst = Math.min(incidentHist1.numInst, incidentHist2.numInst);
		
		return curHist;
	}

}
