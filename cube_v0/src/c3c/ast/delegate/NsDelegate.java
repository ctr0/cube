package c3c.ast.delegate;

import c3c.ast.MethodDecl;
import c3c.ast.NsDecl;
import c3c.ast.RecordDecl;
import c3c.ast.UsingDecl;
import c3c.ast.VarDecl;
import c3c.ast.cpp.TypeDefDecl;
import c3c.ast.delegate.BlockDelegate.BlockTrivial;
import c3c.ast.delegate.RecordDelegate.RecordTrivial;
import c3c.ast.visitor.ScopeVisitor.NsVisitor;
import c3c.cube.CubeLang.inline;
import c3c.cube.CubeLang.internal;
import c3c.cube.CubeLang.override;

public abstract class NsDelegate implements ScopeDelegate, NsVisitor {
	
	private NsDelegate delegate;
	
	public @internal static NsDelegate wrap(NsDecl decl) {
		return new NsTrivial(decl);
	}
	
	public NsDelegate(NsDelegate delegate) {
		this.delegate = delegate != null ? delegate : new NsTrivial(null);
	}
	
	protected NsDelegate() {
		
	}
	
//	public NsDecl getDecl() {
//		return delegate.getDecl();
//	}

	public @inline @override NsDelegate visitNamespaceDecl(NsDecl decl) {
		return delegate.visitNamespaceDecl(decl);
	}

	public @inline @override void visitUsingDecl(UsingDecl decl) {
		delegate.visitUsingDecl(decl);
	}

	public @inline @override void visitVarDecl(VarDecl decl) {
		delegate.visitVarDecl(decl);
	}

	public @inline @override BlockDelegate visitMethodDecl(MethodDecl decl) {
		return delegate.visitMethodDecl(decl);
	}

	public @inline @override RecordDelegate visitRecordDecl(RecordDecl decl) {
		return delegate.visitRecordDecl(decl);
	}

	public @inline @override void visitTypeDefDecl(TypeDefDecl decl) {
		delegate.visitTypeDefDecl(decl);
	}

	public @inline @override void visitEnd() {
		delegate.visitEnd();
	}

	public @internal static class NsTrivial extends NsDelegate {
		private NsDecl decl;
		public NsTrivial(NsDecl decl) {
			super();
			this.decl = decl;
		}
		public NsDecl getDecl() {
			return decl;
		}
//		public IGlobalScope getNamespace() {
//			return decl;
//		}
		public @inline @override NsDelegate visitNamespaceDecl(NsDecl decl) {
			return new NsTrivial(decl);
		}
		public @inline @override BlockDelegate visitMethodDecl(MethodDecl decl) {
			return new BlockTrivial(decl);
		}
		public @inline @override RecordDelegate visitRecordDecl(RecordDecl decl) {
			return new RecordTrivial(decl);
		}
		public @inline @override void visitUsingDecl(UsingDecl dir) {}
		public @inline @override void visitVarDecl(VarDecl decl) {}
		public @inline @override void visitTypeDefDecl(TypeDefDecl decl) {}
		public @inline @override void visitEnd() {}
	}
}
