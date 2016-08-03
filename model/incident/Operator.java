package model.incident;

import java.util.List;

public abstract class Operator {
	public abstract List<Occurrence> execute(List<Occurrence> occ1, List<Occurrence> occ2);

	public abstract double getCost1(double c1, double c2);
	public abstract double getCost2(double c1, double c2);
}
