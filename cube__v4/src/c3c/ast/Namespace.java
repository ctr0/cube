package c3c.ast;

import java.util.Iterator;

import c3c.ast.IScope.Scope;
import c3c.cube.List;

public class Namespace extends Scope implements Comparable<String> {

	private String name;
	
	private List<Namespace> namespaces;
	
	public Namespace(String name) {
		this.name = name;
		this.namespaces = new List<Namespace>();
	}
	
	public void clean() {
		namespaces.clean();
	}
	
	@Override
	public void visitUsingDecl(UsingDecl decl) {}

	@Override
	public NsVisitor visitNamespaceDecl(NsDecl decl) {
		Namespace ns = namespaces.get(decl.getName());
		if (ns == null) {
			ns = new Namespace(decl.getName());
			namespaces.add(ns);
		}
		decl.setNamespace(ns);
		return super.visitNamespaceDecl(decl);
	}

	public Namespace getNamespace(NsDecl decl) {
		return namespaces.get(decl.getName());
	}
	
	public Namespace getNamespace(String name) {
		return namespaces.get(name);
	}
	
	public Iterator<Namespace> getNamespaces() {
		return namespaces.iterator();
	}

	public Decl remove(String name) {
		return decls.remove(name);
	}

	public void remove(List.Filter<Decl> filter) {
		decls.remove(filter);
		Iterator<Namespace> iterator = namespaces.iterator();
		while (iterator.hasNext()) {
			iterator.next().remove(filter);
		}
	}
	
	@Override
	public int compareTo(String name) {
		return this.name.compareTo(name);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof String) {
			return compareTo(o.toString()) == 0;
		}
		return super.equals(o);
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "NAMESPACE " + name;
	}


}
