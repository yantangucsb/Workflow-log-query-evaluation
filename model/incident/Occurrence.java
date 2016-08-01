package model.incident;

import java.util.ArrayList;
import java.util.List;

import model.log.*;

public class Occurrence {
	List<LogRecord> seq;
	long wid, start, end;
	
	public Occurrence(long wid){
		seq = new ArrayList<LogRecord>();
		start = -1;
		end = -1;
		this.wid = wid;
	}

	public void setTimeInterval(long s, long e) {
		start = s;
		end = e;
	}
}
