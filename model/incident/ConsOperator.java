package model.incident;

import java.util.ArrayList;
import java.util.List;

public class ConsOperator extends Operator {
	public List<Occurrence> execute(List<Occurrence> occs1, List<Occurrence> occs2){
		List<Occurrence> res = new ArrayList<Occurrence>();
		for(Occurrence occ1: occs1){
			for(Occurrence occ2: occs2){
				if(occ1.wid == occ2.wid && occ1.end+1 == occ2.start){
					res.add(merge(occ1, occ2));
				}
			}
		}
		return res;
	}

	private Occurrence merge(Occurrence occ1, Occurrence occ2) {
		Occurrence occ = new Occurrence(occ1.wid);
		occ.seq.addAll(occ1.seq);
		occ.seq.addAll(occ2.seq);
		occ.setTimeInterval(occ1.start, occ2.end);
		
		//Merge the effects on attributes
		occ.atts.putAll(occ1.atts);
		occ.atts.putAll(occ2.atts);
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
