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

package info.jtrac.mylar;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * this class has the responsibility of communicating with a JTrac
 * server / repository over HTTP.  The REST style is used as far as possible
 */
public class JtracClient {
	
	private HttpClient httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
	
	private String repositoryUrl;
	
	public JtracClient(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}
	
	public byte[] doGet(String url) throws Exception {
		HttpMethod get = new GetMethod(url);
		byte[] response = null;
		try {
			httpClient.executeMethod(get);
			response = get.getResponseBody();
		} finally {
			get.releaseConnection();
		}
		return response;
	} 
	

}
