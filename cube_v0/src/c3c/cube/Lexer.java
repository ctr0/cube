package c3c.cube;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;


@SuppressWarnings("all")
public final class Lexer {
	
	public static int CAPTURE_DEFAULT = 0;
	public static int CAPTURE_ENDL = 	1;
	public static int CAPTURE_LTGT = 	2;

	// TODO BUFF SZ
	private final int CHBUFFSZ = 1 << 3;	
	
	public static class Buffer {
		public final char[] chars;
		public final int length;
		private Buffer(char[] buffer, int length) {
			this.chars = buffer;
			this.length = length;
		}
	}
	
	public final Buffer buffer;
	
	private int mode;
	
	private int begin, end, offset, line;
	private int mark_begin, mark_end, mark_offset, mark_line;
	
	public Lexer(InputStreamReader stream) throws IOException {
		buffer = fillBuffer(stream);
		begin = end = offset = 0;
		line = 1;
		mark_begin = mark_end = mark_line = mark_offset = -1;
	}
	
	public void setCaptureMode(int mode) {
		this.mode = mode;
	}
	
	public void mark() {
		mark_begin = begin;
		mark_end = end;
		mark_line = line;
		mark_offset = offset;
	}
	
	public void reset() {
		begin = mark_begin;
		end = mark_end;
		line = mark_line;
		offset = mark_offset;
		mark_begin = mark_end = mark_line = mark_offset = -1;
	}

	private Buffer fillBuffer(InputStreamReader stream) throws IOException {
		int size = CHBUFFSZ;
		int length = 0, readed = 0;
		char[] buffer = new char[size];
		length += readed = stream.read(buffer, 0, size);
		while (readed == size) {
			size = buffer.length;
			buffer = Arrays.copyOf(buffer, buffer.length << 1);
			length += readed = stream.read(buffer, length, size);
		}
		return new Buffer(buffer, length);
	}
	
	public SrcPos location() {
		return new SrcPos(line, offset, begin - offset, end - offset);
	}
	
	public String lexeme() {
		return new String(Arrays.copyOfRange(buffer.chars, begin, end));
	}
	
