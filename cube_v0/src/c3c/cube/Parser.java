package c3c.cube;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import c3c.ast.Annotation;


public class Parser {
	
	/*internal*/ Lexer lexer;
	
	/**
	 * The current token ID scanned by the lexer.
	 */
	private long token;
	
	private CodeSource source;
	
	/**
	 * The previous token position.
	 * Messages reported while parsing are attached always to previous scanned token.
	 */
	/*internal*/ SrcPos location;
	
	/*internal*/ public Parser(CodeSource source) throws FileNotFoundException, IOException {
		this.source = source;
		this.lexer = new Lexer(new FileReader(source.toString()));
	}

	public CodeSource getSource() {
		return source;
	}
	
	/**
	 * Scans the next token and stores its Id into {@link ParserContext#lexeme} field.
	 * Saves the previous token location.
	 */
	/*internal*/ /*inline*/ public void scan() {
		consume();
	}
	
	/**
	 * Scans the next token and stores its Id into {@link ParserContext#token} field.
	 * Saves the previous token location.
	 */
	/*internal*/ /*inline*/ public int next() {
		consume();
		return (int) token;
	}
	
	public int id() {
		return (int) token;
	}
	
	public long flags() {
		return token & 0xFFFFFFFFFFFFFF00L;
	}
	
	public boolean is(long flags) {
		return (flags() & flags) != 0;
	}
	
	public void consume() {
		for (;;) {
			location = lexer.location();
			token = lexer.scan();
			System.out.println(lexer.lexeme());
			if (token == Id._SYNTAX_ERR) {
				Report.message(Report._1_SYNTAX_ERROR, getSource(), getLocation());
			} else {
				break;
			}
		}
	}
	
	private void __consumeToken() {
		location = lexer.location();
		token = lexer.scan();
		System.out.println(lexer.lexeme());
	}
	 
//	public void inspect() {
//		for (;;) {
////			location = lexer.location();
//			
//			token = lexer.scan();
//			System.out.println(lexer.lexeme());
//			if (token == Id._SYNTAX_ERR) {
//				Report.message(Report._1_SYNTAX_ERROR, getSource(),
//						getLocation());
//			} else {
//				break;
//			}
//		}
//	}
//	
//	public void reset() {
//		
//	}
	
//	/*internal*/ public void skipTokens(int tokens) {
//		while ((token & tokens) == 0) {
//			consume();
//		}
//	}
	
	/**
	 * Ensures that current token is one of the expected flags given.
	 * If not, reports the given message and tries to recover a valid token.
	 * 
	 * @param expected token flags expected
	 * @param message the error message reported if none of the expected token IDs are found
	 * @param recovery the token IDs to recover normal parser flow
	 * @return true if one of the expected IDs matches with current or recovered token.
	 * @see ParserContext#recover(int expected, int reovery)
	 */
	public boolean expect(long expected, long recovery, String message) {
		if ((flags() & expected) == 0) {
			Report.message(message, source, location);
			return recover(expected, recovery);
		} 
		return true;
	}
	
	public boolean expectAndConsume(long required, long recovery, String message) {
		if (expect(required, recovery, message)) {
			consume();
			return true;
		}
		return false;
	}
	
	/**
	 * @param matches the chars to check for
	 * @return true if the next non white space character matches with one of the given
	 */
	public boolean match(char... matches) {
		return lexer.match(matches);
	}
	
	/**
	 * 
	 * @param expected
	 * @param recovery
	 * @return
	 */
	public boolean recover(long expected, long recovery) {
		recovery |= expected;
		SrcPos error = null;
		while ((flags() & recovery) == 0 && token != Id._EOF) {
			if (error == null) error = getLocation();
			else error.add(getLocation());
			token = lexer.scan();
			System.out.println(lexer.lexeme());
		}
		if (error != null) {
			Report.message(Report._1_SYNTAX_ERROR, source, error);
		}
		return (token & expected) != 0;
	}
	
