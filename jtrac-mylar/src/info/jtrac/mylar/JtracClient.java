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

import info.jtrac.mylar.domain.Item;
import info.jtrac.mylar.domain.JtracVersion;
import info.jtrac.mylar.domain.Success;
import info.jtrac.mylar.exception.HttpException;
import info.jtrac.mylar.util.XmlUtils;

import java.net.HttpURLConnection;
import java.net.Proxy;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

/**
 * this class has the responsibility of communicating with a JTrac
 * server / repository over HTTP(S).  Messages are Plain Old XML (POX), 
 * and requests are made REST style as HTTP GET / POST depending on the
 * complexity of input parameters
 */
public class JtracClient {
	
	private HttpClient httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
	
	private String repoUrl;
	private String username;
	private String password;
	private Proxy proxy;
	
	public JtracClient(String repoUrl, String username, String password, Proxy proxy) {
		this.repoUrl = repoUrl;
		this.username = username;
		this.password = password;
		this.proxy = proxy;
	}
	
	private String doGet(String url) throws Exception {
		HttpMethod get = new GetMethod(url);
		String response = null;
		int code;
		try {
			code = httpClient.executeMethod(get);
			if (code != HttpURLConnection.HTTP_OK) {
				throw new HttpException("HTTP Response Code: " + code);
			}
			response = get.getResponseBodyAsString();			
		} finally {
			get.releaseConnection();
		}
		return response;
	} 
	
	private String doPost(String url, String message) throws Exception {
		PostMethod post = new PostMethod(url);
		post.setRequestEntity(new StringRequestEntity(message, "text/xml", "UTF-8"));
		String response = null;
		int code;
		try {
			code = httpClient.executeMethod(post);
			if (code != HttpURLConnection.HTTP_OK) {
				throw new HttpException("HTTP Response Code: " + code);
			}
			response = post.getResponseBodyAsString();
		} finally {
			post.releaseConnection();
		}
		return response;
	} 	
	
	public JtracVersion getJtracVersion() throws Exception {
		RequestUri uri = new RequestUri("version.get");
		String xml = doGet(repoUrl + uri);
		return new JtracVersion(XmlUtils.parseJtrac(xml));
	}
	
	public String putItem(Item item) throws Exception {
		RequestUri uri = new RequestUri("item.put");
		String xml = doPost(repoUrl + uri, item.getAsXml());
		Success success = new Success(XmlUtils.parse(xml));
		return success.getValue("refId");
	}
	

}
