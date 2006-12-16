package info.jtrac.mylar;

import java.io.File;

import org.eclipse.mylar.tasks.core.ITaskRepositoryListener;
import org.eclipse.mylar.tasks.core.TaskRepository;

public class JtracTaskRepositoryListener implements ITaskRepositoryListener {

	private File configFile;
	
	public JtracTaskRepositoryListener(File configFile) {
		this.configFile = configFile;
		readConfig();
	}
	
	public void repositoriesRead() {
		// TODO Auto-generated method stub
		
	}

	public void repositoryAdded(TaskRepository repository) {
		// TODO Auto-generated method stub
		
	}

	public void repositoryRemoved(TaskRepository repository) {
		// TODO Auto-generated method stub
		
	}

	public void repositorySettingsChanged(TaskRepository repository) {
		// TODO Auto-generated method stub
		
	}
	
	public void readConfig() {
		
	}
	
	public void writeConfig() {
		
	}

}
