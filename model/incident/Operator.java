package model.incident;

import java.util.List;
import java.util.Map;

public abstract class Operator {
	public abstract Map<Long, List<Occurrence>> 
	execute(Map<Long, List<Occurrence>> occ1, Map<Long, List<Occurrence>> occ2);

	public abstract double getCost1(double c1, double c2);
	public abstract double getCost2(double c1, double c2);
}
