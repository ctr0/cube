package ecubic.builder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;

import c3c.ast.Project;
import c3c.cube.CodeSource;
import c3c.cube.Report;
import c3c.cube.Report.Appender;
import c3c.cube.SrcPos;
import ecubic.CubePlugin;

public class CubeProjectBuilder extends IncrementalProjectBuilder implements Appender {

	public static final String ID = "ecubic.builder.CubeProjectBuilder";
	private static final String MARKER_TYPE = "ecubic.cubeProblem";
	
	private static final c3c.cube.Compiler compiler = new c3c.cube.Compiler();
	
	public static Project getCubeProject() {
		return compiler.getProject();
	}
	
	public CubeProjectBuilder() {
		Report.addAppender(this);
	}

	@Override
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
		IResourceDelta delta = null;
		if (kind == AUTO_BUILD || kind == INCREMENTAL_BUILD) {
			delta = getDelta(getProject());
		}
		if (delta == null) {
			getCubeProject().clean();
		}
		fullBuild(monitor, delta);
		return new IProject[0];
	}
	
	private static void deleteMarkers(IFile file) {
		try {
			file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
		} catch (CoreException ce) {
		}
	}

	/**
	 * Performs a full build of the project, reporting to <code>monitor</code>.
	 * 
	 * @param monitor
	 * @throws CoreException
	 */
	private void fullBuild(IProgressMonitor monitor, IResourceDelta delta) throws CoreException {
		
		monitor.beginTask("CubeProjectBuilding", IProgressMonitor.UNKNOWN);
		ArrayList<CodeSource> files = new ArrayList<CodeSource>();
		IResource[] members = getProject().members();

		for (IResource resource : members) {
			if (resource.getType() == IResource.FILE) {
				addCubeCompilerCompliantFile((IFile) resource, files, delta);
			} else if (resource.getType() == IResource.FOLDER) {
				for (IFile file : getFileChildren((IFolder) resource)) {
					addCubeCompilerCompliantFile(file, files, delta);
				}
			}
		}

		IPreferenceStore store = CubePlugin.getDefault().getPreferenceStore();
		IFolder folder = getProject().getFolder(store.getString(CubePlugin.P_OUTPUT_FOLDER));
		if (!folder.exists()) {
			folder.create(true, true, monitor);
		}

		String valac  = store.getString(CubePlugin.P_VALAC_EXE);
		String vapi   = store.getString(CubePlugin.P_VAPI_PATH);
		String output = folder.getLocation().toString();

		CubeBuildJob job = new CubeBuildJob(compiler, files, valac, vapi, output);
		job.schedule();

		monitor.done();
	}

	private static void addCubeCompilerCompliantFile(IFile file, ArrayList<CodeSource> files, IResourceDelta delta) {
		if (file.getName().endsWith(".cube")
				|| file.getName().endsWith(".c3")
				|| file.getName().endsWith(".h3")) {
			if (delta == null || delta.findMember(file.getProjectRelativePath()) != null) {
				String basePath = file.getProject().getLocation().toOSString() + File.separatorChar;
				String relPath = file.getProjectRelativePath().toOSString();
				files.add(new CodeSource(basePath, relPath));
				deleteMarkers(file);
			}
		}
	}

	private static List<IFile> getFileChildren(IFolder folder) {
		final ArrayList<IFile> files = new ArrayList<IFile>();
		try {
			folder.accept(new IResourceVisitor() {

				public boolean visit(IResource resource) {
					if (resource instanceof IFile) {
						files.add((IFile) resource);
						return false;
					}
					return true;
				}

			});
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
		return files;
	}

	@Override
	protected void clean(IProgressMonitor monitor) throws CoreException {
		// Cleaning consists in deleting the content of the output folder
		IPreferenceStore store = CubePlugin.getDefault().getPreferenceStore();
		String output = store.getString(CubePlugin.P_OUTPUT_FOLDER);

		IFolder folder = getProject().getFolder(output);
		if (!folder.exists()) {
			return;
		}

		for (IResource resource : folder.members()) {
			resource.delete(true, monitor);
		}
	}

	@Override
	public void message(int severity, String message, CodeSource source, SrcPos location) {
		try {
			IMarker marker = getProject().getFile(source.getRelativePath()).createMarker(MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
//			marker.setAttribute(IMarker.LINE_NUMBER, location.line());
			marker.setAttribute(IMarker.LOCATION, "Line " + location.line());
			marker.setAttribute(IMarker.CHAR_START, location.offset() + location.begin());
			marker.setAttribute(IMarker.CHAR_END, location.offset() + location.end());
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
