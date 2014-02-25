package c3c.gen;

import c3c.ast.BinaryExpr;
import c3c.ast.BlockStat;
import c3c.ast.CallExpr;
import c3c.ast.CastExpr;
import c3c.ast.ComplUnit;
import c3c.ast.IStatement.AssignStat;
import c3c.ast.IStatement.CallStat;
import c3c.ast.IStatement.NewStat;
import c3c.ast.IStatement.UnaryStat;
import c3c.ast.InstrStat;
import c3c.ast.InstrStat.InstrBlockStat;
import c3c.ast.LexExpr;
import c3c.ast.MethodDecl;
import c3c.ast.NewExpr;
import c3c.ast.NsDecl;
import c3c.ast.RecordDecl;
import c3c.ast.TernaryExpr;
import c3c.ast.UnaryExpr;
import c3c.ast.UsingDecl;
import c3c.ast.VarDecl;
import c3c.ast.cpp.TypeDefDecl;
import c3c.ast.visitor.BlockVisitor;
import c3c.ast.visitor.ExprVisitor;
import c3c.ast.visitor.ExprVisitor.BinaryExprVisitor;
import c3c.ast.visitor.ExprVisitor.CallExprVisitor;
import c3c.ast.visitor.ExprVisitor.NewExprVisitor;
import c3c.ast.visitor.ExprVisitor.TernaryExprVisitor;
import c3c.ast.visitor.ExprVisitor.UnaryExprVisitor;
import c3c.ast.visitor.ProjectVisitor;
import c3c.ast.visitor.ScopeVisitor.NsVisitor;
import c3c.ast.visitor.ScopeVisitor.RecordVisitor;
import c3c.cube.Lexeme;

public class CxxCodeGen implements ProjectVisitor {
	
	private String output;
	
	private Writer hxx;
	private Writer cxx;
	
	public CxxCodeGen(String output) {
		this.output = output;
	}
	
	public void visitComplUnit(ComplUnit unit) {
		String name = unit.getSource().getFile().getName();
		hxx = Writer.getWriter(output + name + ".h");
//		cxx = Writer.getWriter(output + name + ".cxx");
		if (hxx != null && cxx != null) {
			unit.accept(
				new HxxGen(
					hxx/*, new CxxGen(cxx, unit)*/
				)
			);
			hxx.close();
			cxx.close();
		}
	}
	
	private static class HxxGen extends NsGen {

		public HxxGen(Writer writer) {
			super(writer);
			writer.createFile();
		}

		@Override
		public void visitEnd() {}
		
	}
	
	private static class NsGen implements NsVisitor {

		protected final Writer w;

		public NsGen(Writer writer) {
			this.w = writer;
		}
		
		@Override
		public void visitUsingDecl(UsingDecl decl) {
			w.writeIdent();
			w.writeUsingDecl(decl);
			w.write(";\n");
		}
		
		@Override
		public NsVisitor visitNamespaceDecl(NsDecl decl) {
			w.writeNsDecl(decl);
			return this;
		}

		@Override
		public void visitVarDecl(VarDecl decl) {
			w.writeIdent();
			w.writeVarDecl(decl);
			w.write(';');
		}

		@Override
		public BlockVisitor visitMethodDecl(MethodDecl decl) {
			w.writeIdent();
			w.writeMethodDecl(decl);
			if (decl.hasBody()) {
				w.write(" {\n");
				w.incIdent();
				return new BlockGen(w);
			} else {
				w.write(";\n");
				return null;
			}
		}

		@Override
		public RecordVisitor visitRecordDecl(RecordDecl decl) {
			w.writeIdent();
			w.writeRecordDecl(decl);
			if (decl.hasBody()) {
				w.write(" {\n");
				w.incIdent();
				return new RecordGen(w);
			} else {
				w.write(";\n");
				return null;
			}
		}

		@Override
		public void visitTypeDefDecl(TypeDefDecl decl) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visitEnd() {
			w.decIdent();
			w.writeIdent();
			w.write("}\n");
		}
	}
	
	private static class RecordGen extends NsGen implements RecordVisitor {

		public RecordGen(Writer writer) {
			super(writer);
		}
		
	}
	
	private static class BlockGen extends NsGen implements BlockVisitor {

		public BlockGen(Writer writer) {
			super(writer);
		}

		@Override
		public void visitLabelDecl(Lexeme lexeme) {
			w.writeIdent();
			w.write(lexeme.toString());
			w.write(":\n");
		}

		@Override
		public UnaryExprVisitor visitStatement(UnaryStat stat) {
			return new ExprGen(w);
		}

		@Override
		public BinaryExprVisitor visitStatement(AssignStat stat) {
			return new ExprGen(w);
		}

		@Override
		public CallExprVisitor visitStatement(CallStat stat) {
			return new ExprGen(w);
		}

		@Override
		public NewExprVisitor visitStatement(NewStat stat) {
			return new ExprGen(w);
		}

		@Override
		public void visitStatement(InstrStat stat) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public InstrStatVisitor visitStatement(InstrBlockStat stat) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public BlockStatVisitor visitStatement(BlockStat stat) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

	private static class ExprGen implements ExprVisitor, 
	UnaryExprVisitor, BinaryExprVisitor, TernaryExprVisitor, CallExprVisitor, NewExprVisitor {
		
		private Writer w;
		
		public ExprGen(Writer writer) {
			this.w = writer;
		}

		@Override
		public void visitExpr(Lexeme expr) {
			w.write(expr.getName());
		}

		@Override
		public void visitExpr(LexExpr expr) {
			w.write(expr.getName());
		}

		@Override
		public CallExprVisitor visitExpr(CallExpr expr) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public UnaryExprVisitor visitExpr(UnaryExpr expr) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public BinaryExprVisitor visitExpr(BinaryExpr expr) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public TernaryExprVisitor visitExpr(TernaryExpr expr) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CastExprVisitor visitExpr(CastExpr expr) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public NewExprVisitor visitExpr(NewExpr expr) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
