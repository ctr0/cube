package c3c.cube;

import java.io.FileNotFoundException;
import java.io.IOException;

import c3c.ast.BinaryExpr;
import c3c.ast.CallExpr;
import c3c.ast.CastExpr;
import c3c.ast.IExpr;
import c3c.ast.IExpr.Expression;
import c3c.ast.IType.TemplTypeParam;
import c3c.ast.IType.Type;
import c3c.ast.LexExpr;
import c3c.ast.MethodDecl;
import c3c.ast.NsDecl;
import c3c.ast.RecordDecl;
import c3c.ast.TemplDecl;
import c3c.ast.UnaryExpr;
import c3c.ast.UsingDecl;
import c3c.ast.VarDecl;
import c3c.ast.cpp.TypeDefDecl;
import c3c.ast.visitor.BlockVisitor;
import c3c.ast.visitor.ScopeVisitor;
import c3c.ast.visitor.ScopeVisitor.NsVisitor;

public class HxxBuilder extends Preprocessor {
	
	private static final int $$ACCMODS = Id.PUBLIC|Id.PRIVATE|Id.PROTECTED;
	
	private static final int $$CXXMODS = Id.ABSTRACT | Id.STATIC | Id.CONST 
	| Id.VOLATILE | Id.INLINE | Id.EXTERN | Id.EXPLICIT | Id.SIGNED | Id.UNSIGNED 
	| Id.REGISTER | Id.AUTO | Id.MUTABLE | Id.SHORT | Id.LONG;
	
	private static final long $RECORD_SAFE = 	$$CXXMODS|Id.$$RECO|Id.$$ID|Id.$$TYPE;
	private static final long $NS_SAFE = 		Id.$NS|$RECORD_SAFE;
	private static final long $UNIT_SAFE = 		Id.$USING|$NS_SAFE;
	private static final long $EXPR_SAFE = 		Id.$$ID|Id.$$LITE|Id.$$TYPE|Id.$$OP;
	
	public HxxBuilder(Compiler compiler, CodeSource source) throws FileNotFoundException, IOException {
		super(compiler, source);
	}

	public void parse(ScopeVisitor visitor) {
		consume(); // <start>
		do parseNsScope(visitor, $UNIT_SAFE);
		while (recover($UNIT_SAFE, $UNIT_SAFE));
	}

	public void parseNsScope(ScopeVisitor visitor, long recovery) {
		do switch (id()) {
			case Id.USING: 
				parseUsingDir(visitor, recovery); 
				continue;
			case Id.NS:
				parseNestedNsDecl(visitor, recovery);
				continue;
			case Id.ID:
			case Id.VOID: case Id.SIGNED: case Id.UNSIGNED: 
			case Id.SHORT: case Id.BYTE: case Id.CHAR: case Id.BOOL:
			case Id.INT: case Id.INT32: case Id.INT64: case Id.INT128:
			case Id.LONG: case Id.FLOAT: case Id.DOUBLE:
				parseMember(visitor, null, $UNIT_SAFE);
				continue;
			case Id.TYPEDEF:
				parseTypeDefDecl(visitor, recovery|Id.$SCOLON|Id.$OPEN_KEY);
				continue;
			case Id.CLASS: case Id.STRUCT: case Id.INTERFACE: case Id.TEMPLATE: case Id.UNION:
				parseRecord(visitor, new int[] {}, recovery|$RECORD_SAFE|Id.$CLOSE_KEY);
				continue;
		} while (recover($NS_SAFE, recovery));
	}
	
