package model.incident;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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
		while(i<occ1.seq.size() || j<occ2.seq.size()){
			if(i == occ1.seq.size()){
				occ.seq.add(occ2.seq.get(j));
				occ.atts.putAll(occ2.atts);
				j++;
			}else if(j == occ2.seq.size()){
				occ.seq.add(occ1.seq.get(i));
				occ.atts.putAll(occ1.atts);
				i++;
			}else if(occ1.seq.get(i).lsn < occ2.seq.get(j).lsn){
				occ.seq.add(occ1.seq.get(i));
				occ.atts.putAll(occ1.atts);
				i++;
			}else{
				occ.seq.add(occ2.seq.get(j));
				occ.atts.putAll(occ2.atts);
				j++;
			}
		}
		
		occ.setTimeInterval(occ.seq.get(0).islsn, occ.seq.get(occ.seq.size()-1).islsn);
		
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
