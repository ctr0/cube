package c3c.ast;

import java.util.Iterator;

import c3c.ast.IType.TemplTypeParam;
import c3c.cube.List;



public final class Template extends Node {
	
	public final List<TemplTypeParam> typeParams;
	public final List<VarDecl> varDecls;
	
	/*internal*/ public Template() {
		super();
		typeParams = new List<TemplTypeParam>();
		varDecls = new List<VarDecl>();
	}
	
	public boolean isInferred() {
		return typeParams.last() == null;
	}
	

	public Iterator<TemplTypeParam> getTypeParams() {
		return typeParams.iterator();
	}

	public Iterator<VarDecl> getVarDecls() {
		return varDecls.iterator();
	}

	public TemplTypeParam getTypeDecl(String name) {
		return typeParams.get(name);
	}
	
	public VarDecl getVarDecl(String name) {
		return varDecls.get(name);
	}
	
	public void visitTypeParam(TemplTypeParam type) {
		typeParams.add(type);
	}

	public void visitVarParam(VarDecl decl) {
		varDecls.add(decl);
	}

	@Override
	public boolean isValid() {
		for (TemplTypeParam param : typeParams) {
			if (!param.isValid()) {
				return false;
			}
		}
		for (VarDecl decl : varDecls) {
			if (!decl.isValid()) {
				return false;
			}
		}
		return true;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append('<');
		boolean first = true;
		for (TemplTypeParam param : typeParams) {
			if (!first) builder.append(',');
			else first = true;
			builder.append(param.toString());
		}
		for (VarDecl decl : varDecls) {
			if (!first) builder.append(',');
			else first = true;
			builder.append(decl.toString());
		}
		builder.append('>');
		return builder.toString();
	}
	
}
