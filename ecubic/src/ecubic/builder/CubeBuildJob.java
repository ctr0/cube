package ecubic.builder;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import c3c.cube.CodeSource;
import c3c.cube.Compiler;

public class CubeBuildJob extends Job {

	private Compiler compiler;
	
	private ArrayList<CodeSource> sources;
	private String valac;
	private String vapi;
	private String output;

	public CubeBuildJob(Compiler compiler, ArrayList<CodeSource> sources, String valac, String vapi, String output) {
		super("Compiling Cube...");
		this.compiler = compiler;
		this.sources = sources;
		this.valac = valac;
		this.vapi = vapi;
		this.output = output;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		if (sources.isEmpty()) {
			return Status.OK_STATUS;
		}

		try {
			compiler.compile(sources);
		} catch (Exception e) {
			e.printStackTrace();
			return Status.CANCEL_STATUS;
		}
		
		return Status.OK_STATUS;
	}
	
	
}
