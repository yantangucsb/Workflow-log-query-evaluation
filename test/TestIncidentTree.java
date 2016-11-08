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
		assertEquals("[ACTI,a]", it1.toString());
		IncidentTree it2 = new IncidentTree("[x=1]b[y=2,z=3]");
//		System.out.println(it2.toString());
		assertEquals("[COND,\"x=1\",\"y=2,z=3\"],[ACTI,b],null", it2.toString());
		IncidentTree it3 = new IncidentTree("a.b");
		assertEquals("[OP,.],[ACTI,a],[ACTI,b]", it3.toString());
		IncidentTree it4 = new IncidentTree("(([x=1]a.b)+c)[y=1]");
//		System.out.println(it4);
		assertEquals("[COND,\"\",\"y=1\"],[OP,+],null,[OP,.],[ACTI,c],[COND,\"x=1\",\"\"],[ACTI,b],null,null,[ACTI,a],null,null,null", it4.toString());
		IncidentTree it5 = new IncidentTree("a.b.c");
		assertEquals("[OP,.],[OP,.],[ACTI,c],[ACTI,a],[ACTI,b],null,null", it5.toString());
		IncidentTree it6 = new IncidentTree("a.b.c|d");
		assertEquals("[OP,|],[OP,.],[ACTI,d],[OP,.],[ACTI,c],null,null,[ACTI,a],[ACTI,b],null,null", it6.toString());
	}

}
