package c3c.ast.delegate;

import c3c.ast.BlockStat;
import c3c.ast.IStatement.AssignStat;
import c3c.ast.IStatement.CallStat;
import c3c.ast.IStatement.NewStat;
import c3c.ast.IStatement.UnaryStat;
import c3c.ast.InstrStat;
import c3c.ast.InstrStat.InstrBlockStat;
import c3c.ast.MethodDecl;
import c3c.ast.RecordDecl;
import c3c.ast.UsingDecl;
import c3c.ast.VarDecl;
import c3c.ast.cpp.TypeDefDecl;
import c3c.ast.delegate.RecordDelegate.RecordTrivial;
import c3c.ast.visitor.BlockVisitor;
import c3c.ast.visitor.ExprVisitor.BinaryExprVisitor;
import c3c.ast.visitor.ExprVisitor.CallExprVisitor;
import c3c.ast.visitor.ExprVisitor.NewExprVisitor;
import c3c.ast.visitor.ExprVisitor.UnaryExprVisitor;
import c3c.cube.Lexeme;

public class BlockDelegate implements BlockVisitor {

	private BlockDelegate delegate;
	
	BlockDelegate() {}
	
	public BlockDelegate(BlockDelegate delegate) {
		this.delegate = delegate;
	}

	public void visitUsingDecl(UsingDecl decl) {
		delegate.visitUsingDecl(decl);
	}

	public BlockDelegate visitMethodDecl(MethodDecl decl) {
		return delegate.visitMethodDecl(decl);
	}

	public RecordDelegate visitRecordDecl(RecordDecl decl) {
		return delegate.visitRecordDecl(decl);
	}

	public void visitLabelDecl(Lexeme lexeme) {
		delegate.visitLabelDecl(lexeme);
	}

	public void visitTypeDefDecl(TypeDefDecl decl) {
		delegate.visitTypeDefDecl(decl);
	}

	public void visitVarDecl(VarDecl decl) {
		delegate.visitVarDecl(decl);
	}

	public void visitEnd() {
		delegate.visitEnd();
	}

	public CallExprVisitor visitStatement(CallStat stat) {
		return delegate.visitStatement(stat);
	}

	public UnaryExprVisitor visitStatement(UnaryStat stat) {
		return delegate.visitStatement(stat);
	}

	public BinaryExprVisitor visitStatement(AssignStat stat) {
		return delegate.visitStatement(stat);
	}

	@Override
	public NewExprVisitor visitStatement(NewStat stat) {
		return delegate.visitStatement(stat);
	}

	public BlockStatVisitor visitStatement(BlockStat stat) {
		return delegate.visitStatement(stat);
	}

	@Override
	public void visitStatement(InstrStat stat) {
		delegate.visitStatement(stat);
	}

//	public BlockVisitor visitBlock(Block block) {
//		return delegate.visitBlock(block);
//	}
//
//	public void visitExpr(Lexeme expr) {
//		delegate.visitExpr(expr);
//	}
//
//	public void visitExpr(LexExpr expr) {
//		delegate.visitExpr(expr);
//	}

//	public CallExprVisitor visitExpr(CallExpr expr) {
//		return delegate.visitExpr(expr);
//	}
//
//	public UnaryExprVisitor visitExpr(UnaryExpr expr) {
//		return delegate.visitExpr(expr);
//	}

	public InstrStatVisitor visitStatement(InstrBlockStat stat) {
		return delegate.visitStatement(stat);
	}

//	public BinaryExprVisitor visitExpr(BinaryExpr expr) {
//		return delegate.visitExpr(expr);
//	}
//
//	public TernaryExprVisitor visitExpr(TernaryExpr expr) {
//		return delegate.visitExpr(expr);
//	}
//
//	public CastExprVisitor visitExpr(CastExpr expr) {
//		return delegate.visitExpr(expr);
//	}
	
	static class BlockTrivial extends BlockDelegate implements BlockStatVisitor, InstrStatVisitor {

		public BlockTrivial(MethodDecl decl) {
			super();
		}
		@Override
		public void visitUsingDecl(UsingDecl decl) {}
		@Override
		public BlockDelegate visitMethodDecl(MethodDecl decl) {
			return new BlockTrivial(decl);
		}
		@Override
		public RecordDelegate visitRecordDecl(RecordDecl decl) {
			return new RecordTrivial(decl);
		}
		@Override
		public void visitLabelDecl(Lexeme lexeme) {}

		@Override
		public void visitTypeDefDecl(TypeDefDecl decl) {}

		@Override
		public void visitVarDecl(VarDecl decl) {}

		@Override
		public void visitEnd() {}

		@Override
		public CallExprVisitor visitStatement(CallStat stat) {
			return null;
		}

		@Override
		public UnaryExprVisitor visitStatement(UnaryStat stat) {
			return null;
		}

		@Override
		public BinaryExprVisitor visitStatement(AssignStat stat) {
			return null;
		}

		@Override
		public NewExprVisitor visitStatement(NewStat stat) {
			return null;
		}
		
		@Override
		public BlockStatVisitor visitStatement(BlockStat stat) {
			return new BlockTrivial(null);
		}

		@Override
		public void visitStatement(InstrStat stat) {}

		@Override
		public InstrStatVisitor visitStatement(InstrBlockStat stat) {
			return new BlockTrivial(null);
		}
	}
	
}
