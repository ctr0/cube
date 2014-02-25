package c3c.ast;

import java.util.Iterator;

import c3c.ast.IExpr.Expression;
import c3c.ast.IType.Type;
import c3c.ast.visitor.ExprVisitor;
import c3c.ast.visitor.ExprVisitor.NewExprVisitor;
import c3c.cube.IToken;
import c3c.cube.Id;
import c3c.cube.Lexeme;
import c3c.cube.List;
import c3c.cube.SrcPos;

public class NewExpr extends Expression implements NewExprVisitor {
	
	/*internal*/ public Type type;
	
	/*internal*/ public List<IExpr> params;

	public NewExpr(Type type) {
		this.type = type;
		params = new List<IExpr>();
	}
	
	public NewExpr(NewExpr expr) {
		this.type = expr.type;
		this.params = expr.params;
	}

	@Override
	public boolean isValid() {
		return type.isValid();
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
		return Id.Op.NEW;
	}

	@Override
	public Type getType() {
		return type;
	}

	public Iterator<IExpr> getParams() {
		return params.iterator();
	}

	@Override
	public boolean isStatement() {
		return true;
	}
	
	@Override
	public boolean contains(IExpr e) {
		return false;
	}

	@Override
	public void accept(ExprVisitor visitor) {
		visitor.visitExpr(this);
	}
	
	public void accept(NewExprVisitor visitor) {
		for (IExpr param : params) {
			param.accept(visitor);
		}
	}

	@Override
	public void visitExpr(Lexeme expr) {
		params.add(expr);
	}
	
	@Override
	public void visitExpr(LexExpr expr) {
		params.add(expr);
	}

	@Override
	public NewExprVisitor visitExpr(NewExpr expr) {
		params.add(expr);
		return expr;
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
	public INode clone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (type != null) {
			builder.append(type.toString());
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
