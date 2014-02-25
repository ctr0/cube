package c3c.ast;

import java.util.Iterator;

import c3c.ast.IScope.ScopeDecl;
import c3c.ast.IType.ITypeDecl;
import c3c.ast.visitor.BlockVisitor;
import c3c.ast.visitor.ScopeVisitor;
import c3c.ast.visitor.ScopeVisitor.RecordVisitor;
import c3c.cube.CodeSource;
import c3c.cube.IToken;
import c3c.cube.List;
import c3c.cube.Modifiers;

public class RecordDecl extends ScopeDecl implements IType, ITypeDecl, IStatement, RecordVisitor {
	
	public int modifiers;
	
	public Template templDecl;
	
	public List<Type> baseDecls;

	public boolean forward = false;
	
	public RecordDecl(int modifiers, IToken token, CodeSource source, List<Annotation> annos) {
		super(token, source, annos);
		this.modifiers = modifiers;
		this.baseDecls = new List<Type>();
	}

	public int getModifiers() {
		return modifiers;
	}

	@Override
	public boolean isPrimitive() {
		return false;
	}
	
	@Override
	public boolean isGeneric() {
		return templDecl != null;
	}

	@Override
	public IType getType() {
		return this;
	}

	@Override
	public ITypeDecl getDecl() {
		return this;
	}

	@Override
	public Template getTemplDecl() {
		return templDecl;
	}
	
	public Iterator<Type> getBaseDecls() {
		return baseDecls.iterator();
	}

	public void accept(ScopeVisitor visitor) {
		RecordVisitor v = visitor.visitRecordDecl(this);
		if (v != null) {
			accept(v);
		}
	}
	
	@Override
	public void accept(BlockVisitor visitor) {
		RecordVisitor v = visitor.visitRecordDecl(this);
		if (v != null) {
			accept(v);
		}
	}

	public void accept(RecordVisitor visitor) {
		if (body != null) {
			body.accept(visitor);
		}
	}

	public void visitTemplDecl(Template decl) {
		templDecl = decl;
	}

	public void visitBaseDecl(Type type) {
		baseDecls.add(type);
	}

	public void visitBlock() {
		body = new Scope();
		body.setScope(this);
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder(Modifiers.toString(modifiers));
		b.append(super.toString());
		if (templDecl != null) {
			b.append(templDecl.toString());
		}
		boolean first = true;
		for (Type decl : baseDecls) {
			if (first) {
				b.append(" : ");
			}
			first = false;
			b.append(decl.toString());
		}
		return b.toString();
	}
}
