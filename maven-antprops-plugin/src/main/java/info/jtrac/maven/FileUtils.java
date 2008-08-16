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

package info.jtrac.maven;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {

	public static boolean exists(String fileName) {
		File file = new File(fileName);
		return file.exists();
	}
	
	public static StringBuffer readFile(String fileName) {
		InputStream is = null;
		try {
			is = new FileInputStream(fileName);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		return readFile(is);
	}
	
	public static StringBuffer readFile(Class clazz, String fileName) {
		InputStream is = clazz.getResourceAsStream(fileName);
		return readFile(is);
	}
	
	private static StringBuffer readFile(InputStream is) {
		BufferedReader buffer = null;
		StringBuffer sb = new StringBuffer();
		String s = null;
		try {
			try {
				buffer = new BufferedReader(new InputStreamReader(is));
				while ((s = buffer.readLine()) != null) {
					sb.append(s).append('\n');
				}
			} finally {
				is.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return sb;		
	}

	public static void writeFile(String content, String fileName, boolean append) {
		FileWriter writer = null;
		try {
			try {
				writer = new FileWriter(fileName, append);
				writer.write(content);
			} finally {
				writer.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}	
	
}
