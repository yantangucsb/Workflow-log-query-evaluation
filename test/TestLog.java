package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.log.Log;
import model.log.LogRecord;

import org.junit.Test;

public class TestLog {
	Log log = new Log("data/output_07.txt");
	
	@Test
	public void test() {
		System.out.println(log);
	}
	
	@Test
	public void testPreprocess() {
//		System.out.println(log);
		Set<Integer> set = new HashSet<Integer>();
		for(LogRecord lr: log.records){
			if(lr.actiName.equals("ICU") && lr.attWrite.containsKey("los") && lr.attWrite.get("los").equals("1")){
				set.add(lr.wid);
			}
		}
		List<Integer> li = new ArrayList<Integer>(set);
		Collections.sort(li);
		System.out.println(li.toString());
	}
	
	@Test
	public void testStat(){
		System.out.println(log.statInfo());
	}
	
	@Test
	public void testExtendLog(){
		System.out.println(log.getExtendLog());
	}
	
	@Test
	public void testGetInstance(){
		System.out.println(log.getInstance(1).toString());
	}

}
