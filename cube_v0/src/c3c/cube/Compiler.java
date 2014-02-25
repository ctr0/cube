package c3c.cube;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import c3c.ast.ComplUnit;
import c3c.ast.Project;
import c3c.sema.SemaAnalizer;


public class Compiler {
	
	public static final int GO = 0;
	public static final int GONE = 1;
	
	public static class Arguments {
		private String[] args;
		public Arguments(String[] args) {
			this.args = args;
		}
	}
	
	private Project project;
	
	private Arguments args;
	
	private String basePath;
	
	public Compiler() {
		project = new Project();
	}
	
	public Project getProject() {
		return project;
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
//		compile0("/home/j0rd1/.cube/expr2.c4", null);
//		compile0("C:\\c3\\hpp\\fltk.h", null);
//		compile0("C:\\c3\\hpp\\part.h", null);
		Compiler compiler = new Compiler();
//		compiler.compile(new Arguments(args), "C:\\c3\\hpp\\libstdc++-v3\\include");
//		compiler.compile(new Arguments(args), "/home/j0rd1/.cube/hpp/libstdc++-v3/include");
//		String dir = new File("").getAbsolutePath();
//		compiler.compile(new Arguments(args), dir + "\\src\\c4\\rt");
		
		compiler.compile(new Arguments(args), "C:\\c3\\method");
		
//		compiler.compile(new Arguments(args), "/home/j0rd1/.cube/hello");
	}
	
	public void compile(Compiler.Arguments args, String dir) throws FileNotFoundException, IOException {
		this.args = args;
		this.basePath = dir;
		
		File file = new File(dir);
		if (!file.isDirectory()) {
			throw new IllegalArgumentException(dir + " is not a directory");
		}
		compile(file);
	}
	
	private void compile(File dir) throws FileNotFoundException, IOException {
		compile(dir.getAbsolutePath(), dir.list());
	}
	
	public void compile(String basePath, Object[] files) throws FileNotFoundException, IOException {
		for (Object file : files) {
			File f = new File(file.toString());
			if (f.isDirectory()) {
				compile(basePath, f.list());
			} else {
				String name = f.getName();
				if (name.endsWith(".cube")) {
					String relPath = f.getPath();
					compile0(basePath + File.separatorChar, relPath);
				}
			}
		}
		
		SemaAnalizer sema = new SemaAnalizer(getProject(), null);
		Iterator<ComplUnit> iterator = getProject().getUnits();
		while (iterator.hasNext()) {
			ComplUnit unit = iterator.next();
			unit.accept(sema);
		}
		toString();
	}
	
	public void compile(ArrayList<CodeSource> sources) {
		for (CodeSource source : sources) {
			compile(source, null);
		}
		
		SemaAnalizer sema = new SemaAnalizer(getProject(), null);
		Iterator<ComplUnit> iterator = getProject().getUnits();
		while (iterator.hasNext()) {
			ComplUnit unit = iterator.next();
			unit.accept(sema);
		}
		toString();
	}
	
	private void compile0(String basePath, String relPath) {
		CodeSource source = new CodeSource(basePath, relPath);
		compile(source, null);
	}
	
	public void compile(CodeSource source, SrcPos includeLocation) {
		System.out.println("@@@@@@@@@@ FILE " + source.getFile().getAbsolutePath());
		ComplUnit unit = project.removeUnit(source);
		if (unit != null) {
			// dispose
		}
		if (!source.getFile().exists()) {
			Report.message(Report._0_FILE_NOT_FOUND, source, includeLocation);
			return;
		}
		unit = new ComplUnit(project, source);
		Builder builder = null;
		try {
			builder = new Builder(source);
		} catch (FileNotFoundException e) {
			Report.message(Report._0_FILE_NOT_FOUND, source, includeLocation);
			return;
		} catch (IOException e) {
			Report.message(Report._0_FILE_NOT_FOUND, source, includeLocation);
			return;
		}
		builder.parse(unit);
		project.addUnit(unit);
		
//		try {
//			ComplUnitGen gen = new ComplUnitGen("/home/j0rd1/.cube/hello/hello.cpp");
//			unit.accept(gen);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
//	public CodeSource getCodeSource(String relativePath) {
//		String path = basePath + File.separatorChar + relativePath;
//		return new CodeSource(path);
//	}

}
