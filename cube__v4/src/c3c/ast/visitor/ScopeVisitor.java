package c3c.ast.visitor;

import c3c.ast.MethodDecl;
import c3c.ast.NsDecl;
import c3c.ast.RecordDecl;
import c3c.ast.UsingDecl;
import c3c.ast.VarDecl;
import c3c.ast.cpp.TypeDefDecl;

public interface ScopeVisitor {
	
	public abstract void visitUsingDecl(UsingDecl decl);
	
	public abstract NsVisitor visitNamespaceDecl(NsDecl decl);
	
	public abstract void visitVarDecl(VarDecl decl);
	
	public abstract BlockVisitor visitMethodDecl(MethodDecl decl);
	
	public abstract RecordVisitor visitRecordDecl(RecordDecl decl);
	
	public abstract void visitTypeDefDecl(TypeDefDecl decl);
	
	public abstract void visitEnd();
	
	
	public static interface RecordVisitor extends ScopeVisitor {
		
	}
	
	public interface NsVisitor extends ScopeVisitor {
		
	}

}
