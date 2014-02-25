package ecubic.builder;

import java.util.Iterator;

import c3c.ast.Namespace;
import c3c.ast.Project;


public class ProjectDumper {
	
	public static String dump(Project project) {
		Namespace namespace = project.getGlobalNamespace();
		Iterator<Namespace> nss = namespace.getNamespaces();
		while (nss.hasNext()) {
			Namespace ns = nss.next();
			
		}
		
		return null;
	}

	public static void dump(StringBuilder builder, Namespace namespace) {
		Iterator<Namespace> nss = namespace.getNamespaces();
		while (nss.hasNext()) {
			Namespace ns = nss.next();
			
		}
	}
}
