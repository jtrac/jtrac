package info.jtrac.mylar;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.IAttachmentHandler;
import org.eclipse.mylar.tasks.core.RepositoryAttachment;
import org.eclipse.mylar.tasks.core.TaskRepository;

public class JtracAttachmentHandler implements IAttachmentHandler {

	private JtracRepositoryConnector connector;
	
	public JtracAttachmentHandler(JtracRepositoryConnector connector) {
		this.connector = connector;
	}
	
	public boolean canDeprecate(TaskRepository repository,
			RepositoryAttachment attachment) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean canDownloadAttachment(TaskRepository repository,
			AbstractRepositoryTask task) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean canUploadAttachment(TaskRepository repository,
			AbstractRepositoryTask task) {
		// TODO Auto-generated method stub
		return false;
	}

	public void downloadAttachment(TaskRepository taskRepository,
			RepositoryAttachment attachment, File file) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	public byte[] getAttachmentData(TaskRepository repository,
			RepositoryAttachment attachment) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateAttachment(TaskRepository repository,
			RepositoryAttachment attachment) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	public void uploadAttachment(TaskRepository repository,
			AbstractRepositoryTask task, String comment, String description,
			File file, String contentType, boolean isPatch)
			throws CoreException {
		// TODO Auto-generated method stub
		
	}

}
