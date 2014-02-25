package c3c.ast;

import c3c.ast.IType.Primitive;
import c3c.ast.visitor.ExprVisitor;
import c3c.cube.IToken;
import c3c.cube.SrcPos;

public class LexExpr implements IExpr {
	
	private IToken lexeme;
	
	private Decl decl;

	public LexExpr(IToken lexeme) {
		this.lexeme = lexeme;
	}
	
	public String getName() {
		return lexeme.getName();
	}

	public IToken getToken() {
		return lexeme;
	}
	
	@Override
	public int getId() {
		// TODO LexExpr#getId()
		return lexeme.getId();
	}

	@Override
	public SrcPos getLocation() {
		return lexeme.getLocation();
	}

	@Override
	public boolean isValid() {
		return lexeme.isValid();
	}

	@Override
	public IType getType() {
		if (isPrimitive()) {
			return Primitive.toPrimitive(getId());
		}
		return getDecl().getType();
	}

	private boolean isPrimitive() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean isLiteral() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStatement() {
		return false;
	}

	public Decl getDecl() {
		return decl;
	}

	public void setDecl(Decl decl) {
		this.decl = decl;
	}
	
	@Override
	public void accept(ExprVisitor visitor) {
		visitor.visitExpr(this);
	}

	@Override
	public String toString() {
		return lexeme.toString();
	}

}
