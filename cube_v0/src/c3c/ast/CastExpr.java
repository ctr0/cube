package c3c.ast;

import c3c.ast.IType.Type;
import c3c.ast.visitor.ExprVisitor.CastExprVisitor;
import c3c.cube.Id;

public class CastExpr extends UnaryExpr implements CastExprVisitor {
	
	private Type decl;

	public CastExpr(Type decl) {
		super(Id.Op.CAST);
		this.decl = decl;
	}

	public void visitExpr(Type expr) {
		this.decl = expr;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("((");
		if (decl != null) builder.append(decl.toString());
		else builder.append("null");
		builder.append(')');
		if (operand != null) builder.append(operand.toString());
		else builder.append("null");
		builder.append(')');
		return builder.toString();
	}

}
