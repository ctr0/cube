package ecubic.preference;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ecubic.CubePlugin;

public class CubePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private String _(String key) {
		return CubePlugin.getResourceBundle().getString(key);
	}

	public CubePreferencePage() {
		super(GRID);
		setPreferenceStore(CubePlugin.getDefault().getPreferenceStore());
		setDescription(_("preferences.general.description"));
	}

	public void createFieldEditors() {
		addField(
			new FileFieldEditor(
				CubePlugin.P_VALAC_EXE, 
				_("preferences.general.valac.executable"), 
				getFieldEditorParent()
			)
		);
		addField(
			new DirectoryFieldEditor(
				CubePlugin.P_VAPI_PATH, 
				_("preferences.general.vapi.path"), 
				getFieldEditorParent()
			)
		);	
		addField(
			new StringFieldEditor(
				CubePlugin.P_OUTPUT_FOLDER, 
				_("preferences.general.output.directory"), 
				getFieldEditorParent()
			)
		);	
	}

	public void init(IWorkbench workbench) {}

}