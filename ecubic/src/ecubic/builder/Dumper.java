package ecubic.builder;

import c3c.ast.BinaryExpr;
import c3c.ast.BlockStat;
import c3c.ast.CallExpr;
import c3c.ast.CastExpr;
import c3c.ast.IStatement.AssignStat;
import c3c.ast.IStatement.CallStat;
import c3c.ast.IStatement.UnaryStat;
import c3c.ast.InstrStat;
import c3c.ast.InstrStat.InstrBlockStat;
import c3c.ast.LexExpr;
import c3c.ast.MethodDecl;
import c3c.ast.NsDecl;
import c3c.ast.RecordDecl;
import c3c.ast.TernaryExpr;
import c3c.ast.UnaryExpr;
import c3c.ast.UsingDecl;
import c3c.ast.VarDecl;
import c3c.ast.cpp.TypeDefDecl;
import c3c.ast.visitor.BlockVisitor;
import c3c.ast.visitor.BlockVisitor.BlockStatVisitor;
import c3c.ast.visitor.BlockVisitor.InstrStatVisitor;
import c3c.ast.visitor.ExprVisitor;
import c3c.ast.visitor.ScopeVisitor.NsVisitor;
import c3c.ast.visitor.ScopeVisitor.RecordVisitor;
import c3c.cube.Lexeme;

public class Dumper implements NsVisitor, RecordVisitor, 
	BlockVisitor, BlockStatVisitor, InstrStatVisitor, ExprVisitor {
	
	private StringBuffer buffer = new StringBuffer();
	
	private int ident = 0;
	
	public String getString() {
		return buffer.toString();
	}

	@Override
	public void visitUsingDecl(UsingDecl decl) {
		ident();
		buffer.append(decl.toString());
		buffer.append('\n');
	}
	
	@Override
	public NsVisitor visitNamespaceDecl(NsDecl decl) {
		ident();
		buffer.append(decl.toString());
		buffer.append(" {\n");
		ident++;
		return this;
	}

	@Override
	public void visitVarDecl(VarDecl decl) {
		ident();
		buffer.append(decl.toString());
		buffer.append('\n');
	}

	@Override
	public BlockVisitor visitMethodDecl(MethodDecl decl) {
		ident();
		buffer.append(decl.toString());
		if (decl.hasBody()) {
			buffer.append(" {\n");
			ident++;
			return this;
		} else {
			buffer.append(";\n");
			return null;
		}
	}

	@Override
	public RecordVisitor visitRecordDecl(RecordDecl decl) {
		ident();
		buffer.append(decl.toString());
		buffer.append(" {\n");
		ident++;
		return this;
	}

	@Override
	public void visitTypeDefDecl(TypeDefDecl decl) {
		ident();
		buffer.append(decl.toString());
		buffer.append('\n');
	}
	
	@Override
	public void visitExpr(Lexeme expr) {
		ident();
		buffer.append(expr.toString());
		buffer.append('\n');
	}

	@Override
	public void visitExpr(LexExpr expr) {
		ident();
		buffer.append(expr.toString());
		buffer.append('\n');
	}

	@Override
	public CallExprVisitor visitExpr(CallExpr expr) {
		ident();
		buffer.append(expr.toString());
		buffer.append('\n');
		return null;
	}

	@Override
	public UnaryExprVisitor visitExpr(UnaryExpr expr) {
		ident();
		buffer.append(expr.toString());
		buffer.append('\n');
		return null;
	}

	@Override
	public BinaryExprVisitor visitExpr(BinaryExpr expr) {
		ident();
		buffer.append(expr.toString());
		buffer.append('\n');
		return null;
	}

	@Override
	public TernaryExprVisitor visitExpr(TernaryExpr expr) {
		ident();
		buffer.append(expr.toString());
		buffer.append('\n');
		return null;
	}

	@Override
	public CastExprVisitor visitExpr(CastExpr expr) {
		ident();
		buffer.append(expr.toString());
		buffer.append('\n');
		return null;
	}

	@Override
	public void visitLabelDecl(Lexeme lexeme) {
		ident();
		buffer.append(lexeme.toString());
		buffer.append(": ");
	}

	@Override
	public UnaryExprVisitor visitStatement(UnaryStat stat) {
		ident();
		buffer.append(stat.toString());
		buffer.append('\n');
		return null;
	}

	@Override
	public BinaryExprVisitor visitStatement(AssignStat stat) {
		ident();
		buffer.append(stat.toString());
		buffer.append('\n');
		return null;
	}

	@Override
	public CallExprVisitor visitStatement(CallStat stat) {
		ident();
		buffer.append(stat.toString());
		buffer.append('\n');
		return null;
	}

	@Override
	public void visitStatement(InstrStat stat) {
		ident();
		buffer.append(stat.toString());
		buffer.append('\n');
	}

	@Override
	public InstrStatVisitor visitStatement(InstrBlockStat stat) {
		ident();
		buffer.append(stat.toString());
		buffer.append('\n');
		return this;
	}

	@Override
	public BlockStatVisitor visitStatement(BlockStat stat) {
		ident();
		buffer.append("{\n");
		ident++;
		return this;
	}

	@Override
	public void visitEnd() {
		ident--;
		ident();
		buffer.append('}');
		buffer.append('\n');
	}

	private void ident() {
		for (int i = 0; i < ident; i++) {
			buffer.append('\t');
		}
	}
}
