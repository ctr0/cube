package org.cube.bender.test;

import org.cube.bender.BenderContext;
import org.cube.bender.BenderSession;
import org.cube.bender.xml.XmlSource;

public class Test {

	
	public static void main(String[] args) throws Exception {
		BenderSession session = new BenderSession();
		BenderContext context = session.getContext(null);
		
		XmlSource source = new XmlSource(null);
		context.visit(source);
		
	}
	
}
