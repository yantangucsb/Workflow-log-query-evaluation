package model.incident;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsOperator extends Operator {
	public Map<Long, List<Occurrence>> execute(Map<Long, List<Occurrence>> occs1, Map<Long, List<Occurrence>> occs2){
		Map<Long, List<Occurrence>> res = new HashMap<Long, List<Occurrence>>();
		
		for(long key: occs1.keySet()){
			if(!occs2.containsKey(key))
				continue;
			List<Occurrence> li1 = occs1.get(key);
			List<Occurrence> li2 = occs2.get(key);
			for(Occurrence occ1: li1){
				for(Occurrence occ2: li2){
					if(occ1.end+1 == occ2.start){
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

	private Occurrence merge(Occurrence occ1, Occurrence occ2) {
		Occurrence occ = new Occurrence(occ1.wid);
		occ.seq.addAll(occ1.seq);
		occ.seq.addAll(occ2.seq);
		occ.setTimeInterval(occ1.start, occ2.end);
		
		//Merge the effects on attributes
		occ.preMap.putAll(occ1.preMap);
		occ.postMap.putAll(occ1.postMap);
		occ.postMap.putAll(occ2.postMap);
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
