package info.jtrac.mylar;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.IAttachmentHandler;
import org.eclipse.mylar.tasks.core.ITaskDataHandler;
import org.eclipse.mylar.tasks.core.QueryHitCollector;
import org.eclipse.mylar.tasks.core.TaskRepository;

public class JtracRepositoryConnector extends AbstractRepositoryConnector {
	
	private final static String UI_LABEL = "JTrac";
	private final static String REPO_TYPE = "jtrac";
	
	private JtracTaskRepositoryListener taskRepositoryListener;

	@Override
	public boolean canCreateNewTask(TaskRepository repository) {
		return false;
	}

	@Override
	public boolean canCreateTaskFromKey(TaskRepository repository) {
		return false;
	}

	@Override
	public AbstractRepositoryTask createTaskFromExistingKey(TaskRepository repository, String id) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAttachmentHandler getAttachmentHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLabel() {
		return UI_LABEL;
	}

	@Override
	public String getRepositoryType() {
		return REPO_TYPE;
	}

	@Override
	public String getRepositoryUrlFromTaskUrl(String taskFullUrl) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getSupportedVersions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITaskDataHandler getTaskDataHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTaskIdFromTaskUrl(String taskFullUrl) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTaskWebUrl(String repositoryUrl, String taskId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus performQuery(AbstractRepositoryQuery query,
			TaskRepository repository, IProgressMonitor monitor,
			QueryHitCollector resultCollector) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateAttributes(TaskRepository repository,
			IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateTask(TaskRepository repository,
			AbstractRepositoryTask repositoryTask) throws CoreException {
		// TODO Auto-generated method stub
		
	}
	
	public void stop() {
		if (taskRepositoryListener != null) {
			taskRepositoryListener.writeConfig();
		}
	}	

}
