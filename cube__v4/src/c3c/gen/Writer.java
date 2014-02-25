package c3c.gen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import c3c.ast.IExpr;
import c3c.ast.IType.TemplTypeParam;
import c3c.ast.IType.Type;
import c3c.ast.MethodDecl;
import c3c.ast.NsDecl;
import c3c.ast.RecordDecl;
import c3c.ast.TemplDecl;
import c3c.ast.UsingDecl;
import c3c.ast.VarDecl;
import c3c.cube.IToken;
import c3c.cube.Lexeme;
import c3c.cube.Modifiers;
import c3c.cube.Report;

public class Writer {
	
	private File f;
	private BufferedWriter w;
	
	private int ident = 0;
	
	public static Writer getWriter(String path) {
		try {
			File file = new File(path);
			return new Writer(file, new BufferedWriter(new FileWriter(file)));
		} catch (IOException e) {
			Report.fatal(e.getMessage());
		}
		return null;
	}
	
	private Writer(File f, BufferedWriter w) {
		this.f = f;
		this.w = w;
	}
	
	public void close() {
		if (w != null) {
			try {
				w.flush();
				w.close();
			} catch (IOException e) {
				Report.fatal(e.getMessage());
			}
		}
	}

	public void createFile() {
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void write(String s) {
		try {
			w.append(s);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void write(char c) {
		try {
			w.append(c);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void write(Object o) {
		try {
			w.append(o.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeCannonical(IToken token) {
		if (token.isComplex()) {
			boolean first = true;
			for (Lexeme lex : token.getLexemes()) {
				if (first) first = false;
				else write("::");
				write(lex.getName());
			}
		} else write(token.getName());
	}

	public void incIdent() {
		write('\n');
		ident++;
	}
	
	public void decIdent() {
		write('\n');
		ident--;
	}
	
	public void writeIdent() {
		for (int i = 0; i < ident; i++) {
			write('\t');
		}
	}

	public void writeSpace() {
		write(' ');
	}
	
	public void writeNewLine() {
		write('\n');
	}
	
	public void writeType(Type type) {
		writeCannonical(type.getToken());
		TemplDecl templ = type.getTemplDecl();
		if (templ != null) {
			writeTemplate(templ);
		}
	}

	public void writeTemplate(TemplDecl templ) {
		write('<');
		Iterator<TemplTypeParam> itt = templ.getTypeParams();
		boolean first = true;
		while (itt.hasNext()) {
			if (!first) write(',');
			else first = false;
			writeTemplTypeParam(itt.next());
		}
		Iterator<VarDecl> itv = templ.getVarDecls();
		while (itv.hasNext()) {
			if (!first) write(',');
			else first = false;
			writeVarDecl(itv.next());
		}
		write('>');
	}

	private void writeTemplTypeParam(TemplTypeParam param) {
		writeType(param);
		if (param.hasConstraint()) {
			write(" : ");
			writeType(param.getConstraint());
		}
	}
	
	public void writeVarDecl(VarDecl decl) {
		Type type = decl.getType();
		if (type != null) {
			writeType(type);
			writeSpace();
		}
		write(decl.getToken());
		IExpr init = decl.getInitializer();
		if (init != null) {
			write(" = ");
			writeExpr(init);
		}
	}

	public void writeExpr(IExpr init) {
		// TODO Auto-generated method stub
		
	}
	
	public void writeUsingDecl(UsingDecl decl) {
		write("using namespace ");
		writeCannonical(decl.getToken());
	}
	
	public void writeNsDecl(NsDecl decl) {
		IToken token = decl.getToken();
		if (token.isComplex()) {
			for (Lexeme lexeme : token.getLexemes()) {
				writeNewLine();
				writeIdent();
				write("namespace ");
				write(lexeme);
				write(" {");
				incIdent();
	}	}	}
	
	public void writeMethodDecl(MethodDecl decl) {
		Type returnType = decl.getType();
		if (returnType != null) {
			writeType(returnType);
			writeSpace();
		}
		int modifiers = decl.getModifiers();
		write(Modifiers.toString(modifiers & ~(Modifiers.CONST|Modifiers.VOLATILE)));
		write(decl.getName());
		TemplDecl templ = decl.getTemplDecl();
		if (templ != null) {
			writeTemplate(templ);
		}
		write('(');
		boolean first = true;
		Iterator<VarDecl> iterator = decl.getArgDecls();
		while (iterator.hasNext()) {
			if (!first) write(", ");
			else first = false;
			writeVarDecl(iterator.next());
		}
		write(") ");
		write(Modifiers.toString(modifiers & (Modifiers.CONST|Modifiers.VOLATILE)));
	}

	public void writeRecordDecl(RecordDecl decl) {
		write(Modifiers.toString(decl.getModifiers()));
		write(decl.getName());
		TemplDecl templ = decl.getTemplDecl();
		if (templ != null) {
			writeTemplate(templ);
		}
		boolean first = true;
		Iterator<Type> iterator = decl.getBaseDecls();
		while (iterator.hasNext()) {
			if (first) write(" : ");
			else write(", ");
			writeType(iterator.next());
		}
	}
}
