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
