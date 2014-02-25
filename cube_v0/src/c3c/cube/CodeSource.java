package c3c.cube;

import java.io.File;

public class CodeSource implements Comparable<CodeSource> {
	
	private String basePath;
	private String relPath;
	
	private String path;
	
	public CodeSource(String basePath, String relPath) {
		this.basePath = basePath;
		this.relPath = relPath;
		this.path = basePath + relPath;
	}
	
	public File getFile() {
		return new File(path);
	}
	
	public String getBasePath() {
		return relPath;
	}
	
	public String getRelativePath() {
		return relPath;
	}
	
	public String getFullPath() {
		return path;
	}
	
	@Override
	public int hashCode() {
		return path.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof String)
			return equals((CodeSource) obj);
		return false;
	}
	
	public boolean equals(CodeSource source) {
		return compareTo(source) == 0;
	}
	
	public boolean equals(String id) {
		return this.path.equals(id);
	}

	@Override
	public int compareTo(CodeSource source) {
		return path.compareTo(source.path);
	}
	
	public String toString() {
		return path;
	}
}
