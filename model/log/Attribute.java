package model.log;

import java.util.HashMap;
import java.util.Map;

public class Attribute{
	public String name;
	public Map<Long, Long> wids;
	public long aveStart;
	public long numStart;
	public long aveInterval;
	public long numInterval;
	public long count;
	
	public Attribute(String name){
		this.name = name;
		wids = new HashMap<Long, Long>();
		aveStart = 0;
		numStart = 0;
		aveInterval = 0;
		numInterval = 0;
		count = 0;
	}

	public void update(long wid, long islsn) {
		count++;
		if(!wids.containsKey(wid)){
			wids.put(wid, islsn);
			numStart ++;
			aveStart = (aveStart*(numStart-1) + islsn)/numStart;
		}else{
			long pre = wids.get(wid);
			aveInterval = ((aveInterval*numInterval) + islsn - pre) / (numInterval+1);
			numInterval++;
		}
	}
	
	public String toString(){
		return String.format("%s total=%d aveStart=%d numStart=%d aveInterval=%d numInterval=%d%n", name, count, aveStart, numStart, aveInterval, numInterval);
	}
}

