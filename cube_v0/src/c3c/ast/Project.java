package c3c.ast;

import java.util.Iterator;

import c3c.ast.cpp.CxxDefine;
import c3c.ast.cpp.TypeDefDecl;
import c3c.ast.visitor.BlockVisitor;
import c3c.ast.visitor.ProjectVisitor;
import c3c.ast.visitor.ScopeVisitor;
import c3c.cube.CodeSource;
import c3c.cube.IToken;
import c3c.cube.List;
import c3c.cube.SrcPos;

public class Project implements IScope, ScopeVisitor {
	
	private final List<ComplUnit> units;
	
	private final List<CxxDefine> defs;
	
	private final Namespace ns;
	
	private IScope scope;
	
	public Project() {
		ns = new Namespace(""); // global
		units = new List<ComplUnit>();
		defs = new List<CxxDefine>();
	}
	
	public void clean() {
		units.clean();
		ns.clean();
	}
	
	@Override
	public IScope getScope() {
		return scope;
	}

	@Override
	public Project getProject() {
		return this;
	}

	@Override
	public void setScope(IScope scope) {
		this.scope = scope;
	}

	public Namespace getGlobalNamespace() {
		return ns;
	}
	
	public Iterator<ComplUnit> getUnits() {
		return units.iterator();
	}
	
	public void addUnit(ComplUnit unit) {
		units.add(unit);
	}
	
	public ComplUnit getUnit(CodeSource source) {
		return getUnit(source.toString());
	}

	public ComplUnit removeUnit(CodeSource source) {
		return removeUnit(source.toString());
	}
	
	public ComplUnit getUnit(String source) {
		return units.get(source);
	}
	
	public ComplUnit removeUnit(final String source) {
		ComplUnit unit = units.remove(source);
		if (unit != null) {
			ns.remove(
				new List.Filter<Decl>() {
					@Override
					public boolean matches(Decl node) {
						try {
							CodeSource s = node.getCodeSource();
							assert s != null;
							if (s == null) {
								toString();
							}
							return s.equals(source);
						} catch (Exception e) {
							e.printStackTrace();
						}
						return false;
					}
				}
			);
		}
		return unit;
	}
	
	public void addDefine(CxxDefine def) {
		defs.add(def);
	}
	
	public CxxDefine getDefined(IToken lexeme) {
		return defs.get(lexeme.getName());
	}

	@Override
	public Decl resolve(String name, SrcPos location) {
		return ns.resolve(name, location);
	}
	
	public void accept(ProjectVisitor visitor) {
		for (ComplUnit unit : units) {
			visitor.visitComplUnit(unit);
		}
	}

	public void visitUsingDecl(UsingDecl decl) {
		ns.visitUsingDecl(decl);
	}

	public NsVisitor visitNamespaceDecl(NsDecl decl) {
		return ns.visitNamespaceDecl(decl);
	}

	public void visitVarDecl(VarDecl decl) {
		ns.visitVarDecl(decl);
	}

	public BlockVisitor visitMethodDecl(MethodDecl decl) {
		return ns.visitMethodDecl(decl);
	}

	public RecordVisitor visitRecordDecl(RecordDecl decl) {
		return ns.visitRecordDecl(decl);
	}

	public void visitTypeDefDecl(TypeDefDecl decl) {
		ns.visitTypeDefDecl(decl);
	}

	public void visitEnd() {
		ns.visitEnd();
	}

}
