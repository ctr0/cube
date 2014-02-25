package c3c.ast.cpp;

import c3c.ast.Annotation;
import c3c.ast.Decl;
import c3c.ast.IStatement;
import c3c.ast.IType.ITypeDecl;
import c3c.ast.IType.Type;
import c3c.ast.Template;
import c3c.ast.visitor.BlockVisitor;
import c3c.ast.visitor.ScopeVisitor;
import c3c.cube.IToken;
import c3c.cube.List;

public class TypeDefDecl extends Decl implements ITypeDecl, IStatement {
	
	Type type;
	IToken token;
	
	/*internal*/ public TypeDefDecl(IToken token, List<Annotation> annos) {
		super(token, annos);
	}
	
	public Type getType() {
		return type;
	}

	@Override
	public boolean isPrimitive() {
		return type.isPrimitive();
	}

	@Override
	public boolean isGeneric() {
		return type.isGeneric();
	}

	@Override
	public Template getTemplDecl() {
		return type.getTemplDecl();
	}

	@Override
	public void accept(ScopeVisitor visitor) {
		visitor.visitTypeDefDecl(this);
	}

	@Override
	public void accept(BlockVisitor visitor) {
		visitor.visitTypeDefDecl(this);
	}

	public void visitTypename(IToken token) {
		this.token = token;
	}

	public void visitType(Type type) {
		this.type = type;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder("typedef ");
		if (type != null) {
			b.append(type.toString());
			b.append(' ');
		}
		b.append(super.toString());	
		return b.toString();
	}

}
