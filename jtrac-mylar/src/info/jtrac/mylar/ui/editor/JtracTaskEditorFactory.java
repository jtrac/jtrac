package info.jtrac.mylar.ui.editor;

import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.ui.editors.ITaskEditorFactory;
import org.eclipse.mylar.tasks.ui.editors.TaskEditor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

public class JtracTaskEditorFactory implements ITaskEditorFactory {

	public boolean canCreateEditorFor(ITask task) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean canCreateEditorFor(IEditorInput input) {
		// TODO Auto-generated method stub
		return false;
	}

	public IEditorPart createEditor(TaskEditor parentEditor,
			IEditorInput editorInput) {
		// TODO Auto-generated method stub
		return null;
	}

	public IEditorInput createEditorInput(ITask task) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean providesOutline() {
		// TODO Auto-generated method stub
		return false;
	}

}
