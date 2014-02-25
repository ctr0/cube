package ecubic.editor;

import java.util.ArrayList;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;

import c3c.ast.ComplUnit;
import c3c.ast.Namespace;
import c3c.ast.Project;
import ecubic.CubePlugin;
import ecubic.builder.Dumper;
import ecubic.builder.CubeProjectBuilder;

public class CubeMultiPageEditor extends MultiPageEditorPart 
implements IResourceChangeListener {

	private TextEditor editor;
	private ArrayList<StyledText> previews;
	
	/**
	 * Creates a multi-page editor example.
	 */
	public CubeMultiPageEditor() {
		super();
		previews = new ArrayList<StyledText>(3);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}
	
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}
	
	void createPage() {
		try {
			editor = new TextEditor();
			int index = addPage(editor, getEditorInput());
			setPageText(index, editor.getTitle());
		} catch (PartInitException e) {
			ErrorDialog.openError(
				getSite().getShell(),
				"Error creating nested text editor",
				null,
				e.getStatus());
		}
	}
	
	void createPreview(String title) {
		Composite composite = new Composite(getContainer(), SWT.NONE);
		FillLayout layout = new FillLayout();
		composite.setLayout(layout);
		StyledText preview = new StyledText(composite, SWT.H_SCROLL | SWT.V_SCROLL);
		preview.setEditable(false);
		previews.add(preview);
		int index = addPage(composite);
		setPageText(index, title);
	}
	
	protected void createPages() {
		createPage();
		createPreview("dump");
		createPreview("AST");
		createPreview(".h");
		createPreview(".cxx");
	}
	
	/**
	 * Saves the multi-page editor's document.
	 */
	public void doSave(IProgressMonitor monitor) {
		getEditor(0).doSave(monitor);
	}
	
	/**
	 * Saves the multi-page editor's document as another file.
	 * Also updates the text for page 0's tab, and updates this multi-page editor's input
	 * to correspond to the nested editor's.
	 */
	public void doSaveAs() {
		IEditorPart editor = getEditor(0);
		editor.doSaveAs();
		setPageText(0, editor.getTitle());
		setInput(editor.getEditorInput());
	}
	
	/* (non-Javadoc)
	 * Method declared on IEditorPart
	 */
	public void gotoMarker(IMarker marker) {
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}
	
	/**
	 * The <code>MultiPageEditorExample</code> implementation of this method
	 * checks that the input is an instance of <code>IFileEditorInput</code>.
	 */
	public void init(IEditorSite site, IEditorInput editorInput)
		throws PartInitException {
		if (!(editorInput instanceof IFileEditorInput))
			throw new PartInitException("Invalid Input: Must be IFileEditorInput");
		super.init(site, editorInput);
	}
	
	/* (non-Javadoc)
	 * Method declared on IEditorPart.
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}
	
	/**
	 * Closes all project files on project close.
	 */
	public void resourceChanged(final IResourceChangeEvent event){
		if(event.getType() == IResourceChangeEvent.PRE_CLOSE){
			Display.getDefault().asyncExec(new Runnable(){
				public void run(){
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
					for (int i = 0; i < pages.length; i++){
						if(((FileEditorInput)editor.getEditorInput()).getFile().getProject().equals(event.getResource())){
							IEditorPart editorPart = pages[i].findEditor(editor.getEditorInput());
							pages[i].closeEditor(editorPart,true);
						}
					}
				}            
			});
		}
	}
	
	protected void pageChange(int index) {
		Project project = CubeProjectBuilder.getCubeProject();
		switch (index) {
		
		case 1: 
			IPersistableElement persistable = editor.getEditorInput().getPersistable();
			if (persistable instanceof FileEditorInput) {
				FileEditorInput f = (FileEditorInput) persistable;
				String path = f.getPath().toOSString();
				ComplUnit unit = project.getUnit(path);
				Dumper dumper = new Dumper();
				unit.accept(dumper);
				String text = dumper.getString();
				previews.get(index-1).setText(text);
			}
			
			break;
			
		case 2:
			Namespace ns = project.getGlobalNamespace();
			Dumper dumper = new Dumper();
			ns.accept(dumper);
			String text = dumper.getString();
			previews.get(index-1).setText(text);
			break;

		case 3:
			break;
			
		case 4:
			break;
		}
	}

}
