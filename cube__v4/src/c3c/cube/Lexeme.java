package c3c.cube;

import c3c.ast.IExpr;
import c3c.ast.IScope;
import c3c.ast.IStatement;
import c3c.ast.IType.Type;
import c3c.ast.visitor.BlockVisitor;
import c3c.ast.visitor.ExprVisitor;


/**
 * Token implementation that stores extra information in a string.
 * Identifiers or literals are Lexemes.
 * <ul>
 * <li>Implements IExpr for literal expression operands.
 * <li>Implements IStatement for label declarations.
 * </ul>
 * 
 * @author j0rd1 Carretero
 */
public class Lexeme implements IToken, IExpr, IStatement {
	
	private String lexeme;
	private SrcPos location;
	
	Lexeme(String lexeme, SrcPos location) {
		this.lexeme = lexeme;
		this.location = location;
	}

	@Override
	public int getId() {
		return Id.ID;
	}

	@Override
	public SrcPos getLocation() {
		return location;
	}

	@Override
	public boolean isValid() {
		return lexeme != null;
	}

	@Override
	public boolean isComplex() {
		return false;
	}

	@Override
	public Lexeme[] getLexemes() {
		return null;
	}

	@Override
	public Type getType() {
		return null;
	}

	@Override
	public void setScope(IScope scope) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isStatement() {
		return false;
	}

	@Override
	public void accept(ExprVisitor visitor) {
		visitor.visitExpr(this);
	}

	@Override
	public void accept(BlockVisitor visitor) {
		visitor.visitLabelDecl(this);
	}
	
	@Override
	public boolean equals(IToken token) {
		return getId() == token.getId() && lexeme.equals(token.getName());
	}
	
	@Override
	public boolean equals(String lexeme) {
		return this.lexeme.equals(lexeme);
	}
	
	@Override
	public int compareTo(IToken token) {
		String name = token.getName();
		if (name != null && getId() == token.getId())
			return lexeme.compareTo(name);
		else
			return getId() - token.getId();
	}
	
	@Override
	public String getName() {
		return lexeme;
	}

	@Override
	public Lexeme getLexeme() {
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof String) return equals(o.toString());
		else if (o instanceof IToken) return equals((IToken) o);
		return false;
	}

	@Override
	public int hashCode() {
		return lexeme.hashCode();
	}

	@Override
	public String toString() {
		return (lexeme != null) ? lexeme : Id.toString(getId());
	}

	public static boolean isLiteral(Lexeme lexeme) {
		// FIXME will not work!
		return (lexeme.getId() & Id.$$LITE) != 0; 
	}
	
}
