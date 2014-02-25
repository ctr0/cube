package c3c.ast;

import java.util.Iterator;

import c3c.ast.IScope.ScopeDecl;
import c3c.ast.cpp.TypeDefDecl;
import c3c.ast.visitor.BlockVisitor;
import c3c.ast.visitor.ScopeVisitor;
import c3c.ast.visitor.ScopeVisitor.NsVisitor;
import c3c.cube.CodeSource;
import c3c.cube.CubeLang.internal;
import c3c.cube.IToken;
import c3c.cube.List;
import c3c.cube.SrcPos;

public class NsDecl extends ScopeDecl implements NsVisitor {
	
	private Namespace ns;
	
	@internal public NsDecl(IToken token, CodeSource source, List<Annotation> annos) {
		super(token, source, annos);
		body = new Scope();
	}

	@internal void setNamespace(Namespace ns) {
		this.ns = ns;
	}
	
	public Namespace getNamespace() {
		return ns;
	}
	
	@Override
	public Project getProject() {
		return getScope().getProject();
	}

	@Override
	public IType getType() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void accept(ScopeVisitor visitor) {
		NsVisitor v = visitor.visitNamespaceDecl(this);
		if (v != null) {
			accept(v);
		}
	}

	public void accept(NsVisitor visitor) {
		body.accept(visitor);
	}

	@Override
	public Node resolve(String name, SrcPos location) {
		// Resolve using namespace instead of declaration scope (related to the file)
		return ns.resolve(name, location);
	}

	@Override
	public NsVisitor visitNamespaceDecl(NsDecl decl) {
		ns.visitNamespaceDecl(decl);
		super.visitNamespaceDecl(decl);
//		decl.setNamespace(ns);
		return decl;
	}

	@Override
	public void visitUsingDecl(UsingDecl decl) {
		super.visitUsingDecl(decl);
		//decl.setScope(ns); NOPE!
		decl.setNamespace(ns);
	}

	@Override
	public void visitVarDecl(VarDecl decl) {
		ns.visitVarDecl(decl);
		super.visitVarDecl(decl);
	}

	@Override
	public BlockVisitor visitMethodDecl(MethodDecl decl) {
		ns.visitMethodDecl(decl);
		super.visitMethodDecl(decl);
		return decl;
	}

	@Override
	public RecordVisitor visitRecordDecl(RecordDecl decl) {
		ns.visitRecordDecl(decl);
		super.visitRecordDecl(decl);
		return decl;
	}

	@Override
	public void visitTypeDefDecl(TypeDefDecl decl) {
		ns.visitTypeDefDecl(decl);
		super.visitTypeDefDecl(decl);
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("namespace ");
		b.append(super.toString());
		return b.toString();
	}

}
