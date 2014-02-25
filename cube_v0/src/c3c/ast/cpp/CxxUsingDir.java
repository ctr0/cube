package c3c.ast.cpp;

import c3c.ast.Annotation;
import c3c.ast.UsingDecl;
import c3c.cube.IToken;
import c3c.cube.List;

public class CxxUsingDir extends UsingDecl {
	
	/**
	 * Indicates if directive namespace is defined explicitly global 
	 * (i.e. starting with the scope operator '::')
	 */
	private boolean explicit;

	public CxxUsingDir(IToken token, List<Annotation> annos) {
		super(token, annos);
	}

	/**
	 * Indicates if directive namespace is defined explicitly global 
	 * (i.e. starting with the scope operator '::')
	 */
	@Override
	public boolean isExplicit() {
		return explicit;
	}

}
