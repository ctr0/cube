package c3c.ast;

import c3c.ast.IScope.Scope;
import c3c.cube.CodeSource;
import c3c.cube.List;

/**
 * 
 * @author Jordi Carretero
 *
 */
public class ComplUnit extends Scope implements Comparable<ComplUnit> {
	
	/**
	 * The Abstract Syntax Tree
	 */
	private Project project;
	
	/**
	 * Source file represented by this compilation unit.
	 */
	private CodeSource source;
	
	/*internal*/ public List<Annotation> annos;
	
	/**
	 * Internal constructor.
	 * 
	 * @param source The source file represented by this compilation unit.
	 */
	public ComplUnit(Project project, CodeSource source) {
		this.project = project;
		this.source = source;
		annos = new List<Annotation>();
		
		setScope(project);
	}
	
	/**
	 * @return the code source
	 */
	public CodeSource getSource() {
		return source;
	}

	@Override
	public NsVisitor visitNamespaceDecl(NsDecl decl) {
//		decl.setCodeSource(getSource());
		project.visitNamespaceDecl(decl);
		super.visitNamespaceDecl(decl); // override parent scope to unit scope
		return decl;
	}

	@Override
	public int compareTo(ComplUnit o) {
		return getSource().compareTo(o.getSource());
	}
	
	public boolean equals(String path) {
		return getSource().getFullPath().equals(path);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof String) {
			return equals((String) o);
		} else if (o instanceof ComplUnit) {
			return compareTo((ComplUnit) o) == 0;
		}
		return this == o;
	}

	@Override
	public int hashCode() {
		return getSource().hashCode();
	}

	@Override
	public String toString() {
		return getSource().toString();
	}
}
