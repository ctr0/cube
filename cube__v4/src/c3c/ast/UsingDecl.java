package c3c.ast;

import c3c.ast.visitor.BlockVisitor;
import c3c.ast.visitor.ScopeVisitor;
import c3c.cube.CodeSource;
import c3c.cube.IToken;
import c3c.cube.List;

public class UsingDecl extends Decl implements IStatement {
	
	private Namespace namespace;

	/*internal*/ public UsingDecl(IToken token, CodeSource source, List<Annotation> annos) {
		super(token, source, annos);
	}
	
	public Namespace getNamespace() {
		return namespace;
	}
	
	public void setNamespace(Namespace namespace) {
		this.namespace = namespace;
	}
	
	public boolean isExplicit() {
		return true;
	}

	@Override
	public IType getType() {
		// TODO Auto-generated method stub
		return null;
	}	

	@Override
	public void accept(ScopeVisitor visitor) {
		visitor.visitUsingDecl(this);
	}

	@Override
	public void accept(BlockVisitor visitor) {
		visitor.visitUsingDecl(this);
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("using ");
		b.append(super.toString());
		return b.toString();
	}

}
