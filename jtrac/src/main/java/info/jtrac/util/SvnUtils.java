/*
 * Copyright 2002-2005 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package info.jtrac.util;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * Utilities that talk to and get data from a Subversion repository
 * using the JavaSvn library
 */
public class SvnUtils {
    
    public static SVNRepository getRepository(String url, String username, String password) {
        DAVRepositoryFactory.setup();
        SVNRepositoryFactoryImpl.setup();
        SVNRepository repository = null;
        SVNNodeKind nodeKind = null;
        try {
            repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
            ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(username, password);
            repository.setAuthenticationManager(authManager);
            nodeKind = repository.checkPath("", -1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (nodeKind == SVNNodeKind.NONE) {
            throw new RuntimeException("There is no entry at '" + url + "'.");
        } else if (nodeKind == SVNNodeKind.FILE) {
            throw new RuntimeException("The entry at '" + url + "' is a file while a directory was expected.");
        }
        return repository;
    }
    
    public static Map<String, Integer> getCommitsPerCommitter(SVNRepository repository) {
        Collection<SVNLogEntry> svnLogEntries = null;
        try {
            svnLogEntries = repository.log(new String[] {""}, null, 0, repository.getLatestRevision(), true, true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Date createdDate = null;
        long now = new Date().getTime();
        long commits = 0;
        Map<String, Integer> commitsPerCommitter = new TreeMap<String, Integer>();
        Map<String, Integer> commitsPerFile = new TreeMap<String, Integer>();
        for (SVNLogEntry entry : svnLogEntries) {
            if(entry.getAuthor() == null || entry.getDate() == null) {
                // skip invalid log entry
                continue;
            }
            if (createdDate == null) {
                createdDate = entry.getDate();
            }
            commits++;
            long age = now - entry.getDate().getTime();
            String committer = trimName(entry.getAuthor());
            Integer commitsByThisCommitter = commitsPerCommitter.get(committer);
            commitsPerCommitter.put(committer, (commitsByThisCommitter == null) ? 1 : commitsByThisCommitter + 1);            
        }
        return commitsPerCommitter;
    }
    
    private static String trimName(String in) {
        int pos = in.indexOf('\\');
        if (pos != -1) {
            return in.substring(pos + 1).toLowerCase();
        }
        return in.toLowerCase();
    }
    
}
