package c3c.cube;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;

public class Id {
	
	/*******************/
	/* RECOVERY GROUPS */
	/*******************/
	
	public static final long $$ALL = 						0xFFFFFFFFFFFFFF00L;

	public static final long $$PREP = 						1L << 63;
	public static final long $$RECO = 						1L << 62;
	public static final long $$STMT = 						1L << 61;
	
	public static final long $$TYPE = 						1L << 60;
	public static final long $$LITE = 						1L << 59;
	public static final long $$OP = 						1L << 58;
	//public static final long $$MODS = 
	public static final long $$ID = 						1L << 57;
	
	/****************/
	/* SPECIAL  IDs */
	/****************/
	
	public static final int _EOF = 							0;
	public static final int _ENDL = 						254;
	public static final int _SYNTAX_ERR = 					255;
	
	/*******************/
	/* RECOVERY TOKENS */
	/*******************/

	public static final int ID = 							1;
	public static final int SCOLON = 						7;
	public static final int COLON =		 					8;
	public static final int COMMA = 						9;
	public static final int DOT = 							10;
	public static final int ARROW = 						11;
	public static final int OPEN_BRA = 						12;
	public static final int CLOSE_BRA = 					13;
	public static final int OPEN_KEY = 						14;
	public static final int CLOSE_KEY = 					15;
	public static final int OPEN_PAR = 						16;
	public static final int CLOSE_PAR = 					17;
	public static final int STAR = 							19;
	public static final int EQ = 							20;
	//public static final int LT_LTLT =		 				id++;
	//public static final int GT_GTGT = 					id++;
	public static final int ATSIGN = 						21;
	public static final int AMP = 							22;
	public static final int TILDE = 						23;
	public static final int QMARK = 						24;
	
	public static final long TYPENAME = 					200;
	public static final long FRIEND = 						201;
	public static final long CONSTEXPR = 					202;
	
	public static final int DDD = 							203;
	
	public static final long $SCOLON = 						1L << 56;
	public static final long $COLON =		 				1L << 55;
	public static final long $COMMA = 						1L << 54;
	public static final long $DOT = 						1L << 53; 
	public static final long $ARROW = 						1L << 52;
	public static final long $OPEN_BRA = 					1L << 51;
	public static final long $CLOSE_BRA = 					1L << 50;
	public static final long $OPEN_KEY = 					1L << 49;
	public static final long $CLOSE_KEY = 					1L << 48;
	public static final long $OPEN_PAR = 					1L << 47;
	public static final long $CLOSE_PAR = 					1L << 46;
	public static final long $STAR = 						1L << 45;
	public static final long $EQ = 							1L << 44;
	public static final long $LT_LTLT =		 				1L << 43;
	public static final long $GT_GTGT = 					1L << 42;
	public static final long $ATSIGN = 						1L << 41;
	public static final long $AMP = 						1L << 40;
	public static final long $TILDE = 						1L << 39;
	public static final long $QMARK = 						1L << 38;
	public static final long $DEFCASE = 					1L << 37;
	
	public static final long $TYPENAME = 					1L << 36;
	public static final long $FRIEND = 						1L << 35;
	public static final long $CONSTEXPR = 					1L << 34;
	
	public static final long $DDD = 						1L << 33;
//	public static final long  = 						1L << 32;
	
	public static final long $USING = 						1L << 31; 		// 31
	public static final int USING = 						(int) $USING; 	// 31
	public static final long $NS = 							1L << 30; 		// 30
	public static final int NS = 							(int) $NS; 		// 30
	
	public static final long _ID = 							$$ID | ID;
	public static final long _SCOLON = 						$SCOLON | SCOLON;
	public static final long _COLON =		 				$COLON | COLON;
	public static final long _COMMA = 						$COMMA | COMMA;
	public static final long _DOT = 						$DOT | DOT | $$OP;
	public static final long _ARROW = 						$ARROW | ARROW | $$OP;
	public static final long _OPEN_BRA = 					$OPEN_BRA | OPEN_BRA | $$OP;
	public static final long _CLOSE_BRA = 					$CLOSE_BRA | CLOSE_BRA;
	public static final long _OPEN_KEY = 					$OPEN_KEY | OPEN_KEY;
	public static final long _CLOSE_KEY = 					$CLOSE_KEY | CLOSE_KEY;
	public static final long _OPEN_PAR = 					$OPEN_PAR | OPEN_PAR | $$OP;
	public static final long _CLOSE_PAR = 					$CLOSE_PAR | CLOSE_PAR;
	public static final long _STAR = 						$STAR | STAR | $$OP;
	public static final long _EQ = 							$EQ | EQ | $$OP;
	public static final long _ATSIGN = 						$ATSIGN | ATSIGN | $$OP;
	public static final long _AMP = 						$AMP | AMP | $$OP;
	public static final long _TILDE = 						$TILDE | TILDE | $$OP;
	public static final long _QMARK = 						$QMARK | QMARK | $$OP;
	public static final long _DDD = 						$DDD | DDD;
	
