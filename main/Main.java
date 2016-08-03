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
		String line = "[317=x] [3007] on [June]";
		String pattern = "\\[([^\\[\\]]+)\\]";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(line);
		int count = 0;
		while(m.find() && count < 2){
			System.out.println("Found value: " + m.group(0));
			System.out.println("Found value: " + m.group(1));
			System.out.println(m.end(0));
			line = line.substring(m.end(1)+1);
			m = r.matcher(line);
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(Type.A);
		System.out.println(sb.toString());
	}
	
	public void test(){
		Log log = new Log("filename");
		String query = "a*b";
		Incident incident = new Incident(query);
		Optimizer.generateOptimalTree(incident);
		QueryEngine.queryEngine.query(incident, log);
	}
}
