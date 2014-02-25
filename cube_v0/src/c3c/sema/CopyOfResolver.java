package c3c.sema;

import java.util.Iterator;

import c3c.ast.BinaryExpr;
import c3c.ast.BlockStat;
import c3c.ast.CallExpr;
import c3c.ast.CastExpr;
import c3c.ast.Decl;
import c3c.ast.IExpr;
import c3c.ast.IScope;
import c3c.ast.IType.ITypeDecl;
import c3c.ast.IType.TemplTypeParam;
import c3c.ast.IType.Type;
import c3c.ast.InstrStat;
import c3c.ast.InstrStat.InstrBlockStat;
import c3c.ast.LexExpr;
import c3c.ast.MethodDecl;
import c3c.ast.Namespace;
import c3c.ast.NsDecl;
import c3c.ast.Project;
import c3c.ast.RecordDecl;
import c3c.ast.Template;
import c3c.ast.TernaryExpr;
import c3c.ast.UnaryExpr;
import c3c.ast.UsingDecl;
import c3c.ast.VarDecl;
import c3c.ast.cpp.TypeDefDecl;
import c3c.ast.visitor.BlockVisitor;
import c3c.ast.visitor.ExprVisitor.BinaryExprVisitor;
import c3c.ast.visitor.ExprVisitor.CallExprVisitor;
import c3c.ast.visitor.ExprVisitor.CastExprVisitor;
import c3c.ast.visitor.ExprVisitor.TernaryExprVisitor;
import c3c.ast.visitor.ExprVisitor.UnaryExprVisitor;
import c3c.ast.visitor.ScopeVisitor.NsVisitor;
import c3c.cube.IToken;
import c3c.cube.Lexeme;
import c3c.cube.List;
import c3c.cube.Report;
import c3c.cube.SrcPos;

public class CopyOfResolver implements NsVisitor, BlockVisitor {	
	
	private Project project;
	
	private List<IScope> stack;
	
	public CopyOfResolver(Project project) {
		this.project = project;
	}
	
	@Override
	public void visitUsingDecl(UsingDecl decl) {
		Namespace ns = null;
		Namespace namespace = project.getGlobalNamespace();
		IToken token = decl.getToken();
		if (token.isComplex()) {
			Lexeme[] lexemes = token.getLexemes();
			for (Lexeme lexeme : lexemes) {
				ns = namespace.getNamespace(lexeme.getName());
				if (ns == null) {
					SrcPos location = new SrcPos(lexeme.getLocation());
					location.add(token.getLexeme().getLocation());
					Report.message("NS NOT FOUND", decl, location);
					return;
				}
				namespace = ns;
			}
		} else {
			ns = namespace.getNamespace(token.getName());
			if (ns == null) {
				Report.message("NS NOT FOUND", decl);
				return;
			}
		}
		decl.setNamespace(ns);
	}

	@Override
	public NsVisitor visitNamespaceDecl(NsDecl decl) {
		return null;
	}

	@Override
	public void visitVarDecl(VarDecl decl) {
		if (!decl.resolved()) {
			resolveVarDecl(decl);
		}
	}
	
	private void resolveVarDecl(VarDecl decl) {
		if (resolveType(decl.getScope(), decl.getType())) {
			IExpr init = decl.getInitializer();
			if (init != null) init.accept(
				new ExprResolver(decl.getScope())
			);
		}
	}

	@Override
	public BlockVisitor visitMethodDecl(MethodDecl decl) {
		if (!decl.resolved()) {
			resolveMethodDecl(decl);
		}
		return null;
	}
	
	private void resolveMethodDecl(MethodDecl decl) {
		IScope scope = decl.getScope();
		Type type = decl.getType();
		if (type != null) {
			resolveType(scope, type);
		}
		if (decl.isGeneric()) {
			resolveTemplDecl(scope, decl.getTemplDecl());
		}
		Iterator<VarDecl> it = decl.getArgDecls();
		while(it.hasNext()) {
			resolveVarDecl(it.next());
		}
	}

	@Override
	public RecordVisitor visitRecordDecl(RecordDecl decl) {
		if (!decl.resolved()) {
			resolveRecordDecl(decl);
		}
		return null;
	}
	
	private void resolveRecordDecl(RecordDecl decl) {
		IScope scope = decl.getScope();
		if (decl.isGeneric()) {
			resolveTemplDecl(scope, decl.getTemplDecl());
		}
		Iterator<Type> it = decl.getBaseDecls();
		while (it.hasNext()) {
			resolveType(scope, it.next());
		}
	}
	
	@Override
	public void visitTypeDefDecl(TypeDefDecl decl) {
		if (!decl.resolved()) {
			resolveTypeDefDecl(decl);
		}
	}
	
	private void resolveTypeDefDecl(TypeDefDecl decl) {
		IScope scope = decl.getScope();
		if (resolveType(scope, decl.getType())) {
			if (decl.isGeneric()) {
				resolveTemplDecl(scope, decl.getTemplDecl());
			}
		}
	}