	public static final long _TYPENAME = 					$TYPENAME | TYPENAME;
	public static final long _FRIEND = 						$FRIEND | FRIEND;
	public static final long _CONSTEXPR = 					$CONSTEXPR | CONSTEXPR;
	
	/*************/
	/* MODIFIERS */
	/*************/
	
	public static final int ABSTRACT = 						1 << 29;
	public static final int PUBLIC = 						1 << 28;
	public static final int PRIVATE = 						1 << 27;
	public static final int PROTECTED = 					1 << 26;
	public static final int INTERNAL = 						1 << 25;
	public static final int STATIC = 						1 << 24;
	public static final int CONST = 						1 << 23;
	public static final int VOLATILE = 						1 << 22;
	public static final int SYNCHRONIZED = 					1 << 21;
	public static final int INLINE = 						1 << 20;
	public static final int UNSAFE = 						1 << 19;
	public static final int EXTERN = 						1 << 18;
	public static final int EXPLICIT = 						1 << 17;
	public static final int MUTABLE = 						1 << 16;
	public static final int VIRTUAL = 						1 << 15;
	public static final int AUTO = 							1 << 14;
	public static final int REGISTER = 						1 << 13;
	public static final int SIGNED = 						1 << 12;
	public static final int UNSIGNED = 						1 << 11;
	public static final int SHORT = 						1 << 10;
	public static final int LONG = 							1 << 9;
	public static final int OVERRIDE = 						1 << 8;
	
	/**
	 * ABSTRACT | PUBLIC | PRIVATE | PROTECTED
	 * | INTERNAL | STATIC | CONST | VOLATILE | SYNCHRONIZED | INLINE | UNSAFE
	 * | EXTERN | EXPLICIT | SIGNED | UNSIGNED | REGISTER | AUTO | MUTABLE
	 * | SHORT | LONG | VIRTUAL | OVERRIDE;
	 */
	public static final int $$MODS = ABSTRACT | PUBLIC | PRIVATE | PROTECTED
	| INTERNAL | STATIC | CONST | VOLATILE | SYNCHRONIZED | INLINE | UNSAFE
	| EXTERN | EXPLICIT | SIGNED | UNSIGNED | REGISTER | AUTO | MUTABLE
	| SHORT | LONG | VIRTUAL | OVERRIDE;
	
	// Types
	/*internal*/static final long _CLASS = 					$$RECO | 230;
	/*internal*/static final long _INTERFACE = 				$$RECO | 231;
	/*internal*/static final long _STRUCT = 				$$RECO | 232;
	/*internal*/static final long _ENUM = 					$$RECO | 233;
	/*internal*/static final long _UNION = 					$$RECO | 234;
	/*internal*/static final long _TEMPLATE = 				$$RECO | 235;
	/*internal*/static final long _OPERATOR = 				$$RECO | 236;
	/*internal*/static final long _TYPEDEF = 				$$RECO | 237;
	
	public static final int CLASS = 						(int) _CLASS;
	public static final int INTERFACE = 					(int) _INTERFACE;
	public static final int STRUCT = 						(int) _STRUCT;
	public static final int ENUM = 							(int) _ENUM;
	public static final int UNION = 						(int) _UNION;
	public static final int TEMPLATE = 						(int) _TEMPLATE;
	public static final int OPERATOR = 						(int) _OPERATOR;
	public static final int TYPEDEF = 						(int) _TYPEDEF;
	
	// Data types
	/*internal*/static final long _BYTE = 					$$TYPE | 31;
	/*internal*/static final long _BOOL = 					$$TYPE | 32;
	/*internal*/static final long _INT = 					$$TYPE | 33;
	/*internal*/static final long _INT32 = 					$$TYPE | 34;
	/*internal*/static final long _INT64 = 					$$TYPE | 35;
	/*internal*/static final long _INT128 = 				$$TYPE | 36;
	/*internal*/static final long _FLOAT = 					$$TYPE | 37;
	/*internal*/static final long _DOUBLE = 				$$TYPE | 38;
	/*internal*/static final long _CHAR = 					$$TYPE | 39;
	/*internal*/static final long _CHAR16 = 				$$TYPE | 40;
	/*internal*/static final long _CHAR16_T = 				$$TYPE | 41;
	/*internal*/static final long _CHAR32 = 				$$TYPE | 42;
	/*internal*/static final long _CHAR32_T = 				$$TYPE | 43;
	/*internal*/static final long _WCHAR = 					$$TYPE | 44;
	/*internal*/static final long _WCHAR_T = 				$$TYPE | 45;
	/*internal*/static final long _SIGNED = 				$$TYPE | SIGNED /*| 46*/;
	/*internal*/static final long _UNSIGNED = 				$$TYPE | UNSIGNED /*| 47*/;
	/*internal*/static final long _SHORT = 					$$TYPE | SHORT /*| 48*/;
	/*internal*/static final long _LONG = 					$$TYPE | LONG /*| 49*/;
	
