package model.log;

import java.util.List;
import java.util.Map;

import model.incident.*;

public class LogRecord {
	public long lsn;
	public long wid;
	public long islsn;
	public String actiName;
	public Map<String, String> attRead;
	public Map<String, String> attWrite;
	
	public LogRecord(String logEntry){
		
	}
}
