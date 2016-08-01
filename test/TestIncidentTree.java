package test;

import static org.junit.Assert.*;
import model.incidentree.IncidentTree;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestIncidentTree {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testBuildTree() {
		IncidentTree it1 = new IncidentTree("a");
		assertEquals("[ACTI,a],null,null,", it1.toString());
		IncidentTree it2 = new IncidentTree("[x=1]b[y=2,z=3]");
//		System.out.println(it2.toString());
		assertEquals("[COND,\"x=1\",\"y=2,z=3\"],[ACTI,b],null,null,null,", it2.toString());
		IncidentTree it3 = new IncidentTree("a.b");
		assertEquals("[OP,.],[ACTI,a],[ACTI,b],null,null,null,null,", it3.toString());
		IncidentTree it4 = new IncidentTree("(([x=1]a.b)+c)[y=1]");
		System.out.println(it4);
		assertEquals("[COND,\"\",\"y=1\"],[OP,+],null,[OP,.],[ACTI,c],[COND,\"x=1\",\"\"],[ACTI,b],null,null,[ACTI,a],null,null,null,null,null,", it4.toString());
	}

}
