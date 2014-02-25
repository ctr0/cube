package c3c.ast.visitor;

import c3c.ast.BlockStat;
import c3c.ast.IStatement.AssignStat;
import c3c.ast.IStatement.CallStat;
import c3c.ast.IStatement.NewStat;
import c3c.ast.IStatement.UnaryStat;
import c3c.ast.InstrStat;
import c3c.ast.InstrStat.InstrBlockStat;
import c3c.ast.RecordDecl;
import c3c.ast.UsingDecl;
import c3c.ast.VarDecl;
import c3c.ast.cpp.TypeDefDecl;
import c3c.ast.visitor.ExprVisitor.BinaryExprVisitor;
import c3c.ast.visitor.ExprVisitor.CallExprVisitor;
import c3c.ast.visitor.ExprVisitor.NewExprVisitor;
import c3c.ast.visitor.ExprVisitor.UnaryExprVisitor;
import c3c.ast.visitor.ScopeVisitor.RecordVisitor;
import c3c.cube.Lexeme;


public interface BlockVisitor /*extends ExprVisitor*/ {
	
	public abstract void visitUsingDecl(UsingDecl decl);

	public abstract void visitLabelDecl(Lexeme lexeme);
	
	public abstract void visitVarDecl(VarDecl decl);
	
	public abstract UnaryExprVisitor visitStatement(UnaryStat stat);
	
	public abstract BinaryExprVisitor visitStatement(AssignStat stat);
	
	public abstract CallExprVisitor visitStatement(CallStat stat);
	
	public abstract NewExprVisitor visitStatement(NewStat stat);
	
	public abstract void visitStatement(InstrStat stat);
	
	public abstract InstrStatVisitor visitStatement(InstrBlockStat stat);
	
	public abstract BlockStatVisitor visitStatement(BlockStat stat);
	
	public abstract RecordVisitor visitRecordDecl(RecordDecl decl);
	
	public abstract void visitTypeDefDecl(TypeDefDecl decl);
	
	public abstract void visitEnd();
	
	
	
	public interface BlockStatVisitor extends BlockVisitor {

	}
	
	public interface InstrStatVisitor extends BlockVisitor {

	}
	
}
