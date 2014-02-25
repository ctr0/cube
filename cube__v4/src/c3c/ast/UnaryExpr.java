package c3c.ast;

import c3c.ast.IExpr.Expression;
import c3c.ast.IType.Type;
import c3c.ast.visitor.ExprVisitor;
import c3c.ast.visitor.ExprVisitor.NewExprVisitor;
import c3c.ast.visitor.ExprVisitor.UnaryExprVisitor;
import c3c.cube.Id;
import c3c.cube.Lexeme;
import c3c.cube.SrcPos;


public class UnaryExpr extends Expression implements UnaryExprVisitor {
	
	int operator;
	IExpr operand;
	Type type;
	
	public UnaryExpr(int op) {
		this.operator = op;
	}

	public UnaryExpr(int op, IExpr expr) {
		this.operator = op;
		this.operand = expr;
	}
	
	@Override
	public int getId() {
		return operator;
	}
	
	public IExpr getOperand() {
		return operand;
	}

	@Override
	public IType getType() {
		return type;
	}

	@Override
	public SrcPos getLocation() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean isStatement() {
		return false;
	}

	@Override
	public boolean contains(IExpr e) {
		return operand == e;
	}

	@Override
	public boolean isValid() {
		return operator != 0 && operand != null;
	}

//	@Override
//	public void accept(BlockVisitor visitor) {
//		visitor.visitStatement(this);
//	}

	@Override
	public void accept(ExprVisitor visitor) {
		visitor.visitExpr(this);
	}

	public void accept(UnaryExprVisitor visitor) {
		operand.accept(visitor);
	}

	@Override
	public void visitExpr(Lexeme expr) {
		this.operand = expr;
	}
	
	@Override
	public void visitExpr(LexExpr expr) {
		this.operand = expr;
	}

	@Override
	public CallExprVisitor visitExpr(CallExpr expr) {
		this.operand = expr;
		return expr;
	}

	@Override
	public UnaryExprVisitor visitExpr(UnaryExpr expr) {
		this.operand = expr;
		return expr;
	}

	@Override
	public BinaryExprVisitor visitExpr(BinaryExpr expr) {
		this.operand = expr;
		return expr;
	}

	@Override
	public TernaryExprVisitor visitExpr(TernaryExpr expr) {
		this.operand = expr;
		return expr;
	}
	
	@Override
	public CastExprVisitor visitExpr(CastExpr expr) {
		this.operand = expr;
		return expr;
	}

	@Override
	public NewExprVisitor visitExpr(NewExpr expr) {
		this.operand = expr;
		return expr;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append('(');
		if (operand != null) {
			builder.append(operand.toString());
		} else {
			builder.append("null");
		}
		builder.append(' ');
		builder.append(Id.Op.toString(operator));
		builder.append(')');
		return builder.toString();
	}
}
