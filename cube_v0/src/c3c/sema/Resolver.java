package c3c.sema;

import java.util.Iterator;

import c3c.ast.BlockStat;
import c3c.ast.Decl;
import c3c.ast.IExpr;
import c3c.ast.IScope;
import c3c.ast.IStatement.AssignStat;
import c3c.ast.IStatement.CallStat;
import c3c.ast.IStatement.UnaryStat;
import c3c.ast.IType.ITypeDecl;
import c3c.ast.IType.TemplTypeParam;
import c3c.ast.IType.Type;
import c3c.ast.InstrStat;
import c3c.ast.InstrStat.InstrBlockStat;
import c3c.ast.MethodDecl;
import c3c.ast.Namespace;
import c3c.ast.NsDecl;
import c3c.ast.RecordDecl;
import c3c.ast.Template;
import c3c.ast.UsingDecl;
import c3c.ast.VarDecl;
import c3c.ast.cpp.TypeDefDecl;
import c3c.ast.delegate.BlockDelegate;
import c3c.ast.delegate.RecordDelegate;
import c3c.ast.visitor.BlockVisitor;
import c3c.ast.visitor.ExprVisitor.BinaryExprVisitor;
import c3c.ast.visitor.ExprVisitor.CallExprVisitor;
import c3c.ast.visitor.ExprVisitor.UnaryExprVisitor;
import c3c.ast.visitor.ScopeVisitor;
import c3c.cube.CodeSource;
import c3c.cube.IToken;
import c3c.cube.Lexeme;
import c3c.cube.Report;
import c3c.cube.SrcPos;

public class Resolver implements ScopeVisitor, BlockVisitor {	
	
	@Override
	public void visitUsingDecl(UsingDecl decl) {
		resolveUsingDecl(decl);
	}
	
	public void resolveUsingDecl(UsingDecl decl) {
		if (decl.isExplicit()) {
			Namespace ns = decl.getScope().getProject().getGlobalNamespace();
			IToken token = decl.getToken();
			if (token.isComplex()) {
				Lexeme[] lexemes = token.getLexemes();
				for (Lexeme lexeme : lexemes) {
					ns = ns.getNamespace(lexeme.getName());
					if (ns == null) {
						SrcPos location = new SrcPos(lexeme.getLocation());
						location.add(token.getLexeme().getLocation());
						Report.message("NS NOT FOUND", decl, location);
						return;
					}
				}
			} else {
				ns = ns.getNamespace(token.getName());
				if (ns == null) {
					Report.message("NS NOT FOUND", decl);
					return;
			}	}
			decl.setNamespace(ns);
		} else {
			assert false; // TODO C++
		}
	}

	@Override
	public NsVisitor visitNamespaceDecl(NsDecl decl) {
		return null;
	}

	@Override
	public void visitVarDecl(VarDecl decl) {
		if (!decl.resolved()) {
			resolveVarDecl(decl);
	}	}
	
	public void resolveVarDecl(VarDecl decl) {
		IScope scope = decl.getScope();
		CodeSource source = decl.getCodeSource();
		if (resolveType(source, scope, decl.getType())) {
			IExpr init = decl.getInitializer();
			if (init != null) {
				init.accept(
					/*static*/ new ExprResolver(this, scope, source)
				);
	}	}	}
	
	public void resolveLocalVarDecl(VarDecl decl) {
		IScope scope = decl.getScope();
		CodeSource source = decl.getCodeSource();
		if (resolveType(source, scope, decl.getType())) {
			IExpr init = decl.getInitializer();
			if (init != null) {
				init.accept(
					/*static*/ new ExprResolver(this, scope, source)
				);
	}	}	}

	@Override
	public BlockDelegate visitMethodDecl(MethodDecl decl) {
		if (!decl.resolved()) {
			resolveMethodDecl(decl);
		}
		return null;
	}
	
	public void resolveMethodDecl(MethodDecl decl) {
		CodeSource source = decl.getCodeSource();
		IScope scope = decl.getScope();
		Type type = decl.getType();
		if (type != null) {
			resolveType(source, scope, type);
		}
		if (decl.isGeneric()) {
			resolveTemplDecl(
				source, scope, decl.getTemplDecl()
			);
		}
		Iterator<VarDecl> it = decl.getArgDecls();
		while(it.hasNext()) {
			resolveVarDecl(it.next());
	}	}

