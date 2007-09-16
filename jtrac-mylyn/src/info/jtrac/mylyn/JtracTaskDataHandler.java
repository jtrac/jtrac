package info.jtrac.mylyn;

import java.util.Collections;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.tasks.core.AbstractAttributeFactory;
import org.eclipse.mylyn.tasks.core.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;

public class JtracTaskDataHandler extends AbstractTaskDataHandler {

	private JtracRepositoryConnector connector;
	
	private AbstractAttributeFactory attributeFactory = new JtracAttributeFactory();	
	
	public JtracTaskDataHandler(JtracRepositoryConnector connector) {
		this.connector = connector;
	}	
	
	@Override
	public AbstractAttributeFactory getAttributeFactory(String repositoryUrl,
			String repositoryKind, String taskKind) {
		return attributeFactory;
	}

	@Override
	public AbstractAttributeFactory getAttributeFactory(RepositoryTaskData taskData) {
		return getAttributeFactory(taskData.getRepositoryUrl(), taskData.getRepositoryKind(), taskData.getTaskKind());
	}

	@Override
	public Set<String> getSubTaskIds(RepositoryTaskData taskData) {
		return Collections.emptySet();
	}

	@Override
	public RepositoryTaskData getTaskData(TaskRepository repository,
			String taskId, IProgressMonitor monitor) throws CoreException {
		RepositoryTaskData taskData = new RepositoryTaskData(attributeFactory, 
				JtracRepositoryConnector.REPO_TYPE, repository.getUrl(), taskId);
		return taskData;
	}

	@Override
	public boolean initializeTaskData(TaskRepository repository,
			RepositoryTaskData data, IProgressMonitor monitor)
			throws CoreException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String postTaskData(TaskRepository repository,
			RepositoryTaskData taskData, IProgressMonitor monitor)
			throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	public AbstractAttributeFactory getAttributeFactory() {
		return attributeFactory;
	}

}
