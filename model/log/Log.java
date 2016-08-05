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
	public Map<Long, Map<String, String>> snapshots;
	public long numWorkflow;
	
	public Log(String filename){
		records = new ArrayList<LogRecord>();
		actiStat = new HashMap<String, Activity>();
		snapshots = new HashMap<Long, Map<String, String>>();
		numWorkflow = 0;
		
		BufferedReader br = null;
	    try {
	    	br = new BufferedReader(new FileReader(filename));
	        String line = br.readLine();
	        while (line != null) {
	        	LogRecord record = new LogRecord(line);
	        	records.add(record);
	        	updateStatistics(record);
	            line = br.readLine();
	        }
	        
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
		
		if(!actiStat.containsKey(record.actiName)){
			Activity acti = new Activity(record.actiName);
			acti.update(record.wid, record.islsn);
			actiStat.put(record.actiName, acti);
			return;
		}
		Activity acti = actiStat.get(record.actiName);
		acti.update(record.wid, record.islsn);
		
	}

	public List<Occurrence> filter(String name) {
		List<Occurrence> res = new ArrayList<Occurrence>();
		for(LogRecord r: records){
			if(r.actiName.equals(name))
				res.add(new Occurrence(r));
		}
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
}

