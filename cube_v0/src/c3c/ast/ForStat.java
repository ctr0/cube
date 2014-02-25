package c3c.ast;

import c3c.ast.IScope.Block;
import c3c.ast.cpp.CxxTypeDefDeclecl;
import c3c.ast.visitor.BlockVisitor;
import c3c.ast.visitor.BlockVisitor.InstrStatVisitor;
import c3c.ast.visitor.ExprVisitor;
import c3c.ast.visitor.ScopeVisitor.NsVisitor;
import c3c.ast.visitor.ScopeVisitor.RecordVisitor;
import c3c.cube.Lexeme;
import c3c.cube.List;
import c3c.cube.SrcPos;
import c3c.cube.Token;

public class ForStat implements IStatement, InstrStatVisitor {
	
	Token token;
	
	private VarDecl decl;
	private List<IExpr> exprs;
	
	private Block body;

	public ForStat(Token token) {
		this.token = token;
	}
	
	public void addVarDecl(VarDecl decl) {
		this.decl = decl;
	}
	
	public void addExpr(IExpr expr) {
		if (exprs == null) {
			exprs = new List<IExpr>();
		}
		exprs.add(expr);
	}
	
	public VarDecl getVarDecl() {
		return decl;
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return token.isValid();
	}

	@Override
	public SrcPos getLocation() {
		return token.getLocation();
	}

	@Override
	public int getId() {
		return token.getId();
	}

	@Override
	public void setScope(IScope scope) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void accept(BlockVisitor visitor) {
		InstrStatVisitor v = visitor.visitStatement(this);
		if (v != null) {
			accept(v);
		}
	}
	
	public void accept(InstrStatVisitor visitor) {
		if (body != null) {
			body.accept(visitor);
		}
	}
	
	public void visitBlock() {
		body = new Block();
	}

	public NsVisitor visitNamespaceDecl(NsDecl decl) {
		return body.visitNamespaceDecl(decl);
	}

	public void visitUsingDecl(UsingDecl decl) {
		body.visitUsingDecl(decl);
	}

	public void visitLabelDecl(Lexeme lexeme) {
		body.visitLabelDecl(lexeme);
	}

	public BlockVisitor visitMethodDecl(MethodDecl decl) {
		return body.visitMethodDecl(decl);
	}

	public void visitVarDecl(VarDecl decl) {
		body.visitVarDecl(decl);
	}

	public ExprVisitor visitStatement(InstrStat stat) {
		return body.visitStatement(stat);
	}

	public InstrStatVisitor visitStatement(ForStat stat) {
		return body.visitStatement(stat);
	}

	public BlockStatVisitor visitStatement(BlockStat stat) {
		return body.visitStatement(stat);
	}

	public RecordVisitor visitRecordDecl(RecordDecl decl) {
		return body.visitRecordDecl(decl);
	}

	public void visitTypeDefDeCxxTypeDefDeclefDecl decl) {
		body.visitTypeDefDecl(decl);
	}

	public void visitEnd() {
		body.visitEnd();
	}

	public void visitExpr(Lexeme expr) {
		body.visitExpr(expr);
	}

	public void visitExpr(LexExpr expr) {
		body.visitExpr(expr);
	}

	public CallExprVisitor visitExpr(CallExpr expr) {
		return body.visitExpr(expr);
	}

	public UnaryExprVisitor visitExpr(UnaryExpr expr) {
		return body.visitExpr(expr);
	}

	public BinaryExprVisitor visitExpr(BinaryExpr expr) {
		return body.visitExpr(expr);
	}

	public TernaryExprVisitor visitExpr(TernaryExpr expr) {
		return body.visitExpr(expr);
	}

	public CastExprVisitor visitExpr(CastExpr expr) {
		return body.visitExpr(expr);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(token.toString());
		for (IExpr expr : expr) {
			
		}
		return builder.toString();
	}
	
	
}