	public long scan() {
		begin = end;
		for (;;) {
			if (end >= buffer.length) {
				return Id._EOF;
			}
			switch (buffer.chars[end]) {
			
				// White spaces
				case '\n':	offset = end;
							line++; begin++; end++; 
							if ((mode & CAPTURE_ENDL) != 0) return Id._ENDL;
							continue;
				case '\t':	
				case '\r': 
				case ' ':	begin++; end++; continue;
				
				case '@': end++; return Id._ATSIGN;
				// Punctuation and operators 
				case ';': end++; return Id._SCOLON;
				case '{': end++; return Id._OPEN_KEY;
				case '}': end++; return Id._CLOSE_KEY;
				case '[': end++; return Id._OPEN_BRA;
				case ']': end++; return Id._CLOSE_BRA;
				case '(': end++; return Id._OPEN_PAR;
				case ')': end++; return Id._CLOSE_PAR;
				case ':': end++; return Id._COLON;
				case ',': end++; return Id._COMMA;
				case '~': end++; return Id._TILDE;
				case '?': end++; return Id._QMARK;
				case '.': return dotOrRealLiteral();
				case '/': return divOperatorOrComment();
				case '*': return operator(Id._STAR, '=', Id._MULTEQ);
				case '=': return operator(Id._EQ, '=', Id._EQEQ);
				case '^': return operator(Id._XOR, '=', Id._XOREQ);
				case '%': return operator(Id._PERC, '=', Id._MODEQ);
				case '!': return operator(Id._NOT, '=', Id._NE);
				case '+': switch (buffer.chars[++end]) {
					case '=': end++; return Id._PLUSEQ;
					case '+': end++; return Id._PLUSPLUS;
					default: return Id._PLUS;
				}
				case '-': switch (buffer.chars[++end]) {
					case '=': end++; return Id._MINUSEQ;
					case '-': end++; return Id._MINUSMINUS;
					default: return Id._MINUS;
				}
				case '&': switch (buffer.chars[++end]) {
					case '=': end++; return Id._ANDEQ;
					case '&': end++; return Id._ANDAND;
					default: return Id._AMP;
				}
				case '|': switch (buffer.chars[++end]) {
					case '=': end++; return Id._OREQ;
					case '|': end++; return Id._OROR;
					default: return Id._OR;
				}
				case '<': end++; 
					if (mode != CAPTURE_LTGT) switch (buffer.chars[end]) {
						case '=': end++; return Id._LTEQ;
						case '<': return operator(Id._LTLT, '=', Id._LTLTEQ);
					}
					return Id._LT;
				case '>': end++; 
					if (mode != CAPTURE_LTGT) switch (buffer.chars[++end]) {
						case '=': end++; return Id._GTEQ;
						case '>': return operator(Id._GTGT, '=', Id._GTGTEQ);
					}
					return Id._GT;
				// Potential keywords 
				case 'a': 
					return keywordOrIdentifier(Id.ABSTRACT, /*a*/"bstract", 7);
					
				case 'b': switch (buffer.chars[++end]) {
					case 'o': return keywordOrIdentifier(Id._BOOL, /*bo*/"ol", 2);
					case 'y': return keywordOrIdentifier(Id._BYTE, /*by*/"te", 2);
				}	return identifier();
				
				case 'c': switch (buffer.chars[++end]) {
					case 'h': return keywordOrIdentifier(Id._CHAR, /*ch*/"ar", 2);
					case 'l': return keywordOrIdentifier(Id._CLASS, /*cl*/"ass", 3);
					case 'o': return keywordOrIdentifier(Id.CONST, Id._CONSTEXPR, /*co*/"nstexpr", 2, 7);
					case 'a': switch (buffer.chars[++end]) {
						case 's': return keywordOrIdentifier(Id._CASE, /*cas*/"e", 1);
						case 't': return keywordOrIdentifier(Id._CATCH, /*cat*/"ch", 2);
				}	}	return identifier();
				
				case 'd': switch (buffer.chars[++end]) {
					case 'e': return keywordOrIdentifier(Id._DEFAULT, /*de*/"fault", 5);
					case 'o': return keywordOrIdentifier(Id._DO, Id._DOUBLE, /*do*/"uble", 0, 4);
				}	return identifier();
					
				case 'e': switch (buffer.chars[++end]) {
					case 'l': return keywordOrIdentifier(Id._ELSE, /*el*/"se", 2);
					case 'n': return keywordOrIdentifier(Id._ENUM, /*en*/"um", 2);
					case 'x': return keywordOrIdentifier(Id.EXTERN, /*ex*/"tern", 4);
				}	return identifier();
				
				case 'f': switch (buffer.chars[++end]) {
//					case 'i': return keywordOrIdentifier(Id._FINAL, /*fi*/"nal", 3);
					case 'l': return keywordOrIdentifier(Id._FLOAT, /*fl*/"oat", 3);
					case 'r': return keywordOrIdentifier(Id._FRIEND, /*fr*/"iend", 4);
				}	return identifier();
				
				case 'i': switch (buffer.chars[++end]) {
					case 'f': end++; return Id._IF;
					case 'n': switch (buffer.chars[++end]) {
						case 't': return keywordOrIdentifier(Id._INT, Id._INTERFACE, /*int*/"erface", 0, 6);
						case 'l': return keywordOrIdentifier(Id.INLINE, /*inl*/"ine", 3);
				}	}	return identifier();
				
				case 'l': return keywordOrIdentifier(Id._LONG, /*l*/"ong", 3);
				case 'n': switch (buffer.chars[++end]) {
					case 'a': return keywordOrIdentifier(Id.$NS, /*na*/"mespace", 7);
					case 'e': return keywordOrIdentifier(Id._NEW, /*ne*/"w", 1);
					case 'u': return keywordOrIdentifier(Id._NULL_LITERAL, /*nu*/"ll", 2);
				}	return identifier();
				
				case 'p': switch (buffer.chars[++end]) {
					case 'u': return keywordOrIdentifier(Id.PUBLIC, /*pu*/"blic", 4);
					case 'r': switch (buffer.chars[++end]) {
						case 'i': return keywordOrIdentifier(Id.PRIVATE, /*pri*/"vate", 4);
						case 'o': return keywordOrIdentifier(Id.PROTECTED, /*pro*/"tected", 6);
				}	}	return identifier();
				
				case 'r': 
					return keywordOrIdentifier(Id._RETURN, /*r*/"eturn", 5);
				
				case 't': switch (buffer.chars[++end]) {
					case 'h': return keywordOrIdentifier(Id._THIS, /*th*/"is", 2);
					case 'y': if (match(/*t*/"ype", 3)) switch (buffer.chars[++end]) {
						case 'd': return keywordOrIdentifier(Id._TYPEDEF, /*typed*/"ef", 2);
						case 'n': return keywordOrIdentifier(Id._TYPENAME, /*typen*/"ame", 3);
				}	}	return identifier();
				
				case 's': switch (buffer.chars[++end]) {
					case 'h': return keywordOrIdentifier(Id._SHORT, /*sh*/"ort", 3);
					case 't': switch (buffer.chars[++end]) {
						case 'a': return keywordOrIdentifier(Id.STATIC, /*sta*/"tic", 3);
						case 'r': return keywordOrIdentifier(Id._STRUCT, /*str*/"uct", 3);
					}	break;
					case 'i': return keywordOrIdentifier(Id._SIGNED, /*si*/"gned", 4);
					case 'w': return keywordOrIdentifier(Id._SWITCH, /*sw*/"itch", 4);
				}	return identifier();
				
				case 'u': switch (buffer.chars[++end]) {
					case 's': return keywordOrIdentifier(Id.$USING, /*us*/"ing", 3);
					case 'n': switch (buffer.chars[++end]) {
						case 'i': return keywordOrIdentifier(Id._UNION, /*uni*/"on", 2);
						case 's': switch (buffer.chars[++end]) {
							case 'a': return keywordOrIdentifier(Id.UNSAFE, /*unsa*/"fe", 2);
							case 'i': return keywordOrIdentifier(Id._UNSIGNED, /*unsi*/"gned", 4);
				}	}	}	return identifier();
				
				case 'v': switch (buffer.chars[++end]) {
					case 'i': return keywordOrIdentifier(Id.VIRTUAL, /*vi*/"rtual", 5);
					case 'o':	switch (buffer.chars[++end]) {
						case 'i': return keywordOrIdentifier(Id._VOID, /*voi*/"d", 1);
						case 'l': return keywordOrIdentifier(Id.VOLATILE, /*vol*/"atile", 5);
				}	}	return identifier();
				
				// Number literals
				case '0': switch (buffer.chars[++end]) {
					case 'x': return hexLiteral();
					case 'X': return hexLiteral();
					case '.': return realLiteral();
				} //$FALL-THROUGH$
				case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9':
					return intOrRealLiteral();
				
				// String literal
				case '"': return stringLiteral();
				case '\'': return charLiteral();
				
				// Preprocessor directives
				case '#': skipWhitespace();
				switch (buffer.chars[end]) {
					case 'd': return keywordOrIdentifier(Id.___DEFINE, /*d*/"efine", 5);
					case 'e': 
					switch (buffer.chars[++end]) {
						case 'l': return keywordOrIdentifier(Id.___ELSE, /*el*/"se", 2);
						case 'n': return keywordOrIdentifier(Id.___ENDIF, /*en*/"dif", 3);
					}
					case 'p': return keywordOrIdentifier(Id.___PRAGMA, /*p*/"ragma", 5);
					case 'i': switch (buffer.chars[++end]) {
						case 'n': return keywordOrIdentifier(Id.___INCLUDE, /*in*/"clude", 5);
						case 'f': switch (buffer.chars[++end]) {
							case 'd': return keywordOrIdentifier(Id.___IFDEF, /*ifd*/"ef", 2);
							case 'n': return keywordOrIdentifier(Id.___IFNDEF, /*ifn*/"def", 3);
							case ' ': return Id.___IF;
						}
					}
					case 'u': return keywordOrIdentifier(Id.___UNDEF, /*u*/"ndef", 4);
					default:  return invalidChars();
				}
					
				// Others (identifier or invalid char sequence)
				default: if (isAlphaEx(buffer.chars[end])) {
					return identifier();
				} else {
					return invalidChars();
				}
			}
		}
	}
	
