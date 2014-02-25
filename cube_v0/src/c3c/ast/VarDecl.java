package c3c.ast;

import c3c.ast.IType.ITypeDecl;
import c3c.ast.IType.Type;
import c3c.ast.visitor.BlockVisitor;
import c3c.ast.visitor.ExprVisitor;
import c3c.ast.visitor.ScopeVisitor;
import c3c.cube.CodeSource;
import c3c.cube.IToken;
import c3c.cube.Lexeme;
import c3c.cube.List;

public class VarDecl extends Decl implements IStatement, ExprVisitor {
	
	/*internal*/ public Type type;
	/*internal*/ public IExpr init;
	
	public /*internal*/ VarDecl(Type type, IToken token, CodeSource source, List<Annotation> annos) {
		super(token, source, annos);
		this.type = type;
	}
	
	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public int getModifiers() {
		return type != null ? type.getModifiers() : 0;
	}
	
	public Type getType() {
		return type;
	}
	
	public ITypeDecl getTypeDecl() {
		return type.getDecl();
	}

	public IExpr getInitializer() {
		return init;
	}

	@Override
	public void accept(ScopeVisitor visitor) {
		visitor.visitVarDecl(this);
	}

	@Override
	public void accept(BlockVisitor visitor) {
		visitor.visitVarDecl(this);
	}
	
	public void visitType(Type type) {
		this.type = type;
	}

	@Override
	public void visitExpr(Lexeme expr) {
		init = expr;
	}

	@Override
	public void visitExpr(LexExpr expr) {
		init = expr;
	}

	@Override
	public CallExprVisitor visitExpr(CallExpr expr) {
		init = expr;
		return expr;
	}

	@Override
	public UnaryExprVisitor visitExpr(UnaryExpr expr) {
		init = expr;
		return expr;
	}

	@Override
	public BinaryExprVisitor visitExpr(BinaryExpr expr) {
		init = expr;
		return expr;
	}

	@Override
	public TernaryExprVisitor visitExpr(TernaryExpr expr) {
		init = expr;
		return expr;
	}

	@Override
	public CastExprVisitor visitExpr(CastExpr expr) {
		init = expr;
		return expr;
	}

	@Override
	public boolean isValid() {
		return super.isValid() && type != null && type.isValid();
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (type != null) {
			builder.append(type.toString());
			builder.append(' ');
		}
		builder.append(super.toString());
		if (init != null) {
			builder.append(" = ");
			builder.append(init.toString());
		}
		return builder.toString();
	}

}
