package c3c.ast.visitor;

import c3c.ast.BinaryExpr;
import c3c.ast.CallExpr;
import c3c.ast.CastExpr;
import c3c.ast.IType.Type;
import c3c.ast.LexExpr;
import c3c.ast.TernaryExpr;
import c3c.ast.UnaryExpr;
import c3c.cube.Lexeme;



public interface ExprVisitor {
	
	public abstract void visitExpr(Lexeme expr);
	
	public abstract void visitExpr(LexExpr expr);

	public abstract CallExprVisitor visitExpr(CallExpr expr);
	
	public abstract UnaryExprVisitor visitExpr(UnaryExpr expr);

	public abstract BinaryExprVisitor visitExpr(BinaryExpr expr);
	
	public abstract TernaryExprVisitor visitExpr(TernaryExpr expr);
	
	public abstract CastExprVisitor visitExpr(CastExpr expr);
	
	
	public static interface UnaryExprVisitor extends ExprVisitor {}
	
	public static interface BinaryExprVisitor extends ExprVisitor {}
	
	public static interface TernaryExprVisitor extends ExprVisitor {}
	
	public static interface CastExprVisitor extends ExprVisitor {
		
		public abstract void visitExpr(Type type);
	}
	
	public interface CallExprVisitor extends ExprVisitor {}
	
}