	public void  parseRecordScope(ScopeVisitor visitor, long recovery) {
		int[] modifiers = new int[1];
		do switch (id()) {
			case Id.PUBLIC: case Id.PRIVATE: case Id.PROTECTED: 
				parseAccessModifier(modifiers, recovery);
				continue;
			case Id.ABSTRACT: case Id.VIRTUAL: case Id.CONST: 
			case Id.STATIC:	case Id.INLINE:	case Id.VOLATILE: 
				modifiers[0] = parseModifiers($$CXXMODS, recovery|Id.SCOLON|Id.$OPEN_KEY|Id.OPEN_PAR);
				continue;
			case Id.ID:
			case Id.VOID: case Id.SIGNED: case Id.UNSIGNED: 
			case Id.SHORT: case Id.BYTE: case Id.CHAR: case Id.BOOL:
			case Id.INT: case Id.INT32: case Id.INT64: case Id.INT128:
			case Id.LONG: case Id.FLOAT: case Id.DOUBLE:
				parseMember(visitor, modifiers, recovery);
				continue;
			case Id.TILDE:
				consume();//~
				if (Id.$$ID == id()) 
					parseRecordDtor(visitor, modifiers, recovery);
				else Report.message(Report._1_SYNTAX_ERROR, getSource(), getPrevLocation());
				continue;
			case Id.CLASS: case Id.STRUCT: case Id.INTERFACE: case Id.TEMPLATE: case Id.UNION:  {
				parseRecord(visitor, modifiers, recovery|$RECORD_SAFE|Id.$CLOSE_KEY);
				continue;
			}
		} while (recover($RECORD_SAFE, recovery));
	}
	
	private void parseAccessModifier(int[] modifiers, long recovery) {
		modifiers[0] = getIdAndConsume() | (modifiers[0] & ~$$ACCMODS);
		expectAndConsume(Id.$COLON, recovery, "MISS COLON");
	}

	private void parseUsingDir(ScopeVisitor visitor, long recovery) {
		consume(); //using
		IToken token = parseIdentifier(recovery|Id.$SCOLON);
		if (token != null) {
			UsingDecl dir = new UsingDecl(token, getSource(), getAnnotations());
			if (expect(Id.$SCOLON, recovery, Report._1_ERR_MISS_USING_SCOLON))
				consume(); //;
			visitor.visitUsingDecl(dir);
		}
	}
	
	private NsVisitor parseNsDecl(ScopeVisitor visitor, long recovery) {
		consume(); //namespace
		IToken token = parseIdentifier(recovery);
		if (token != null) {
			if (token.isComplex()) { 
				Lexeme[] tokens = token.getLexemes();
				for (int i = 0; i < tokens.length -1; i++) {
					visitor = visitor.visitNamespaceDecl(
						new NsDecl(tokens[i], getSource(), null)
					);
			}	}
			return visitor.visitNamespaceDecl(
				new NsDecl(token, getSource(), getAnnotations())
			);
		}
		return null;
	}
	
	private void parseNestedNsDecl(ScopeVisitor visitor, long recovery) {
		recovery |= $NS_SAFE|Id.$OPEN_KEY|Id.$CLOSE_KEY;
		NsVisitor v = parseNsDecl(visitor, recovery);
		if (v != null) {
			expectAndConsume(Id.$OPEN_KEY, recovery, Report._1_ERR_MISS_NS_OKEY); //{
			parseNsScope(v, recovery|$NS_SAFE|Id.$CLOSE_KEY); 
			expectAndConsume(Id.$CLOSE_KEY, recovery, Report._1_ERR_MISS_NS_CKEY); //}
		}
	}
	
	private void parseTypeDefDecl(ScopeVisitor visitor, long recovery) {
		int modifiers = 0;
		consume(); //typedef
		//FIXME long long
		modifiers = parseModifiers(Id.SIGNED|Id.UNSIGNED|Id.SHORT|Id.LONG, recovery|Id.$$ID|Id.$$TYPE);
		Type type = null;
		if (modifiers != 0 && recover(Id.$$TYPE, recovery|Id.$$ID)) {
			if (id() == Id.INT)	type = new Type(modifiers, getAndConsumeToken());
			else				type = parseVectorType(new int[] {modifiers}, recovery);
		} else {
			if (modifiers != 0) type = new Type(modifiers, new Token(Id.INT, getPrevLocation()));
			else 				type = parseVectorType(new int[] {modifiers}, recovery);
		}		TypeDefDecl decl = null;
		IToken token = parseIdentifier(recovery, "MISS TYPEDEF ID");
		if (token != null) {
			decl = new TypeDefDecl(token, null);
			if (type != null) {
				decl.visitType(type);
			}
		}
		expectAndConsume(Id.$SCOLON, recovery, "MISS TYPEDEF SCOLON");
		visitor.visitTypeDefDecl(decl);
	}
	
