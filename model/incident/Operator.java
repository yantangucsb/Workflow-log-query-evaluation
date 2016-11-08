package model.incident;

import java.util.*;

import evaluation.CostModel;
import model.log.*;

public abstract class Operator {
	public abstract Map<Long, List<Occurrence>> 
	execute(Map<Long, List<Occurrence>> occ1, Map<Long, List<Occurrence>> occ2);

	public abstract long getResultSize1(long c1, long c2);
	public abstract CostModel getResultSize2(CostModel acti1, CostModel acti2);
}
