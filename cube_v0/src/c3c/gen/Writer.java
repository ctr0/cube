package c3c.gen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import c3c.cube.IToken;
import c3c.cube.Lexeme;
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
		try {
			if (token.isComplex()) {
				boolean first = true;
				for (Lexeme lex : token.getLexemes()) {
					if (first) first = false;
					else w.write("::");
					w.write(lex.getName());
				}
			} else w.write(token.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void incIdent() {
		try {
			w.append('\n');
			ident++;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void decIdent() {
		try {
			w.append('\n');
			ident--;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void writeIdent() {
		for (int i = 0; i < ident; i++) {
			try {
				w.append('\t');
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	}

	public void writeSpace() {
		try {
			w.append(' ');
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void writeNewLine() {
		try {
			w.append('\n');
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
}
