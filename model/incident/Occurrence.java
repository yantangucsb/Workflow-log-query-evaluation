package model.incident;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import evaluation.QueryEngine;
import model.log.*;

public class Occurrence {
	public List<LogRecord> seq;
	public long wid;
	long start;
	long end;
	//preMap is the preSnapshot for the 1st record
	//postMap is the preSnapshot for the size-th record (the one after the last record)
	public Map<String, String> preMap, postMap;
	
	
	public Occurrence(long wid){
		seq = new ArrayList<LogRecord>();
		start = -1;
		end = -1;
		this.wid = wid;
		//11/1/2016 by Yan for save space
		preMap = null;
		postMap = null;
	}

	public Occurrence(LogRecord r) {
		seq = new ArrayList<LogRecord>();
		seq.add(r);
		start = r.islsn;
		end = r.islsn;
		wid = r.wid;
		preMap = r.preSnapshot;
		int recordSize = QueryEngine.queryEngine.log.records.size();
		if(r.lsn >= recordSize){
			postMap = QueryEngine.queryEngine.log.snapshots.get(r.wid);
		}else
			postMap = QueryEngine.queryEngine.log.records.get((int) (r.lsn)).preSnapshot;
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
		preMap = preSnapshot;
		
	}

	public void setPostMap(Map<String, String> postSnapshot) {
		postMap = postSnapshot;
	}
	
	//for estimation
	
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
