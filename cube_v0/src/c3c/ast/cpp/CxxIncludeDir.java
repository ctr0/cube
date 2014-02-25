package c3c.ast.cpp;

import c3c.cube.CodeSource;
import c3c.cube.IToken;

public class CxxIncludeDir {

	private IToken token;
	private CodeSource source;

	public CxxIncludeDir(IToken token, CodeSource source) {
		this.token = token;
		this.source = source;
	}
	
	public IToken getToken() {
		return token;
	}

	public CodeSource getCodeSource() {
		return source;
	}
}
