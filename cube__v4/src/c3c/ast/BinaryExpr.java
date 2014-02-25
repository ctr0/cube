package c3c.ast;

import c3c.ast.IExpr.Expression;
import c3c.ast.visitor.BlockVisitor;
import c3c.ast.visitor.ExprVisitor;
import c3c.ast.visitor.ExprVisitor.BinaryExprVisitor;
import c3c.ast.visitor.ExprVisitor.NewExprVisitor;
import c3c.cube.Id;
import c3c.cube.Lexeme;
import c3c.cube.SrcPos;

public class BinaryExpr extends Expression implements BinaryExprVisitor {
	
	int operator;
	IExpr o1, o2;
	
	public BinaryExpr(int op) {
		operator = op;
	}
	
	@Override
	public boolean isValid() {
		return operator != 0 && o1 != null && o2 != null;
	}
	
	@Override
	public int getId() {
		return operator;
	}

	@Override
	public SrcPos getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IType getType() {
		return o1.getType();
	}
	
	public IExpr getLeftOp() {
		return o1;
	}
	
	public IExpr getRightOp() {
		return o2;
	}
	
//	@Override
//	public void accept(BlockVisitor visitor) {
//		visitor.visitStatement(this);
//	}

	@Override
	public void accept(ExprVisitor visitor) {
		visitor.visitExpr(this);
	}
	
	public void accept(BinaryExprVisitor visitor) {
		if (o1 != null) o1.accept(visitor);
		if (o2 != null) o2.accept(visitor);
	}

	@Override
	public boolean isStatement() {
		switch (getId()) {
		case Id.DOT:
		case Id.EQ:
		case Id.NEW:
			return true;
		}
		return false;
	}

	@Override
	public boolean contains(IExpr e) {
		return o1 == e || o2 == e;
	}

	private void setExprOperand(IExpr expr) {
		if (o1 != null) o2 = expr;
		else o1 = expr;
	}

	@Override
	public void visitExpr(Lexeme expr) {
		setExprOperand(expr);
	}
	
	@Override
	public void visitExpr(LexExpr expr) {
		setExprOperand(expr);
	}

	@Override
	public CallExprVisitor visitExpr(CallExpr expr) {
		setExprOperand(expr);
		return expr;
	}
	
	@Override
	public UnaryExprVisitor visitExpr(UnaryExpr expr) {
		setExprOperand(expr);
		return expr;
	}

	@Override
	public BinaryExprVisitor visitExpr(BinaryExpr expr) {
		setExprOperand(expr);
		return expr;
	}

	@Override
	public TernaryExprVisitor visitExpr(TernaryExpr expr) {
		setExprOperand(expr);
		return expr;
	}
	
	@Override
	public CastExprVisitor visitExpr(CastExpr expr) {
		setExprOperand(expr);
		return expr;
	}

	@Override
	public NewExprVisitor visitExpr(NewExpr expr) {
		setExprOperand(expr);
		return expr;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append('(');
		if (o1 != null) {
			builder.append(o1.toString());
		} else {
			builder.append("null");
		}
		builder.append(' ');
		builder.append(Id.Op.toString(operator));
		builder.append(' ');
		if (o2 != null) {
			builder.append(o2.toString());
		} else {
			builder.append("null");
		}
		builder.append(')');
		return builder.toString();
	}

}
