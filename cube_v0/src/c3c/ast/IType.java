package c3c.ast;

import java.util.ArrayList;

import c3c.ast.visitor.ScopeVisitor;
import c3c.cube.IToken;
import c3c.cube.Id;
import c3c.cube.Modifiers;

public interface IType {
	
	public boolean isPrimitive();
	
	public ITypeDecl getDecl();
	
	public static interface ITypeDecl {

		public boolean isPrimitive();
		
		public boolean isGeneric();
		
		public IToken getToken();
		
		public Template getTemplDecl();

		public void accept(ScopeVisitor visitor);
	}
	
	
	public static class Primitive implements IType {
		
		public static final Primitive INT = new Primitive(Id.INT);
		public static final Primitive STRING = new Primitive(Id.STRING);
		
		private int id;
		
		public Primitive(int id) {
			this.id = id;
		}

		@Override
		public boolean isPrimitive() {
			return true;
		}

		@Override
		public ITypeDecl getDecl() {
			return null;
		}

		public static Primitive toPrimitive(int id) {
			switch (id) {
				case Id.INT: 			return INT;
				case Id.STRING: 		return STRING;
			}
			return null;
		}
	}
	
	public static class Type extends Node implements IType, ITypeDecl {
		
		private int modifiers;
		private boolean nullable;
		private Template templDecl;
		private ArrayList<Dim> dims;
		
		private ITypeDecl decl;
		
		private static class Dim {
			static final int ARRAY = 0;
			static final int POINTER = 1;
			static final int REFERENCE = 2;
			
			int type;
			int modifiers;
			boolean nullable;
			
			private Dim(int mods, int tp, boolean nul) {
				modifiers = mods;
				type = tp;
				nullable = nul;
			}
		}
		
		/*internal*/ public Type(int modifiers, IToken token) {
			this(modifiers, token, false);
		}
		
		/*internal*/ public Type(int modifiers, IToken token, boolean nullable) {
			super(token);
			this.modifiers = modifiers;
			this.nullable = nullable;
			this.dims = new ArrayList<Dim>();
		}
		
		public int getModifiers() {
			return modifiers;
		}
		
		@Override
		public boolean isPrimitive() {
			return getId() != Id.ID;
		}

		@Override
		public boolean isGeneric() {
			return templDecl != null;
		}
		
		public boolean isNullable() {
			return nullable;
		}

		@Override
		public Template getTemplDecl() {
			return templDecl;
		}
		
		public ITypeDecl getDecl() {
			return decl;
		}
		
		public void setDecl(ITypeDecl decl) {
			this.decl = decl;
		}

		public void setNullable(boolean nullable) {
			this.nullable = nullable;
		}

		@Override
		public void accept(ScopeVisitor visitor) {
			decl.accept(visitor);
		}

		public void visitTemplDecl(Template decl) {
			this.templDecl = decl;
		}

		public void visitArrayDimension(int modifiers, boolean nullable) {
			dims.add(new Dim(modifiers, Dim.ARRAY, nullable));
		}

		public void visitPointerDimension(int modifiers, boolean nullable) {
			dims.add(new Dim(modifiers, Dim.POINTER, nullable));
		}

		public void visitReferenceDimension(int modifiers, boolean nullable) {
			dims.add(new Dim(modifiers, Dim.REFERENCE, nullable));
		}

		@Override
		public String toString() {
			StringBuilder builder = Modifiers.toString(modifiers);
			builder.append(super.toString());
			if (templDecl != null) {
				builder.append(templDecl.toString());
			}
			if (nullable) builder.append('?');
			for (Dim dim : dims) {
				builder.append(' ');
				builder.append(Modifiers.toString(dim.modifiers));
				switch (dim.type) {
				case Dim.ARRAY: 	builder.append("[]"); 	break;
				case Dim.POINTER: 	builder.append('*'); 	break;
				case Dim.REFERENCE: builder.append('&'); 	break;
				}
				if (dim.nullable)	builder.append('?');
			}
			return builder.toString();
		}
	}
	
	public class TemplTypeParam extends Type {
		
		/*internal*/ public Type constr;
		
		public TemplTypeParam(int modifiers, IToken token) {
			super(modifiers, token);
		}
		
		public boolean hasConstraint() {
			return constr != null;
		}
		
		public Type getConstraint() {
			return constr;
		}

		public void visitConstraint(Type decl) {
			this.constr = decl;
		}

	}

}