	public static final int BYTE = 							(int) _BYTE;
	public static final int BOOL = 							(int) _BOOL;
	public static final int INT = 							(int) _INT;
	public static final int INT32 = 						(int) _INT32;
	public static final int INT64 = 						(int) _INT64;
	public static final int INT128 = 						(int) _INT128;
	//public static final int SHORT = 						(int) _SHORT;
	//public static final int LONG = 						(int) _LONG;
	public static final int FLOAT = 						(int) _FLOAT;
	public static final int DOUBLE = 						(int) _DOUBLE;
	public static final int CHAR = 							(int) _CHAR;
	public static final int CHAR16 = 						(int) _CHAR16;
	public static final int CHAR16_T = 						(int) _CHAR16_T;
	public static final int CHAR32 = 						(int) _CHAR32;
	public static final int CHAR32_T = 						(int) _CHAR32_T;
	public static final int WCHAR = 						(int) _WCHAR;
	public static final int WCHAR_T = 						(int) _WCHAR_T;
	
	// Data types EX
	/*internal*/static final long _OBJECT = 				$$TYPE | 50;
	/*internal*/static final long _STRING = 				$$TYPE | 51;
	/*internal*/static final long _DELEGATE = 				$$TYPE | 52;
	/*internal*/static final long _VOID = 					$$TYPE | 53;
	
	public static final int OBJECT = 						(int) _OBJECT;
	public static final int STRING = 						(int) _STRING;
	public static final int DELEGATE = 						(int) _DELEGATE;
	public static final int VOID = 							(int) _VOID;
	
	// Instructions
	/*internal*/static final long _BASE = 					$$STMT | 54;
	/*internal*/static final long _THIS = 					$$STMT | 55;
	/*internal*/static final long _FOR = 					$$STMT | 56;
	/*internal*/static final long _FOREACH = 				$$STMT | 57;
	/*internal*/static final long _WHILE = 					$$STMT | 58;
	/*internal*/static final long _IF = 					$$STMT | 59;
	/*internal*/static final long _ELSE = 					$$STMT | 60;
	/*internal*/static final long _DO = 					$$STMT | 61;
	/*internal*/static final long _CONTINUE = 				$$STMT | 62;
	/*internal*/static final long _BREAK = 					$$STMT | 63;
	/*internal*/static final long _GOTO = 					$$STMT | 64;
	/*internal*/static final long _SWITCH = 				$$STMT | 65;
	/*internal*/static final long _CASE = 					$$STMT | 66 | $DEFCASE;
	/*internal*/static final long _TRY = 					$$STMT | 67;
	/*internal*/static final long _CATCH = 					$$STMT | 68;
	/*internal*/static final long _FINALLY = 				$$STMT | 69;
	/*internal*/static final long _THROW = 					$$STMT | 70;
	/*internal*/static final long _THROWS = 				$$STMT | 71;
	/*internal*/static final long _IS = 					$$STMT | 72;
	/*internal*/static final long _REF = 					$$STMT | 73;
	/*internal*/static final long _OUT = 					$$STMT | 74;
	/*internal*/static final long _RETURN = 				$$STMT | 75;
	/*internal*/static final long _NOEXCEPT = 				$$STMT | 76;
	/*internal*/static final long _ALIGNOF = 				$$STMT | 77;
	/*internal*/static final long _THREAD_LOCAL = 			$$STMT | 78;
	/*internal*/static final long _ASM = 					$$STMT | 79;
	/*internal*/static final long _ASSERT = 				$$STMT | 80;
	/*internal*/static final long _DEFAULT = 				$$STMT | 81 | $DEFCASE;
	
