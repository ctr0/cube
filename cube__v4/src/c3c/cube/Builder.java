package c3c.cube;

import java.io.FileNotFoundException;
import java.io.IOException;

import c3c.ast.Annotation;
import c3c.ast.BinaryExpr;
import c3c.ast.CallExpr;
import c3c.ast.CastExpr;
import c3c.ast.ComplUnit;
import c3c.ast.IExpr;
import c3c.ast.IExpr.Expression;
import c3c.ast.IStatement;
import c3c.ast.IStatement.AssignStat;
import c3c.ast.IStatement.CallStat;
import c3c.ast.IStatement.NewStat;
import c3c.ast.IStatement.UnaryStat;
import c3c.ast.IType.TemplTypeParam;
import c3c.ast.IType.Type;
import c3c.ast.InstrStat;
import c3c.ast.InstrStat.InstrBlockStat;
import c3c.ast.InstrStat.InstrExprBlockStat;
import c3c.ast.InstrStat.InstrExprStat;
import c3c.ast.InstrStat.InstrForStat;
import c3c.ast.LexExpr;
import c3c.ast.MethodDecl;
import c3c.ast.NewExpr;
import c3c.ast.NsDecl;
import c3c.ast.RecordDecl;
import c3c.ast.TemplDecl;
import c3c.ast.TernaryExpr;
import c3c.ast.UnaryExpr;
import c3c.ast.UsingDecl;
import c3c.ast.VarDecl;
import c3c.ast.cpp.TypeDefDecl;
import c3c.ast.visitor.BlockVisitor;
import c3c.ast.visitor.ExprVisitor;
import c3c.ast.visitor.ScopeVisitor;
import c3c.ast.visitor.ScopeVisitor.NsVisitor;

public class Builder extends CubePrep {
	
	private static final long $RECORD_SAFE = 	Id.$$MODS|Id.$$RECO|Id.$$ID|Id.$$TYPE;
	private static final long $NS_SAFE = 		Id.$NS|$RECORD_SAFE;
	private static final long $UNIT_SAFE = 		Id.$USING|$NS_SAFE;
	private static final long $EXPR_SAFE = 		Id.$$ID|Id.$$LITE|Id.$$TYPE|Id.$$OP;
	private static final long $BLOCK_SAFE = 	Id.$$STMT|Id.$$ID|Id.$$TYPE|Id.$$MODS;
	
	public Builder(ComplUnit unit) throws FileNotFoundException, IOException {
		super(unit);
	}

	public void parse(ScopeVisitor visitor) {
		consume(); // <start>
		do switch (id()) {
			case Id.USING: 
				parseUsingDir(visitor, $UNIT_SAFE); 
				continue;
			case Id.NS:
				parseTopNsDecl(visitor, $UNIT_SAFE);
				continue;
			default: parseNsScope(visitor, $UNIT_SAFE);
		} while (recover($UNIT_SAFE, $UNIT_SAFE));
	}

	public void parseNsScope(ScopeVisitor visitor, long recovery) {
		do switch (id()) {
			case Id.NS:
				parseNestedNsDecl(visitor, recovery);
				continue;
			default: parseRecordScope(visitor, recovery);
		} while (recover($NS_SAFE, recovery));
	}
	
