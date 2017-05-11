package model.log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.incident.*;

public class LogRecord {
	public int lsn;
	public int wid;
	public int islsn;
	public String actiName;
	public Map<String, String> attRead;
	public Map<String, String> attWrite;
	public Map<String, String> preSnapshot;
	
	public LogRecord(String logEntry){
		attRead = new HashMap<String, String>();
		attWrite = new HashMap<String, String>();
		
		String[] parts = logEntry.split(" ");

		lsn = Integer.parseInt(parts[0]);
		wid = Integer.parseInt(parts[1]);
		islsn = Integer.parseInt(parts[2]);
		actiName = parts[3];
		int i = 4;
		
		//timestamp, currently no use
		String time = parts[i++] + " " + parts[i++];
		
		while(i < parts.length && !parts[i].equals("#")){
			String[] pair = parts[i].split("=");
			attRead.put(pair[0], pair[1]);
			i++;
		}
		i++;
		while(i < parts.length){
			if(parts[i].length() <= 1){
				i++;
				continue;
			}
//			System.out.println("att = val: " + parts[i]);
			String[] pair = parts[i].split("=");
//			System.out.println(pair.length);
			attWrite.put(pair[0], pair[1]);
			i++;
		}
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(lsn);
		sb.append(' ');
		sb.append(wid);
		sb.append(' ');
		sb.append(islsn);
		sb.append(' ');
		sb.append(actiName);
		sb.append(' ');
		for(Map.Entry<String, String> pair: attRead.entrySet()){
			sb.append(pair.getKey());
			sb.append('=');
			sb.append(pair.getValue());
			sb.append(' ');
		}
		sb.append("# ");
		for(Map.Entry<String, String> pair: attWrite.entrySet()){
			sb.append(pair.getKey());
			sb.append('=');
			sb.append(pair.getValue());
			sb.append(' ');
		}
		
		sb.deleteCharAt(sb.length()-1);
		sb.append('\n');
		return sb.toString();
	}
	
	//add the pre/post map
	public String extendRecord() {
		StringBuilder sb = new StringBuilder();
		sb.append("Cur snapshot: ");
		for(Map.Entry<String, String> pair: this.preSnapshot.entrySet()){
			sb.append(pair.getKey());
			sb.append('=');
			sb.append(pair.getValue());
			sb.append(' ');
		}
		sb.append('\n');
		sb.append(this.toString());
		return sb.toString();
	}

	//add the snapshot of attribute value pairs
	//for preCondition matching
	//Update the current snapshot
	public void addPreSnapshot(Map<String, String> atts) {
		preSnapshot = new HashMap<String, String>(atts);
		updatePostSnapshot(atts);
		
	}

	public void updatePostSnapshot(Map<String, String> atts) {
		for(Map.Entry<String, String> pair: attWrite.entrySet()){
			atts.put(pair.getKey(), pair.getValue());
		}
		
	}
	
}