	public static final int BASE = 							(int) _BASE;
	public static final int THIS = 							(int) _THIS;
	public static final int FOR = 							(int) _FOR;
	public static final int FOREACH = 						(int) _FOREACH;
	public static final int WHILE = 						(int) _WHILE;
	public static final int IF = 							(int) _IF;
	public static final int ELSE = 							(int) _ELSE;
	public static final int DO = 							(int) _DO;
	public static final int CONTINUE = 						(int) _CONTINUE;
	public static final int BREAK = 						(int) _BREAK;
	public static final int GOTO = 							(int) _GOTO;
	public static final int SWITCH = 						(int) _SWITCH;
	public static final int CASE = 							(int) _CASE;
	public static final int DEFAULT = 						(int) _DEFAULT;
	public static final int TRY = 							(int) _TRY;
	public static final int CATCH = 						(int) _CATCH;
	public static final int FINALLY = 						(int) _FINALLY;
	public static final int THROW = 						(int) _THROW;
	public static final int THROWS = 						(int) _THROWS;
	public static final int IS = 							(int) _IS;
	public static final int REF = 							(int) _REF;
	public static final int OUT = 							(int) _OUT;
	public static final int RETURN = 						(int) _RETURN;
	public static final int NOEXCEPT = 						(int) _NOEXCEPT;
	public static final int ALIGNOF = 						(int) _ALIGNOF;
	public static final int THREAD_LOCAL = 					(int) _THREAD_LOCAL;
	public static final int ASM = 							(int) _ASM;
	public static final int ASSERT = 						(int) _ASSERT;

	// Literals
	/*internal*/static final long _NULL_LITERAL = 			$$LITE | 80;
	/*internal*/static final long _STRING_LITERAL = 		$$LITE | 81;
	/*internal*/static final long _CHAR_LITERAL = 			$$LITE | 82;
	/*internal*/static final long _HEX_LITERAL = 			$$LITE | 83;
	/*internal*/static final long _INT_LITERAL = 			$$LITE | 84;
	/*internal*/static final long _UINT_LITERAL = 			$$LITE | 85;
	/*internal*/static final long _LONG_LITERAL = 			$$LITE | 86;
	/*internal*/static final long _ULONG_LITERAL = 			$$LITE | 87;
	/*internal*/static final long _FLOAT_LITERAL = 			$$LITE | 88;
	/*internal*/static final long _DOUBLE_LITERAL = 		$$LITE | 89;
	/*internal*/static final long _BOOLEAN_LITERAL = 		$$LITE | 90;
	/*internal*/static final long _BOOLEAN_TRUE = 			$$LITE | 91;
	/*internal*/static final long _BOOLEAN_FALSE = 			$$LITE | 92;
	/*internal*/static final long _NULLPTR = 				$$LITE | 93;
	
	public static final int NULL_LITERAL = 					(int) _NULL_LITERAL;
	public static final int STRING_LITERAL = 				(int) _STRING_LITERAL;
	public static final int CHAR_LITERAL = 					(int) _CHAR_LITERAL;
	public static final int HEX_LITERAL = 					(int) _HEX_LITERAL;
	public static final int INT_LITERAL = 					(int) _INT_LITERAL;
	public static final int UINT_LITERAL = 					(int) _UINT_LITERAL;
	public static final int LONG_LITERAL = 					(int) _LONG_LITERAL;
	public static final int ULONG_LITERAL = 				(int) _ULONG_LITERAL;
	public static final int FLOAT_LITERAL = 				(int) _FLOAT_LITERAL;
	public static final int DOUBLE_LITERAL = 				(int) _DOUBLE_LITERAL;
	public static final int BOOLEAN_LITERAL = 				(int) _BOOLEAN_LITERAL;
	public static final int BOOLEAN_TRUE = 					(int) _BOOLEAN_TRUE;
	public static final int BOOLEAN_FALSE = 				(int) _BOOLEAN_FALSE;
	public static final int NULLPTR = 						(int) _NULLPTR;
	
	/*internal*/static final long ___SHARP = 				$$PREP | 103;
	/*internal*/static final long ___SHARPSHARP = 			$$PREP | 104;
	
	// Preprocessor directives
	/*internal*/static final long ___INCLUDE = 				$$PREP | 94;
	/*internal*/static final long ___DEFINE = 				$$PREP | 95;
	/*internal*/static final long ___UNDEF = 				$$PREP | 96;
	/*internal*/static final long ___IF = 					$$PREP | 97;
	/*internal*/static final long ___ELSE = 				$$PREP | 98;
	/*internal*/static final long ___IFDEF = 				$$PREP | 99;
	/*internal*/static final long ___IFNDEF = 				$$PREP | 100;
	/*internal*/static final long ___ENDIF = 				$$PREP | 101;
	/*internal*/static final long ___PRAGMA = 				$$PREP | 102;
	
	public static final int __SHARP = 						(int) ___SHARP;
	public static final int __SHARPSHARP = 					(int) ___SHARPSHARP;
	public static final int __INCLUDE = 					(int) ___INCLUDE;
	public static final int __DEFINE = 						(int) ___DEFINE;
	public static final int __UNDEF = 						(int) ___UNDEF;
	public static final int __IF = 							(int) ___IF;
	public static final int __ELSE = 						(int) ___ELSE;
	public static final int __IFDEF = 						(int) ___IFDEF;
	public static final int __IFNDEF = 						(int) ___IFNDEF;
	public static final int __ENDIF = 						(int) ___ENDIF;
	public static final int __PRAGMA = 						(int) ___PRAGMA;
	
