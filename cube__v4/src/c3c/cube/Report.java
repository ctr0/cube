package c3c.cube;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Stack;

import c3c.ast.Decl;



public class Report {
	
	public static final String _0_FILE_NOT_FOUND 				= "File not found: %s";

	public static final String _1_PARSE_ERROR 					= "Parse error";
	public static final String _1_SYNTAX_ERROR 					= "Syntax error";
	
	public static final String _1_ERR_MISS_USING_SCOLON			= "Parse error: insert ';' to complete using declaration";
	public static final String _1_ERR_MISS_NS_OKEY				= "Parse error: insert '{' to begin namespace body declaration";
	public static final String _1_ERR_MISS_NS_SC				= "Parse error: insert '{' or ';' to begin namespace body declaration";
	public static final String _1_ERR_MISS_NS_CKEY				= "Parse error: insert '}' to complete namespace body declaration";
	public static final String _1_ERR_MISS_RECORD_OKEY			= "Parse error: insert '{' to begin record body declaration";
	public static final String _1_ERR_MISS_RECORD_CKEY			= "Parse error: insert '}' to complete record body declaration";
	public static final String _1_ERR_MISS_BLOCK_OKEY			= "Parse error: insert '{' to begin block declaration";
	public static final String _1_ERR_MISS_BLOCK_CKEY			= "Parse error: insert '}' to complete block declaration";
	public static final String _1_ERR_MISS_TEMPL_GT				= "Parse error: insert '>' to complete template declaration";
	
	public static final String _1_ERR_MISS_METHOD_OPAR			= "Parse error: insert '(' to continue method declaration";
	public static final String _1_ERR_MISS_METHOD_CPAR			= "Parse error: insert ')' to complete method arguments declaration";
	public static final String _1_ERR_MISS_METHOD_SYMBOL		= "Parse error: insert '{' or ';' to complete method declaration";
	
	public static final String _1_ERR_MISS_NEW_OPAR				= "Parse error: insert '(' to complete new expression";
	public static final String _1_ERR_MISS_NEW_CPAR				= "Parse error: insert ')' to complete new expression";
	public static final String _1_ERR_MISS_CALL_CPAR			= "Parse error: insert ')' to complete method call expression";
	public static final String _1_ERR_MISS_CAST_CPAR			= "Parse error: insert ')' to complete cast expression";
	public static final String _1_ERR_MISS_ARRAY_EXPR			= "Parse error: insert ']' to complete expression";
	
	public static final String _1_ERR_MISS_STMT_SCOLON			= "Parse error: insert ';' to complete statement";
	
	public static final String _1_MISS_ID 						= "Syntax error: missing identifier";
	public static final String _1_MISS_TYPE 					= "Syntax error: missing type";
	public static final String _1_MISS_TEMPL_PARAM 				= "Syntax error: missing template parameter";
	public static final String _1_MISS_PARAM_CONST 				= "Syntax error: missing parameter constraint";
	public static final String _1_MISS_COMMA					= "Syntax error: missing sequential operator: ','";
	public static final String _1_MISS_EXPR 					= "Syntax error: missing expresion";
	
	public static final String _1_ILL_ASSIGN_OP 				= "Illegal assigment operator";
	public static final String _1_ILL_EXPR 						= "Illegal expression";
	public static final String _1_ILL_EXPR_STMT 				= "Illegal statement: Left-hand side expression required";
	
	public static final String _1_ERR_MISS_WHILE				= "Parse error: missing while statement";
	
	public static final String _1_MISPL_MODS 					= "Syntax error: Misplaced modifiers";
	
//	public static final String _1_MISPL_MODS 					= "Syntax error: Misplaced modifiers";
	
	public static final String _1_ERR_DUP_ID 					= "Identifier '%s' is already in use";
	
	public static final String _1_ERR_NOT_FOUND 				= "%s cannot be resolved";
	public static final String _1_ERR_TYPE_NOT_FOUND 			= "%s cannot be resolved to a type";
	
	public static final String _2_GLOBAL_DECL 					= "Global namespace declarations are discouraged";
	public static final String _2_ILL_CONSTRAINT 				= "Illegal constraint declaration";
	public static final String _2_ILL_NS_VAR_MODS 				= "Illegal field modifiers. Only " + Modifiers.toString(Modifiers.NS_VAR_MODIFIERS) + " are allowed";
	public static final String _2_ILL_NS_FUNC_MODS 				= "Illegal class method modifiers. Only " + Modifiers.toString(Modifiers.NS_FUNC_MODIFIERS) + " are allowed";
	
	
	
	private static Stack<Message> messages = new Stack<Message>();
	private static ArrayList<Appender> appenders = new ArrayList<Appender>(3);
	
	static {
		addAppender(new DefaultAppender());
	}
	
	public static class Message {
		
		public static final int SEVERITY_ERROR = 2;
		public static final int SEVERITY_WARNING = 1;
		
		private final int severity;
		private final String message;
		private final CodeSource source;
		private final SrcPos location;
		
		Message(String message, CodeSource source, SrcPos location) {
			this(SEVERITY_ERROR, message, source, location);
		}
		
		Message(int severity, String message, CodeSource source, SrcPos location) {
			this.severity = severity;
			this.message = message;
			this.source = source;
			this.location = location;
		}
		
		public int getSeverity() {
			return severity;
		}
		
		public String getMessage() {
			return message;
		}
		
		public CodeSource getSource() {
			return source;
		}

		public SrcPos getLocation() {
			return location;
		}
	}
	
	public static interface Appender {
		
		public void message(int severity, String message, CodeSource source, SrcPos location);
	}
	
	private static class DefaultAppender implements Appender {

		@Override
		public void message(int severity, String message, CodeSource source, SrcPos location) {
			System.out.println(location + message);
		}
	}
	
	private Report() {}
	
	public static void addAppender(Appender appender) {
		appenders.add(appender);
	}
	
	public static void message(String message, CodeSource source, IToken token) {
		dispatch(String.format(message, token.toString()), source, token.getLocation());
	}
	
	public static void message(String message, CodeSource source, IToken token, Object... args) {
		dispatch(String.format(message, args), source, token.getLocation());
	}
	
	public static void message(String message, CodeSource source, SrcPos location) {
		dispatch(message, source, location);
	}
	
	public static void message(String message, CodeSource source, SrcPos location, Object... args) {
		dispatch(String.format(message, args), source, location);
	}
	
	public static void message(String message, Decl decl) {
		IToken token = decl.getToken();
		dispatch(String.format(message, token.toString()), decl.getCodeSource(), token.getLocation());
	}
	
	public static void message(String message, Decl decl, SrcPos location) {
		IToken token = decl.getToken();
		dispatch(String.format(message, token.toString()), decl.getCodeSource(), location);
	}
	
	private static void dispatch(String message, CodeSource source, SrcPos location) {
		for (Appender appender : appenders) {
			appender.message(Message.SEVERITY_ERROR, message, source, location);
		}
	}
	
	static class Dispatcher implements Runnable {

		@Override
		public void run() {
			
			
		}
		
	}

	public static void fatal(String message) {
		
	}
}
