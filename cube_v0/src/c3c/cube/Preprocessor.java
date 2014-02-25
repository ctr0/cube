package c3c.cube;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import c3c.ast.cpp.CxxDefine;
import c3c.ast.cpp.CxxIncludeDir;

public class Preprocessor extends Parser {
	
	private Compiler compiler;
	
	private Macro macro;
	private List<IToken> macroParams;
	private Iterator<Tk> replacement;
	
	private Tk curr;
	
	private static class Tk {
		long token;
		SrcPos location;
		Tk(long token, SrcPos location) {
			this.token = token;
			this.location = location;
		}
	}
	
	private static class Macro {
		List<Tk> tks;
	}
	
	Preprocessor(Compiler compiler, CodeSource source) throws FileNotFoundException, IOException {
		super(source);
		this.compiler = compiler;
	}

	@Override
	public int id() {
		if (macro != null) {
			return (int) curr.token;
		}
		return super.id();
	}
	
	@Override
	public long flags() {
		if (macro != null) {
			return curr.token & 0xFFFFFFFFFFFFFF00L;
		}
		return super.flags();
	}
	
	private Tk getReplacement() {
		if (macro != null) {
			if (replacement.hasNext()) {
				curr = replacement.next();
				token = curr.id;
				return;
			} else {
				macro = null;
			}
		}
	}

	@Override
	public void consume() {
		// replacement
		if (macro != null) {
			if (replacement.hasNext()) {
				curr = replacement.next();
				return;
			} else {
				macro = null;
			}
		}
		// preprocess
		super.consume();
		for (;;) switch (id()) {
			case Id.__INCLUDE:	processInclude();			continue;
			case Id.__ENDIF:
			case Id.__PRAGMA:	skipLine();					continue;
			case Id.__DEFINE:	processDefine();			continue;
			case Id.__UNDEF:	processDefine();			continue;
			case Id.__IF:		processIfDir();				continue;
			case Id.__IFDEF: 	processIfdefDir(true); 		continue;
			case Id.__IFNDEF: 	processIfdefDir(false); 	continue;
			case Id.__ELSE:		skipBlockDir(); skipLine(); continue;
			case Id.ID:
				IToken token = getLexeme();
				macro = compiler.getProject().getDefined(token);
				if (macro != null) {
					parseMacroParams();
				}
				//$FALL-THROUGH$
			default: return;
		}
	}

	private void processIfDir() {
		skipBlockDir();
		if (id() == Id._ELSE) {
			super.consume();
		}
	}

	@Override
	public IToken getToken() {
		if (macro != null) {
			if (macro.getArg(curr) != null) {
				return replacement.next();
			}
			return curr;
		}
		return super.getToken();
	}

	@Override
	public Lexeme getLexeme() {
		if (macro != null) {
			if (macro.getArg(curr) != null) {
				return replacement.next().getLexeme();
			}
			return curr.getLexeme();
		}
		return super.getLexeme();
	}

	@Override
	public SrcPos getLocation() {
		if (macro != null) {
			return curr.getLocation();
		}
		return super.getLocation();
	}

	@Override
	public SrcPos getPrevLocation() {
		if (macro != null) {
			return prev.getLocation();
		}
		return super.getPrevLocation();
	}

	private void parseMacroParams() {
		macroParams = null;
		if (macro.isMacro()) {
			super.consume();
			if (!recover(Id.$OPEN_PAR, 1)) {
				Report.message("Missing required parameters for macro", getSource(), getLocation());
			} else {
				super.consume();
				macroParams = new List<IToken>();
				do {
					macroParams.add(super.getLexeme());
					super.consume();
					if (token == Id.$COMMA) {
						super.consume();
					}
				} while (token != Id.$CLOSE_PAR);
				super.consume();
			}
		}
		// Start injection
		replacement = macro.tokens.iterator();
		if (replacement.hasNext()) {
			prev = curr;
			curr = replacement.next();
			token = curr.id;
			return;
		} else {
			super.consume();
			macro = null;
		}
	}

	private void processInclude() {
		lexer.setCaptureMode(Lexer.CAPTURE_ENDL);
		IToken tk = getToken();
		super.consume();
		switch (id()) {
		
			case Id.STRING_LITERAL: {
				Lexeme lexeme = getLexeme();
				super.consume();
				String path = lexeme.toString().replace("\"", "");
				CxxIncludeDir include = new CxxIncludeDir(tk, getCodeSource(path));
				compiler.compile(include.getCodeSource(), include.getToken().getLocation());
				break;
			}

			case Id.LT: {
				super.consume();
				String path = parseInlcude();
				CxxIncludeDir include = new CxxIncludeDir(tk, getCodeSource(path));
				lexer.skipLine();
				compiler.compile(include.getCodeSource(), include.getToken().getLocation());
				break;
			}
				
			case Id._ENDL:
				Report.message("Missing argument for include directive", getSource(), getLocation());
				break;
		
			default: 
				Report.message("Invalid include directive", getSource(), getLocation());
				break;
		}
		lexer.setCaptureMode(Lexer.CAPTURE_DEFAULT);
		super.consume();
	}

	private CodeSource getCodeSource(String path) {
		return new CodeSource(getSource().getBasePath(), path);
	}

	private void processDefine() {
		lexer.setCaptureMode(Lexer.CAPTURE_ENDL);
		super.consume();
		switch (id()) {

			case Id._ENDL:
				Report.message("Missing definition", getSource(), getLocation());
				break;
		
			default: 
				CxxDefine def = parseDefine();
				compiler.getProject().addDefine(def);
				break;
		}
		lexer.setCaptureMode(Lexer.CAPTURE_DEFAULT);
		super.consume();
	}

	private CxxDefine parseDefine() {
		CxxDefine decl = new CxxDefine(super.getLexeme());
		super.consume();
		if (super.id() == Id.$OPEN_PAR) {
			super.consume();
			for (;;) {
				switch (id()) {
				
				case Id._ENDL: 
					break;
				
				case Id.ID: 
					decl.visitArgument(super.getLexeme());
					super.consume();
					if (recover(Id.$COMMA, Id.$$ID | Id.$CLOSE_PAR)) {
						super.consume();
						continue;
					}
					
				//$FALL-THROUGH$
				default:
					if (recover(Id.$$ID, Id.$CLOSE_PAR)) {
						continue;
					}
					super.consume();
				}
				break;
			}
		}
		while (token != Id._ENDL) {
			if (token == '\\') {
				lexer.skipLine();
			}
			decl.visitToken(super.getLexeme());
			super.consume();
		}
		return decl;
	}

	private void processIfdefDir(boolean logic) {
		super.consume();
		Lexeme lexeme = super.getLexeme();
		super.consume();
		if ((compiler.getProject().getDefined(lexeme) != null) == logic) {
			return; // process conditional region
		} else {
			skipBlockDir();
			if (token == Id._ELSE) {
				return; // process conditional region
			}
		}
	}

	public String parseInlcude() {
		if (Id.ID == id()) {
			return lexer.getString('>');
		}
		Report.message("Invalid include", getSource(), getLocation());
		return null;
	}
	
	private void skipBlockDir() {
		int ident = 1;
		while (ident > 0) {
			super.consume();
			switch (id()) {
				case Id.__IF:
				case Id.__IFDEF:
				case Id.__IFNDEF: 	ident++; 		continue;
				case Id.__ELSE: 	if (ident > 1) 	continue;
				//$FALL-THROUGH$
				case Id.__ENDIF: 	ident--;		continue;
				default: 							continue;
				case Id._EOF: 						return;
			}
		}
	}
}
