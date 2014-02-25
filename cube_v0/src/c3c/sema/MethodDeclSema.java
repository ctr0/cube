package c3c.sema;

import c3c.ast.BlockStat;
import c3c.ast.IStatement.AssignStat;
import c3c.ast.IStatement.CallStat;
import c3c.ast.IStatement.UnaryStat;
import c3c.ast.InstrStat;
import c3c.ast.InstrStat.InstrBlockStat;
import c3c.ast.MethodDecl;
import c3c.ast.RecordDecl;
import c3c.ast.UsingDecl;
import c3c.ast.VarDecl;
import c3c.ast.cpp.TypeDefDecl;
import c3c.ast.delegate.BlockDelegate;
import c3c.ast.delegate.RecordDelegate;
import c3c.ast.visitor.ExprVisitor.BinaryExprVisitor;
import c3c.ast.visitor.ExprVisitor.CallExprVisitor;
import c3c.ast.visitor.ExprVisitor.UnaryExprVisitor;
import c3c.cube.Lexeme;

public class MethodDeclSema extends BlockDelegate {
	
	private Resolver resolver;

	public MethodDeclSema(Resolver resolver, BlockDelegate delegate) {
		super(delegate);
		this.resolver = resolver;
	}

	@Override
	public void visitUsingDecl(UsingDecl decl) {
		resolver.resolveUsingDecl(decl);
		super.visitUsingDecl(decl);
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
	public void visitLabelDecl(Lexeme lexeme) {
		// TODO Auto-generated method stub
		super.visitLabelDecl(lexeme);
	}

	@Override
	public void visitTypeDefDecl(TypeDefDecl decl) {
		// TODO Auto-generated method stub
		super.visitTypeDefDecl(decl);
	}

	@Override
	public void visitVarDecl(VarDecl decl) {
		resolver.resolveVarDecl(decl);
		super.visitVarDecl(decl);
	}

	@Override
	public void visitEnd() {
		// TODO Auto-generated method stub
		super.visitEnd();
	}

	@Override
	public UnaryExprVisitor visitStatement(UnaryStat stat) {
		// TODO Auto-generated method stub
		return super.visitStatement(stat);
	}

	@Override
	public CallExprVisitor visitStatement(CallStat stat) {
		// TODO Auto-generated method stub
		return super.visitStatement(stat);
	}

	@Override
	public BinaryExprVisitor visitStatement(AssignStat stat) {
		// TODO Auto-generated method stub
		return super.visitStatement(stat);
	}

	@Override
	public BlockStatVisitor visitStatement(BlockStat stat) {
		// TODO Auto-generated method stub
		return super.visitStatement(stat);
	}

	@Override
	public void visitStatement(InstrStat stat) {
		// TODO Auto-generated method stub
		super.visitStatement(stat);
	}

	@Override
	public InstrStatVisitor visitStatement(InstrBlockStat stat) {
		// TODO Auto-generated method stub
		return super.visitStatement(stat);
	}

}
