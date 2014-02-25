package c3c.ast;

import java.util.ArrayList;

public class TypeExpr implements IExpr {
	
	private boolean nullable;
	private boolean cubeRef;
	private boolean varArg;
	private TemplDecl templDecl;
	private ArrayList<IType.Dim> dims;

}
