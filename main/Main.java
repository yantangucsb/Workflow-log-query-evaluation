package main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import evaluation.*;
import model.incident.*;
import model.log.*;

public class Main {
	enum Type{
		A, B
	}

	public static void main(String[] args){
		Log log = new Log();
		log.loadFile("data/output_07.txt");
//		testRecord(log);
//		testRecordWithCond(log);
//		testConsOp(log);
		testSeqOp(log);
		testOrOp(log);
		testParaOp(log);
	}
	
	public static void testRecordWithCond(Log log){
		System.out.println("Start testing single record with cond:");
		String[] qs = {"ICU[los=1]", "MAINDATA[admdate=20070104]", "INJDETS[inj_city=santa_barbara]",
				"PROTECT[protective=none]", "TRANSFER[los=0]", "VITALS[gcs=0]", "TTDETLS[speciality=sur]",
				"RADIOLOG[study=ct_scan]", "TOXIANAL[substance=marijuana]", "PERHIST[pregnant=y]"};
		test(log, qs);
	}
	public static void testRecord(Log log){
		System.out.println("Start testing single record:");
		String[] qs = {"ICU", "MAINDATA", "PROTECT", "PERHIST", "MTOS", "LAB",
				"HEMO", "INJDETS", "GENMECH", "NARRATIV"};
		test(log, qs);
	}
	public static void testConsOp(Log log){
		System.out.println("Start testing cons op:");
		String[] qs = {"ICU.INJDETS", "TRANSFER.MAINDATA", "EMERG.RADIOLOG", "PRECONDS.HOSPREV",
				"MTOS.QAISSUE", "INJDETS.PROTECT", "FLDDETAI.INJDETS", "VITALS.EMERG",
				"TREATMEN.LAB", "MORTDETS.ORGANS"};
		test(log, qs);
	}
	
	public static void testSeqOp(Log log){
		System.out.println("Start testing sequ op:");
		String[] qs = {"ICU:INJDETS", "TRANSFER:MAINDATA", "EMERG:RADIOLOG", "PRECONDS:HOSPREV",
				"MTOS:QAISSUE", "INJDETS:PROTECT", "FLDDETAI:INJDETS", "VITALS:EMERG",
				"TREATMEN:LAB", "MORTDETS:ORGANS"};
		test(log, qs);
	}
	
	public static void testOrOp(Log log){
		System.out.println("Start testing or op:");
		String[] qs = {"ICU|INJDETS", "TRANSFER|MAINDATA", "EMERG|RADIOLOG", "PRECONDS|HOSPREV",
				"MTOS|QAISSUE", "INJDETS|PROTECT", "FLDDETAI|INJDETS", "VITALS|EMERG",
				"TREATMEN|LAB", "MORTDETS|ORGANS"};
		test(log, qs);
	}
	
	public static void testParaOp(Log log){
		System.out.println("Start testing para op:");
		String[] qs = {"ICU+INJDETS", "TRANSFER+MAINDATA", "EMERG+RADIOLOG", "PRECONDS+HOSPREV",
				"MTOS+QAISSUE", "INJDETS+PROTECT", "FLDDETAI+INJDETS", "VITALS+EMERG",
				"TREATMEN+LAB", "MORTDETS+ORGANS"};
		test(log, qs);
	}
	
	public static void test(Log log, String[] qs){
		long time = 0, max = 0, min = Integer.MAX_VALUE;
		for(int i=0; i<qs.length; i++){
			Incident incident = new Incident(qs[i]);
//		Optimizer.generateOptimalTree(incident);
			long t1 = System.currentTimeMillis();
			QueryEngine.queryEngine.query(incident, log);
			long t2 = System.currentTimeMillis();
			t2 -= t1;
			time += t2;
			max = Math.max(max, t2);
			min = Math.min(min, t2);
			System.out.println("Running time(milis) for test case " + i + " : " + t2);
		}
		System.out.println("Total test cases: " + qs.length);
		System.out.println("Average runtime: " + time/qs.length);
		System.out.println("Max runtime: " + max);
		System.out.println("Min runtime: " + min);
		System.out.println();
	}
	
	
}
