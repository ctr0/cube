package c3c.sema;

import c3c.ast.MethodDecl;
import c3c.ast.NsDecl;
import c3c.ast.RecordDecl;
import c3c.ast.UsingDecl;
import c3c.ast.VarDecl;
import c3c.ast.cpp.TypeDefDecl;
import c3c.ast.delegate.BlockDelegate;
import c3c.ast.delegate.NsDelegate;
import c3c.ast.delegate.RecordDelegate;

public class RecordDeclSema extends RecordDelegate {
	
	private Resolver resolver;

	public RecordDeclSema(Resolver resolver, RecordDelegate delegate) {
		super(delegate);
		this.resolver = resolver;
	}

	@Override
	public void visitUsingDecl(UsingDecl dir) {
		// TODO Auto-generated method stub
		super.visitUsingDecl(dir);
	}

	@Override
	public NsDelegate visitNamespaceDecl(NsDecl decl) {
		// TODO Auto-generated method stub
		return super.visitNamespaceDecl(decl);
	}

	@Override
	public void visitVarDecl(VarDecl decl) {
		resolver.resolveVarDecl(decl);
		super.visitVarDecl(decl);
	}

	@Override
	public BlockDelegate visitMethodDecl(MethodDecl decl) {
		resolver.visitMethodDecl(decl);
		return new MethodDeclSema(
			resolver, super.visitMethodDecl(decl)
		);
	}

	@Override
	public RecordDelegate visitRecordDecl(RecordDecl decl) {
		resolver.visitRecordDecl(decl);
		return new RecordDeclSema(
			resolver, super.visitRecordDecl(decl)
		);
	}

	@Override
	public void visitTypeDefDecl(TypeDefDecl decl) {
		// TODO Auto-generated method stub
		super.visitTypeDefDecl(decl);
	}

	@Override
	public void visitEnd() {
		// TODO Auto-generated method stub
		super.visitEnd();
	}

	
}
