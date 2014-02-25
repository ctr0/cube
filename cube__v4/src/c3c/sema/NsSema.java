package c3c.sema;

import c3c.ast.MethodDecl;
import c3c.ast.Namespace;
import c3c.ast.NsDecl;
import c3c.ast.RecordDecl;
import c3c.ast.UsingDecl;
import c3c.ast.VarDecl;
import c3c.ast.cpp.TypeDefDecl;
import c3c.ast.delegate.BlockDelegate;
import c3c.ast.delegate.NsDelegate;
import c3c.ast.delegate.RecordDelegate;
import c3c.cube.Modifiers;
import c3c.cube.Report;

/*
 * Top builder
 */
public class NsSema extends NsDelegate {
	
	private Resolver resolver;
	
	public NsSema(NsDelegate delegate) {
		super(delegate);
		resolver = new Resolver();
	}

	@Override
	public void visitUsingDecl(UsingDecl decl) {
		resolver.visitUsingDecl(decl);
		Namespace namespace = decl.getNamespace();
		// TODO resolve assembly
	}

	@Override
	public NsDelegate visitNamespaceDecl(NsDecl decl) {
		return new NsSema(
			super.visitNamespaceDecl(decl)
		);
	}

	@Override
	public void visitVarDecl(VarDecl decl) {
		if (!Modifiers.in(decl.getModifiers(), Modifiers.NS_FUNC_MODIFIERS)) {
			Report.message(Report._2_ILL_NS_FUNC_MODS, decl);
		}
		super.visitVarDecl(decl); // resolve
		if (decl.getType() != decl.getInitializer().getType()) {
			// TODO error incompatible types
		}
		
	}

	@Override
	public BlockDelegate visitMethodDecl(MethodDecl decl) {
		if (!Modifiers.in(decl.getModifiers(), Modifiers.NS_FUNC_MODIFIERS)) {
			Report.message(Report._2_ILL_NS_VAR_MODS, decl);
		}
		resolver.visitMethodDecl(decl);
		return new MethodDeclSema(
			resolver, super.visitMethodDecl(decl)
		);
	}

	@Override
	public RecordDelegate visitRecordDecl(RecordDecl decl) {
		if (!Modifiers.in(decl.getModifiers(), Modifiers.NS_RECORD_MODIFIERS)) {
			Report.message(Report._2_ILL_NS_VAR_MODS, decl);
		}
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

//	@Override
//	public void visitEnd() {
//		super.visitEnd();
//	}
}