	/********************/
	/* OPERATOR SYMBOLS */
	/********************/
	
	public static final long _PLUSPLUS =					$$OP | 105; 
	
	public static final long _MINUSMINUS =					$$OP | 107; 		// 106 Reserved
													
	public static final long _DCAST =						$$OP | 109;			// 108 Reserved
	public static final long _SCAST =						$$OP | 110;
	public static final long _RCAST =						$$OP | 111;
	public static final long _CCAST =						$$OP | 112;
	
	public static final long _NOT =							$$OP | 138;
	
	public static final long _PLUS =						$$OP | 115;
	public static final long _MINUS =						$$OP | 117;			// 116 Reserved						
	
	public static final long _SIZEOF =						$$OP | 121;
	public static final long _NEW =							$$OP | 122;
	public static final long _DELETE =						$$OP | 123;

	public static final long _PTR_SEL =						$$OP | 125;
	public static final long _OBJ_SEL =						$$OP | 126;
	
	public static final long _BAR =							$$OP | 127;
	public static final long _PERC =						$$OP | 129;
	
	public static final long _LTLT =						$$OP | 130;
	public static final long _GTGT =						$$OP | 131;
	
	public static final long _LT =		 					$$OP | $LT_LTLT | 132;
	public static final long _GT =		 					$$OP | $GT_GTGT | 133;
	public static final long _LTEQ =						$$OP | 134;
	public static final long _GTEQ =						$$OP | 135;
	
	public static final long _EQEQ =						$$OP | 136;
	public static final long _NE =							$$OP | 137;

	public static final long _XOR =							$$OP | 139;
	public static final long _OR =							$$OP | 140;
	public static final long _ANDAND =						$$OP | 141;
	public static final long _OROR =						$$OP | 142;
	public static final long _TERN =						$$OP | 143;

	public static final long _PLUSEQ =						$$OP | 145;
	public static final long _MINUSEQ =						$$OP | 146;
	public static final long _MULTEQ =						$$OP | 147;
	public static final long _DIVEQ =						$$OP | 148;
	public static final long _MODEQ =						$$OP | 149;
	public static final long _XOREQ =						$$OP | 150;
	public static final long _ANDEQ =						$$OP | 151;
	public static final long _OREQ =						$$OP | 152;
	public static final long _LTLTEQ =						$$OP | 153;
	public static final long _GTGTEQ =						$$OP | 154;
	
	public static final int PLUSPLUS =						(int) _PLUSPLUS;
	public static final int MINUSMINUS =					(int) _MINUSMINUS;
	public static final int DCAST =							(int) _DCAST;
	public static final int SCAST =							(int) _SCAST;
	public static final int RCAST =							(int) _RCAST;
	public static final int CCAST =							(int) _CCAST;
	public static final int NOT =							(int) _NOT;
	public static final int PLUS =							(int) _PLUS;
	public static final int MINUS =							(int) _MINUS;
	public static final int SIZEOF =						(int) _SIZEOF;
	public static final int NEW =							(int) _NEW;
	public static final int DELETE =						(int) _DELETE;
	public static final int PTR_SEL =						(int) _PTR_SEL;
	public static final int OBJ_SEL =						(int) _OBJ_SEL;
	public static final int BAR =							(int) _BAR;
	public static final int PERC =							(int) _PERC;
	public static final int LTLT =							(int) _LTLT;
	public static final int GTGT =							(int) _GTGT;
	public static final int LT =		 					(int) _LT;
	public static final int GT =		 					(int) _GT;
	public static final int LTEQ =							(int) _LTEQ;
	public static final int GTEQ =							(int) _GTEQ;
	public static final int EQEQ =							(int) _EQEQ;
	public static final int NE =							(int) _NE;
	public static final int XOR =							(int) _XOR;
	public static final int OR =							(int) _OR;
	public static final int ANDAND =						(int) _ANDAND;
	public static final int OROR =							(int) _OROR;
	public static final int TERN =							(int) _TERN;
	public static final int PLUSEQ =						(int) _PLUSEQ;
	public static final int MINUSEQ =						(int) _MINUSEQ;
	public static final int MULTEQ =						(int) _MULTEQ;
	public static final int DIVEQ =							(int) _DIVEQ;
	public static final int MODEQ =							(int) _MODEQ;
	public static final int XOREQ =							(int) _XOREQ;
	public static final int ANDEQ =							(int) _ANDEQ;
	public static final int OREQ =							(int) _OREQ;
	public static final int LTLTEQ =						(int) _LTLTEQ;
	public static final int GTGTEQ =						(int) _GTGTEQ;
	
