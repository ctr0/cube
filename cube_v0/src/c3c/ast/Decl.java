package c3c.ast;

import c3c.ast.visitor.ScopeVisitor;
import c3c.cube.CodeSource;
import c3c.cube.CubeLang.internal;
import c3c.cube.IToken;
import c3c.cube.List;

public abstract class Decl extends Node {
	
	private IScope scope;
	
	private List<Annotation> annos;
	
	private CodeSource source;
	
	private volatile boolean resolved;
	
	public Decl(IToken token, CodeSource source, List<Annotation> annos) {
		super(token);
		this.source = source;
		this.annos = annos;
	}
	
	public IScope getScope() {
		return scope;
	}

	@internal public void setScope(IScope scope) {
		this.scope = scope;
	}

	public CodeSource getCodeSource() {
		return source;
	}

	public synchronized boolean resolved() {
		boolean r = resolved;
		resolved = true;
		return r;
	}
	
	public abstract IType getType();

	public void addAnnotation(Annotation decl) {
		if (annos == null) {
			annos = new List<Annotation>();
		}
		annos.add(decl);
	}

	public abstract void accept(ScopeVisitor visitor);

}
