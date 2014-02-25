package c3c.ast;

import c3c.ast.IExpr.Expression;
import c3c.ast.visitor.BlockVisitor;
import c3c.ast.visitor.ExprVisitor;
import c3c.ast.visitor.ExprVisitor.BinaryExprVisitor;
import c3c.ast.visitor.ExprVisitor.CallExprVisitor;
import c3c.ast.visitor.ExprVisitor.UnaryExprVisitor;
import c3c.cube.CodeSource;
import c3c.cube.Lexeme;
import c3c.cube.SrcPos;



public interface IStatement {
	
	public abstract void accept(BlockVisitor visitor);
	
	public abstract boolean isValid();
	
	public abstract SrcPos getLocation();
	
	public abstract int getId();
	
	public abstract void setScope(IScope scope);
	
	public static abstract class ExprStat implements IStatement, ExprVisitor {
		
		private IScope scope;
		private CodeSource source;
		private Expression expr;
		
		public ExprStat(Expression expr) {
			this.expr = expr;
		}
		
		public Expression getExpr() {
			return expr;
		}
		
		public IScope getScope() {
			return scope;
		}

		public CodeSource getCodeSource() {
			return source;
		}
		
		public void setCodeSource(CodeSource source) {
			this.source = source;
		}

		@Override
		public void setScope(IScope scope) {
			this.scope = scope;
		}

		public int getId() {
			return expr.getId();
		}

		public SrcPos getLocation() {
			return expr.getLocation();
		}

		public boolean isValid() {
			return expr.isValid();
		}
		
		public void visitExpr(Lexeme expr) {
			this.expr.visitExpr(expr);
		}

		public void visitExpr(LexExpr expr) {
			this.expr.visitExpr(expr);
		}

		public CallExprVisitor visitExpr(CallExpr expr) {
			return this.expr.visitExpr(expr);
		}

		public UnaryExprVisitor visitExpr(UnaryExpr expr) {
			return this.expr.visitExpr(expr);
		}

		public BinaryExprVisitor visitExpr(BinaryExpr expr) {
			return this.expr.visitExpr(expr);
		}

		public TernaryExprVisitor visitExpr(TernaryExpr expr) {
			return this.expr.visitExpr(expr);
		}

		public CastExprVisitor visitExpr(CastExpr expr) {
			return this.expr.visitExpr(expr);
		}
		
	}
	
	public static class UnaryStat extends ExprStat implements IStatement, UnaryExprVisitor {

		public UnaryStat(UnaryExpr expr) {
			super(expr);
		}
		
		@Override
		public UnaryExpr getExpr() {
			return (UnaryExpr) super.getExpr();
		}

		@Override
		public void accept(BlockVisitor visitor) {
			visitor.visitStatement(this);
		}
	}
	
	public static class CallStat extends ExprStat implements IStatement, CallExprVisitor {

		public CallStat(CallExpr expr) {
			super(expr);
		}
		
		@Override
		public CallExpr getExpr() {
			return (CallExpr) super.getExpr();
		}

		@Override
		public void accept(BlockVisitor visitor) {
			visitor.visitStatement(this);
		}
	}
	
	public static class AssignStat extends ExprStat implements IStatement, BinaryExprVisitor {

		public AssignStat(BinaryExpr expr) {
			super(expr);
		}
		
		@Override
		public BinaryExpr getExpr() {
			return (BinaryExpr) super.getExpr();
		}

		@Override
		public void accept(BlockVisitor visitor) {
			visitor.visitStatement(this);
		}
	}

}
