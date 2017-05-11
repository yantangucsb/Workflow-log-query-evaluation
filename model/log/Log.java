package model.log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.incident.Occurrence;

public class Log {
	public List<LogRecord> records;
	public Map<String, Activity> actiStat;
	public Map<Integer, Map<String, String>> snapshots;
	//info for model 3
	public Map<String, ProbModel> probInfo;
	public long numWorkflow;
	
	public Map<String, Map<Integer, List<Occurrence>>> actiIndex;
	
	public Log(){
		init();
	}
	
	public Log(String filename){
		init();
		loadFile(filename);
	}
	
	private void init() {
		records = new ArrayList<LogRecord>();
		actiStat = new HashMap<String, Activity>();
		probInfo = new HashMap<String, ProbModel>();
		snapshots = new HashMap<Integer, Map<String, String>>();
		numWorkflow = 0;
		actiIndex = new HashMap<String, Map<Integer, List<Occurrence>>>();
	}
	
	public int size(){
		return records.size();
	}
	
	public void loadFile(String filename){
		BufferedReader br = null;
	    try {
	    	br = new BufferedReader(new FileReader(filename));
	        String line = br.readLine();
	        int count = 0;
	        while (line != null) {
	        	if(line.length() == 0){
	        		line = br.readLine();
	        		continue;
	        	}
	        	LogRecord record = new LogRecord(line);
//	        	if(record.lsn != count + 1){
//	        		System.err.println("Oops! LSN " + record.lsn + " is not correct!");
//	        		break;
//	        	}
	        	records.add(record);
	        	updateStatistics(record);
	        	count++;
	        	if(count%100000 == 0){
	        		System.out.println(count + " lines processed.");
	        	}
	            line = br.readLine();
//	            System.out.println(record.toString());
	        }
	        for(String str: probInfo.keySet()){
	        	probInfo.get(str).normalize();
	        }
	        System.out.println("# of Lines processed: " + count);
	    }catch(Exception e){
	    	System.out.println("load log file failed! ");
	    	e.printStackTrace();
	    }finally
		{
		    try
		    {
		        if ( br != null)
		        br.close( );
		    }
		    catch ( Exception e)
		    {
		    }
		}
	}

	//update snapshots and statistics
	private void updateStatistics(LogRecord record) {
		if(!snapshots.containsKey(record.wid)){
			Map<String, String> atts = new HashMap<String, String>();
			record.addPreSnapshot(atts);
			snapshots.put(record.wid, atts);
			numWorkflow++;
		}else{
			Map<String, String> atts = snapshots.get(record.wid);
			record.addPreSnapshot(atts);
		}
		
		//update model 1/2 statistics
/*		if(!actiStat.containsKey(record.actiName)){
			Activity acti = new Activity(record.actiName);
			acti.update(record.wid, record.islsn);
			actiStat.put(record.actiName, acti);
			return;
		}
		Activity acti = actiStat.get(record.actiName);
		acti.update(record.wid, record.islsn);*/
		
		//update model 3 statistics
		if(!probInfo.containsKey(record.actiName)){
			probInfo.put(record.actiName, new ProbModel());
			
		}
		probInfo.get(record.actiName).updateActiHist(record);
		
	}

	public Map<Integer, List<Occurrence>> filter(String name) {
		if(actiIndex.containsKey(name)){
			return actiIndex.get(name);
		}
		Map<Integer, List<Occurrence>> res = new HashMap<Integer, List<Occurrence>>();
		for(LogRecord r: records){
			if(r.actiName.equals(name)){
				if(!res.containsKey(r.wid)){
					res.put(r.wid, new ArrayList<Occurrence>());
				}
				res.get(r.wid).add(new Occurrence(r));
			}
		}
		actiIndex.put(name, res);
		return res;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(LogRecord r: records){
			sb.append(r);
		}
		
		return sb.toString();
	}
	
	public String statInfo(){
		
		return actiStat.toString();
	}

	public String getExtendLog() {
		StringBuilder sb = new StringBuilder();
		for(LogRecord r: records){
			sb.append(r.extendRecord());
		}
		return sb.toString();
	}
	
	public List<LogRecord> getInstance(int wid){
		List<LogRecord> res = new ArrayList<LogRecord>();
		for(LogRecord lr: records){
			if(lr.wid == wid){
				res.add(lr);
			}
		}
		return res;
	}
}

