package model.incident;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExclOperator extends Operator {
	public Map<Long, List<Occurrence>> execute(Map<Long, List<Occurrence>> occs1, Map<Long, List<Occurrence>> occs2){
		Map<Long, List<Occurrence>> res = new HashMap<Long, List<Occurrence>>(occs1);
		for(long key: occs2.keySet()){
			if(!res.containsKey(key)){
				res.put(key, new ArrayList<Occurrence>());
			}
			res.get(key).addAll(occs2.get(key));
		}
		return res;
	}

	@Override
	public double getCost1(double c1, double c2) {
		return c1 + c2;
	}

	@Override
	public double getCost2(double c1, double c2) {
		// TODO Auto-generated method stub
		return 0;
	}
}