	private final /*inline*/ long operator(long simple, char c, long doble) {
		if (buffer.chars[++end] == c) {
			end++; 
			return doble; 
		} else {
			return simple;
		}
	}
	
	private final /*inline*/ long divOperatorOrComment() {
		switch (buffer.chars[++end]) {
			case '=':
				end++; 
				return Id._DIVEQ;
			case '/':
				skipLine();
				return scan();
			case '*':
				skipTo("*/", 2);
				return scan();
			default:
				return Id._BAR;
		}
	}
	
	private final boolean match(final String s, final int len) {
		int i = 0;
		while(buffer.chars[++end] == s.charAt(i++) && i < len);
		return i == len;
	}

	private final /*inline*/ long keywordOrIdentifier(long keyword, final String s, long len) {
		int i = 0;
		while(buffer.chars[++end] == s.charAt(i++) && i < len);
		// TODO '\0' in buffer ???
		if (i == len && !isAlphaEx(buffer.chars[++end])) {
			return keyword;
		}
		return identifier();
	}
	
	// TODO contained identifier such as 'interfaces'
	private final /*inline*/ long keywordOrIdentifier(long kw1, long kw2, final String s, final int len1, final int len2) {
		int i = 0;
		while(i < len1 && buffer.chars[++end] == s.charAt(i))
			i++;
		// TODO '\0' in buffer ???
		if (i == len1) {
			if (isAlphaEx(buffer.chars[++end])) {
				while(i < len2 && buffer.chars[end] == s.charAt(i)) {
					i++;
					end++;
				}
				if (i < len2 && isWhitespace(buffer.chars[end])) 
					return kw1; 
				if (i == len2) {
//					end++;
					return kw2;
				}
			} else {
				return kw1;
			}
		}
		return identifier();
	}

