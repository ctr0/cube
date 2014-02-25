package c3c.ast;

import java.util.Iterator;

import c3c.ast.IStatement.AssignStat;
import c3c.ast.IStatement.CallStat;
import c3c.ast.IStatement.NewStat;
import c3c.ast.IStatement.UnaryStat;
import c3c.ast.InstrStat.InstrBlockStat;
import c3c.ast.cpp.TypeDefDecl;
import c3c.ast.visitor.BlockVisitor;
import c3c.ast.visitor.ExprVisitor.BinaryExprVisitor;
import c3c.ast.visitor.ExprVisitor.CallExprVisitor;
import c3c.ast.visitor.ExprVisitor.NewExprVisitor;
import c3c.ast.visitor.ExprVisitor.UnaryExprVisitor;
import c3c.ast.visitor.ScopeVisitor;
import c3c.ast.visitor.ScopeVisitor.RecordVisitor;
import c3c.cube.CodeSource;
import c3c.cube.CubeLang.internal;
import c3c.cube.IToken;
import c3c.cube.Lexeme;
import c3c.cube.List;
import c3c.cube.SrcPos;


public interface IScope {
	
	public IScope getScope();
	
	public Project getProject();
	
	public void setScope(IScope scope);
	
	public Node resolve(String name, SrcPos location);
	
	public Iterator<UsingDecl> getUsingDecls();
	
//	public int getId();
	
	public static class Scope implements IScope, ScopeVisitor {
		
		@internal public List<Decl> decls;
		
		private List<UsingDecl> usings;
		
		private IScope scope;
		
		/*internal*/ public Scope() {
			decls = new List<Decl>();
			usings = new List<UsingDecl>();
		}
		
		public IScope getScope() {
			return scope;
		}

		@Override
		public Project getProject() {
			return scope.getProject();
		}

		@Override
		public void setScope(IScope scope) {
			this.scope = scope;
		}

		@Override
		public Iterator<UsingDecl> getUsingDecls() {
			return usings.iterator();
		}

		public void accept(ScopeVisitor visitor) {
			for (Decl decl : decls) {
				decl.accept(visitor);
			}
			visitor.visitEnd();
		}

		@Override
		public Node resolve(String name, SrcPos location) {
			return decls.get(name);
		}
		
		public Decl get(Decl decl) {
			return decls.get(decl);
		}

		public Iterator<UsingDecl> getUsings() {
			return usings.iterator();
		}

		@Override
		public void visitUsingDecl(UsingDecl decl) {
			decl.setScope(this);
			decls.add(decl);
			usings.add(decl);
		}

		@Override
		public NsVisitor visitNamespaceDecl(NsDecl decl) {
			decl.setScope(this);
			decls.add(decl);
			return decl;
		}

		@Override
		public void visitVarDecl(VarDecl decl) {
			decl.setScope(this);
			decls.add(decl);
		}

		@Override
		public BlockVisitor visitMethodDecl(MethodDecl decl) {
			decl.setScope(this);
			decls.add(decl);
			return decl;
		}

		@Override
		public RecordVisitor visitRecordDecl(RecordDecl decl) {
			decl.setScope(this);
			decls.add(decl);
			return decl;
		}

		@Override
		public void visitTypeDefDecl(TypeDefDecl decl) {
			decl.setScope(this);
			decls.add(decl);
		}

		@Override
		public void visitEnd() {}

	}
	
	public static abstract class ScopeDecl extends Decl implements IScope, ScopeVisitor {
		
		Scope body;
		
		public @internal ScopeDecl(IToken token, CodeSource source, List<Annotation> annos) {
			super(token, source, annos);
		}

		@Override
		public Project getProject() {
			return getScope().getProject();
		}
		
		@Override
		public Iterator<UsingDecl> getUsingDecls() {
			return body.getUsingDecls();
		}

		@Override
		public Node resolve(String name, SrcPos location) {
			return body.resolve(name, location);
		}
		
		@Override
		public NsVisitor visitNamespaceDecl(NsDecl decl) {
//			decl.setCodeSource(getCodeSource());
			body.visitNamespaceDecl(decl);
			decl.setScope(this);
			return decl;
		}

		@Override
		public void visitUsingDecl(UsingDecl decl) {
//			decl.setCodeSource(getCodeSource());
			body.visitUsingDecl(decl);
			decl.setScope(this);
		}

		public void visitVarDecl(VarDecl decl) {
//			decl.setCodeSource(getCodeSource());
			body.visitVarDecl(decl);
			decl.setScope(this);
		}

		public BlockVisitor visitMethodDecl(MethodDecl decl) {
//			decl.setCodeSource(getCodeSource());
			BlockVisitor v = body.visitMethodDecl(decl);
			decl.setScope(this);
			return v;
		}

		public RecordVisitor visitRecordDecl(RecordDecl decl) {
//			decl.setCodeSource(getCodeSource());
			RecordVisitor v = body.visitRecordDecl(decl);
			decl.setScope(this);
			return v;
		}

		public void visitTypeDefDecl(TypeDefDecl decl) {
//			decl.setCodeSource(getCodeSource());
			body.visitTypeDefDecl(decl);
			decl.setScope(this);
		}

