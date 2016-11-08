package evaluation;

import model.log.Activity;

public class CostModel extends Activity {
	double cost;
	int len;

	public CostModel(String name) {
		super(name);
		len = 1;
		
	}

	public CostModel(Activity a) {
		super(a);
		if(a instanceof CostModel){
			len = ((CostModel)a).len;
		}else{
			len = 1;
		}
		cost = 0;
	}

}