	private long identifier() {
		char c;
		while(isAlphaEx(c = buffer.chars[end]) && !isWhitespace(c)) {
			end++;
		}
		return Id._ID;
	}
	
	private long invalidChars() {
		char c;
		while(!isAlphaEx(c = buffer.chars[++end]) && !isWhitespace(c));
		return Id._SYNTAX_ERR;
	}
	
	private final long hexLiteral() {
		while (isHex(buffer.chars[++end]));
		return Id.HEX_LITERAL;
	}
	
	private final long intOrRealLiteral() {
		while (isNum(buffer.chars[end])) {
			end++;
		}
		switch (buffer.chars[end]) {
		
			case 'u': 
			case 'U': switch (buffer.chars[++end]) {
				case 'l': 
				case 'L': ++end; return Id._ULONG_LITERAL;
				default: ++end; return Id._UINT_LITERAL;
			}
			
			case 'l': 
			case 'L': switch (buffer.chars[++end]) {
				case 'u': 
				case 'U': return Id._ULONG_LITERAL;
				default: return Id._LONG_LITERAL;
			}
			
			case 'e': 
			case 'E': return realExponentLiteral();
			case '.': return realLiteral();
			
			default: return Id._INT_LITERAL;	
		}
	}

	private final /*inline*/ long dotOrRealLiteral() {
		if (isNum(buffer.chars[++end])) {
			return realLiteral();
		} else {
			return Id._DOT;
		}
	}
	
	private final /*inline*/ long realLiteral() {
		char c;
		while (isNum(c = buffer.chars[++end]));
		switch (c) {
			case 'f': 
			case 'F': end++; return Id._FLOAT_LITERAL;
			case 'd': 
			case 'D': end++; return Id._DOUBLE_LITERAL;
			case 'e': 
			case 'E': return realExponentLiteral();
			default: return Id._DOUBLE_LITERAL;	
		}
	}
	
