package main;

import java.util.*;

import evaluation.*;
import model.incident.*;
import model.log.*;

public class Main {
	enum Type{
		A, B
	}
	
	static String[] actis = {"BURNS", "ICU", "MAINDATA", "ADM_INPT", "CONSULT", 
			"CULTURE", "DIAGS", "EMERG", "FINANCE", "FLDDETAI", "GENMECH",
			"HEMO", "HOSPREV", "INJDETS", "INJDIAG", "INJMECH", "LAB",
			"MORTDETS", "MTOS", "NARRATIV", "OPRM", "ORGANS", "PERHIST",
			"POSTHOSP", "PRECONDS", "PROTECT", "QAISSUE", "RADIOLOG",
			"READMIT", "STEP", "SURG", "TOXIANAL", "TRA", "TRANSFER",
			"TRANSPRT", "TREATMEN", "TTDETLS", "VITALS", "WARD"};

	public static void main(String[] args){
		Log log = new Log();
		log.loadFile("data/output_07.txt");
//		log.loadFile("data/output_11.txt");
		QueryEngine.queryEngine.log = log;
//		testRecord(log);
//		testRecordWithCond(log);
//		testConsOp(log);
//		testSeqOp(log);
//		testOrOp(log);
//		testParaOp(log);
		
		String[] qs1 = generateIncident('*', 3, 50);
		for(int i=0; i<qs1.length; i++){
			System.out.println("\"" + qs1[i] + "\",");
		}
		
		String[] qs = {"POSTHOSP+OPRM:FINANCE:TRA:TRA.TRANSFER.INJDETS",
				"WARD+PROTECT+PERHIST:TRANSFER.HEMO|ICU.ICU",
				"CULTURE.POSTHOSP.PROTECT+TOXIANAL:QAISSUE.VITALS+FLDDETAI",
				"ORGANS.TRA|TOXIANAL|TRA+INJDETS.LAB+FLDDETAI",
				"HOSPREV|MAINDATA:TREATMEN|QAISSUE|GENMECH|TREATMEN+PERHIST",
				"TRANSPRT|ORGANS|TOXIANAL.MAINDATA:INJDETS.MORTDETS|ORGANS",
				"INJMECH.INJDETS.RADIOLOG|QAISSUE+TREATMEN:GENMECH:TRANSFER",
				"QAISSUE+MTOS:TTDETLS|CULTURE+STEP+STEP|TREATMEN",
				"INJDETS:LAB.SURG+INJDIAG|MTOS:CULTURE|MAINDATA",
				"QAISSUE:VITALS.TRANSFER:PRECONDS.FINANCE|DIAGS:FLDDETAI"};
		
		String[] qs2 = {"NARRATIV+WARD:HOSPREV|TTDETLS.OPRM.HEMO+STEP"};
		String[] qs3 = {"POSTHOSP:HOSPREV|TREATMEN|WARD.(CONSULT.POSTHOSPP)"};
		String[] qs4 = {"(TTDETLS:NARRATIV)|(TRA:NARRATIV).(QAISSUE:ICU)"};
		String[] qs5 = {"TOXIANAL|TREATMEN:DIAGS:DIAGS"};
		String[] qs6 = {"PROTECT.INJDIAG|WARD.INJMECH",
				"STEP+BURNS:FLDDETAI+VITALS",
				"FINANCE|ADM_INPT|MTOS:HOSPREV",
				"TTDETLS:MORTDETS.BURNS.WARD",
				"TREATMEN:EMERG+NARRATIV+FINANCE",
				"GENMECH+MAINDATA|CONSULT|RADIOLOG",
				"SURG|HOSPREV+INJMECH+READMIT",
				"ORGANS:INJDETS:PRECONDS|INJDETS",
				"TREATMEN+TTDETLS+TRANSFER+READMIT",
				"FLDDETAI.PROTECT|QAISSUE.MAINDATA"};
		test(qs1, 3);
//		test(qs, 1);
//		test(qs, 0);
	}
	
	public static void testRecordWithCond(Log log, int model){
		System.out.println("Start testing single record with cond:");
		String[] qs = {"ICU[los=1]", "MAINDATA[admdate=20070104]", "INJDETS[inj_city=santa_barbara]",
				"PROTECT[protective=none]", "TRANSFER[los=0]", "VITALS[gcs=0]", "TTDETLS[speciality=sur]",
				"RADIOLOG[study=ct_scan]", "TOXIANAL[substance=marijuana]", "PERHIST[pregnant=y]"};
		test(qs, model);
	}
	public static void testRecord(Log log, int model){
		System.out.println("Start testing single record:");
		String[] qs = {"ICU", "MAINDATA", "PROTECT", "PERHIST", "MTOS", "LAB",
				"HEMO", "INJDETS", "GENMECH", "NARRATIV"};
		test(qs, model);
	}
	
	/*
	 * Notes:
	 * large test case: "EMERG.RADIOLOG" with # of records 37927 and 32669 resp.
	 */
	public static void testConsOp(Log log, int model){
		System.out.println("Start testing cons op:");
		String[] qs = {"ICU.INJDETS", "TRANSFER.TRA", "ICU.HOSPREV", "PRECONDS.HOSPREV",
				"MTOS.QAISSUE", "INJDETS.PROTECT", "FLDDETAI.INJDETS", "VITALS.FLDDETAI",
				"TREATMEN.LAB", "MORTDETS.ORGANS"};
		test(qs, model);
	}
	
