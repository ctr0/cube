package c3c.ast;

import java.util.Iterator;

import c3c.ast.IScope.BlockDecl;
import c3c.ast.IType.Type;
import c3c.ast.InstrStat.InstrBlockStat;
import c3c.ast.cpp.TypeDefDecl;
import c3c.ast.visitor.BlockVisitor;
import c3c.ast.visitor.MethodVisitor;
import c3c.ast.visitor.ScopeVisitor;
import c3c.ast.visitor.ScopeVisitor.RecordVisitor;
import c3c.cube.CodeSource;
import c3c.cube.IToken;
import c3c.cube.Lexeme;
import c3c.cube.List;
import c3c.cube.Modifiers;
import c3c.cube.SrcPos;


public class MethodDecl extends BlockDecl implements IScope, MethodVisitor {
	
	/*internal*/ public int modifiers;
	/*internal*/ public Type returnType;
	/*internal*/ public Template templDecl;
	/*internal*/ public List<VarDecl> argDecls;
	
	/*internal*/ public MethodDecl(int modifiers, IToken token, 
			CodeSource source, List<Annotation> annos) {
		super(token, source, annos);
		this.modifiers = modifiers;
		this.argDecls = new List<VarDecl>();
	}

	@Override
	public Decl resolve(String name, SrcPos location) {
		
		return null;
	}
	
	public boolean isConstructor() {
		return false;
	}
	
	public boolean isDestructor() {
		return false;
	}
	
	public boolean isGeneric() {
		return templDecl != null;
	}

	public int getModifiers() {
		return modifiers;
	}
	
	public Type getType() {
		return returnType;
	}
	
	public boolean hasBody() {
		return body != null;
	}

	public Template getTemplDecl() {
		return templDecl;
	}
	
	public Iterator<VarDecl> getArgDecls() {
		return argDecls.iterator();
	}
	
	public void visitReturnType(Type returnType) {
		this.returnType = returnType;
	}

	public void visitTemplDecl(Template templDecl) {
		this.templDecl = templDecl;
	}
	
	public BlockVisitor visitCodeBlock() {
		body = new Block();
		body.setScope(this);
		return body;
	}

	@Override
	public void accept(ScopeVisitor visitor) {
		BlockVisitor v = visitor.visitMethodDecl(this);
		if (v != null) {
			accept(v);
		}
	}

	public void accept(BlockVisitor visitor) {
		if (body != null) {
			body.accept(visitor);
		}
	}

	@Override
	public void visitVarDecl(VarDecl decl) {
		decl.setScope(this);
//		decl.setCodeSource(getCodeSource());
		if (body == null) {
			argDecls.add(decl);
		} else {
			body.visitVarDecl(decl);
		}
	}

	@Override
	public RecordVisitor visitRecordDecl(RecordDecl decl) {
		decl.setScope(this);
		return body.visitRecordDecl(decl);
	}

	@Override
	public void visitTypeDefDecl(TypeDefDecl decl) {
		decl.setScope(this);
		body.visitTypeDefDecl(decl);
	}

	@Override
	public void visitLabelDecl(Lexeme lexeme) {
		body.visitLabelDecl(lexeme);
	}

	@Override
	public void visitStatement(InstrStat stat) {
		body.visitStatement(stat);
	}

	public InstrStatVisitor visitStatement(InstrBlockStat stat) {
		return body.visitStatement(stat);
	}

	public BlockStatVisitor visitStatement(BlockStat stat) {
		return body.visitStatement(stat);
	}

	public void visitEnd() {
		body.visitEnd();
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		if (returnType != null) {
			b.append(returnType.toString());
			b.append(' ');
		}
		b.append(Modifiers.toString(modifiers & ~Modifiers.CONST));
		b.append(super.toString());
		b.append('(');
		boolean first = true;
		for (VarDecl decl : argDecls) {
			if (!first) b.append(", ");
			first = false;
			b.append(decl.toString());
		}
		b.append(") ");
		b.append(Modifiers.toString(modifiers & Modifiers.CONST));
		return b.toString();
	}

}