		public void visitEnd() {
			body.visitEnd();
		}
	}

	public static class Block extends Scope implements BlockVisitor {
		
		private List<IStatement> stats;
		
		public Block() {
			stats = new List<IStatement>();
		}

		public void accept(BlockVisitor visitor) {
			for (IStatement stat : stats) {
				stat.toString();
				stat.accept(visitor);
		
			}
			visitor.visitEnd();
		}

		@Override
		public void visitUsingDecl(UsingDecl decl) {
			stats.add(decl);
			super.visitUsingDecl(decl);
		}

		@Override
		public void visitLabelDecl(Lexeme lexeme) {
			stats.add(lexeme);
		}

		@Override
		public void visitVarDecl(VarDecl decl) {
			stats.add(decl);
			super.visitVarDecl(decl);
		}
		
		@Override
		public UnaryExprVisitor visitStatement(UnaryStat stat) {
			stats.add(stat);
			stat.setScope(this);
			return stat;
		}

		@Override
		public BinaryExprVisitor visitStatement(AssignStat stat) {
			stats.add(stat);
			stat.setScope(this);
			return stat;
		}

		@Override
		public CallExprVisitor visitStatement(CallStat stat) {
			stats.add(stat);
			stat.setScope(this);
			return stat;
		}
		
		@Override
		public NewExprVisitor visitStatement(NewStat stat) {
			stats.add(stat);
			stat.setScope(this);
			return stat;
		}

		@Override
		public void visitStatement(InstrStat stat) {
			stat.setScope(this);
			stats.add(stat);
		}

		@Override
		public InstrStatVisitor visitStatement(InstrBlockStat stat) {
			stat.setScope(this);
			stats.add(stat);
			return stat;
		}

		@Override
		public BlockStatVisitor visitStatement(BlockStat stat) {
			stat.setScope(this);
			stats.add(stat);
			return stat;
		}

		@Override
		public RecordVisitor visitRecordDecl(RecordDecl decl) {
			stats.add(decl);
			return super.visitRecordDecl(decl);
		}

		@Override
		public void visitTypeDefDecl(TypeDefDecl decl) {
			stats.add(decl);
			super.visitTypeDefDecl(decl);
		}
	}
	
	public static abstract class BlockDecl extends Decl implements IScope, BlockVisitor {
		
		Block body;

		public BlockDecl(IToken token, CodeSource source, List<Annotation> annos) {
			super(token, source, annos);
		}
		
		@Override
		public Project getProject() {
			return getScope().getProject();
		}
		
		@Override
		public Iterator<UsingDecl> getUsingDecls() {
			if (body != null) {
				return body.getUsingDecls();
			}
			return null;
		}

		@Override
		public Node resolve(String name, SrcPos location) {
			if (body != null) {
				return body.resolve(name, location);
			}
			return null;
		}

		@Override
		public void visitUsingDecl(UsingDecl decl) {
//			decl.setCodeSource(getCodeSource());
			body.visitUsingDecl(decl);
			decl.setScope(this);
		}

		@Override
		public void visitLabelDecl(Lexeme lexeme) {
			body.visitLabelDecl(lexeme);
		}

		@Override
		public void visitVarDecl(VarDecl decl) {
//			decl.setCodeSource(getCodeSource());
			body.visitVarDecl(decl);
			decl.setScope(this);
		}

		@Override
		public UnaryExprVisitor visitStatement(UnaryStat stat) {
			stat.setCodeSource(getCodeSource());
			body.visitStatement(stat);
			stat.setScope(this);
			return stat;
		}

		@Override
		public BinaryExprVisitor visitStatement(AssignStat stat) {
			stat.setCodeSource(getCodeSource());
			BinaryExprVisitor v = body.visitStatement(stat);
			stat.setScope(this);
			return v;
		}

		@Override
		public CallExprVisitor visitStatement(CallStat stat) {
			stat.setCodeSource(getCodeSource());
			CallExprVisitor v = body.visitStatement(stat);
			stat.setScope(this);
			return v;
		}

		@Override
		public NewExprVisitor visitStatement(NewStat stat) {
			stat.setCodeSource(getCodeSource());
			NewExprVisitor v = body.visitStatement(stat);
			stat.setScope(this);
			return v;
		}

		@Override
		public void visitStatement(InstrStat stat) {
//			stat.setCodeSource(getCodeSource());
			body.visitStatement(stat);
			stat.setScope(this);
		}

		@Override
		public InstrStatVisitor visitStatement(InstrBlockStat stat) {
			InstrStatVisitor v = body.visitStatement(stat);
			stat.setScope(this);
			return v;
		}

		@Override
		public BlockStatVisitor visitStatement(BlockStat stat) {
			BlockStatVisitor v = body.visitStatement(stat);
			stat.setScope(this);
			return v;
		}

		@Override
		public RecordVisitor visitRecordDecl(RecordDecl decl) {
			RecordVisitor v = body.visitRecordDecl(decl);
			decl.setScope(this);
			return v;
		}

		@Override
		public void visitTypeDefDecl(TypeDefDecl decl) {
			body.visitTypeDefDecl(decl);
			decl.setScope(this);
		}
		
		@Override
		public void visitEnd() {
			body.visitEnd();
		}
	}
}