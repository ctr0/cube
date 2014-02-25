package c3c.cube;

import java.util.Iterator;

import c3c.ast.Node;

public class NodeList extends List<Node> {

	public Node get(IToken token) {
		return get(token.getName(), token.getLocation());
	}
	
	public Node get(String name, SrcPos location) {
		Iterator<Node> iterator = iterator();
		while (iterator.hasNext()) {
			Node n = iterator.next();
			if (n.getLocation().compareTo(location) > 0) {
				break;
			}
			if (n.equals(name)) {
				return n;
			}
		} 
		return null;
	}

}
