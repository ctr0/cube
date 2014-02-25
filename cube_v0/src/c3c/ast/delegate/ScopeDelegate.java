package c3c.ast.delegate;

import c3c.ast.MethodDecl;
import c3c.ast.NsDecl;
import c3c.ast.RecordDecl;
import c3c.ast.VarDecl;
import c3c.ast.cpp.TypeDefDecl;
import c3c.ast.visitor.ScopeVisitor;


public interface ScopeDelegate extends ScopeVisitor {
	
//	public IScope getDecl();
	
//	public IGlobalScope getNamespace();
	
	public abstract NsDelegate visitNamespaceDecl(NsDecl decl);
	
	public abstract void visitVarDecl(VarDecl decl);
	
	public abstract BlockDelegate visitMethodDecl(MethodDecl decl);
	
	public abstract RecordDelegate visitRecordDecl(RecordDecl decl);
	
	public abstract void visitTypeDefDecl(TypeDefDecl decl);

}
