package ecubic.preference;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import ecubic.CubePlugin;

public class CubePreferenceInitializer extends AbstractPreferenceInitializer {
	
	public void initializeDefaultPreferences() {
		IPreferenceStore store = CubePlugin.getDefault().getPreferenceStore();
		store.setDefault(CubePlugin.P_VALAC_EXE, "/usr/local/bin/valac");
		store.setDefault(CubePlugin.P_VAPI_PATH, "/usr/local/share/vala/vapi/");
		store.setDefault(CubePlugin.P_OUTPUT_FOLDER, "/output/");
	}

}
