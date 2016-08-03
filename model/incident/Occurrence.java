package model.incident;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.log.*;

public class Occurrence {
	public List<LogRecord> seq;
	public long wid;
	long start;
	long end;
	public Map<String, String> atts;
	
	public Occurrence(long wid){
		seq = new ArrayList<LogRecord>();
		start = -1;
		end = -1;
		this.wid = wid;
		atts = new HashMap<String, String>();
	}

	public Occurrence(LogRecord r) {
		seq = new ArrayList<LogRecord>();
		seq.add(r);
		start = r.islsn;
		end = r.islsn;
		wid = r.wid;
		atts = new HashMap<String, String>();
		atts.putAll(r.attWrite);
	}

	public void setTimeInterval(long s, long e) {
		start = s;
		end = e;
	}
}
