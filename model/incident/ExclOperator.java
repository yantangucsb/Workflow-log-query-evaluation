package model.incident;

import java.util.ArrayList;
import java.util.List;

public class ExclOperator extends Operator {
	public List<Occurrence> execute(List<Occurrence> occs1, List<Occurrence> occs2){
		List<Occurrence> res = new ArrayList<Occurrence>();
		res.addAll(occs1);
		res.addAll(occs2);
		return res;
	}
}