	public static abstract class Op {
		
		/* Precedence 1 */
		public static final int SCOPE_RESOLUTION =			COLON; // COLONCOLON
		
		/* Precedence 2 */
		public static final int MEMBER_ACCESS =				DOT;	
		public static final int FUNTION_CALL =				OPEN_PAR;
		public static final int ARRAY_ACCESS =				OPEN_BRA;
		public static final int POSTINC =					(int) _PLUSPLUS;
		public static final int POSTDEC =					(int) _MINUSMINUS;		
		public static final int DCAST =						(int) _DCAST;
		public static final int SCAST =						(int) _SCAST;
		public static final int RCAST =						(int) _RCAST;
		public static final int CCAST =						(int) _CCAST;
		
		/* Precedence 3 */
		public static final int NOT =						(int) _NOT;
		public static final int COMPL =						TILDE;
		public static final int PREINC =					(int) (_PLUSPLUS   -1);
		public static final int PREDEC =					(int) (_MINUSMINUS -1);
		public static final int POSITIVE =					(int) (_PLUS -1);
		public static final int NEGATIVE =					(int) (_MINUS -1);				
		public static final int INDIRECTION =				STAR - 1; // FIXME
		public static final int REFERENCE =					AMP  - 1; // FIXME
		public static final int SIZEOF =					(int) _SIZEOF;
		public static final int NEW =						(int) _NEW;
		public static final int DELETE =					(int) _DELETE;
		public static final int CAST =						CLOSE_PAR;	 	
		
		/* Precedence 4 */
		public static final int PTR_SEL =					(int) _PTR_SEL;
		public static final int OBJ_SEL =					(int) _OBJ_SEL;
		
		/* Precedence 5 */
		public static final int MULT = 						STAR;
		public static final int DIV =						(int) _BAR;
		public static final int MOD =						(int) _PERC;
		
		/* Precedence 6 */
		public static final int BPLUS =						(int) _PLUS;
		public static final int BMINUS =					(int) _MINUS;
		
		/* Precedence 7 */
		public static final int LTLT =						(int) _LTLT;
		public static final int GTGT =						(int) _GTGT;
		
		/* Precedence 8 */
		public static final int LT =		 				(int) _LT;
		public static final int GT =		 				(int) _GT;
		public static final int LTEQ =						(int) _LTEQ;
		public static final int GTEQ =						(int) _GTEQ;
		
		/* Precedence 9 */
		public static final int EQEQ =						(int) _EQEQ;
		public static final int NE =						(int) _NE;
		
		/* Precedence 10 */
		public static final int AND =						AMP;
		/* Precedence 11 */
		public static final int XOR =						(int) _XOR;
		/* Precedence 12 */
		public static final int OR =						(int) _OR;
		/* Precedence 13 */
		public static final int ANDAND =					(int) _ANDAND;
		/* Precedence 14 */
		public static final int OROR =						(int) _OROR;
		/* Precedence 15 */
		public static final int TERN =						(int) _TERN;
		
		/* Precedence 16 */
		public static final int EQ = 						Id.EQ;
		public static final int PLUSEQ =					(int) Id._PLUSEQ;
		public static final int MINUSEQ =					(int) Id._MINUSEQ;
		public static final int MULTEQ =					(int) Id._MULTEQ;
		public static final int DIVEQ =						(int) Id._DIVEQ;
		public static final int MODEQ =						(int) Id._MODEQ;
		public static final int XOREQ =						(int) Id._XOREQ;
		public static final int ANDEQ =						(int) Id._ANDEQ;
		public static final int OREQ =						(int) Id._OREQ;
		public static final int LTLTEQ =					(int) Id._LTLTEQ;
		public static final int GTGTEQ =					(int) Id._GTGTEQ;
		
		/* Precedence 17 */
		public static final long SEQUENTIAL =				COMMA;
		
		public static long compare(long op1, long op2) {
			int precedence = (int) (precedence(op2) - precedence(op1));
			if (precedence == 0) {
				return associativity(op1);
			}
			return precedence;
		}
		