	public void parseRecordScope(ScopeVisitor visitor, long recovery) {
		int[] modifiers = new int[1];
		do switch (id()) {
			case Id.PUBLIC: case Id.PRIVATE: case Id.PROTECTED: case Id.INTERNAL: 
			case Id.ABSTRACT: case Id.VIRTUAL: case Id.CONST: case Id.SYNCHRONIZED: 
			case Id.STATIC:	case Id.INLINE:	case Id.VOLATILE: case Id.UNSAFE: 
				modifiers[0] = parseModifiers(Id.$$MODS, recovery|Id.$SCOLON|Id.$OPEN_KEY|Id.$OPEN_PAR);
				continue;
			case Id.ID:
			case Id.SIGNED: case Id.UNSIGNED: case Id.SHORT: case Id.LONG: 
			case Id.BYTE: case Id.CHAR: case Id.BOOL:
			case Id.INT: case Id.INT32: case Id.INT64: case Id.INT128:
			case Id.FLOAT: case Id.DOUBLE:
			case Id.VOID: case Id.STRING: case Id.OBJECT: case Id.DELEGATE:
				parseRecordMember(visitor, modifiers, recovery);
				continue;
			case Id.TILDE:
				consume();//~
				if (Id.$$ID == id()) 
					parseRecordDtor(visitor, modifiers, recovery);
				else Report.message(Report._1_SYNTAX_ERROR, getSource(), getPrevLocation());
				continue;
			case Id.CLASS: case Id.STRUCT: case Id.INTERFACE: case Id.TEMPLATE: case Id.UNION:
				parseRecord(visitor, modifiers, recovery|$RECORD_SAFE|Id.$CLOSE_KEY); // FIXME $RECORD_SAFE in reovery???
				continue;
			case Id.ENUM:
				parseEnum(visitor, modifiers, recovery|$RECORD_SAFE|Id.$CLOSE_KEY); // FIXME $RECORD_SAFE in reovery???
				continue;
			case Id.TYPEDEF:
				parseTypeDef(visitor, modifiers, recovery);
		} while (recover($RECORD_SAFE, recovery));
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
	
	private void parseTopNsDecl(ScopeVisitor visitor, long recovery) {
		recovery |= $NS_SAFE|Id.$SCOLON|Id.$OPEN_KEY|Id.$CLOSE_KEY;
		NsVisitor v = parseNsDecl(visitor, recovery);
		if (v != null) {
			if (expect(Id.$SCOLON|Id.$OPEN_KEY, recovery, Report._1_ERR_MISS_NS_SC))
				consume(); //{ or ;
			parseNsScope(v, recovery|$NS_SAFE);
		}
	}
	
	private void parseNestedNsDecl(ScopeVisitor visitor, long recovery) {
		recovery |= $NS_SAFE|Id.$OPEN_KEY|Id.$CLOSE_KEY;
		NsVisitor v = parseNsDecl(visitor, recovery);
		if (v != null) {
			if (expect(Id.$OPEN_KEY, recovery, Report._1_ERR_MISS_NS_OKEY))
				consume(); //{
			parseNsScope(v, recovery|$NS_SAFE|Id.$CLOSE_KEY); 
			if (expect(Id.$CLOSE_KEY, recovery, Report._1_ERR_MISS_NS_CKEY))
				consume(); //}
		}
	}
	
	private void parseRecord(ScopeVisitor visitor, int[] modifiers, long recovery) {
		long r = Id.$OPEN_KEY|Id.$SCOLON|Id.$LT_LTLT|Id.$COLON;
		consume(); //class
		IToken token = parseTypeOrId(recovery | r); // allow types as class names TODO other kws?
		if (token != null /*&& recover(r, recovery)*/) {
			RecordDecl decl = new RecordDecl(
				modifiers[0], token, getSource(), getAnnotations()
			);
			modifiers[0] = 0;
			TemplDecl templ = null;
			DO: do switch (id()) {
				case Id.LTLT:
					Report.message(Report._1_SYNTAX_ERROR, getSource(), getLocation());
				//$FALL-THROUGH$
				case Id.LT:
					if (templ == null) { // parse once
						templ = parseTemplDecl(Id.$OPEN_KEY|Id.$SCOLON|Id.$COLON);
						if (templ != null) decl.visitTemplDecl(templ);
					} else recover(Id.$COLON, Id.$OPEN_KEY|Id.$SCOLON);
					continue;
				case Id.COLON:
					parseRecordBaseDecl(decl, Id.$OPEN_KEY|Id.$SCOLON|Id.$CLOSE_KEY);
					break DO;
			} while(recover(Id.$LT_LTLT|Id.$COLON, Id.$OPEN_KEY|Id.$SCOLON));
			expectAndConsume(Id.$OPEN_KEY, recovery, Report._1_ERR_MISS_RECORD_OKEY);
			decl.visitBlock();
			parseRecordScope(
				visitor.visitRecordDecl(decl), recovery|$RECORD_SAFE
			);
			expectAndConsume(Id.$CLOSE_KEY, recovery, Report._1_ERR_MISS_RECORD_CKEY);
		}
	}
	
	private void parseEnum(ScopeVisitor visitor, int[] modifiers, long recovery) {
		long r = Id.$OPEN_KEY;
		consume(); //enum
		IToken token = parseTypeOrId(recovery | r); // allow types as class names TODO other kws?
		if (token != null /*&& recover(r, recovery)*/) {
			RecordDecl decl = new RecordDecl(
				modifiers[0], token, getSource(), getAnnotations()
			);
			modifiers[0] = 0;
			expectAndConsume(Id.$OPEN_KEY/*|Id.$SCOLON*/, recovery, Report._1_ERR_MISS_RECORD_OKEY);
			decl.visitBlock();
			parseEnumScope(
				visitor.visitRecordDecl(decl), recovery
			);
			expectAndConsume(Id.$CLOSE_KEY, recovery, Report._1_ERR_MISS_RECORD_CKEY);
		}
	}
	
	public void parseEnumScope(ScopeVisitor visitor, long recovery) {
		if (!recover(Id.$CLOSE_KEY, recovery)) {
			IToken token = parseTypeOrId(recovery|Id.$EQ|Id.$COMMA);
			if (token != null) {
				VarDecl d = new VarDecl(null, token, getSource(), getAnnotations());
				visitor.visitVarDecl(d);
			}
			while (recover(Id.$COMMA|Id.$$ID, recovery)) {
				expectAndConsume(Id.$COMMA, Id.$$ALL, Report._1_MISS_COMMA);
				 token = parseTypeOrId(recovery|Id.$EQ|Id.$COMMA);
				if (token != null) {
					VarDecl d = new VarDecl(null, token, getSource(), getAnnotations());
					visitor.visitVarDecl(d);
	}	}	}	}
	
	private void parseTypeDef(ScopeVisitor visitor, int[] modifiers, long recovery) {
		consume(); //typedef
		IToken token = parseIdentifier(recovery, "Missing typedef alias");
		if (token != null) {
			Type type = parseVectorType(recovery);
			if (type != null) {
				TypeDefDecl decl = new TypeDefDecl(type, token, getSource(), getAnnotations());
				visitor.visitTypeDefDecl(decl);
		}	}
		expectAndConsume(Id.$SCOLON, recovery, Report._1_ERR_MISS_STMT_SCOLON);
	}

	private TemplDecl parseTemplDecl(long recovery) {
		setMode(Lexer.CAPTURE_LTGT);
		consume(); //< 
		if (Id.GT == id()) {
			consume(); //> inferred declaration
			setMode(Lexer.CAPTURE_DEFAULT);
			return new TemplDecl();
		}
		TemplDecl[] decl = new TemplDecl[1];
		FOR: for (;;) {
			parseTemplParam(decl, recovery|Id.$COMMA|Id.$$TYPE|Id.$$ID|Id.$GT_GTGT);
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
	
	private void parseTemplParam(TemplDecl[] ref, long recovery) {
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
	
	private TemplDecl parseTempl2(long recovery) {
		setMode(Lexer.CAPTURE_LTGT);
		consume(); //< 
		if (Id.GT == id()) {
			consume(); //> inferred declaration
			setMode(Lexer.CAPTURE_DEFAULT);
			return new TemplDecl();
		}
		TemplDecl[] templ = new TemplDecl[1];
		parseTempl2(templ, recovery);
		return templ[0];
	}
	
	private void parseTempl2(TemplDecl[] ref, long recovery) {
		long r = Id.$$OP|Id.$$LITE|Id.$LT_LTLT|Id.$OPEN_PAR|Id.$COMMA;
		for (;;) {
			if (recover(Id.$$ID, recovery|r)) {
				IToken token = parseTypeOrId(recovery|r);
				// LT | COMMA | OPERATOR
				if (recover(Id.$$OP, recovery|r)) {
					// [ * < > 
					int op = 0;
					switch (id()) {
					case Id.OPEN_BRA: 
						op = getIdAndConsume();
						if (recover(Id.$CLOSE_BRA, $EXPR_SAFE|Id.$GT_GTGT)) {
							// array type
						} else {
							// array expression
							IExpr expr = parseArrayExpr(
								null, parseExpr(token.getLexemes()), op, recovery
							);
							if (expr != null) {
								//ref[0].vi
							}
						}
						break;

					case Id.STAR:
						op = getIdAndConsume();
						if (recover(Id.$$ID|Id.$$LITE, $EXPR_SAFE|Id.$GT_GTGT)) {
							// binary expression
							IExpr expr = parseExpr(
								null, new BinaryExpr(op), parseExpr(token.getLexemes()), recovery
							);
						} else {
							// pointer type
						}
					}
				}
				if (recover(Id.$LT_LTLT|Id.$COMMA, recovery|r)) {
					TemplTypeParam type = new TemplTypeParam(0, token);
					parseType(type, recovery|Id.$COMMA);
					ref[0].visitTypeParam(type);
				}
			} else {
				
			}
		}
	}

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
		Type type = parseVectorType(token, modifiers, recovery);
		if (type != null) {
			recovery |= Id.$$OP;
			if (modifiers != null) modifiers[0] = 0;
			VarDecl decl = new VarDecl(
				type, parseIdentifier(recovery), getSource(), getAnnotations()
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
			MethodDecl decl = new MethodDecl(
				modifiers[0], token, getSource(), getAnnotations()
			);
			if (expect(Id.$OPEN_PAR, recovery, "DTOR")) consume(); //(
			if (expect(Id.$CLOSE_PAR, recovery, "DTOR")) consume(); //)
			if (expect(Id.$OPEN_KEY|Id.$SCOLON, recovery, "DTOR")) consume(); //; or {
			
			BlockVisitor v = null;
			if (Id.$OPEN_KEY == id()) {
				v = decl.visitCodeBlock();
			}
			visitor.visitMethodDecl(decl);
			if (v != null) {
				parseBlockScope(v, recovery);
			}
			modifiers[0] = 0;
	}	}
	
	private void parseRecordMember(ScopeVisitor visitor, int[] modifiers, long recovery) {
		IToken token = parseTypeOrId(Id.$$ALL);
		if (token != null) {
			Type type = null;
			TemplDecl templ = null;
			if (recover(Id.$LT_LTLT, Id.$$ALL)) {
				templ = parseTemplDecl(recovery|Id.$OPEN_PAR|Id.$CLOSE_PAR|Id.$SCOLON|Id.$OPEN_KEY);
			}
			if (id() != Id.OPEN_PAR || token.getId() != Id.ID || token.isComplex()) { 
				type = new Type(modifiers[0], token);
				if (templ != null) {
					type.visitTemplDecl(templ);
				}
				parseVectorType(type, recovery);
				if (id() == Id.OPERATOR) {
					consume(); // operator;
					if (expect(Id.$$OP, /*recovery|*/Id.$OPEN_PAR|Id.$CLOSE_PAR|Id.$SCOLON|Id.$OPEN_KEY, "MISS OP OP")) {
						token = getAndConsumeToken();
						if (token.getId() == Id.OPEN_BRA) {
							expectAndConsume(Id.$CLOSE_BRA, recovery, Report._1_ERR_MISS_ARRAY_EXPR);
						}
					} else {
						// TODO here
					}
				} else {
					token = parseIdentifier(recovery|Id.$$OP|Id.$OPEN_PAR|Id.$CLOSE_PAR|Id.$SCOLON|Id.$OPEN_KEY);
					if (recover(Id.$LT_LTLT, recovery|Id.$$OP|Id.$OPEN_PAR|Id.$CLOSE_PAR|Id.$SCOLON|Id.$OPEN_KEY)) {
						templ = parseTemplDecl(recovery|Id.$$OP|Id.$OPEN_PAR|Id.$CLOSE_PAR|Id.$SCOLON|Id.$OPEN_KEY);
					}
				}
				// TODO token == null ???
			}
			
			IExpr expr = null;
			DO: do switch (id()) {
				case Id.OPEN_PAR:
					if (type != null) {
						MethodDecl decl = new MethodDecl(
							modifiers[0], token, getSource(), getAnnotations()
						);
						decl.visitReturnType(type);
						if (templ != null) decl.visitTemplDecl(templ);
						parseMethodDecl(visitor, decl, recovery);
					} else {
						MethodDecl decl = new MethodDecl(
							modifiers[0], token, getSource(), getAnnotations()
						);
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
					VarDecl decl = new VarDecl(
						type, token, getSource(), getAnnotations()
					);
					if (expr != null) expr.accept(decl);
//					if (scope.getVarDecl(token.getName()) != null) {
//						Report.message(Report._1_ERR_DUP_ID, token);
//					}
					visitor.visitVarDecl(decl);
					break DO;
			} while (recover(Id.$OPEN_PAR|Id.$SCOLON|Id.$$OP, recovery));
			expectAndConsume(Id.$SCOLON, recovery, Report._1_ERR_MISS_STMT_SCOLON);
	}	}
	
	private void parseMethodDecl(ScopeVisitor visitor, MethodDecl decl, long recovery) {
		recovery |= Id.$OPEN_KEY|Id.$SCOLON;
		parseMethodDeclArgs(decl, recovery);
		if (recover(Id.CONST, recovery)) {
			decl.modifiers |= Id.CONST;
			consume();
		}		
		if (expect(Id.$OPEN_KEY|Id.$SCOLON, recovery, Report._1_ERR_MISS_METHOD_SYMBOL)) {
			if (id() == Id.OPEN_KEY) {
				consume(); //{
				parseBlockScope(
					decl.visitCodeBlock(),
					recovery
				);
			} else consume();
		}
		visitor.visitMethodDecl(decl);
	}

	private void parseMethodDeclArgs(MethodDecl decl, long recovery) {
		consume(); //(
		recovery |= Id.$COMMA|Id.$CLOSE_PAR|Id.$ATSIGN;
		int[] modifiers = new int[1];
		int r = Id.CONST|Id.UNSAFE;
		if (!recover(Id.$CLOSE_PAR, r|recovery)) {
			modifiers[0] = parseModifiers(r, recovery);
			VarDecl d = parseVarDecl(modifiers, recovery);
			if (d != null) {
				decl.visitVarDecl(d);
			}
			while (recover(r|Id.$COMMA|Id.$$TYPE|Id.$$ID, recovery)) {
				expectAndConsume(Id.$COMMA, Id.$$ALL, Report._1_MISS_COMMA);
				modifiers[0] = parseModifiers(r, recovery);
				d = parseVarDecl(modifiers, recovery);
				if (d != null) {
					decl.visitVarDecl(d);
		}	}	}
		expectAndConsume(Id.$CLOSE_PAR, recovery, Report._1_ERR_MISS_METHOD_CPAR);
	}
	
	private Type parseType(long recovery) {
		return parseType(Report._1_MISS_TYPE, null, null, recovery);
	}
	
	private Type parseType(int[] modifiers, long recovery) {
		return parseType(Report._1_MISS_TYPE, null, modifiers, recovery);
	}
	
	private Type parseType(String message, int[] modifiers, long recovery) {
		return parseType(message, null, modifiers, recovery);
	}
	
	private Type parseType(IToken token, long recovery) {
		return parseType(null, token, null, recovery);
	}
	
	private Type parseType(IToken token, int[] modifiers, long recovery) {
		return parseType(null, token, modifiers, recovery);
	}
	
	private Type parseType(String message, IToken token, int[] modifiers, long recovery) {
		return parseType(message, false, token, modifiers, recovery);
	}
	
	private Type parseType(String message, boolean ref, IToken token, int[] modifiers, long recovery) {
		if (token == null) 
			token = parseTypeOrId(recovery|Id.$LT_LTLT|Id.$QMARK, message);
		if (token != null) {
			if (modifiers == null) modifiers = new int[] {0};
			Type type = new Type(modifiers[0], ref, token, false);
			modifiers[0] = 0;
			parseType(type, recovery);
			return type;
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
			TemplDecl d = parseTemplDecl(recovery|Id.$LT_LTLT);
			if (d != null) type.visitTemplDecl(d);
			break DO;
		} while (recover(Id.$LT_LTLT, recovery));
		if (recover(Id.$QMARK, Id.$$ALL)) {
			type.setNullable(true);
			consume(); //?
	}	}
	
	private Type parseVectorType(long recovery) {
		return parseVectorType(Report._1_MISS_TYPE, null, null, recovery);
	}
	
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
		boolean ref = false;
		if (token != null) { 
			if (token.getId() == Id.ATSIGN) {
				ref = true;
				token = null;
			}
		} else  if (recover(Id.$ATSIGN, recovery|Id.$$ID)) {
			ref = true;
			consume(); //@
		}
		recovery |= Id.$OPEN_BRA|Id.$STAR|Id.$AMP|Id.$QMARK|Id.$DDD|Id.CONST|Id.UNSAFE;
		Type type = parseType(msg, ref, token, modifiers, recovery);
		if (type != null) {
			parseVectorType(type, recovery);
		}
		return type;
	}
	
	private void parseVectorType(Type decl, long recovery) {
		long r = Id.$OPEN_BRA|Id.$STAR|Id.$AMP|Id.$QMARK|Id.$DDD;
		DO: do {
			boolean nullable = false;
			int modifiers = parseModifiers(Id.CONST|Id.UNSAFE, r|recovery);
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
					decl.setNullable(true); modifiers = 0; continue;
				case Id.DDD:
					break DO;
			}
		} while (recover(r, recovery));
		if (recover(Id.$DDD, recovery)) {
			// Set VarArg type: Type...
			decl.setVarArg(true);
			consume(); //...
		}
		
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
		return preParseExpr(prev, recovery);
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
		if (curr.contains(e)) {
			return curr;
		}
		return e;
	}
	
	private IExpr parseCastExpr(Expression prev, Type type, long recovery) {
		Expression curr = new CastExpr(type);
		return parseExpr(prev, curr, null, recovery);
	}

	private IExpr parseArrayExpr(Expression prev, IExpr operand, long recovery) {
		return parseArrayExpr(prev, operand, getIdAndConsume(), recovery);
	}
	
	private IExpr parseArrayExpr(Expression prev, IExpr operand, int operator, long recovery) {
		BinaryExpr expr = new BinaryExpr(operator);
		IExpr e = parseExpr(prev, expr, operand, recovery|Id.$CLOSE_BRA);
		assert e == expr;
		expectAndConsume(Id.$CLOSE_BRA, recovery, Report._1_ERR_MISS_ARRAY_EXPR);
		return postParseExpr(prev, expr, recovery);
	}
	
	private CallExpr parseCallExpr(IToken token, long recovery) {
		consume(); //(
		CallExpr expr = new CallExpr(token);
		parseCallExprArgs(expr, recovery);
		expectAndConsume(Id.$CLOSE_PAR, recovery, Report._1_ERR_MISS_CALL_CPAR);
		return expr;
	}
	
	private IExpr parseCallExpr(Expression prev, IToken token, long recovery) {
		CallExpr expr = parseCallExpr(token, recovery);
		return postParseExpr(prev, expr, recovery);
	}
	
	private void parseCallExprArgs(Expression expr, long recovery) {
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
	}	}	}
	
	private IExpr parseNewExpr(Expression prev, long recovery) {
		consume(); //new
		Type type = parseType(recovery|Id.$OPEN_PAR);
		if (type != null) {
			NewExpr curr = new NewExpr(type);
			DO: do switch (id()) {
			case Id.OPEN_PAR:
				//expectAndConsume(Id.$OPEN_PAR, recovery, Report._1_ERR_MISS_NEW_OPAR);
				consume();
				parseCallExprArgs(curr, recovery);
				expectAndConsume(Id.$CLOSE_PAR, recovery, Report._1_ERR_MISS_NEW_CPAR);
				break DO;
			case Id.OPEN_BRA:
				consume();
				IExpr e = parseExpression(recovery|Id.$CLOSE_BRA);
				if (e != null) e.accept(curr);
				expectAndConsume(Id.$CLOSE_BRA, recovery, Report._1_ERR_MISS_ARRAY_EXPR);
				break DO;
			} while (recover(Id.$OPEN_PAR|Id.$OPEN_BRA, recovery));
			
			return postParseExpr(prev, curr, recovery);
		}
		return prev;
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
			case Id.DELETE:	
			case Id.SIZEOF:	case Id.ATSIGN:	
				return parseExpr(
					expr, new UnaryExpr(getIdAndConsume()), null, recovery
				);
			case Id.NEW: 
				return parseNewExpr(expr, recovery);
			case Id.ID: case Id.OBJECT: case Id.STRING: case Id.THIS:
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
				
			// TODO must raise an error on no binary operator but and operand found
			// TODO must avoid infinite loop if recover a unary operator (not in cases)
//			case Id.OPEN_PAR:
//				return parseCallExpr(expr, operand, recovery);
			default: break DO; //FIXME infilite loop here 
				
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
		IExpr expr = preParseExpr(null, recovery);
		expectAndConsume(Id.$CLOSE_PAR, recovery, "MISS EXPR CLOSE PAR");
		return postParseExpr(prev, expr, recovery);
	}
	
	private void parseBlockScope(BlockVisitor visitor, long recovery) {
		recovery |= recovery|$BLOCK_SAFE|Id.$CLOSE_KEY;
		while (recover($BLOCK_SAFE, recovery)) {
			parseStatement(visitor, recovery);
		}
		expectAndConsume(Id.$CLOSE_KEY, recovery, Report._1_ERR_MISS_STMT_SCOLON);
	}
	
	private void parseStatement(BlockVisitor visitor, long recovery) {
		IToken token = null;
		switch (id()) {
			case Id.IF:	case Id.WHILE: parseInstrBlockStat(
					visitor, parseInstrExprStat(recovery), recovery);
				return;
			case Id.ELSE: parseInstrBlockStat(
					visitor, new InstrBlockStat(getAndConsumeToken()), recovery);
				return;
			case Id.DO: parseInstrBlockStat(
					visitor, new InstrBlockStat(getAndConsumeToken()), recovery);
				if (Id.WHILE == id()) parseInstrBlockStat(
					visitor, new InstrBlockStat(getAndConsumeToken()), recovery); 	
				else 
					Report.message(Report._1_ERR_MISS_WHILE, getSource(), getLocation());
				return;
			case Id.FOR:
				parseForStat(visitor, recovery);
				return;
			case Id.SWITCH: parseSwitchStat(
					visitor, parseInstrExprStat(recovery), recovery);
				return;
			case Id.RETURN: token = getAndConsumeToken();
				if (recover(Id.$SCOLON, $EXPR_SAFE|recovery)) {
					parseInstrStat(visitor, token, recovery);
				} else parseInstrExprStat(
					visitor, token, parseExpression(recovery|Id.$SCOLON), recovery
				);
				return;
			case Id.ASSERT:	parseInstrExprStat(
					visitor, getAndConsumeToken(), parseExpression(recovery|Id.$SCOLON), recovery
				);
				return;
			case Id.CONTINUE: case Id.BREAK: parseInstrStat(
					visitor, getAndConsumeToken(), recovery);
				return;
			case Id.ID:
				token = getLexemeAndConsumeToken();
				if (recover(Id.$COLON, Id.$$ALL)) {
					visitor.visitLabelDecl(getLexeme());
					consume(); //:
					return; // TODO attach next statement to label
				}	//$FALL-THROUGH$
			default:
			case Id.ATSIGN:
				parseVarDeclOrStat(visitor, token, recovery);
				expectAndConsume(Id.$SCOLON, recovery, Report._1_ERR_MISS_STMT_SCOLON);
				return;
			case Id.THIS:
				parseExprStat(visitor, token, recovery);
				expectAndConsume(Id.$SCOLON, recovery, Report._1_ERR_MISS_STMT_SCOLON);
				return;
		}	
	}

	private void parseInstrBlockStat(BlockVisitor visitor, InstrBlockStat instr, long recovery) {
		do switch (id()) {
			case Id.SCOLON: 	
				consume(); 
				return;
			case Id.OPEN_KEY:	
				consume();
				instr.visitBlock();
				parseBlockScope(
					visitor.visitStatement(instr), recovery
				);	
				return;
			default:
				instr.visitBlock();
				parseStatement(
					visitor.visitStatement(instr), recovery
				);	
				return;
		} while (recover(Id.$SCOLON|Id.$OPEN_KEY|$BLOCK_SAFE, recovery));
	}
	
	private InstrBlockStat parseInstrExprStat(long recovery) {
		IToken token = getToken();
		consume(); //[instr]
		if (expect(Id.$OPEN_PAR, Id.$CLOSE_PAR|Id.$OPEN_KEY|$BLOCK_SAFE, "MSG")) {
			consume(); // `('
			IExpr expr = parseExpression(Id.$CLOSE_PAR|Id.$OPEN_KEY|$BLOCK_SAFE);
			InstrExprBlockStat instr = new InstrExprBlockStat(token, expr);
			expectAndConsume(Id.$CLOSE_PAR, Id.$CLOSE_PAR|Id.$OPEN_KEY|$BLOCK_SAFE, "MSG"); //)
			return instr;
		}
		return null;
	}
	
	private void parseInstrStat(BlockVisitor visitor, IToken token, long recovery) {
		visitor.visitStatement(
			new InstrStat(token)
		);
		expectAndConsume(Id.$SCOLON, recovery, Report._1_ERR_MISS_STMT_SCOLON);
	}
	
	private void parseInstrExprStat(BlockVisitor visitor, IToken token, IExpr expr, long recovery) {
		visitor.visitStatement(
			new InstrExprStat(token, expr)
		);
		expectAndConsume(Id.$SCOLON, recovery, Report._1_ERR_MISS_STMT_SCOLON);
	}
	
	private void parseSwitchStat(BlockVisitor visitor, InstrBlockStat stat, long recovery) {
		expectAndConsume(Id.$OPEN_KEY, recovery, Report._1_ERR_MISS_BLOCK_OKEY);
		stat.visitBlock();
		visitor = visitor.visitStatement(stat);
		do switch (id()) {
		case Id.CASE:
			parseSwitchScope(
				visitor, 
				new InstrExprBlockStat(
					getAndConsumeToken(),
					parseExpression(recovery|Id.$COLON)
				), 
				recovery
			);
			continue;
		case Id.DEFAULT:
			parseSwitchScope(
				visitor, new InstrBlockStat(getAndConsumeToken()), recovery
			);
			continue;
		} while (recover(Id.$DEFCASE, recovery));
		expectAndConsume(Id.$CLOSE_KEY, recovery, Report._1_ERR_MISS_STMT_SCOLON);
	}
	
	private void parseSwitchScope(BlockVisitor visitor, InstrBlockStat stat, long recovery) {
		expectAndConsume(Id.$COLON, recovery, "MISS CASE COLON");
		stat.visitBlock();
		visitor = visitor.visitStatement(stat);
		do parseStatement(visitor, recovery);
		while (!recover(Id.$DEFCASE|Id.$CLOSE_KEY, recovery));
	}
	
	private void parseForStat(BlockVisitor visitor, long recovery) {
		IToken token = getAndConsumeToken();
		if (expect(Id.$OPEN_PAR, Id.$CLOSE_PAR|Id.$OPEN_KEY|recovery, "MSG")) {
			consume(); // `('
			int i = 0;
			IExpr[] exprs = new IExpr[3];
			InstrForStat stat = new InstrForStat(token);
			// Parse the first expression
			IExpr expr = parseVarDeclOrExpr(stat, null, recovery);
			if (expr != null) exprs[i++] = expr;
			// Parse next expressions
			do {
				expectAndConsume(Id.$SCOLON, $BLOCK_SAFE, Report._1_ERR_MISS_STMT_SCOLON);
				IExpr e = parseExpression(
					Id.$SCOLON|Id.$CLOSE_PAR|Id.$OPEN_KEY|recovery
				);
				if (e != null) {
					exprs[i++] = e;
				}
			} while (!recover(Id.$CLOSE_PAR, Id.$SCOLON|Id.$OPEN_KEY|recovery) && i < 3);
			stat.setExpressions(exprs);
			expectAndConsume(Id.$CLOSE_PAR, Id.$CLOSE_PAR|Id.$OPEN_KEY|recovery, "MSG");
			parseInstrBlockStat(visitor, stat, recovery);
		}
	}
	
	private VarDecl parseVarDeclOrExpr(IToken token, long recovery) {
		if (token == null && recover(Id.$$TYPE|Id.$$ID, recovery)) {
			token = parseTypeOrId(recovery);
		}
		switch (id()) {
		case Id.OPEN_BRA: 
			if (!match(']'))
				break;
			//$FALL-THROUGH$
		case Id.ID: 
		case Id.LT: case Id.LTLT:
		case Id.STAR: case Id.AMP: case Id.QMARK:
			VarDecl decl = parseVarDecl(token, null, recovery|Id.$SCOLON);
			return decl;
		}
		return null;
	}
	
	private IExpr parseVarDeclOrExpr(InstrForStat instr, IToken token, long recovery) {
		VarDecl decl = parseVarDeclOrExpr(token, recovery);
		if (decl != null) {
			instr.setVarDecl(decl);
			return null;
		} else {
			return parseExpression(token, recovery);
		}
	}
	
	private void parseVarDeclOrStat(BlockVisitor visitor, IToken token, long recovery) {
		VarDecl decl = parseVarDeclOrExpr(token, recovery);
		if (decl != null) {
			visitor.visitVarDecl(decl);
		} else {
			parseExprStat(visitor, token, recovery);
		}
	}

	private void parseExprStat(BlockVisitor visitor, IToken token, long recovery) {
		IExpr expr = parseExpression(token, recovery|Id.$SCOLON);
		if (expr != null) {
			if (expr.isStatement()) {
				StatBuilder b = /*static*/ new StatBuilder();
				expr.accept(b);
				b.stat.accept(visitor);
			} else {
				Report.message(Report._1_ILL_EXPR_STMT, getSource(), getLocation());
	}	}	}
	
	private static class StatBuilder implements ExprVisitor {
		IStatement stat;
		@Override
		public void visitExpr(Lexeme expr) {}
		@Override
		public void visitExpr(LexExpr expr) {}
		@Override
		public CallExprVisitor visitExpr(CallExpr expr) {
			stat = new CallStat(expr);
			return null;
		}
		@Override
		public UnaryStat visitExpr(UnaryExpr expr) {
			stat = new UnaryStat(expr);
			return null;
		}
		@Override
		public BinaryExprVisitor visitExpr(BinaryExpr expr) {
			stat = new AssignStat(expr);
			return null;
		}
		@Override
		public TernaryExprVisitor visitExpr(TernaryExpr expr) {
			return null;
		}
		@Override
		public CastExprVisitor visitExpr(CastExpr expr) {
			return null;
		}
		@Override
		public NewExprVisitor visitExpr(NewExpr expr) {
			stat = new NewStat(expr);
			return null;
		}
	}
	
	@Override
	protected Annotation parseAnnotation(IToken token) {
		Annotation anno = new Annotation(token);
		switch (id()) {
		case Id.LTLT:
			Report.message(Report._1_SYNTAX_ERROR, getSource(), getLocation());
			//$FALL-THROUGH$
		case Id.LT:
			consume(); //<
			IExpr expr = parseExpression(Id.$GT_GTGT);
			if (expr != null) {
				expr.accept(anno);
				consume(); //> TODO check >>
			}
			break;
		case Id.OPEN_PAR:
			CallExpr call = parseCallExpr(token, Id.$$ALL);
			if (call != null) {
				anno.visitCallArgs(call);
			}
			break;
		default:
			break;
		}
		return anno;
	}

}
