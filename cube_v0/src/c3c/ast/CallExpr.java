package c3c.ast;

import java.util.Iterator;

import c3c.ast.IExpr.Expression;
import c3c.ast.visitor.ExprVisitor;
import c3c.ast.visitor.ExprVisitor.CallExprVisitor;
import c3c.cube.IToken;
import c3c.cube.Id;
import c3c.cube.Lexeme;
import c3c.cube.List;
import c3c.cube.SrcPos;

public class CallExpr extends Expression implements CallExprVisitor {
	
	/*internal*/ public IToken token;
	
	/*internal*/ public List<IExpr> params;

	public CallExpr(IToken token) {
		this.token = token;
		params = new List<IExpr>();
	}
	
	public CallExpr(CallExpr expr) {
		this.token = expr.token;
		this.params = expr.params;
	}

	@Override
	public boolean isValid() {
		return token.isValid();
	}

	@Override
	public SrcPos getLocation() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public IToken getToken() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getId() {
		return Id.Op.FUNTION_CALL;
	}

	@Override
	public IType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	public Iterator<IExpr> getParams() {
		return params.iterator();
	}

	@Override
	public boolean isStatement() {
		return true;
	}
	
//	@Override
//	public void accept(BlockVisitor visitor) {
//		visitor.visitStatement(this);
//	}

	@Override
	public void accept(ExprVisitor visitor) {
		visitor.visitExpr(this);
	}
	
	public void accept(CallExprVisitor visitor) {
		for (IExpr param : params) {
			param.accept(visitor);
		}
	}

	@Override
	public void visitExpr(Lexeme expr) {
		if (token == null) {
			token = expr;
		} else {
			params.add(expr);
		}
	}
	
	@Override
	public void visitExpr(LexExpr expr) {
		params.add(expr);
	}

	@Override
	public CallExprVisitor visitExpr(CallExpr expr) {
		params.add(expr);
		return expr;
	}

	@Override
	public UnaryExprVisitor visitExpr(UnaryExpr expr) {
		params.add(expr);
		return expr;
	}

	@Override
	public BinaryExprVisitor visitExpr(BinaryExpr expr) {
		params.add(expr);
		return expr;
	}

	@Override
	public TernaryExprVisitor visitExpr(TernaryExpr expr) {
		params.add(expr);
		return expr;
	}

	@Override
	public CastExprVisitor visitExpr(CastExpr expr) {
		params.add(expr);
		return expr;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (token != null) {
			builder.append(token.toString());
		}
		builder.append('(');
		boolean first = true;
		for (IExpr e : params) {
			if (!first) builder.append(", ");
			else first = false;
			builder.append(e.toString());
		}
		builder.append(')');
		return builder.toString();
	}

}