	public boolean inspect(long expected, long recovery) {
		recovery |= expected;
		while ((flags() & recovery) == 0 && token != Id._EOF) {
			token = lexer.scan();
			System.out.println(lexer.lexeme());
		}
		return (token & expected) != 0;
	}
	
//	public boolean fixRecover(int recovery, int... ids) {
//		if ((token & recovery) != 0) {
//			for (int i = 0; i < ids.length; i++) {
//				if (token == ids[i]) 
//					return true;
//			}
//		}
//		return false;
//	}
	
	public void skip(long recovery) {
		while ((flags() & recovery) == 0 && token != Id._EOF) {
			scan();
		}
	}
	
	public void skipTo(long recovery) {
		while (flags() != recovery && token != Id._EOF) {
			__consumeToken();
		}
	}
	
	public void skipLine() {
		lexer.skipLine();
		__consumeToken();
	}

	public char first(char... chars) {
		return lexer.first(chars);
	}

	public IToken getToken() {
		return new Token(id(), __location());
	}
	
	public IToken getAndConsumeToken() {		IToken token = getToken();
		consume();
		return token;
	}
	
	public Lexeme getLexeme() {
		return new Lexeme(lexer.lexeme(), __location());
	}

	public Lexeme getLexemeAndConsumeToken() {
		Lexeme lexeme = getLexeme();
		consume();
		return lexeme;
	}
	
	public int getIdAndConsume() {
		int id = id();
		consume();
		return id;
	}
	public SrcPos getLocation() {
		return lexer.location();
	}
	
	public SrcPos __location() {
		return lexer.location();
	}

	public SrcPos getPrevLocation() {
		return location;
	}
	
//	public void skipBlock() {
//		int ident = 1;
//		__consumeToken();
//		while (ident > 0) {
//			switch (token) {
//				case Id.$OPEN_KEY: ident++; break;
//				case Id.$CLOSE_KEY: ident--; break;
//				case Id.EOF: return;
//			}
//			consume();
//		}
//	}
	
	/** parser: {@lITokenToken} */
	public IToken parseIdentifier(long recovery) {
		return parseIdentifier(recovery, "Missing identifier");
	}
	
	/** parser: {IToken IToken} */
	public IToken parseIdentifier(long recovery, String message) {
		if (expect(Id.$$ID, recovery, message)) {
			ArrayList<Lexeme> lexemes = new ArrayList<Lexeme>(4);
			do {
				lexemes.add(getLexemeAndConsumeToken());
				if ((recovery & flags()) != 0) {
					break;
				}
				if (recover(Id.$DOT, recovery)) {
					consume();
				}
			} while (recover(Id.$$ID, recovery));
			if (lexemes.size() > 1)
				return new ComplexLexeme(lexemes.toArray(new Lexeme[lexemes.size()]));
			else
				return lexemes.get(0);
		}
		return null;
	}
	
	/** parser: <b>int</b> | <b>string</b> | <b>void</b> ... DATA_TYPES | {@link Lexeme} */
	public /*iIToken*/ IToken parseTypeOrId(long recovery) {
		return parseTypeOrId(recovery, Report._1_MISS_TYPE);
	}

	/** parser: <b>int</b> | <b>string</b> | <b>void</b> ... DATA_TYPES | {@link Lexeme} */	public IToken parseTypeOrId(long recovery, String message) {
		if (expect(Id.$$TYPE|Id.$$ID, recovery, message)) {
			if (is(Id.$$TYPE))
				return getAndConsumeToken();
			else
				return parseIdentifier(recovery|Id.$$TYPE|Id.$$ID, message);
		}
		return null;
	}
	
	public int parseModifiers(int expected, long recovery) {
		int modifiers = 0;
		recovery |= expected;
		while (recover(expected, recovery)) {
			modifiers |= token;
			consume();
		}
		return modifiers;
	}
	
	protected List<Annotation> getAnnotations() {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected void setMode(int mode) {
		lexer.setCaptureMode(mode);
	}
}
