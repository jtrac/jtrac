package info.jtrac.mylar;

import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.TaskList;

public class JtracQueryHit extends AbstractQueryHit {

	public JtracQueryHit(TaskList taskList, String repositoryUrl, String description, String id) {
		super(taskList, repositoryUrl, description, id);
	}

	@Override
	protected AbstractRepositoryTask createTask() {
		JtracRepositoryTask task = new JtracRepositoryTask(getHandleIdentifier(), getSummary(), true);
		task.setPriority(priority);
		return task;
	}

}
