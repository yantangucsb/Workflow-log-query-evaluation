package evaluation;

import model.incidentree.IncidentTree;
import model.incidentree.IncidentTreeNode;

public abstract class OperatorRule {

	public abstract boolean checkQualified(IncidentTreeNode cur);

	public abstract void perform(IncidentTree tmp, IncidentTreeNode cur, String accessCode);
}
