package c3c.cube;


public final class Modifiers {
	
	public static final int ABSTRACT = 				Id.ABSTRACT;
	public static final int PUBLIC = 				Id.PUBLIC;
	public static final int PROTECTED = 			Id.PROTECTED;
	public static final int PRIVATE = 				Id.PRIVATE;
	public static final int STATIC = 				Id.STATIC;
	public static final int UNSAFE = 				Id.UNSAFE;
	public static final int VIRTUAL = 				Id.VIRTUAL;
	public static final int INTERNAL = 				Id.INTERNAL;
	public static final int CONST = 				Id.CONST;
	public static final int INLINE = 				Id.INLINE;
	public static final int VOLATILE = 				Id.VOLATILE;
	public static final int SYNCHRONIZED = 			Id.SYNCHRONIZED;
	
	// Type modifiers
	public static final int SIGNED =				Id.SIGNED;
	public static final int UNSIGNED =				Id.UNSIGNED;
	public static final int SHORT =					Id.SHORT;
	public static final int LONG =					Id.LONG;
	
	public static final int EXTERN = 				Id.EXTERN;
	
	public static final int NS_VAR_MODIFIERS = STATIC | PUBLIC | PROTECTED | PRIVATE | INTERNAL  | CONST;
	public static final int NS_FUNC_MODIFIERS = STATIC | PUBLIC | PROTECTED | PRIVATE | INTERNAL  | CONST | VOLATILE;
	public static final int NS_RECORD_MODIFIERS = PUBLIC | PROTECTED | PRIVATE | INTERNAL | ABSTRACT | VIRTUAL;
	
	public static final int CLASS_MODIFIERS = ABSTRACT | PUBLIC | UNSAFE | INTERNAL | CONST;
	
	public static final int INTERFACE_MODIFIERS = ABSTRACT | PUBLIC | INTERNAL | CONST;
	
	public static final int STRUCT_MODIFIERS = ABSTRACT | PUBLIC | INTERNAL | CONST;
	
	public static final int ENUM_MODIFIERS = PUBLIC | INTERNAL;
	
	public static final int UNION_MODIFIERS = PUBLIC | INTERNAL | CONST; // TODO
	
	public static final int NESTED_MODIFIERS = STATIC | PROTECTED | PRIVATE; // allowed private inner interfaces
	
	public static final int CLASS_FIELD_MODIFIERS = STATIC | PUBLIC | PROTECTED | PRIVATE | INTERNAL | CONST;
	
	public static final int CLASS_METHOD_MODIFIERS = STATIC | PUBLIC | PROTECTED | PRIVATE | INTERNAL 
		| CONST | VIRTUAL | SYNCHRONIZED | INLINE | UNSAFE;
	
	public static final int CLASS_FUNC_RETURN_MODS = CONST | SIGNED | UNSIGNED;
	
	public static final String CLASS_ERROR = "Illegal class modifiers, only public, abstract, final and const are permitted";
	
	public static final String CLASS_FIELD_MODIFIERS_ERR = "Illegal field modifiers, only .... are permitted";
	
	
	public static final int METHOD_PRE_MODS = STATIC | PUBLIC | PROTECTED | PRIVATE | INTERNAL;
	public static final int METHOD_POST_MODS = CONST | VOLATILE;
	public static final int METHOD_MODIFIERS = METHOD_PRE_MODS | METHOD_POST_MODS;
	
	public static boolean in(int modifiers, int list) {
		return (modifiers & ~list) == 0;
	}
	
	public static int mask(int modifiers, int mask) {
		return modifiers & ~mask;
	}
	
	public static StringBuilder toString(int modifiers) {
		StringBuilder builder = new StringBuilder();
		if ((PUBLIC & modifiers) != 0) builder.append("public ");
		if ((PROTECTED & modifiers) != 0) builder.append("protected ");
		if ((PRIVATE & modifiers) != 0) builder.append("private ");
		if ((INTERNAL & modifiers) != 0) builder.append("internal ");
		if ((ABSTRACT & modifiers) != 0) builder.append("abstract ");
		if ((STATIC & modifiers) != 0) builder.append("static ");
		if ((UNSAFE & modifiers) != 0) builder.append("unsafe ");
		if ((VIRTUAL & modifiers) != 0) builder.append("virtual ");
		if ((CONST & modifiers) != 0) builder.append("const ");
		if ((INLINE & modifiers) != 0) builder.append("inline ");
		if ((VOLATILE & modifiers) != 0) builder.append("volatile ");
		if ((SYNCHRONIZED & modifiers) != 0) builder.append("synchronized ");
		if ((UNSIGNED & modifiers) != 0) builder.append("unsigned ");
		if ((SIGNED & modifiers) != 0) builder.append("signed ");
		if ((SHORT & modifiers) != 0) builder.append("short ");
		if ((LONG & modifiers) != 0) builder.append("long ");
		if ((EXTERN & modifiers) != 0) builder.append("extern ");
		return builder;
	}


}
