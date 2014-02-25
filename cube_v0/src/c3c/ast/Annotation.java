package c3c.ast;

import java.util.ArrayList;

import c3c.cube.IToken;

public class Annotation extends Node {
	
	/*internal*/ public ArrayList<Object> params;
	
	public Annotation(IToken token) {
		super(token);
		params = new ArrayList<Object>();
	}

	
	
}
