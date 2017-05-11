package model.log;

import java.util.Map;
import java.util.HashMap;

public class ProbModel {
	public Map<Integer, Double> startHist;
	public Map<Integer, Double> durHist;
	public int numActi;
	public Map<Integer, Double> instDict;
	public int numInst;
	public double cost;

	public ProbModel() {
		// TODO Auto-generated constructor stub
		startHist = new HashMap<Integer, Double>();
		durHist = new HashMap<Integer, Double>();
		numActi = 0;
		instDict = new HashMap<Integer, Double>();
		cost = 0;
	}
	
	//only for activity model, should not be used for complicated incident construction
	public void updateActiHist(LogRecord l){
		++numActi;
		//all log record has duration 1
		if(!durHist.containsKey(1))
			durHist.put(1, 1.0);
		
		if(!startHist.containsKey(l.islsn)){
			startHist.put(l.islsn, 1.0);
		}else
			startHist.put(l.islsn, startHist.get(l.islsn)+1);
		
		if(!instDict.containsKey(l.wid)){
			instDict.put(l.wid, 1.0);
			numInst++;
		}else
			instDict.put(l.wid, instDict.get(l.wid)+1);
	}
	
	public void normalize(){
		for(int s: startHist.keySet()){
			startHist.put(s, startHist.get(s)/numActi);
		}
		for(int s: instDict.keySet()){
			instDict.put(s, instDict.get(s)/numActi);
		}
	}
	public String toString(){
		String output = "[\n[startHist: ";
		for(int x: startHist.keySet()){
			output += "(" + x + "," + startHist.get(x) + "),";
		}
		output += "]\n[durHist: ";
		for(int x: durHist.keySet()){
			output += "(" + x + "," + durHist.get(x) + "),";
		}
		output += "]\n[instDict: ";
		for(int x: instDict.keySet()){
			output += "(" + x + "," + instDict.get(x) + "),";
		}
		output += "]\n[numActi: " + numActi + "]\n]";
		return output;
	}

}
