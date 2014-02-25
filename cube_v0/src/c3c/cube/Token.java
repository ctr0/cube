package c3c.cube;


public class Token implements IToken {

	int id;
	SrcPos location;
	
	Token(int id, SrcPos location) {
		this.id = id;
		this.location = location;
	}
	
	public int getId() {
		return id;
	}

	/*internal*/ public void setId(int id) {
		this.id = id;
	}
	
	/*internal*/ public void addId(int id) {
		this.id |= id;
	}
	
	@Override
	public SrcPos getLocation() {
		return location;
	}

	@Override
	public boolean isValid() {
		return true;
	}
	
	@Override
	public String getName() {
		return null;
	}
	
	public Lexeme getLexeme() {
		return null;
	}
	
	@Override
	public boolean isComplex() {
		return false;
	}
	
	@Override
	public Lexeme[] getLexemes() {
		return null;
	}

	@Override
	public int compareTo(IToken token) {
		return id - token.getId();
	}

	@Override
	public boolean equals(IToken token) {
		return id == token.getId();
	}
	
	@Override
	public boolean equals(String lexeme) {
		return toString().equals(lexeme);
	}

	@Override
	public String toString() {
		return Id.toString(id);	
	}
	
	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof IToken) {
			return compareTo((IToken) o) == 0;
		}
		return false;
	}

}
