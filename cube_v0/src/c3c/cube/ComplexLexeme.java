package c3c.cube;



public class ComplexLexeme implements IToken {
	
	private Lexeme[] lexemes;
	private SrcPos location;
	
	public ComplexLexeme(Lexeme[] lexemes) {
		this.lexemes = lexemes;
		this.location = lexemes[0].getLocation(); 
		for (int i = 1; i < lexemes.length; i++) {
			this.location.add(lexemes[i].getLocation());
		}
	}
	
	@Override
	public String getName() {
		return lexemes[lexemes.length -1].getName();
	}

	@Override
	public Lexeme getLexeme() {
		return lexemes[lexemes.length -1];
	}

	@Override
	public boolean isComplex() {
		return true;
	}

	@Override
	public Lexeme[] getLexemes() {
		return lexemes;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < lexemes.length; i++) {
			if (i > 0) b.append('.');
			b.append(lexemes[i].toString());
		}
		return b.toString();
	}

	@Override
	public int compareTo(IToken token) {
		String name = token.getName();
		if (token.isComplex()) {
			Lexeme[] tokens = token.getLexemes();
			for (int i = 0; i < tokens.length && i < lexemes.length; i++) {
				// TODO
			}
		}
		return lexemes[0].compareTo(token);
	}

	@Override
	public int getId() {
		return Id.ID;
	}

	@Override
	public SrcPos getLocation() {
		return location;
	}

	@Override
	public boolean isValid() {
		return lexemes != null && lexemes[0] != null;
	}

	@Override
	public boolean equals(IToken token) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean equals(String lexeme) {
		// TODO Auto-generated method stub
		return false;
	}
}
