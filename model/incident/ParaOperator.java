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
		return null;
	}

}
