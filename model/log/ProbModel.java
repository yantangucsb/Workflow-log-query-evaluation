package model.log;

import java.util.Map;
import java.util.HashMap;

public class ProbModel {
	public Map<Integer, Double> startHist;
	public Map<Integer, Double> durHist;
	public int minStart, maxStart;
	public int minDur, maxDur;
	public int numActi;
	public Map<Integer, Double> instDict;
	public int numInst;
	public double cost;
	//the threshold only works for dataset less than size 10^8
	public double ZERO = 1e-8;

	public ProbModel() {
		// TODO Auto-generated constructor stub
		startHist = new HashMap<Integer, Double>();
		durHist = new HashMap<Integer, Double>();
		numActi = 0;
		instDict = new HashMap<Integer, Double>();
		cost = 0;
		minStart = Integer.MAX_VALUE;
		maxStart = 1;
		minDur = Integer.MAX_VALUE;
		maxDur = 1;
	}
	
	//only for activity model, should not be used for complicated incident construction
	public void updateActiHist(LogRecord l){
		++numActi;
		//all log record has duration 1
		if(!durHist.containsKey(1))
			durHist.put(1, 1.0);
		minDur = maxDur =1;
		
		if(!startHist.containsKey(l.islsn)){
			startHist.put(l.islsn, 1.0);
		}else
			startHist.put(l.islsn, startHist.get(l.islsn)+1);
		if(l.islsn > maxStart){
			maxStart = l.islsn;
		}
		if(l.islsn < minStart){
			minStart = l.islsn;
		}
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
		String output = "[\n[startHist: {";
		for(int x: startHist.keySet()){
			output += x + ":" + (int)(startHist.get(x)*numActi) + ",";
		}
		output += "}]\n[durHist: {";
		for(int x: durHist.keySet()){
			output += x + ":" + durHist.get(x) + ",\n";
		}
		output += "}]\n[instDict: {";
		for(int x: instDict.keySet()){
			output += x + ":" + (int)(instDict.get(x)*numActi) + ",";
		}
		output += "}]\n[numActi: " + numActi;
		output += "]\n[numInst: " + numInst + "]\n]";
		return output;
	}

	public boolean isZero(double prob) {
		// TODO Auto-generated method stub
		return Math.abs(prob) <= ZERO;
	}

}
