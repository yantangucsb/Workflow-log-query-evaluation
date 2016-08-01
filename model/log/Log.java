package model.log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import model.incident.Occurrence;

public class Log {
	List<LogRecord> records;
	
	public Log(String filename){
		records = new ArrayList<LogRecord>();
		
		BufferedReader br = null;
	    try {
	    	br = new BufferedReader(new FileReader(filename));
	        String line = br.readLine();
	        while (line != null) {
	        	LogRecord record = new LogRecord(line);
	        	records.add(record);
	            line = br.readLine();
	        }
	        
	    }catch(Exception e){
	    	System.out.println("load log file failed!");
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

	public List<Occurrence> filter(String name) {
		// TODO Auto-generated method stub
		return new ArrayList<Occurrence>();
	}
}
