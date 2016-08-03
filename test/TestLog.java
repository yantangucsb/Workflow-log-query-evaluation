package test;

import static org.junit.Assert.*;
import model.log.Log;

import org.junit.Test;

public class TestLog {
	Log log = new Log("data/tranlog.txt");
	
	@Test
	public void test() {
		System.out.println(log);
	}
	
	@Test
	public void testStat(){
		System.out.println(log.statInfo());
	}

}