	private void parseRecord(ScopeVisitor visitor, int[] modifiers, long recovery) {
		long r = Id.$OPEN_KEY|Id.$SCOLON|Id.$LT_LTLT|Id.$COLON;
		consume(); //class
		IToken token = parseIdentifier(recovery | r);
		if (token != null && recover(r, recovery)) {
			RecordDecl decl = new RecordDecl(modifiers[0], token, getSource(), getAnnotations());
			modifiers[0] = 0;
			TemplDecl d = null;
			DO: do switch (id()) {
				case Id.LTLT:
					Report.message(Report._1_SYNTAX_ERROR, getSource(), getLocation());
				//$FALL-THROUGH$
				case Id.LT:
					if (d == null) { // parse once
						d = parseGenericDecl(Id.$OPEN_KEY|Id.$SCOLON|Id.$COLON);
						if (d != null) decl.visitTemplDecl(d);
					} else recover(Id.$COLON, Id.$OPEN_KEY|Id.$SCOLON);
					continue;
				case Id.COLON:
					parseRecordBaseDecl(decl, Id.$OPEN_KEY|Id.$SCOLON|Id.$CLOSE_KEY);
					break DO;
			} while(recover(Id.$LT_LTLT|Id.$COLON, Id.$OPEN_KEY|Id.$SCOLON));
			
			if (expect(Id.$OPEN_KEY/*|Id.$SCOLON*/, recovery, Report._1_ERR_MISS_RECORD_OKEY)) {
//				decl.forward = id() == Id.$SCOLON;
				consume(); //{ or ;		
//				if (id() == Id.$SCOLON) {
//					decl.forward = true;
//					return;
//				}
			}
			decl.visitBlock();
			parseRecordScope(
				visitor.visitRecordDecl(decl), 
				recovery|$RECORD_SAFE
			);
			if (expect(Id.$CLOSE_KEY, recovery, Report._1_ERR_MISS_RECORD_CKEY)) {
				consume(); //}
			}
		}
	}

	private TemplDecl parseGenericDecl(long recovery) {
		setMode(Lexer.CAPTURE_LTGT);
		consume(); //< 
		if (Id.GT == id()) {
			consume(); //> inferred declaration
			setMode(Lexer.CAPTURE_DEFAULT);
			return new TemplDecl();
		}
		TemplDecl[] decl = new TemplDecl[1];
		FOR: for (;;) {
			parseGenericParam(decl, recovery|Id.$COMMA|Id.$$TYPE|Id.$$ID|Id.$GT_GTGT);
			do switch (id()) {
				case Id.ID:
				case Id.VOID: case Id.SIGNED: case Id.UNSIGNED: 
				case Id.SHORT: case Id.BYTE: case Id.CHAR: case Id.BOOL:
				case Id.INT: case Id.INT32: case Id.INT64: case Id.INT128:
				case Id.LONG: case Id.FLOAT: case Id.DOUBLE:
					Report.message(Report._1_MISS_COMMA, getSource(), getLocation());
					continue FOR;
				case Id.COMMA:
					consume(); //,
					continue FOR;
			} while (recover(Id.$COMMA|Id.$$TYPE|Id.$$ID, recovery|Id.$GT_GTGT)); 
			break FOR;
		}
		if (decl[0] != null && expect(Id.$GT_GTGT, recovery, Report._1_ERR_MISS_TEMPL_GT)) {
			consume(); //>
		}
		setMode(Lexer.CAPTURE_DEFAULT);
		return decl[0];
	}

	private void parseGenericParam(TemplDecl[] ref, long recovery) {
		TemplTypeParam type = parseTemplTypeParam(Report._1_MISS_TEMPL_PARAM, recovery|Id.$COLON|Id.$$ID);
		if (type != null) {
			parseVectorType(type, recovery|Id.$COLON|Id.$$ID);
			do switch (id()) {
				case Id.COLON:
					consume(); //:
					Type d = parseVectorType(Report._1_MISS_PARAM_CONST, new int[]{0}, recovery);
					if (d != null) type.visitConstraint(d);
					if (ref[0] == null) ref[0] = new TemplDecl();
					ref[0].visitTypeParam(type);
					return;
				case Id.ID:
					IToken token = parseIdentifier(recovery);
					if (token != null) {
						if (ref[0] == null) ref[0] = new TemplDecl();
						ref[0].visitVarParam(
							new VarDecl(type, token, getSource(), null)
						);
					return;
				}
			} while (recover(Id.$COLON|Id.$$ID, recovery));
			if (ref[0] == null) ref[0] = new TemplDecl();
			ref[0].visitTypeParam(type);
	}	}

