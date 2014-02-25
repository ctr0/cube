package ecubic.wizard;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

import ecubic.builder.CubeNature;
import ecubic.builder.CubeProjectBuilder;


public class CubeProjectWizard extends Wizard implements INewWizard {
	
	public static final String ID = "ecubic.wizard.CubeProjectWizard";

	private IWorkbench workbench;
	private IStructuredSelection selection;
	
	private CubeProjectWizardPage page;
	
	public CubeProjectWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		this.selection = selection;
	}
	
	@Override
	public void addPages() {
		this.addPage(
			page = new CubeProjectWizardPage()
		);
	}
	
	@Override
	public boolean performFinish() {
		IProject project = createProject();
		if (project == null) {
			return false;
		}
		IWorkbenchWindow activeWindow = workbench.getActiveWorkbenchWindow();
		try {
			workbench.showPerspective("ecubic.perspective.CubePerspective", activeWindow);
		} catch (WorkbenchException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	private IProject createProject() {
		final IProject project = page.getProjectHandle();
		IPath projectPath = page.getProjectPath();
        IPath defaultPath = Platform.getLocation();

        if (defaultPath.equals(projectPath)){
        	projectPath = null;
        }

        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        final IProjectDescription description = workspace.newProjectDescription(project.getName());
        description.setLocation(projectPath);
        description.setNatureIds(new String[] {
        	CubeNature.ID
        });

		// Instanciate a project creation operation
		WorkspaceModifyOperation projectCreation = new WorkspaceModifyOperation() {
			@Override
			protected void execute(IProgressMonitor monitor)
					throws CoreException, InvocationTargetException, InterruptedException {
				createProject(project, description, monitor);
			}

		};

		// Run the project creation operation
		try {
			getContainer().run(true, true, projectCreation);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return null;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}

		return project;
	}
	
	private void createProject(
		IProject project, IProjectDescription description, IProgressMonitor monitor
	) throws CoreException {
		try {
			monitor.beginTask("cubeProjectCreation", 3);
			project.create(
				description, 
				new SubProgressMonitor(monitor, 1)
			);
			project.open(
				IResource.BACKGROUND_REFRESH, 
				new SubProgressMonitor(monitor, 1)
			);
			// TODO create generated files
			addBuilders(project, new SubProgressMonitor(monitor, 1));
		} finally {
			monitor.done();
		}
	}
	
	private static void addBuilders(IProject project, IProgressMonitor monitor)
			throws CoreException {
		IProjectDescription description = project.getDescription();
		ICommand[] commands = description.getBuildSpec();
		boolean found = false;

		for (ICommand command : commands) {
			if (command.getBuilderName().equals(CubeProjectBuilder.ID)) {
				found = true;
				break;
			}
		}

		if (!found) {
			// Add builder to the project
			ICommand command = description.newCommand();
			command.setBuilderName(CubeProjectBuilder.ID);
			ICommand[] newCommands = new ICommand[commands.length + 1];

			// Add it before other builders
			System.arraycopy(commands, 0, newCommands, 1, commands.length);
			newCommands[0] = command;
			description.setBuildSpec(newCommands);
			project.setDescription(description, null);
		}
	}

}
