package c3c.ast.delegate;

import c3c.ast.ComplUnit;
import c3c.ast.MethodDecl;
import c3c.ast.NsDecl;
import c3c.ast.RecordDecl;
import c3c.ast.UsingDecl;
import c3c.ast.VarDecl;
import c3c.ast.cpp.TypeDefDecl;
import c3c.cube.CubeLang.inline;
import c3c.cube.CubeLang.override;



public class DelegateBuilder extends NsDelegate {
	
	public DelegateBuilder(DelegateBuilder delegate) {
		super(delegate);
	}
	
	public static DelegateBuilder wrap(ComplUnit unit) {
		return new Trivial(unit);
	}
	
	private static class Trivial extends DelegateBuilder {
		private ComplUnit unit;
		public Trivial(ComplUnit unit) {
			super(null);
			this.unit = unit;
		}
		public ComplUnit getUnit() {
			return unit;
		}
//		public IGlobalScope getNamespace() {
//			return decl;
//		}
		public @inline @override NsDelegate visitNamespaceDecl(NsDecl decl) {
			return new NsTrivial(decl);
		}
		public @inline @override BlockDelegate visitMethodDecl(MethodDecl decl) {
			return null;
		}
		public @inline @override RecordDelegate visitRecordDecl(RecordDecl decl) {
			return null;
		}
		public @inline @override void visitUsingDecl(UsingDecl dir) {}
		public @inline @override void visitVarDecl(VarDecl decl) {}
		public @inline @override void visitTypeDefDecl(TypeDefDecl decl) {}
		public @inline @override void visitEnd() {}
	}

}
