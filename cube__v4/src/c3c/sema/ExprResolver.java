package c3c.sema;

import c3c.ast.BinaryExpr;
import c3c.ast.CallExpr;
import c3c.ast.CastExpr;
import c3c.ast.Decl;
import c3c.ast.IScope;
import c3c.ast.IType.Type;
import c3c.ast.LexExpr;
import c3c.ast.NewExpr;
import c3c.ast.TernaryExpr;
import c3c.ast.UnaryExpr;
import c3c.ast.visitor.ExprVisitor;
import c3c.ast.visitor.ExprVisitor.BinaryExprVisitor;
import c3c.ast.visitor.ExprVisitor.CallExprVisitor;
import c3c.ast.visitor.ExprVisitor.CastExprVisitor;
import c3c.ast.visitor.ExprVisitor.NewExprVisitor;
import c3c.ast.visitor.ExprVisitor.TernaryExprVisitor;
import c3c.ast.visitor.ExprVisitor.UnaryExprVisitor;
import c3c.cube.CodeSource;
import c3c.cube.Lexeme;
import c3c.cube.Report;

public class ExprResolver implements ExprVisitor, 
		CallExprVisitor, CastExprVisitor, NewExprVisitor, 
		UnaryExprVisitor, BinaryExprVisitor, TernaryExprVisitor {
	
	private Resolver resolver;
	private IScope scope;
	private CodeSource source;

	public ExprResolver(Resolver resolver, IScope scope, CodeSource source) {
		this.resolver = resolver;
		this.scope = scope;
		this.source = source;
	}
	
	@Override
	public void visitExpr(Lexeme expr) {
		if (!Lexeme.isLiteral(expr)) {
//			Decl decl = resolver.resolve(scope, expr.getName());
//			if (decl != null) expr.setDecl(decl);
//			else Report.message(Report._1_ERR_NOT_FOUND, source, expr.getToken());
		}
	}

	@Override
	public void visitExpr(LexExpr expr) {
		if (!expr.isLiteral()) {
			Decl decl = resolver.resolve(
				scope, expr.getName(), expr.getLocation()
			);
			if (decl == null) {
				Report.message(Report._1_ERR_NOT_FOUND, source, expr.getToken());
				return;
			}
			expr.setDecl(decl);
	}	}

	@Override
	public CallExprVisitor visitExpr(CallExpr expr) {
		return this;
	}

	@Override
	public UnaryExprVisitor visitExpr(UnaryExpr expr) {
		return this;
	}

	@Override
	public BinaryExprVisitor visitExpr(BinaryExpr expr) {
		return this;
	}

	@Override
	public TernaryExprVisitor visitExpr(TernaryExpr expr) {
		return this;
	}

	@Override
	public CastExprVisitor visitExpr(CastExpr expr) {
		return this;
	}

	@Override
	public NewExprVisitor visitExpr(NewExpr expr) {
		return this;
	}

	@Override
	public void visitExpr(Type type) {
		resolver.resolveType(source, scope, type);
	}

}
