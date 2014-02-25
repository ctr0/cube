package c3c.ast;

import c3c.ast.IScope.Block;
import c3c.ast.cpp.TypeDefDecl;
import c3c.ast.visitor.BlockVisitor;
import c3c.ast.visitor.BlockVisitor.InstrStatVisitor;
import c3c.ast.visitor.ExprVisitor.BinaryExprVisitor;
import c3c.ast.visitor.ExprVisitor.CallExprVisitor;
import c3c.ast.visitor.ExprVisitor.NewExprVisitor;
import c3c.ast.visitor.ExprVisitor.UnaryExprVisitor;
import c3c.ast.visitor.ScopeVisitor.RecordVisitor;
import c3c.cube.CubeLang.internal;
import c3c.cube.IToken;
import c3c.cube.Id;
import c3c.cube.Lexeme;
import c3c.cube.SrcPos;

public class InstrStat implements IStatement {
	
	private IToken token;

	public InstrStat(IToken token) {
		this.token = token;
	}
	
	public IExpr[] getExpressions() {
		return null;
	}
	
	public boolean hasBody() {
		return false;
	}

	@Override
	public void accept(BlockVisitor visitor) {
		visitor.visitStatement(this);
	}
	
	@Override
	public boolean isValid() {
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
	public String toString() {
		StringBuilder builder = new StringBuilder(Id.toString(getId()));
		IExpr[] exprs = getExpressions();
		if (exprs != null) for (IExpr expr : exprs) {
			builder.append(expr.toString());	
		}
		return builder.toString();
	}
	
	public static class InstrExprStat extends InstrStat {
		
		private IExpr[] exprs;

		public InstrExprStat(IToken token, IExpr expr) {
			super(token);
			this.exprs = new IExpr[] {expr};
		}

		@Override
		public IExpr[] getExpressions() {
			return exprs;
		}
	}
	
	public static class InstrBlockStat extends InstrStat implements InstrStatVisitor {
		
		private Block body;

		public InstrBlockStat(IToken token) {
			super(token);
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

		@Override
		public void visitUsingDecl(UsingDecl decl) {
			body.visitUsingDecl(decl);
		}

		@Override
		public void visitLabelDecl(Lexeme lexeme) {
			body.visitLabelDecl(lexeme);
		}

		@Override
		public void visitVarDecl(VarDecl decl) {
			body.visitVarDecl(decl);
		}

		@Override
		public UnaryExprVisitor visitStatement(UnaryStat stat) {
			return body.visitStatement(stat);
		}

		@Override
		public BinaryExprVisitor visitStatement(AssignStat stat) {
			return body.visitStatement(stat);
		}

		@Override
		public CallExprVisitor visitStatement(CallStat stat) {
			return body.visitStatement(stat);
		}

		@Override
		public NewExprVisitor visitStatement(NewStat stat) {
			return body.visitStatement(stat);
		}

		@Override
		public void visitStatement(InstrStat stat) {
			body.visitStatement(stat);
		}

		@Override
		public InstrStatVisitor visitStatement(InstrBlockStat stat) {
			return body.visitStatement(stat);
		}

		@Override
		public BlockStatVisitor visitStatement(BlockStat stat) {
			return body.visitStatement(stat);
		}

		@Override
		public RecordVisitor visitRecordDecl(RecordDecl decl) {
			return body.visitRecordDecl(decl);
		}

		@Override
		public void visitTypeDefDecl(TypeDefDecl decl) {
			body.visitTypeDefDecl(decl);
		}

		@Override
		public void visitEnd() {
			body.visitEnd();
		}
	}
	
	public static class InstrExprBlockStat extends InstrBlockStat implements InstrStatVisitor {
		
		IExpr[] exprs;

		public InstrExprBlockStat(IToken token, IExpr expr) {
			super(token);
			this.exprs = new IExpr[] {expr};
		}
		
		InstrExprBlockStat(IToken token) {
			super(token);
		}

		@Override
		public IExpr[] getExpressions() {
			return exprs;
		}
	}
	
	public static class InstrForStat extends InstrExprBlockStat implements InstrStatVisitor {
		
		private VarDecl decl;

		public InstrForStat(IToken token) {
			super(token);
		}
		
		public VarDecl getVarDecl() {
			return decl;
		}

		@internal public void setVarDecl(VarDecl decl) {
			this.decl = decl;
		}

		public IExpr[] getExpressions() {
			return exprs;
		}
		
		@internal public void setExpressions(IExpr[] exprs) {
			this.exprs = exprs;
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder(Id.toString(getId()));
			builder.append('(');
			IExpr[] exprs = getExpressions();
			if (decl != null) {
				builder.append(decl.toString());
			}
			if (exprs != null) for (IExpr expr : exprs) {
				builder.append(';');
				if (expr != null) {
					builder.append(expr.toString());
				}
			}
			builder.append(')');
			return builder.toString();
		}
	}
}