	/*
	 * op: operator
	 * numOp: # of operator
	 * log: log
	 * model: 0 -- no optimizer
	 * 		  1 -- cost model with assumption that no repeated acti
	 * 		  2 -- without the assumption
	 */
	
	//model: 1 -- cost model with assumption that no repeated acti, 2 -- withouth the assumption
	public static void testWithOptimizer(char op, int numOp, Log log, int model){
		System.out.println("Start testing multi cons op:");
		int numQuery = 10;
		String[] qs = generateIncidentwithOptimizer(op, numOp, numQuery, model);
		test(qs, model);
	}
	
	
	
	private static String[] generateIncident(char op, int numOp, int numQuery) {
		String[] res = new String[numQuery];
		List<String> ops = new ArrayList<String>(QueryEngine.queryEngine.operators.keySet());
		Random rand = new Random();
		for(int i=0; i<numQuery; i++){
			StringBuilder sb = new StringBuilder();
			sb.append(actis[rand.nextInt(actis.length)]);
			for(int j=0; j<numOp; j++){
				if(op == '*'){
					sb.append(ops.get(rand.nextInt(ops.size())));
				}else
					sb.append(op);
				sb.append(actis[rand.nextInt(actis.length)]);
			}
			res[i] = sb.toString();
//			System.out.println("New query: " + res[i]);
		}
		return res;
	}
	
	private static String[] generateIncidentwithOptimizer(char op, int numOp, int numQuery, int model) {
		String[] res = new String[numQuery];
		Random rand = new Random();
		for(int i=0; i<numQuery; i++){
			StringBuilder sb = new StringBuilder();
			sb.append(actis[rand.nextInt(actis.length)]);
			for(int j=0; j<numOp; j++){
				sb.append(op);
				sb.append(actis[rand.nextInt(actis.length)]);
			}
			res[i] = sb.toString();
			System.out.println("New query: " + res[i]);
		}
		return res;
	}

	public static void testSeqOp(Log log){
		System.out.println("Start testing sequ op:");
		String[] qs = {"ICU:INJDETS", "TRANSFER:TRA", "ICU:HOSPREV", "PRECONDS:HOSPREV",
				"MTOS:QAISSUE", "INJDETS:PROTECT", "FLDDETAI:INJDETS", "VITALS:FLDDETAI",
				"TREATMEN:LAB", "MORTDETS:ORGANS"};
		test(qs, 0);
	}
	
	public static void testOrOp(Log log){
		System.out.println("Start testing or op:");
		String[] qs = {"ICU|INJDETS", "TRANSFER|TRA", "ICU|HOSPREV", "PRECONDS|HOSPREV",
				"MTOS|QAISSUE", "INJDETS|PROTECT", "FLDDETAI|INJDETS", "VITALS|FLDDETAI",
				"TREATMEN|LAB", "MORTDETS|ORGANS"};
		test(qs, 0);
	}
	
	public static void testParaOp(Log log){
		System.out.println("Start testing para op:");
		String[] qs = {"ICU+INJDETS", "TRANSFER+TRA", "ICU+HOSPREV", "PRECONDS+HOSPREV",
				"MTOS+QAISSUE", "INJDETS+PROTECT", "FLDDETAI+INJDETS", "VITALS+FLDDETAI",
				"TREATMEN+LAB", "MORTDETS+ORGANS"};
		test(qs, 0);
	}
	
	public static void test(String[] qs, int model){
		long time = 0, max = 0, min = Integer.MAX_VALUE;
		long optiTime = 0, maxOpti = 0, minOpti = Integer.MAX_VALUE;
		int count = 0;
		for(int i=0; i<qs.length; i++){
			Incident incident = new Incident(qs[i]);
			if(model > 0){
				long opti1 = System.currentTimeMillis();
				if(Optimizer.generateOptimalTree(incident, model)){
					count++;
				}
				long opti2 = System.currentTimeMillis();
				opti2 -= opti1;
				optiTime += opti2;
				maxOpti = Math.max(maxOpti, opti2);
				minOpti = Math.min(minOpti, opti2);
//				System.out.println(incident.tree.toString());
//				System.out.println(incident.optiTree);
//				System.out.println("Opti time (millis) for test case " + i + " : " + opti2);
			}
			long t1 = System.currentTimeMillis();
			QueryEngine.queryEngine.query(incident);
			long t2 = System.currentTimeMillis();
			t2 -= t1;
			time += t2;
			max = Math.max(max, t2);
			min = Math.min(min, t2);
			System.out.println("Running time(milis) for test case " + i + " : " + t2);
		}
		System.out.println("Correct prediction: " + count);
		System.out.println("Total test cases: " + qs.length);
		System.out.println("Average runtime: " + time/qs.length);
//		System.out.println("Max runtime: " + max);
//		System.out.println("Min runtime: " + min);
		if(model > 0) 
			System.out.println("Average opti time: " + optiTime/qs.length);
		System.out.println();
	}
	
	
}
