package info.jtrac.mylyn.util;

import info.jtrac.mylyn.ui.JtracUiPlugin;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class ExceptionUtils {
	
	public static Status toStatus(Exception e) {
		return new Status(Status.ERROR, JtracUiPlugin.PLUGIN_ID, IStatus.ERROR, "Error", e);
	}

}
