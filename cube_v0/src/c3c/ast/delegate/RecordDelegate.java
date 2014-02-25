package c3c.ast.delegate;

import c3c.ast.MethodDecl;
import c3c.ast.NsDecl;
import c3c.ast.RecordDecl;
import c3c.ast.UsingDecl;
import c3c.ast.VarDecl;
import c3c.ast.cpp.TypeDefDecl;
import c3c.ast.delegate.BlockDelegate.BlockTrivial;
import c3c.ast.visitor.ScopeVisitor.RecordVisitor;
import c3c.cube.CubeLang.inline;
import c3c.cube.CubeLang.internal;
import c3c.cube.CubeLang.override;

public class RecordDelegate implements ScopeDelegate, RecordVisitor {
	
	private RecordDelegate delegate;
	
	RecordDelegate() {}
	
	public RecordDelegate(RecordDelegate delegate) {
		this.delegate = delegate;
	}
	
	public RecordDecl getDecl() {
		return delegate.getDecl();
	}

	public @inline @override void visitUsingDecl(UsingDecl dir) {
		delegate.visitUsingDecl(dir);
	}

	public @inline @override NsDelegate visitNamespaceDecl(NsDecl decl) {
		return delegate.visitNamespaceDecl(decl);
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

	public @internal static class RecordTrivial extends RecordDelegate {
		private RecordDecl decl;
		public RecordTrivial(RecordDecl decl) {
			super();
			this.decl = decl;
		}
		public RecordDecl getDecl() {
			return decl;
		}
//		public IGlobalScope getNamespace() {
//			return decl.getNamespace();
//		}
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
