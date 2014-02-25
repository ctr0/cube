package c3c.ast;

import c3c.ast.IScope.Block;
import c3c.ast.visitor.BlockVisitor;
import c3c.ast.visitor.BlockVisitor.BlockStatVisitor;
import c3c.cube.IToken;
import c3c.cube.List;
import c3c.cube.SrcPos;


public class BlockStat extends Block implements IStatement, BlockStatVisitor {
	
	public BlockStat(List<Annotation> annos) {
//		super(token, annos);
	}
	
	public BlockStat(IToken token) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public SrcPos getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setScope(IScope scope) {
		// TODO Auto-generated method stub
		
	}

	public void accept(BlockVisitor visitor) {
		BlockStatVisitor v = visitor.visitStatement(this);
		if (v != null) {
			accept(v);
		}
	}
	
	public void accept(BlockStatVisitor visitor) {
		super.accept(visitor);
	}

}