		/**
		 * Returns the precedence value of operator or -1 if not valid
		 */
		public static long precedence(long op) {
			switch ((int) op) {
			case Op.SCOPE_RESOLUTION:
				return 1;
			case Op.FUNTION_CALL: case Op.ARRAY_ACCESS: case Op.MEMBER_ACCESS:
			case Op.POSTINC: case Op.POSTDEC:
			case Op.DCAST: case Op.SCAST: case Op.RCAST: case Op.CCAST: 
				return 2;
			case Op.NOT: case Op.COMPL: case Op.PREINC: case Op.PREDEC:
			case Op.POSITIVE: case Op.NEGATIVE:
			case Op.INDIRECTION: case Op.REFERENCE:
			case Op.SIZEOF: case Op.NEW: case Op.DELETE:
			case Op.CAST:
		       return 3;
//			MEMBER SELECTOR, POINTER_SELECTOR
//		       return 4;
			case Op.MULT: 			
			case Op.DIV:			
			case Op.MOD:			
		       return 5;
			case Op.BPLUS:			
			case Op.BMINUS:		
		       return 6;
			case Op.LTLT:			
			case Op.GTGT:			
		       return 7;
			case Op.LT:		 	
			case Op.GT:		 	
			case Op.LTEQ:			
			case Op.GTEQ:			
		       return 8;
			case Op.EQEQ:			
			case Op.NE:			
		       return 9;
			case Op.AND:			
		       return 10;
			case Op.XOR:			
				return 11;
			case Op.OR:			
				return 12;
			case Op.ANDAND:		
				return 13;
			case Op.OROR:			
				return 14;   
			case Op.TERN:
				return 15;
			case Op.EQ: case Op.PLUSEQ:	case Op.MINUSEQ: case Op.MULTEQ: case Op.DIVEQ:			
			case Op.MODEQ: case Op.XOREQ: case Op.ANDEQ: case Op.OREQ:			
			case Op.LTLTEQ: case Op.GTGTEQ:
				return 16;
			default:
				return -1;
			}
		}
		
		public static long associativity(long op) {
			switch ((int) op) {
			case Op.NOT: case Op.COMPL: case Op.PREINC: case Op.PREDEC:
			case Op.POSITIVE: case Op.NEGATIVE:
			case Op.INDIRECTION: case Op.REFERENCE:
			case Op.SIZEOF: case Op.NEW: case Op.DELETE:
			case Op.CAST:
			case Op.EQ: case Op.PLUSEQ:	case Op.MINUSEQ: case Op.MULTEQ: case Op.DIVEQ:			
			case Op.MODEQ: case Op.XOREQ: case Op.ANDEQ: case Op.OREQ:			
			case Op.LTLTEQ: case Op.GTGTEQ:
				return 1;
			}
			return -1;	
		}
		
		public static String toString(int op) {
			switch (op) {
			
			case Op.MEMBER_ACCESS: 
//			case Op.SCOPE_RESOLUTION | Op.MEMBER_ACCESS:
				return ".";
				
			case Op.SCOPE_RESOLUTION: return "::";
			case Op.FUNTION_CALL: return "FCALL";
			case Op.ARRAY_ACCESS: return "[]";
			case Op.POSTINC: return "POST++";
			case Op.POSTDEC: return "POST--";
			case Op.CAST: return "CAST()";
			case Op.NOT: return "NOT";
			case Op.COMPL: return "COMPL";
			case Op.PREINC: return "PRE++";
			case Op.PREDEC: return "PRE--";
			case Op.POSITIVE: return "SIGN+";
			case Op.NEGATIVE: return "SIGN-";
			case Op.INDIRECTION: return "PTR*";
			case Op.REFERENCE: return "REF&";
			// case Op.SIZEOF:
			case Op.NEW: return "NEW";
			case Op.DELETE: return "DELETE";
			// case Op.CAST:
			case Op.MULT: return "*";
			case Op.DIV: return "/";
			case Op.MOD: return "%";
			case Op.BPLUS: return "+";
			case Op.BMINUS: return "-";
			case Op.LTLT: return "<<";
			case Op.GTGT: return ">>";
			case Op.LT: return "<";
			case Op.GT: return ">";
			case Op.LTEQ: return "<=";
			case Op.GTEQ: return ">=";
			case Op.EQEQ: return "==";
			case Op.NE: return "!=";
			case Op.AND: return "&";
			case Op.XOR: return "^";
			case Op.OR: return "|";
			case Op.ANDAND: return "&&";
			case Op.OROR: return "||";
			case Op.EQ: return "=";
			case Op.PLUSEQ: return "+=";
			case Op.MINUSEQ: return "-=";
			case Op.MULTEQ: return "*=";
			case Op.DIVEQ: return "/=";
			case Op.MODEQ: return "%=";
			case Op.XOREQ: return "^=";
			case Op.ANDEQ: return "&=";
			case Op.OREQ: return "|=";
			case Op.LTLTEQ: return "<<=";
			case Op.GTGTEQ: return ">>=";
			default:
				return "UNKNOWN";
			}
		}
		
//		public static boolean isUnary(int op) {
//			// TODO Auto-generated method stub
//			return false;
//		}
//
//		public static boolean isBinary(int op) {
//			// TODO Auto-generated method stub
//			return false;
//		}
	}
	
//	/********************/
//	/* EXPRESSION TYPES */
//	/********************/
//	
//	public static final long €€EXPR = 						1L << 34;
//	public static final long €€STMT = 						$$STMT; 
//	public static final long €UNARY = 						1L << 33;
//	public static final long €UEXPR = 						€€EXPR | €UNARY;
//	public static final long €USTMT = 						€€STMT | €UNARY;
//	public static final long €BINARY = 						1L << 32;
//	public static final long €BEXPR = 						€€EXPR | €BINARY;
//	public static final long €BSTMT = 						€€STMT | €BINARY;
//	public static final long €TEXPR = 						€€EXPR | 1L << 53;
//	public static final long €CEXPR = 						€€EXPR | Op.CAST;
//	public static final long €KEXPR = 						€CEXPR | Op.CCAST;
//	public static final long €SEXPR = 						€CEXPR | Op.SCAST;
//	public static final long €REXPR = 						€CEXPR | Op.RCAST;
//	public static final long €DEXPR = 						€CEXPR | Op.DCAST;
//	public static final long €FCALL = 						€€STMT | €€EXPR | 1L << 51;
	
	
	