	private void parseRecordBaseDecl(RecordDecl decl, final long recovery) {
		consume(); //:
		int[] modifiers = new int[1];
		final int r = Id.PUBLIC|Id.PROTECTED|Id.INTERNAL|Id.PRIVATE;
		modifiers[0] = parseModifiers(r, recovery|Id.$$ID|Id.$$TYPE);
		Type type = parseType(modifiers, r|recovery|Id.$COMMA|Id.$$ID|Id.$$TYPE);
		if (type != null) {
			decl.visitBaseDecl(type);
		}
		while (recover(r|Id.$$ID|Id.$$TYPE|Id.$COMMA, recovery)) {
			expectAndConsume(Id.$COMMA, Id.$$ALL, Report._1_MISS_COMMA);
			modifiers[0] = parseModifiers(r, recovery|Id.$$ID|Id.$$TYPE);
			type = parseType(modifiers, r|recovery|Id.$$ID|Id.$$TYPE);
			if (type != null) {
				decl.visitBaseDecl(type);
	}	}	}

	private VarDecl parseVarDecl(int[] modifiers, long recovery) {
		return parseVarDecl(null, modifiers, recovery);
	}
	
	private VarDecl parseVarDecl(IToken token, int[] modifiers, long recovery) {
		recovery |= Id.$$OP;
		Type type = parseVectorType(token, modifiers, recovery);
		if (type != null) {
			if (modifiers != null) modifiers[0] = 0;
			VarDecl decl = new VarDecl(
				type, 
				parseIdentifier(recovery),
				getSource(), 
				getAnnotations()
			);
			if (recover(Id.$$OP, recovery)) {
				if (id() != Id.EQ) 
					Report.message(Report._1_ILL_ASSIGN_OP, getSource(), getLocation());
				consume(); //=
				IExpr expr = parseExpression(recovery);
				if (expr != null) expr.accept(decl);
			}
			return decl;
		}
		return null;
	}

	private void parseRecordDtor(ScopeVisitor visitor, int[] modifiers, long recovery) {
		consume();//~
		IToken token = parseIdentifier(recovery);
		if (token != null) {
			MethodDecl decl = new MethodDecl(modifiers[0], token, getSource(), getAnnotations());
			if (expect(Id.$OPEN_PAR, recovery, "DTOR")) consume(); //(
			if (expect(Id.$CLOSE_PAR, recovery, "DTOR")) consume(); //)
			if (expect(Id.$OPEN_KEY|Id.$SCOLON, recovery, "DTOR")) consume(); //; or {
			
			BlockVisitor v = null;
			if (Id.$OPEN_KEY == id()) {
				v = decl.visitCodeBlock();
			}
			visitor.visitMethodDecl(decl);
			if (v != null) {
//				parseBlockScope(v, recovery);
				// TODO skip block
			}
			modifiers[0] = 0;
	}	}

