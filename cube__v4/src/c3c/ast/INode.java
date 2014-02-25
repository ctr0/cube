package c3c.ast;

import c3c.cube.SrcPos;

public interface INode {
	
	public int getId();
	
	public SrcPos getLocation();
	
	public boolean isValid();

}
