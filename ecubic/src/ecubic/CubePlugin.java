package ecubic;

import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import ecubic.builder.CubeProjectBuilder;

public class CubePlugin extends AbstractUIPlugin {

	public static final String P_VALAC_EXE = "valacPathPreference";
	public static final String P_VAPI_PATH = "vapiPathPreference";
	public static final String P_OUTPUT_FOLDER = "outputPathPreference";
	
	private static CubePlugin plugin;
	private static ResourceBundle resourceBundle = ResourceBundle.getBundle("ecubic.CubePluginMessages");

	public static ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	public static AbstractUIPlugin getDefault() {
		return plugin;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		synchronized (CubePlugin.class) {
			if (plugin != null)
				return;

			plugin = this;

//			Set<String> scheduledTobuild = new HashSet<String>();
//			project: for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
//				if (!project.isOpen() || scheduledTobuild.contains(project.getName()))
//					continue;
//
//				IProjectDescription desc = project.getDescription();
//				for (ICommand builder : desc.getBuildSpec()) {
//					if (builder.getBuilderName().equals(CubeProjectBuilder.class.getName())) {
//						scheduledTobuild.add(project.getName());
//						final IProject valaProject = project;
//						WorkspaceJob job = new WorkspaceJob("Rebuilding " + valaProject.getName()) {
//							@Override
//							public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
//								try {
//									valaProject.refreshLocal(IResource.DEPTH_INFINITE, monitor);
//									valaProject.build(IncrementalProjectBuilder.FULL_BUILD, monitor);
//								} catch (Throwable t) {
//									t.printStackTrace();
//									return Status.CANCEL_STATUS;
//								}
//								return Status.OK_STATUS;
//							}
//						};
//						job.setRule(ResourcesPlugin.getWorkspace().getRoot());
//						job.schedule();
//						continue project;
//					}
//				}
//			}
		}
	}

}
