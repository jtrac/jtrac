package info.jtrac.svn;

import java.util.Collection;
import java.util.Iterator;
import junit.framework.TestCase;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class SvnTest extends TestCase {
    
    public void testNothing() {
        
    }
    
    /*
    public void testSvn() throws Exception {
        String url = "https://adms.satyam.com/svn/jtrac/trunk/jtrac";
        String name = "pt34469";
        String password = "";
        DAVRepositoryFactory.setup();
        SVNRepositoryFactoryImpl.setup();
        SVNRepository repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(name, password);
        repository.setAuthenticationManager(authManager);
        SVNNodeKind nodeKind = repository.checkPath("", -1);
        if (nodeKind == SVNNodeKind.NONE) {
            System.err.println("There is no entry at '" + url + "'.");
            System.exit(1);
        } else if (nodeKind == SVNNodeKind.FILE) {
            System.err.println("The entry at '" + url + "' is a file while a directory was expected.");
            System.exit(1);
        }
        System.out.println("Repository Root: " + repository.getRepositoryRoot(true));
        System.out.println("Repository UUID: " + repository.getRepositoryUUID());
        listEntries(repository, "");        
        long latestRevision = repository.getLatestRevision();
        System.out.println("Repository latest revision: " + latestRevision);
        System.exit(0);
    }
    
    private static void listEntries(SVNRepository repository, String path) throws SVNException {
        
        Collection entries = repository.getDir(path, -1, null, (Collection) null);
        Iterator iterator = entries.iterator();
        while (iterator.hasNext()) {
            SVNDirEntry entry = (SVNDirEntry) iterator.next();
            System.out.println("/" + (path.equals("") ? "" : path + "/")
            + entry.getName() + " (author:" + entry.getAuthor()
            + "; revision:" + entry.getRevision() + ")");
            if (entry.getKind() == SVNNodeKind.DIR) {
                listEntries(repository, (path.equals("")) ? entry.getName()
                : path + "/" + entry.getName());
            }
        }
    }
     */
    
}