	private void parseMember(ScopeVisitor visitor, int[] modifiers, long recovery) {
		IToken token = parseTypeOrId(Id.$$ALL);
		if (token != null) {
			
			Type type = null;
			TemplDecl templ = null;
			if (recover(Id.$LT_LTLT, Id.$$ALL)) {
				templ = parseGenericDecl(recovery|Id.$OPEN_PAR|Id.$CLOSE_PAR|Id.SCOLON|Id.$OPEN_KEY);
			}
			if (id() != Id.OPEN_PAR || token.getId() != Id.ID || token.isComplex()) { 
				type = new Type(modifiers[0], token);
				if (templ != null) {
					type.visitTemplDecl(templ);
				}
				parseVectorType(type, recovery);
				token = parseIdentifier(Id.$$ALL);
				if (recover(Id.$LT_LTLT, Id.$$ALL)) {
					templ = parseGenericDecl(recovery|Id.$OPEN_PAR|Id.$CLOSE_PAR|Id.SCOLON|Id.$OPEN_KEY);
			}	}
			
			IExpr expr = null;
			DO: do switch (id()) {
				case Id.OPEN_PAR:
					if (type != null) {
						MethodDecl decl = new MethodDecl(modifiers[0], token, getSource(), getAnnotations());
						decl.visitReturnType(type);
						if (templ != null) decl.visitTemplDecl(templ);
						parseMethodDecl(visitor, decl, recovery);
					} else {
						MethodDecl decl = new MethodDecl(modifiers[0], token, getSource(), getAnnotations());
						if (templ != null) decl.visitTemplDecl(templ);
						parseMethodDecl(visitor, decl, recovery);
					}
					modifiers[0] = 0;
					return;
				default: // Handle all operators 
					if (!is(Id.$$OP)) {
						continue; //CONTINUE to recover
					}
					if (id() != Id.EQ) {
						Report.message(Report._1_ILL_ASSIGN_OP, getSource(), getLocation());
					}
					consume(); //=
					expr = parseExpression(recovery|Id.$SCOLON);
					//$FALL-THROUGH$
				case Id.SCOLON:
					VarDecl decl = new VarDecl(type, token, getSource(), getAnnotations());
					if (expr != null) expr.accept(decl);
//					if (scope.getVarDecl(token.getName()) != null) {
//						Report.message(Report._1_ERR_DUP_ID, token);
//					}
					visitor.visitVarDecl(decl);
					break DO;
			} while (recover(Id.$OPEN_PAR|Id.$SCOLON|Id.$$OP, recovery));
			if (expect(Id.$SCOLON, recovery, Report._1_ERR_MISS_STMT_SCOLON)) {
				consume(); //;
	}	}	}
	
	private void parseMethodDecl(ScopeVisitor visitor, MethodDecl decl, long recovery) {
		recovery |= Id.$OPEN_KEY|Id.$SCOLON;
		parseMethodDeclArgs(decl, recovery);
		if (recover(Id.CONST, recovery)) {
			decl.modifiers |= Id.CONST;
		}		
		if (expect(Id.$OPEN_KEY|Id.$SCOLON, recovery, Report._1_ERR_MISS_METHOD_SYMBOL)) {
			if (id() == Id.OPEN_KEY) {
				consume(); //{
//				parseBlockScope(
//					decl.visitCodeBlock(),
//					recovery
//				);
				// TODO skip block
			} else consume();
		}
		visitor.visitMethodDecl(decl);
	}

	private void parseMethodDeclArgs(MethodDecl decl, long recovery) {
		consume(); //(
		recovery |= Id.$COMMA|Id.$CLOSE_PAR;
		int[] modifiers = new int[1];
		int r = Id.PUBLIC|Id.PROTECTED|Id.INTERNAL|Id.PRIVATE;
		if (!recover(Id.$CLOSE_PAR, recovery)) {
			modifiers[0] = parseModifiers(r, recovery);
			VarDecl d = parseVarDecl(modifiers, recovery);
			if (d != null) {
				decl.visitVarDecl(d);
			}
			while (recover(r|Id.$COMMA|Id.$$TYPE|Id.$$ID, recovery)) {
				if (expect(Id.$COMMA, Id.$$ALL, Report._1_MISS_COMMA)) {
					consume(); //,
				}
				modifiers[0] = parseModifiers(r, recovery);
				d = parseVarDecl(modifiers, recovery);
				if (d != null) {
					decl.visitVarDecl(d);
		}	}	}
		
		if (expect(Id.$CLOSE_PAR, recovery, Report._1_ERR_MISS_METHOD_CPAR)) {
			consume(); //)
	}	}
	
	private Type parseType(int[] modifiers, long recovery) {
		return parseType(Report._1_MISS_TYPE, null, modifiers, recovery);
	}
	
	private Type parseType(String message, int[] modifiers, long recovery) {
		return parseType(message, null, modifiers, recovery);
	}
	
	private Type parseType(IToken token, int[] modifiers, long recovery) {
		return parseType(null, token, modifiers, recovery);
	}
	
