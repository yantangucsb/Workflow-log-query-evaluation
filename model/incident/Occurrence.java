package model.incident;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import evaluation.QueryEngine;
import model.log.*;

public class Occurrence {
	//indices of log records
	public List<Integer> seq;
	public long wid;
	int start;
	int end;
	//preMap is the preSnapshot for the 1st record
	//postMap is the preSnapshot for the size-th record (the one after the last record)
	//public Map<String, String> preMap, postMap;
	public int preMapIdx, postMapIdx;
	
	
	public Occurrence(long wid){
		seq = new ArrayList<Integer>();
		start = -1;
		end = -1;
		this.wid = wid;
		//11/1/2016 by Yan for save space
		preMapIdx = -1;
		postMapIdx = -1;
	}

	public Occurrence(LogRecord r) {
		seq = new ArrayList<Integer>();
		seq.add(r.lsn);
		start = r.islsn;
		end = r.islsn;
		wid = r.wid;
		preMapIdx = r.lsn;
		postMapIdx = r.lsn;
		/*
		preMap = r.preSnapshot;
		int recordSize = QueryEngine.queryEngine.log.records.size();
		if(r.lsn >= recordSize){
			postMap = QueryEngine.queryEngine.log.snapshots.get(r.wid);
		}else
			postMap = QueryEngine.queryEngine.log.records.get((int) (r.lsn)).preSnapshot;
			*/
	}
	
	public Occurrence(Occurrence o1, Occurrence o2){
		seq = new ArrayList<Integer>();
		seq.addAll(o1.seq);
		seq.addAll(o2.seq);
		Collections.sort(seq);
		start = o1.start <= o2.start? o1.start: o2.start;
		end = o1.end <= o2.end? o2.end: o1.end;
		wid = o1.wid;
		preMapIdx = o1.preMapIdx <= o2.preMapIdx? o1.preMapIdx: o2.preMapIdx;
		postMapIdx = o1.postMapIdx <= o2.postMapIdx? o2.postMapIdx: o1.postMapIdx;
	}

	public void setTimeInterval(int s, int e) {
		start = s;
		end = e;
	}

	public int size() {
		return seq.size();
	}

	public LogRecord get(int j) {
		return QueryEngine.queryEngine.log.records.get((int)seq.get(j));
	}
	
	public void add(LogRecord r){
		seq.add(r.lsn);
	}
	
	public Map<String, String> getPreMap(){
		return QueryEngine.queryEngine.log.records.get(preMapIdx).preSnapshot;
	}
	
	public Map<String, String> getPostMap(){
		return QueryEngine.queryEngine.log.records.get(postMapIdx).preSnapshot;
	}
	
	//for estimation
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for(int lr: seq){
			sb.append(lr);
			sb.append(' ');
		}
		sb.append(']');
		return sb.toString();
	}
}
