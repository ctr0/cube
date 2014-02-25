package org.cube.bender;

import java.lang.annotation.Annotation;

public interface Visitor<A extends Annotation> {
	
//	void visit(String key, Object value);
	
	void visitEnd();

}
