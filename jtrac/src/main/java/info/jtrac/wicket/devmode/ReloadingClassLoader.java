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
package info.jtrac.wicket.devmode;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * used by ReloadingWicketFilter, see class level comment there
 */
public class ReloadingClassLoader extends URLClassLoader {

    private static final Logger logger = LoggerFactory.getLogger(ReloadingClassLoader.class);
        
    private List<Pattern> watchPatterns = new ArrayList<Pattern>();
    private List<Pattern> ignorePatterns = new ArrayList<Pattern>();
    private long lastReload;
    private Set<Class> loadedClasses = new HashSet<Class>();

    public ReloadingClassLoader(ClassLoader parent) {
        super(new URL[]{}, parent);
        lastReload = new Date().getTime();
        Enumeration<URL> resources;
        try {
            resources = parent.getResources("");
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        while (resources.hasMoreElements()) {
            addURL(resources.nextElement());
        }
    }

    public void watch(String pattern) {
        watchPatterns.add(Pattern.compile(pattern));
    }

    public void ignore(String pattern) {
        ignorePatterns.add(Pattern.compile(pattern));
    }

    @Override
    public Class loadClass(String name) throws ClassNotFoundException {
        Class clazz = findLoadedClass(name);
        if (clazz != null) {
            return clazz;
        }
        if (mustWatch(name)) {
            try {
                // logger.debug("loading reloadable class: " + name);
                clazz = super.findClass(name);
                loadedClasses.add(clazz);
            } catch (ClassNotFoundException exception) {
                logger.error(exception.getMessage());
            }
        }
        if (clazz == null && getParent() != null) {
            clazz = getParent().loadClass(name);
        }
        if (clazz == null) {
            throw new ClassNotFoundException(name);
        }
        return clazz;
    }

    public boolean hasChanges() {        
        for (Class clazz : loadedClasses) {
            String classRelativeFile = clazz.getName().replaceAll("\\.", "/") + ".class";
            for (URL url : getURLs()) {
                File classAbsoluteFile = new File(url.getFile() + classRelativeFile);
                if (classAbsoluteFile.exists() && classAbsoluteFile.lastModified() > lastReload) {
                    logger.debug("change detected for file: " + clazz.getCanonicalName());
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public ReloadingClassLoader clone() {
        ReloadingClassLoader clone = new ReloadingClassLoader(getParent());
        clone.ignorePatterns = ignorePatterns;
        clone.watchPatterns = watchPatterns;
        return clone;
    }

    private boolean mustWatch(String name) {
        for (Pattern pattern : watchPatterns) {
            if (pattern.matcher(name).matches()) {
                for (Pattern pattern_ : ignorePatterns) {
                    if (pattern_.matcher(name).matches()) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
}