	private final /*inline*/ long realExponentLiteral() {
		char c;
		switch (c = buffer.chars[++end]) {
			case '+':
			case '-': if (isNum(c)) {
				return realExponentLiteral2();
			} else {
				return Id._SYNTAX_ERR;
			}
			case '0': case '1': case '2': case '3': case '4': 
			case '5': case '6': case '7': case '8': case '9':
				return realExponentLiteral2();
			default: 
				return Id._SYNTAX_ERR;
		}
	}
	
	private final /*inline*/ long realExponentLiteral2() {
		char c;
		while (isNum(c = buffer.chars[++end]));
		switch (c) {
			case 'f': 
			case 'F': ++end; return Id._FLOAT_LITERAL;
			case 'd': 
			case 'D': ++end; return Id._DOUBLE_LITERAL;
			default: return Id._DOUBLE_LITERAL;	
		}
	}
	
	private final /*inline*/ int charLiteral() {
		
		return 0;
	}

	private final /*inline*/ long stringLiteral() {
		long error = 0;
		for (;;) {
			switch (buffer.chars[++end]) {
			
				// String literal end
				case '\"': ++end; return Id._STRING_LITERAL | error;		
				case '\n': ++end; return Id._SYNTAX_ERR | error;  // unclosed string literal
			
				// Escape sequence
				case '\\': switch (buffer.chars[++end]) {
					case 'b': 
					case 'f': 
					case 'n': 
					case 'r': 
					case 't': 
					case '\"': 
					case '\'': 
					case '\\': break;
					
					// The Latin-1 character specified by the two hex digits between 00 and FF
					case 'x': if (!isHex(buffer.chars[++end]) || !isHex(buffer.chars[++end])) {
						error |= Id._SYNTAX_ERR;  // invalid Latin-1 hex escape sequence
						// String literal end ?
						switch (buffer.chars[end]) {
							case '\"': ++end; return Id._STRING_LITERAL | error;		
							case '\n': ++end; return Id._SYNTAX_ERR | error;  // unclosed string literal
						} 
					} break;
					
					// The Latin-1 character specified by the three octal digits between 0 and 377	
					case '0': case '1': case '2': case '3': if (buffer.chars[++end] > '7' || buffer.chars[++end] > '7') {
						error |= Id._SYNTAX_ERR;  // invalid Latin-1 octal escape sequence
						// String literal end ?
						switch (buffer.chars[end]) {
							case '\"': ++end; return Id._STRING_LITERAL | error;		
							case '\n': ++end; return Id._SYNTAX_ERR | error;  // unclosed string literal
						} 
					} break;
					case '4': case '5': case '6': case '7': case '8': case '9': error |= Id._SYNTAX_ERR; break; // invalid Latin-1 octal escape sequence
					
					// The Unicode character specified by the four hex digits
					case 'u': if (!isHex(buffer.chars[++end]) || !isHex(buffer.chars[++end]) || !isHex(buffer.chars[++end]) || !isHex(buffer.chars[++end])) {
						error |= Id._SYNTAX_ERR;  // invalid Latin-1 hex escape sequence
						// String literal end ?
						switch (buffer.chars[end]) {
							case '\"': ++end; return Id._STRING_LITERAL | error;		
							case '\n': ++end; return Id._SYNTAX_ERR | error;  // unclosed string literal
						} 
					} break;

					default: error |= Id._SYNTAX_ERR; break; // invalid escape sequence
				} 
			}
		}
	}
	
	private static boolean isWhitespace(char c) {
		switch(c) {
			case ' ': case '\t': case '\n': case '\r': 	
				return true;
			default: 	
				return false;
		}
	}
	
	private static boolean isHex(char c) {
		switch (c) {
			case '0': case '1': case '2': case '3': case '4': 
			case '5': case '6': case '7': case '8': case '9':
			case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
			case 'a': case 'b': case 'c': case 'd': case 'e': case 'f': 
				return true;
			default: 
				return false;
		}
	}
	
