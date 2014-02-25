package c3c.ast.cpp;

import c3c.ast.Project;
import c3c.cube.IToken;
import c3c.cube.List;

public class CxxProject extends Project {
	
	private List<CxxDefine> defs;
	
	public CxxProject() {
		defs = new List<CxxDefine>();
	}
	
	public CxxDefine getDefined(IToken token) {
		return defs.get(token.toString());
	}

}