	private Type parseType(String message, IToken token, int[] modifiers, long recovery) {
		if (token == null) 
			token = parseTypeOrId(recovery|Id.$LT_LTLT|Id.$QMARK, message);
		if (token != null) {
			if (modifiers == null) modifiers = new int[] {0};
			Type decl = new Type(modifiers[0], token);
			modifiers[0] = 0;
			parseType(decl, recovery);
			return decl;
		}
		return null;
	}
	
	private TemplTypeParam parseTemplTypeParam(String message, long recovery) {
		IToken token = parseTypeOrId(recovery|Id.$LT_LTLT|Id.$QMARK, message);
		if (token != null) {
			TemplTypeParam type = new TemplTypeParam(0, token);
			parseType(type, recovery);
			return type;
		}
		return null;
	}
	
	private void parseType(Type type, long recovery) {
		DO: do switch (id()) {
		case Id.LTLT: 
			Report.message(Report._1_SYNTAX_ERROR, getSource(), getLocation());
			//$FALL-THROUGH$
		case Id.LT:
			TemplDecl d = parseGenericDecl(recovery|Id.$LT_LTLT);
			if (d != null) type.visitTemplDecl(d);
			break DO;
		} while (recover(Id.$LT_LTLT, recovery));
		if (recover(Id.$QMARK, Id.$$ALL)) {
			type.setNullable(true);
			consume(); //?
	}	}
	
	private Type parseVectorType(int[] modifiers, long recovery) {
		return parseVectorType(Report._1_MISS_TYPE, null, modifiers, recovery);
	}
	
	private Type parseVectorType(String msg, int[] modifiers, long recovery) {
		return parseVectorType(msg, null, modifiers, recovery);
	}
	
	private Type parseVectorType(IToken token, int[] modifiers, long recovery) {
		return parseVectorType(Report._1_MISS_TYPE, token, modifiers, recovery);
	}
	
	private Type parseVectorType(String msg, IToken token, int[] modifiers, long recovery) {
		recovery |= Id.$OPEN_BRA|Id.$STAR|Id.$AMP|Id.$QMARK|Id.CONST|Id.UNSAFE;
		Type type = parseType(msg, token, modifiers, recovery);
		if (type != null) {
			parseVectorType(type, recovery);
		}
		return type;
	}
	
	private void parseVectorType(Type decl, long recovery) {
		do {
			boolean nullable = false;
			int modifiers = parseModifiers(Id.CONST|Id.UNSAFE, recovery);
			switch (id()) {
				case Id.OPEN_BRA:
					consume(); //[
					if (expect(Id.$CLOSE_BRA, recovery, Report._1_ERR_MISS_ARRAY_EXPR)) {
						consume(); //]
						if (nullable = recover(Id.$QMARK, Id.$$ALL)) 
							consume(); //?
						decl.visitArrayDimension(modifiers, nullable);
						modifiers = 0; nullable = false; continue;
					}
					continue;
				case Id.STAR:
					consume(); //*
					if (nullable = recover(Id.$QMARK, Id.$$ALL)) 
						consume(); //?
					decl.visitPointerDimension(modifiers, nullable);
					modifiers = 0; nullable = false; continue;
				case Id.AMP:
					consume(); //&
					if (nullable = recover(Id.$QMARK, Id.$$ALL)) 
						consume(); //?
					decl.visitReferenceDimension(modifiers, nullable);
					modifiers = 0; nullable = false; continue;
				case Id.QMARK:
			}
		} while (recover(Id.$OPEN_BRA|Id.$STAR|Id.$AMP|Id.$QMARK, recovery));
	}
	
	private IExpr parseExpression(long recovery) {
		return preParseExpr(null, recovery|$EXPR_SAFE);
	}
	
	public IExpr parseExpression(IToken token, long recovery) {
		recovery |= $EXPR_SAFE;
		Expression prev = null;
		if (token != null) {
			if (token.isComplex()) 
				 prev = parseExpr(token.getLexemes());
			else if (recover(Id.$OPEN_PAR, recovery))
				 return parseCallExpr(null, token, recovery);
			else return postParseExpr(
				null, new LexExpr(token), recovery
			);
		}
		return preParseExpr(prev, recovery|$EXPR_SAFE);
	}
	
