package model.incident;

import java.util.List;

public abstract class Operator {
	public abstract List<Occurrence> execute(List<Occurrence> occ1, List<Occurrence> occ2);
}
