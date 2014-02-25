package c3c.ast;

import c3c.ast.visitor.ExprVisitor;

public interface IExpr extends INode {
	
	public abstract void accept(ExprVisitor visitor);
	
	public abstract IType getType();

	public abstract boolean isStatement();
	
	
	
	public abstract class Expression implements ExprVisitor, IExpr {
		
//		private IScope scope;
//		
//		public void setScope(IScope scope) {
//			this.scope = scope;
//		}
		
	}

}
