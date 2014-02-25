package c3c.ast.visitor;

import c3c.ast.BinaryExpr;
import c3c.ast.BlockStat;
import c3c.ast.CallExpr;
import c3c.ast.CastExpr;
import c3c.ast.IStatement.AssignStat;
import c3c.ast.IStatement.CallStat;
import c3c.ast.IStatement.NewStat;
import c3c.ast.IStatement.UnaryStat;
import c3c.ast.IType.TemplTypeParam;
import c3c.ast.IType.Type;
import c3c.ast.InstrStat;
import c3c.ast.InstrStat.InstrBlockStat;
import c3c.ast.LexExpr;
import c3c.ast.MethodDecl;
import c3c.ast.NewExpr;
import c3c.ast.NsDecl;
import c3c.ast.RecordDecl;
import c3c.ast.TemplDecl;
import c3c.ast.TernaryExpr;
import c3c.ast.UnaryExpr;
import c3c.ast.UsingDecl;
import c3c.ast.VarDecl;
import c3c.ast.cpp.TypeDefDecl;
import c3c.cube.Lexeme;

public interface Visitor {
	
	public static interface ScopeVisitor extends CodeVisitor {
		public abstract void visitUsingDecl(UsingDecl decl);
		public abstract NsVisitor visitNamespaceDecl(NsDecl decl);
		public abstract void visitVarDecl(VarDecl decl);
		public abstract BlockVisitor visitMethodDecl(MethodDecl decl);
		public abstract BlockVisitor visitOperatorDecl(OperatorDecl decl);
		public abstract RecordVisitor visitRecordDecl(RecordDecl decl);
		public abstract void visitTypeDefDecl(TypeDefDecl decl);
		public abstract void visitEnd();
	}
	
	public static interface NsVisitor extends ScopeVisitor {
		public abstract void visitUsingDecl(UsingDecl decl);
		public abstract NsVisitor visitNamespaceDecl(NsDecl decl);
		public abstract void visitVarDecl(VarDecl decl);
		public abstract BlockVisitor visitMethodDecl(MethodDecl decl);
		public abstract BlockVisitor visitOperatorDecl(OperatorDecl decl);
		public abstract RecordVisitor visitRecordDecl(RecordDecl decl);
		public abstract void visitTypeDefDecl(TypeDefDecl decl);
		public abstract void visitEnd();
	}
	
	public static interface RecordVisitor extends ScopeVisitor {
		public abstract void visitUsingDecl(UsingDecl decl);
		public abstract void visitVarDecl(VarDecl decl);
		public abstract BlockVisitor visitMethodDecl(MethodDecl decl);
		public abstract BlockVisitor visitOperatorDecl(OperatorDecl decl);
		public abstract RecordVisitor visitRecordDecl(RecordDecl decl);
		public abstract void visitTypeDefDecl(TypeDefDecl decl);
		public abstract void visitEnd();
	}
	
	public static interface BlockVisitor extends CodeVisitor {
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
	}
	
	public static interface ExprVisitor extends CodeVisitor {
		public abstract void visitExpr(Lexeme expr);
		public abstract void visitExpr(LexExpr expr);
		public abstract NewExprVisitor visitExpr(NewExpr expr);
		public abstract CallExprVisitor visitExpr(CallExpr expr);
		public abstract UnaryExprVisitor visitExpr(UnaryExpr expr);
		public abstract BinaryExprVisitor visitExpr(BinaryExpr expr);
		public abstract TernaryExprVisitor visitExpr(TernaryExpr expr);
		public abstract CastExprVisitor visitExpr(CastExpr expr);
	}
	
	public static interface UnaryExprVisitor extends ExprVisitor {}
	public static interface BinaryExprVisitor extends ExprVisitor {}
	public static interface TernaryExprVisitor extends ExprVisitor {}
	public static interface CallExprVisitor extends ExprVisitor {}
	public static interface NewExprVisitor extends ExprVisitor {}
	public static interface CastExprVisitor extends UnaryExprVisitor {
		public abstract void visitType(Type type);
	}
	
	public interface BlockStatVisitor extends BlockVisitor {}
	
	public interface InstrStatVisitor extends BlockVisitor {}
	
	public static interface TemplateVisitor extends CodeVisitor {
		public abstract void visitTypeParam(TemplTypeParam type);
		public abstract void visitVarDecl(VarDecl decl);
	}
	
	public static interface CodeVisitor {
		public abstract void visitUsingDecl(UsingDecl decl);
		public abstract NsVisitor visitNamespaceDecl(NsDecl decl);
		public abstract void visitVarDecl(VarDecl decl);
		public abstract BlockVisitor visitMethodDecl(MethodDecl decl);
		public abstract BlockVisitor visitOperatorDecl(OperatorDecl decl);
		public abstract RecordVisitor visitRecordDecl(RecordDecl decl);
		public abstract void visitTypeDefDecl(TypeDefDecl decl);
		public abstract void visitExpr(Lexeme expr);
		public abstract void visitExpr(LexExpr expr);
		public abstract NewExprVisitor visitExpr(NewExpr expr);
		public abstract CallExprVisitor visitExpr(CallExpr expr);
		public abstract UnaryExprVisitor visitExpr(UnaryExpr expr);
		public abstract BinaryExprVisitor visitExpr(BinaryExpr expr);
		public abstract TernaryExprVisitor visitExpr(TernaryExpr expr);
		public abstract CastExprVisitor visitExpr(CastExpr expr);
		public abstract UnaryExprVisitor visitStatement(UnaryStat stat);
		public abstract BinaryExprVisitor visitStatement(AssignStat stat);
		public abstract CallExprVisitor visitStatement(CallStat stat);
		public abstract NewExprVisitor visitStatement(NewStat stat);
		public abstract void visitStatement(InstrStat stat);
		public abstract InstrStatVisitor visitStatement(InstrBlockStat stat);
		public abstract BlockStatVisitor visitStatement(BlockStat stat);
		public abstract void visitTemplate(TemplDecl template);
		public abstract void visitTemplTypeParam(TemplTypeParam type);
		public abstract void visitTemplVarParam(VarDecl decl);
		public abstract void visitType(Type type);
	}

}