	@Override
	public RecordDelegate visitRecordDecl(RecordDecl decl) {
		if (!decl.resolved()) {
			resolveRecordDecl(decl);
		}
		return null;
	}
	
	public void resolveRecordDecl(RecordDecl decl) {
		CodeSource source = decl.getCodeSource();
		IScope scope = decl.getScope();
		if (decl.isGeneric()) {
			resolveTemplDecl(
				source, scope, decl.getTemplDecl()
			);
		}
		Iterator<Type> it = decl.getBaseDecls();
		while (it.hasNext()) {
			resolveType(
				source, scope, it.next()
			);
	}	}
	
	@Override
	public void visitTypeDefDecl(TypeDefDecl decl) {
		if (!decl.resolved()) {
			resolveTypeDefDecl(decl);
	}	}
	
	public void resolveTypeDefDecl(TypeDefDecl decl) {
		CodeSource source = decl.getCodeSource();
		IScope scope = decl.getScope();
		if (resolveType(source, scope, decl.getType())) {
			if (decl.isGeneric()) {
				resolveTemplDecl(source, scope, decl.getTemplDecl());
	}	}	}

	@Override
	public void visitLabelDecl(Lexeme lexeme) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public UnaryExprVisitor visitStatement(UnaryStat stat) {
//		stat.getExpr().accept(
//			/*static*/ new ExprResolver(this, stat.getScope(), stat.getCodeSource())
//		);
//		return null;
		return new ExprResolver(this, stat.getScope(), stat.getCodeSource());
	}

	@Override
	public BinaryExprVisitor visitStatement(AssignStat stat) {
		return new ExprResolver(this, stat.getScope(), stat.getCodeSource());
	}

	@Override
	public CallExprVisitor visitStatement(CallStat stat) {
		return new ExprResolver(this, stat.getScope(), stat.getCodeSource());
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
	
	@Override
	public void visitEnd() {}
	
	public Decl resolve(IScope scope, String name) {
		Decl decl;
		do {
			decl = scope.resolve(name, null);
		} while (	
			decl != null && 
			(scope = scope.getScope()) != null
		);
		return decl;
	}
	
	public Decl resolve(IScope scope, String name, SrcPos location) {
		Decl decl;
		do {
			decl = scope.resolve(name, location);
		} while (	
			decl != null && 
			(scope = scope.getScope()) != null
		);
		return decl;
	}
	
	public void resolveTemplDecl(CodeSource source, IScope scope, Template decl) {
		Iterator<TemplTypeParam> itt = decl.getTypeParams();
		while (itt.hasNext()) {
			TemplTypeParam next = itt.next();
//			resolveType(scope, next.getName());
			if (next.hasConstraint()) {
				resolveType(source, scope, next.getConstraint());
			}
		}
		Iterator<VarDecl> itv = decl.getVarDecls();
		while (itv.hasNext()) {
			// FIXME type of var decl defined in templ decl ??
			resolveVarDecl(itv.next());
		}
	}

	public boolean resolveType(CodeSource source, IScope scope, Type type) {
		if (!type.isPrimitive()) {
			String name = type.getToken().toString(); // TODO complex lexeme?
			ITypeDecl decl = resolveType(scope, name);
			if (decl == null) {
				Report.message(Report._1_ERR_TYPE_NOT_FOUND, source, type.getToken());
				return false;
			}
			type.setDecl(decl);
		}
		return true;
	}

	private NsDecl resolveNs(IScope scope, String name) {
		Decl d;
		do {
			d = scope.resolve(name, null);
			if (d != null && d instanceof NsDecl) {
				return (NsDecl) d;
			}
		} while ((scope = scope.getScope()) != null);
		return null;
	}
	
	private ITypeDecl resolveType(IScope scope, String name) {
		Decl d;
		do {
			d = scope.resolve(name, null);
			if (d != null && d instanceof ITypeDecl) {
				ITypeDecl decl = (ITypeDecl) d;
				decl.accept(this);
				return decl;
			}
		} while ((scope = scope.getScope()) != null);
		return null;
	}
	
	private VarDecl resolveVar(IScope scope, String name) {
		Decl d;
		do {
			d = scope.resolve(name, null);
			if (d != null && d instanceof VarDecl) {
				d.accept(this);
				VarDecl decl = (VarDecl) d;
				visitVarDecl(decl);
				return decl;
			}
		} while ((scope = scope.getScope()) != null);
		return null;
	}

}
