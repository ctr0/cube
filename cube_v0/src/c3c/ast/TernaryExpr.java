package c3c.ast;

import c3c.ast.IExpr.Expression;
import c3c.ast.visitor.ExprVisitor;
import c3c.ast.visitor.ExprVisitor.TernaryExprVisitor;
import c3c.cube.Lexeme;
import c3c.cube.SrcPos;

public class TernaryExpr extends Expression implements TernaryExprVisitor {
	
	IExpr expr, expr1, expr2;

	@Override
	public void accept(ExprVisitor visitor) {
		visitor.visitExpr(this);
	}

	public void accept(TernaryExprVisitor visitor) {
		// TODO ...
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IType getType() {
		if (expr1.getType().equals(expr2.getType())) {
			return expr1.getType();
		}
		return null;
	}

	@Override
	public boolean isStatement() {
		return false;
	}

	@Override
	public void visitExpr(Lexeme expr) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void visitExpr(LexExpr expr) {
		
	}

	@Override
	public CallExprVisitor visitExpr(CallExpr expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UnaryExprVisitor visitExpr(UnaryExpr expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BinaryExprVisitor visitExpr(BinaryExpr expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TernaryExprVisitor visitExpr(TernaryExpr expr) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public CastExprVisitor visitExpr(CastExpr expr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SrcPos getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

}
