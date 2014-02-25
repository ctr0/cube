package c3c.ast;

import c3c.ast.visitor.ExprVisitor;
import c3c.cube.IToken;
import c3c.cube.Lexeme;

public class Annotation extends Node implements ExprVisitor {
	
	private CallExpr call;
	private IExpr expr;
	
	public Annotation(IToken token) {
		super(token);
	}

	public CallExpr getCall() {
		return call;
	}

	public IExpr getExpr() {
		return expr;
	}

	@Override
	public void visitExpr(Lexeme expr) {
		this.expr = expr;
	}

	@Override
	public void visitExpr(LexExpr expr) {
		this.expr = expr;
	}
	
	@Override
	public CallExprVisitor visitExpr(CallExpr expr) {
		this.expr = expr;
		return expr;
	}

	@Override
	public UnaryExprVisitor visitExpr(UnaryExpr expr) {
		this.expr = expr;
		return expr;
	}
	
	@Override
	public BinaryExprVisitor visitExpr(BinaryExpr expr) {
		this.expr = expr;
		return expr;
	}
	
	@Override
	public TernaryExprVisitor visitExpr(TernaryExpr expr) {
		this.expr = expr;
		return expr;
	}
	
	@Override
	public CastExprVisitor visitExpr(CastExpr expr) {
		this.expr = expr;
		return expr;
	}

	@Override
	public NewExprVisitor visitExpr(NewExpr expr) {
		this.expr = expr;
		return expr;
	}
	
	public CallExprVisitor visitCallArgs(CallExpr expr) {
		return this.call = expr;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getToken().toString());
		if (expr != null) {
			builder.append('<');
			builder.append(expr.toString());
			builder.append('>');
		}
		if (call != null) {
			builder.append(call.toString());
		}
		return builder.toString();
	}
	
}
