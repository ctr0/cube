package c3c.cube;

public interface IToken extends Comparable<IToken> {
	
	public int getId();
	
	public SrcPos getLocation();
	
	public boolean isValid();
	
	public String getName();
	
	public boolean isComplex();
	
	public Lexeme getLexeme();
	
	public Lexeme[] getLexemes();
	
	public boolean equals(Object o);

	public boolean equals(IToken token);
	
	public boolean equals(String lexeme);
	
	public int hashCode();

	public String toString();

}
