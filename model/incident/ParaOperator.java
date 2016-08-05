package model.incident;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import model.log.LogRecord;

public class ParaOperator extends Operator {
	public List<Occurrence> execute(List<Occurrence> occs1, List<Occurrence> occs2){
		List<Occurrence> res = new ArrayList<Occurrence>();
		for(Occurrence occ1: occs1){
			for(Occurrence occ2: occs2){
				if(occ1.wid == occ2.wid && !hasOverlap(occ1, occ2)){
					res.add(merge(occ1, occ2));
				}
			}
		}
		return res;
	}

	private boolean hasOverlap(Occurrence occ1, Occurrence occ2) {
		HashSet<Long> lsns = new HashSet<Long>();
		for(LogRecord l: occ1.seq){
			lsns.add(l.islsn);
		}
		for(LogRecord l: occ2.seq){
			if(lsns.contains(l.islsn)){
				return true;
			}
		}
		return false;
	}

	private Occurrence merge(Occurrence occ1, Occurrence occ2) {
		Occurrence occ = new Occurrence(occ1.wid);
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
			Map<String, String> tmp = new HashMap<String, String>(last.preSnapshot);
			last.updatePostSnapshot(tmp);
			occ.setPostMap(tmp);
		}
		return occ;
	}

	@Override
	public double getCost1(double c1, double c2) {
		return Math.min(c1, c2);
	}

	@Override
	public double getCost2(double c1, double c2) {
		// TODO Auto-generated method stub
		return 0;
	}

}