	private Expression buildExpr(Expression/*?*/ e1, Expression e2, IExpr o) {
		if (e1 != null) {
			if (Id.Op.compare(e1.getId(), e2.getId()) > 0) {
				e1.accept(e2);
				if (o != null) 
					o.accept(e1);
				return e2;
			} else {
				if (o != null) 
					o.accept(e2);
				e2.accept(e1);
				return e1;
		}	}
		if (o != null) 
			o.accept(e2);
		return e2;
	}
	
	private IExpr parseExpr(Expression prev, Expression curr, IExpr o, long recovery) {
		Expression expr = buildExpr(prev, curr, o);
		IExpr e = preParseExpr(curr, recovery);
		if (expr == prev) {
			if (curr != e) 
				e.accept(expr);
			return expr;
		}
		return e;
	}
	
	private IExpr parseCastExpr(Expression prev, Type type, long recovery) {
		Expression curr = new CastExpr(type);
//		Expression expr = buildExpr(prev, curr, null);
		return parseExpr(prev, curr, null, recovery);
	}

	private IExpr parseArrayExpr(Expression prev, IExpr operand, long recovery) {
		BinaryExpr expr = new BinaryExpr(getIdAndConsume()); //[
		IExpr e = parseExpr(prev, expr, operand, recovery|Id.$CLOSE_BRA);
		if (expect(Id.$CLOSE_BRA, recovery, Report._1_ERR_MISS_ARRAY_EXPR)) {
			consume(); //]
		}
		return postParseExpr(expr, null, recovery);
	}
	
	private IExpr parseCallExpr(Expression prev, IToken token, long recovery) {
		consume(); //(
		CallExpr expr = new CallExpr(token);
		long r = recovery|Id.$COMMA|Id.$CLOSE_PAR;
		if (!recover(Id.$CLOSE_PAR, r)) {
			IExpr e = parseExpression(r);
			if (e != null) e.accept(expr);
			while (recover(Id.$COMMA, r)) {
				if (expect(Id.$COMMA, Id.$$ALL, Report._1_MISS_COMMA)) {
					consume(); //,
				}
				e = parseExpression(r);
				if (e != null) e.accept(expr);
			}
		}
		if (expect(Id.$CLOSE_PAR, recovery, Report._1_ERR_MISS_CALL_CPAR)) {
			consume(); //)
		}
		return parseExpr(prev, expr, null, recovery);
	}
	
	public IExpr preParseExpr(Expression expr, long recovery) {
		do switch (id()) {
			case Id.PLUS: case Id.MINUS:
			case Id.MINUSMINUS: case Id.PLUSPLUS: 
				return parseExpr(expr, 
					// Switches operator to PREDEC, PREINC, POSITIVE and NEGATIVE respectively
					new UnaryExpr(getIdAndConsume() +1), null, recovery
				);
			case Id.STAR: case Id.AMP: case Id.NOT: 
			case Id.NEW: case Id.DELETE:	
			case Id.SIZEOF:	
				return parseExpr(
					expr, new UnaryExpr(getIdAndConsume()), null, recovery
				);
			case Id.ID:
			case Id.THIS:
				Lexeme token = getLexemeAndConsumeToken();
				if (recover(Id.$OPEN_PAR, recovery)) {
					 return parseCallExpr(expr, token, recovery);
				} else {
					return postParseExpr(
						expr, new LexExpr(token), recovery
					);
				}
			case Id.STRING_LITERAL:
			case Id.NULL_LITERAL: case Id.CHAR_LITERAL: case Id.HEX_LITERAL: 
			case Id.INT_LITERAL: case Id.UINT_LITERAL: case Id.LONG_LITERAL: case Id.ULONG_LITERAL: 
			case Id.FLOAT_LITERAL: case Id.DOUBLE_LITERAL: case Id.BOOLEAN_LITERAL:
				return postParseExpr(
					expr, getLexemeAndConsumeToken(), recovery
				);
			case Id.OPEN_PAR: 
				return parseCastOrSubExpr(expr, recovery);
				
			default:
				if (is(Id.$$OP)) { // Operator not in case
					Report.message(Report._1_SYNTAX_ERROR, getSource(), getLocation());
					consume(); //
				}
		} while (recover(Id.$$OP|Id.$$ID|Id.$$LITE, recovery));
		
		if (expr == null) {
			Report.message(Report._1_MISS_EXPR, getSource(), getPrevLocation());
		} else if (!expr.isValid()) {
			Report.message(Report._1_ILL_EXPR, getSource(), getPrevLocation());
		}
		return expr;
	}

