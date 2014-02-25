package c3c.sema;


import c3c.ast.MethodDecl;
import c3c.ast.Project;
import c3c.ast.RecordDecl;
import c3c.ast.VarDecl;
import c3c.ast.cpp.TypeDefDecl;
import c3c.ast.delegate.BlockDelegate;
import c3c.ast.delegate.DelegateBuilder;
import c3c.ast.delegate.RecordDelegate;
import c3c.cube.Report;

public class SemaAnalizer extends NsSema {

	public SemaAnalizer(Project project, DelegateBuilder delegate) {
		super(delegate);
	}

	@Override
	public void visitVarDecl(VarDecl decl) {
		Report.message(Report._2_GLOBAL_DECL, decl);
		super.visitVarDecl(decl);
	}

	@Override
	public BlockDelegate visitMethodDecl(MethodDecl decl) {
		Report.message(Report._2_GLOBAL_DECL, decl);
		return super.visitMethodDecl(decl);
	}

	@Override
	public RecordDelegate visitRecordDecl(RecordDecl decl) {
		Report.message(Report._2_GLOBAL_DECL, decl);
		return super.visitRecordDecl(decl);
	}

	@Override
	public void visitTypeDefDecl(TypeDefDecl decl) {
		Report.message(Report._2_GLOBAL_DECL, decl);
		super.visitTypeDefDecl(decl);
	}
}
