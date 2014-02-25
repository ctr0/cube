package c3c.gen;

import c3c.ast.ComplUnit;
import c3c.ast.IExpr;
import c3c.ast.IType.Type;
import c3c.ast.MethodDecl;
import c3c.ast.NsDecl;
import c3c.ast.RecordDecl;
import c3c.ast.UsingDecl;
import c3c.ast.VarDecl;
import c3c.ast.cpp.TypeDefDecl;
import c3c.ast.delegate.BlockDelegate;
import c3c.ast.delegate.DelegateBuilder;
import c3c.ast.delegate.NsDelegate;
import c3c.ast.delegate.RecordDelegate;
import c3c.ast.visitor.ExprVisitor;
import c3c.ast.visitor.ProjectVisitor;
import c3c.cube.IToken;
import c3c.cube.Id;
import c3c.cube.Lexeme;
import c3c.cube.Modifiers;

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
		cxx = Writer.getWriter(output + name + ".cxx");
		if (hxx != null && cxx != null) {
			unit.accept(
				new HxxGen(
					hxx, new CxxGen(
						cxx, DelegateBuilder.wrap(unit)
					)
				)
			);
			hxx.close();
			cxx.close();
		}
	}
	
	private static class HxxGen extends NsGen {

		public HxxGen(Writer writer, DelegateBuilder delegate) {
			super(writer, delegate);
			writer.createFile();
		}
		
		

		@Override
		public void visitEnd() {}
		
	}
	
	private static class CxxGen extends DelegateBuilder {
		
		protected final Writer w;
		private boolean initialized = false;

		public CxxGen(Writer writer, DelegateBuilder delegate) {
			super(delegate);
			this.w = writer;
		}
		
	}
	
	private static class NsGen extends NsDelegate {

		protected final Writer w;

		public NsGen(Writer writer, DelegateBuilder delegate) {
			super(delegate);
			this.w = writer;
		}
		
		@Override
		public void visitUsingDecl(UsingDecl decl) {
			w.writeIdent();
			w.write("using namespace ");
			w.writeCannonical(decl.getToken());
			w.write(";\n");
		}
		
		@Override
		public NsDelegate visitNamespaceDecl(NsDecl decl) {
			IToken token = decl.getToken();
			if (token.isComplex()) {
				for (Lexeme lexeme : token.getLexemes()) {
					w.writeNewLine();
					w.writeIdent();
					w.write("namespace ");
					w.write(lexeme);
					w.write(" {");
					w.incIdent();
				}
			}
			return this;
		}

		@Override
		public void visitVarDecl(VarDecl decl) {
			Type type = decl.getType();
			if (type != null) {
				w.write(Modifiers.mask(
					type.getModifiers(), 
					Id.PUBLIC|Id.PRIVATE|Id.PROTECTED|Id.INTERNAL|Id.UNSAFE|Id.STATIC
				));
				w.writeSpace();
				w.writeCannonical(type.getToken());
				w.writeSpace();
				w.write(decl.getName());
				IExpr init = decl.getInitializer();
				if (init != null) {
					w.write(init);
				} else w.write(';');
			}
		}

		@Override
		public BlockDelegate visitMethodDecl(MethodDecl decl) {
			// TODO Auto-generated method stub
			return super.visitMethodDecl(decl);
		}

		@Override
		public RecordDelegate visitRecordDecl(RecordDecl decl) {
			// TODO Auto-generated method stub
			return super.visitRecordDecl(decl);
		}

		@Override
		public void visitTypeDefDecl(TypeDefDecl decl) {
			// TODO Auto-generated method stub
			super.visitTypeDefDecl(decl);
		}

		@Override
		public void visitEnd() {
			// TODO Auto-generated method stub
			super.visitEnd();
		}
	}

	private static class ExprGen implements ExprVisitor {
		
	}
}
