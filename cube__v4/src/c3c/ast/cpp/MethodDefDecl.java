package c3c.ast.cpp;

import c3c.ast.Annotation;
import c3c.ast.Decl;
import c3c.ast.IType;
import c3c.ast.visitor.ScopeVisitor;
import c3c.cube.IToken;
import c3c.cube.List;

public class MethodDefDecl extends Decl {

	public MethodDefDecl(IToken token, List<Annotation> annos) {
		super(token, annos);
	}

	@Override
	public void accept(ScopeVisitor visitor) {
		
	}

	@Override
	public IType getType() {
		// TODO Auto-generated method stub
		return null;
	}

}