	private static final /*inline*/ boolean isNum(char c) {
		switch (c) {
			case '0': case '1': case '2': case '3': case '4': 
			case '5': case '6': case '7': case '8': case '9':
				return true;
			default: 
				return false;
		}
	}
	
	private static final /*inline*/ boolean isAlphaEx(char c) {
		switch (c) {
			case '0': case '1': case '2': case '3': case '4': 
			case '5': case '6': case '7': case '8': case '9':
			case 'A': case 'B': case 'C': case 'D': case 'E': 
			case 'F': case 'G': case 'H': case 'I': case 'J':
			case 'K': case 'L': case 'M': case 'N': case 'O': 
			case 'P': case 'Q': case 'R': case 'S': case 'T':
			case 'U': case 'V': case 'W': case 'X': case 'Y': case 'Z': 
			case 'a': case 'b': case 'c': case 'd': case 'e': 
			case 'f': case 'g': case 'h': case 'i': case 'j':
			case 'k': case 'l': case 'm': case 'n': case 'o': 
			case 'p': case 'q': case 'r': case 's': case 't':
			case 'u': case 'v': case 'w': case 'x': case 'y': case 'z': 
			case '_': case '$': case '€': 
				return true;
			default: 
				return false;
		}
	}

	/**
	 * Inspects buffer characters searching for one of the given
	 * @param chars The characters to search
	 * @return the first buffer character found contained 
	 * in the arguments or '\0' if any
	 */
	public char first(char... chars) {
		int end = this.end;
		for (;;) {
			if (end == buffer.length) {
				break;
			}
			char c = buffer.chars[end++];
			for (int i = 0; i < chars.length; i++) {
				if (c == chars[i]) {
					return c;
				}
			}
		}
		return '\0';
	}

	public boolean match(char... matches) {
		return match(begin, matches) | match(end, matches);
	}
	
	private boolean match(int begin, char... matches) {
		int scan = begin;
		for (;;) {
			char c = buffer.chars[scan];
			if (!isWhitespace(c)) {
				for (int i = 0; i < matches.length; i++) {
					if (c == matches[i]) {
						return true;
					}
				}
				break;
			}
			if (scan++ == buffer.length) break;
		}
		return false;
	}
	
	public void skipLine() {
		while (buffer.chars[++end] != '\n' && end <= buffer.length) {
//			System.out.print(buffer.chars[end]);
		}
//		System.out.println();
		line++;
		end++;
	}
	
	public void skipTo(String s, int len) {
		int i = 0, matches = 0;
		while(matches != len && end <= buffer.length) {
			if (buffer.chars[end] == '\n') {
				line++;
			}
//			System.out.print(buffer.chars[end]);
			
			if (buffer.chars[++end] == s.charAt(matches))
				matches++;
			else
				matches = 0;
		}
		end++;
	}
	
	private char getch() {
		char c = buffer.chars[++end];
		switch (c) {
			// White spaces
			case '\n':	offset = end;
						line++; begin++; end++; 
						return c;
			case '\t':	
			case '\r': 
			case ' ':	begin++; end++;
			default:	return c;
		}
	}
	
	public void skipTo(char... match) {
		char c;
		int matches = 0;
		do {
//												System.out.print(buffer.chars[end]);
			if (getch() == match[matches]) 
				matches++;
			else 
				matches = 0;
		} while(matches != match.length && end <= buffer.length);
		end++;
	}
	
	public void skipWhitespace() {
		char c;
		do {
			c = buffer.chars[++end];
			if (c == '\n' && (mode & CAPTURE_ENDL) != 0) {
				break;
			}
		} while (isWhitespace(c));
	}
	
	public String getString(char bound) {
		char c;
		do {
			c = getch();
			if (c == '\n' && (mode & CAPTURE_ENDL) != 0) {
				break;
			}
		} while (c != bound);
		return lexeme();
	}
}