	public static String toString(long id) {
		for (Field field : Id.class.getFields()) {
			try {
				if ((field.getModifiers() & Modifier.STATIC) != 0 &&
					 id == (Integer) field.get(null)) {
					return field.getName();
				}
			} catch (Exception e) {}
		}
		return "UNKNOW " + id;
	}
	
	public static void main(String[] args) {
		Field[] fields = Id.class.getFields();
		Arrays.sort(fields, new Comparator<Field>() {
			@Override
			public int compare(Field o1, Field o2) {
				return o1.getName().compareTo(o2.getName());
			}
			
		});
		for (Field field : fields) {
			System.out.println(field.getName());
		}
	}
	
}



/*
==================================================================================================
================================ C++ Operators Precedence ========================================
==================================================================================================
Precedence 	Operator 		Description 						Overloadable 	Associativity
==================================================================================================
1 		:: 					scope resolution 					no 				left to right
--------------------------------------------------------------------------------------------------
2 		() 					function call 						yes 			left to right
		[] 					array access 						yes
		-> 					member access 						yes
		. 					member access						no
		++  -- 				postfix 							yes
		dynamic_cast  
		static_cast  
		reinterpret_cast  
		const_cast 			type conversion 					no
		typeid 				Get type info 						no
---------------------------------------------------------------------------------------------------
3 		! not 				logical negation					yes 			right to left
		~ compl 			bitwise negation (complement) 		yes
		++  -- 				prefix 								yes
		+  - 				unary sign operations 				yes
		*  & 				indirection and reference 			yes
		sizeof 				Size (of the type) in bytes 		no
		new  
		new[]  
		delete  
		delete[] 			dynamic memory management 			yes
		(type) 				Cast to a given type 				yes
---------------------------------------------------------------------------------------------------
4 		->* 				member pointer selector 			yes 			left to right
		.* 					member object selector 				no
---------------------------------------------------------------------------------------------------
5 		*  /  % 			arithmetic operations 				yes 			left to right
---------------------------------------------------------------------------------------------------
6 		+  -				arithmetic operations 				yes				left to right
---------------------------------------------------------------------------------------------------
7 		<<  >> 				shift operations 					yes 			left to right
---------------------------------------------------------------------------------------------------
8 		<  <=  >  >= 		relational operations 				yes 			left to right
---------------------------------------------------------------------------------------------------
9 		==  != not_eq 		relational operations				yes 			left to right
---------------------------------------------------------------------------------------------------
10 		&  bitand 			bitwise AND 						yes 			left to right
---------------------------------------------------------------------------------------------------
11 		^  xor 				bitwise XOR 						yes 			left to right
---------------------------------------------------------------------------------------------------
12 		|  bitor 			bitwise OR 							yes 			left to right
---------------------------------------------------------------------------------------------------
13 		&&  and 			logical AND 						yes 			left to right
---------------------------------------------------------------------------------------------------
14 		||  or 				logical OR 							yes 			left to right
---------------------------------------------------------------------------------------------------
15 		?: 					Ternary conditional (if-then-else) 	no 				right to left
---------------------------------------------------------------------------------------------------
16 		=  +=  -=  *=  
		/=  %=  &=  ^= 
		|=  <<= >>= 		assignment 							yes 			right to left
---------------------------------------------------------------------------------------------------
17 		, 					Sequential evaluation operator 		yes 			left to right 
---------------------------------------------------------------------------------------------------
*/