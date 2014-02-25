package c3c.ast.cpp;

import c3c.ast.Node;
import c3c.cube.CubeLang.internal;
import c3c.cube.List;
import c3c.cube.IToken;

public class CxxDefine extends Node {
	
	public List<IToken> args;
	public List<IToken> tokens;
	
	public CxxDefine(IToken token) {
		super(token);
		args = new List<IToken>();
		tokens = new List<IToken>();
	}
	
	public boolean isMacro() {
		return args.last() != null;
	}

	public IToken getArg(IToken token) {
		return args.get(token);
	}
	
	@internal public void visitToken(IToken token) {
		tokens.add(token);
	}

	@internal public void visitArgument(IToken token) {
		args.add(token);
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(super.toString());
		if (isMacro()) {
			b.append('(');
			boolean first = true;
			for (IToken arg : args) {
				if (!first) b.append(", ");
				first = false;
				b.append(arg.toString());
			}
			b.append(')');
		}
		return b.toString();
	}

}
