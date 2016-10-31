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
	public Map<String, String> preMap, postMap;
	
	
	public Occurrence(long wid){
		seq = new ArrayList<LogRecord>();
		start = -1;
		end = -1;
		this.wid = wid;
		preMap = new HashMap<String, String>();
		postMap = new HashMap<String, String>();
	}

	public Occurrence(LogRecord r) {
		seq = new ArrayList<LogRecord>();
		seq.add(r);
		start = r.islsn;
		end = r.islsn;
		wid = r.wid;
		preMap = new HashMap<String, String>(r.preSnapshot);
		postMap = new HashMap<String, String>(r.preSnapshot);
		for(Map.Entry<String, String> pair:r.attWrite.entrySet()){
			postMap.put(pair.getKey(), pair.getValue());
		}
	}

	public void setTimeInterval(long s, long e) {
		start = s;
		end = e;
	}

	public int size() {
		return seq.size();
	}

	public LogRecord get(int j) {
		return seq.get(j);
	}
	
	public void add(LogRecord r){
		seq.add(r);
	}

	public void setPreMap(Map<String, String> preSnapshot) {
		preMap.putAll(preSnapshot);
		
	}

	public void setPostMap(Map<String, String> postSnapshot) {
		postMap.putAll(postSnapshot);
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for(LogRecord lr: seq){
			sb.append(lr.lsn);
			sb.append(' ');
		}
		sb.append(']');
		return sb.toString();
	}
}
