package c3c.ast;

import c3c.cube.SrcPos;
import c3c.cube.IToken;

public abstract class Node implements INode, Comparable<Node> {
	
	/*internal*/ public IToken token;
	
	/*internal*/ public Node() {}
	
	/*internal*/ public Node(IToken token) {
		this.token = token;
	}
	
	public String getName() {
		return token.getName();
	}
	
	public IToken getToken() {
		return token;
	}
	
	public int getId() {
		return token.getId();
	}

	public SrcPos getLocation() {
		return token.getLocation();
	}
	
	public boolean isValid() {
		return token != null;
	}

	@Override
	public boolean equals(Object obj) {
		return token.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return token.hashCode();
	}

	public boolean equals(Node node) {
		return token.equals(node.getToken());
	}

	@Override
	public int compareTo(Node node) {
		return token.compareTo(node.getToken());
	}
	
	public String toString() {
		if (token != null)
			return token.toString();
		return "";
	}
}
