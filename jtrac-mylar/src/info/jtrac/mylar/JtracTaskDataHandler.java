package info.jtrac.mylar;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylar.tasks.core.AbstractAttributeFactory;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.ITaskDataHandler;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskRepository;

public class JtracTaskDataHandler implements ITaskDataHandler {

	private JtracRepositoryConnector connector;
	
	private AbstractAttributeFactory attributeFactory = new JtracAttributeFactory();
	
	public JtracTaskDataHandler(JtracRepositoryConnector connector) {
		this.connector = connector;
	}
	
	public AbstractAttributeFactory getAttributeFactory() {
		return attributeFactory;
	}

	public Set<AbstractRepositoryTask> getChangedSinceLastSync(
			TaskRepository repository, Set<AbstractRepositoryTask> tasks)
			throws CoreException, UnsupportedEncodingException {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getDateForAttributeType(String attributeKey, String dateString) {
		// TODO Auto-generated method stub
		return null;
	}

	public RepositoryTaskData getTaskData(TaskRepository repository, String taskId) throws CoreException {
		RepositoryTaskData taskData = new RepositoryTaskData(attributeFactory, 
				JtracRepositoryConnector.REPO_TYPE, repository.getUrl(), taskId);
		return taskData;
	}

	public String postTaskData(TaskRepository repository, RepositoryTaskData taskData) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

}