	private IExpr postParseExpr(Expression expr, IExpr operand, long recovery) {
		DO: do switch (id()) {
			case Id.MINUSMINUS: 
			case Id.PLUSPLUS: 
				return parseExpr(
					expr, new UnaryExpr(getIdAndConsume()), operand, recovery
				);
			case Id.DOT:
			case Id.PLUS: case Id.MINUS:
			case Id.LT: case Id.GTGT: case Id.STAR: 
			case Id.EQ: case Id.PLUSEQ: case Id.MINUSEQ: 
			case Id.MULTEQ: case Id.DIVEQ: case Id.MODEQ: case Id.XOREQ: case Id.ANDEQ: case Id.OREQ: 
			case Id.LTLT: case Id.LTLTEQ: case Id.GTGTEQ: case Id.EQEQ: case Id.NE: case Id.LTEQ: 
			case Id.GTEQ: case Id.ANDAND: case Id.OROR:
			case Id.AMP: case Id.OR: case Id.XOR:
				return parseExpr(
					expr, new BinaryExpr(getIdAndConsume()), operand, recovery
				);
			case Id.OPEN_BRA:
				return parseArrayExpr(expr, operand, recovery);
				
//			case Id.OPEN_PAR:
//				return parseCallExpr(expr, operand, recovery);
			default: break DO; //FIXME infilite loop here --> FIXED!
				
		} while (recover(Id.$$OP|Id.$$ID|Id.$$LITE, recovery));
		
		if (operand != null && expr != null) {
			operand.accept(expr);
		}
		return (expr != null) ? expr : operand;
	}

	public Expression parseExpr(Lexeme[] lexemes) {
		assert (lexemes.length > 1);
		Expression prev = null, curr = null;
		for (int i = 0; i < lexemes.length -1; i++) {
			curr = new BinaryExpr(Id.Op.SCOPE_RESOLUTION | Id.Op.MEMBER_ACCESS);
			prev = buildExpr(prev, curr, lexemes[i]);
		}
		lexemes[lexemes.length -1].accept(curr);
		return prev;
	}
	
	private IExpr parseCastOrSubExpr(Expression prev, long recovery) {		
		consume(); //(
		long r = recovery|Id.$CLOSE_PAR|Id.$LT_LTLT|Id.$OPEN_BRA|Id.$STAR|Id.$AMP|Id.$QMARK;
		if (recover(Id.$$ID|Id.$$TYPE, r)) {
			IToken token = parseTypeOrId(r);
			DO: do switch (id()) {
				case Id.CLOSE_PAR: {
					consume(); //)
					if (is(Id.$$OP|Id.$$TYPE|Id.$$ID)) break DO;
					Type decl = new Type(0, token);
					IExpr o = parseCastExpr(prev, decl, recovery);
					return postParseExpr(prev, o, recovery);
				}
				case Id.OPEN_BRA:
					if (id() == Id.$OPEN_BRA && !match(']')) break DO;
					//$FALL-THROUGH$
				case Id.LT: case Id.LTLT:
				case Id.STAR: case Id.AMP: case Id.QMARK: {
					Type decl = parseVectorType(token, null, recovery);
					expect(Id.$CLOSE_PAR, recovery, Report._1_ERR_MISS_CAST_CPAR);
					consume(); //)
					return postParseExpr(
						prev, parseCastExpr(prev, decl, recovery), recovery
					);
				}
			} while (recover(Id.$CLOSE_PAR|Id.$LT_LTLT|Id.$OPEN_BRA|Id.$STAR|Id.$AMP|Id.$QMARK, recovery));
			return postParseExpr( // FIXME prev vs curr expr?
				(token.isComplex()) ? parseExpr(token.getLexemes()) : null, token.getLexeme(), recovery
			);
		}
		return preParseExpr(null, recovery);
	}
	
}
