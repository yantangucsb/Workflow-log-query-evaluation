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
	public Map<String, Attribute> actiStat;
	public Set<Long> wids;
	public long numWorkflow;
	
	public Log(String filename){
		records = new ArrayList<LogRecord>();
		actiStat = new HashMap<String, Attribute>();
		wids = new HashSet<Long>();
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

	private void updateStatistics(LogRecord record) {
		if(!wids.contains(record.wid)){
			wids.add(record.wid);
			numWorkflow++;
		}
		if(!actiStat.containsKey(record.actiName)){
			Attribute att = new Attribute(record.actiName);
			att.update(record.wid, record.islsn);
			actiStat.put(record.actiName, att);
			return;
		}
		Attribute att = actiStat.get(record.actiName);
		att.update(record.wid, record.islsn);
		
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
			sb.append('\n');
		}
		
		return sb.toString();
	}
	
	public String statInfo(){
		
		return actiStat.toString();
	}
}