	@Override
	public void visitLabelDecl(Lexeme lexeme) {
		// TODO Auto-generated method stub
		
	}

//	@Override
//	public CallStatVisitor visitStatement(CallStat stat) {
//		Iterator<IExpr> it = stat.getParams();
//		while (it.hasNext()) {
//			it.next().accept(
//				stat.get
//			);
//		}
//		return null;
//	}
//
//	@Override
//	public UnaryStatVisitor visitStatement(UnaryStat stat) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public AssigStatVisitor visitStatement(AssigStat stat) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public BlockStatVisitor visitStatement(BlockStat stat) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void visitEnd() {}
	
	public static Decl resolve(IScope scope, String name) {
		Decl decl;
		do {
			decl = scope.resolve(name);
		} while (	
			decl != null && 
			(scope = scope.getScope()) != null
		);
		return decl;
	}

	public static NsDecl resolveNs(IScope scope, String name) {
		Decl decl;
		do {
			decl = scope.resolve(name);
		} while (decl != null 
			&& !(decl instanceof NsDecl) 
			&& (scope = scope.getScope()) != null
		);
		return (NsDecl) decl;
	}
	
	public static ITypeDecl resolveType(IScope scope, String name) {
		Decl d;
		do {
			d = scope.resolve(name);
		} while (d != null 
			&& !(d instanceof ITypeDecl) 
			&& (scope = scope.getScope()) != null
		);
		ITypeDecl decl = (ITypeDecl) d;
		decl.accept(this);
		return decl;
	}
	
//	public VarDecl resolveVar(IScope scope, String name) {
//		Decl d;
//		do {
//			d = scope.resolve(name);
//		} while (d != null
//			&& !(d instanceof VarDecl) 
//			&& (scope = scope.getScope()) != null
//		);
//		d.accept(this);
//		VarDecl decl2 = (VarDecl) d;
//		visitVarDecl(decl2);
//		return decl2;
//	}
	
	private void resolveTemplDecl(IScope scope, Template decl) {
		Iterator<TemplTypeParam> itt = decl.getTypeParams();
		while (itt.hasNext()) {
			TemplTypeParam next = itt.next();
//			resolveType(scope, next.getName());
			if (next.hasConstraint()) {
				resolveType(scope, next.getConstraint());
			}
		}
		Iterator<VarDecl> itv = decl.getVarDecls();
		while (itv.hasNext()) {
			visitVarDecl(itv.next());
		}
	}

	static boolean resolveType(IScope scope, Type type) {
		if (!type.isPrimitive()) {
			String name = type.getToken().toString(); // TODO complex lexeme?
			ITypeDecl decl = resolveType(scope, name);
			if (decl == null) {
				Report.message(Report._1_ERR_TYPE_NOT_FOUND, type.getCodeSource(), type.getToken());
				return false;
			}
			type.setDecl(decl);
		}
		return true;
	}

	@Override
	public void visitStatement(UnaryStat stat) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitStatement(BinaryExpr stat) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitStatement(CallExpr stat) {
		// TODO Auto-generated method stub
		
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
	
//	public static NsDecl resolveNs(IGlobalScope scope, String name) {
//		if (name.indexOf('.') > -1) {
//			String[] tokens = name.split("\\.");
//			Decl decl = null;
//			IGlobalScope s = scope;
//			for (String token : tokens) {
//				decl = s.resolve(token);
//				
//				if ((decl = s.resolve(token)) == null) 
//					return null;
//				s = decl;
//			}
//			return decl;
//		}
//		return scope.getNsDecl(name);
//	}
//	
//	public static boolean resolve(IScope scope, LexExpr expr) {
//		IScope s = scope;
//		String name = expr.getName();
//		do {
//			VarDecl varDecl = scope.getVarDecl(name);
//			if (varDecl != null) {
//				expr.setDecl(varDecl);
//				return true;
//			}
//			ITypeDecl typeDecl = scope.getTypeDecl(name);
//			if (typeDecl != null) {
//				expr.setDecl(typeDecl);
//				return true;
//			}
//		} while ((s = s.getParentScope()) != null);
//		return false;
//	}
//	
//	public static boolean resolve(IGlobalScope scope, LexExpr expr) {
//		IGlobalScope s = scope;
//		String name = expr.getName();
//		do {
//			VarDecl varDecl = scope.getVarDecl(name);
//			if (varDecl != null) {
//				expr.setDecl(varDecl);
//				return true;
//			}
//			ITypeDecl typeDecl = scope.getTypeDecl(name);
//			if (typeDecl != null) {
//				expr.setDecl(typeDecl);
//				return true;
//			}
//			NsDecl nsDecl = scope.getNsDecl(name);
//			if (nsDecl != null) {
//				expr.setDecl(nsDecl);
//				return true;
//			}
//		} while ((s = s.getParentScope()) != null);
//		return false;
//	}

}
