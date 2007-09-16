package info.jtrac.mylyn;

import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.tasks.core.AbstractAttachmentHandler;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.RepositoryAttachment;
import org.eclipse.mylyn.tasks.core.TaskRepository;


public class JtracAttachmentHandler extends AbstractAttachmentHandler {

	private JtracRepositoryConnector connector;
	
	public JtracAttachmentHandler(JtracRepositoryConnector connector) {
		this.connector = connector;
	}

	@Override
	public boolean canDeprecate(TaskRepository repository,
			RepositoryAttachment attachment) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canDownloadAttachment(TaskRepository repository,
			AbstractTask task) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canUploadAttachment(TaskRepository repository,
			AbstractTask task) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public InputStream getAttachmentAsStream(TaskRepository repository,
			RepositoryAttachment attachment, IProgressMonitor monitor)
			throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateAttachment(TaskRepository repository,
			RepositoryAttachment attachment) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void uploadAttachment(TaskRepository repository, AbstractTask task,
			ITaskAttachment attachment, String comment, IProgressMonitor monitor)
			throws CoreException {
		// TODO Auto-generated method stub
		
	}	

}
