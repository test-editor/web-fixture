/*******************************************************************************
 * Copyright (c) 2012 - 2016 Signal Iduna Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Signal Iduna Corporation - initial API and implementation
 * akquinet AG
 * itemis AG
 *******************************************************************************/

package org.testeditor.fixture.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelperTool {
	
	private static final Logger logger = LoggerFactory.getLogger(HelperTool.class);
	private static final String PROPERTIES_FILE = "test.properties";
	private String proxyValue = null;
	private String proxyPort = null;
	private String proxyUserValue = null;
	private String proxyPasswordValue = null;
	private String proxyNonProxyValue = null;
	private String userName = null;
	private String passwd = null;
	private String url = null;

	
	public void initializeProperties () {
		this.loadProperties();
		this.setProxyValuesForFirefox();
	}
	
	
	
	private void setProxyValuesForFirefox() {
		logger.info("setting proxy properties");
		String PROXY_HOST_HTTP_VALUE = proxyValue;
		String PROXY_PORT_HTTP_VALUE = proxyPort;
		String PROXY_HOST_HTTPS_VALUE = proxyValue;
		String PROXY_PORT_HTTPS_VALUE = proxyPort;	
		
		String PROXY_USER = "http.proxyUser";
		String PROXY_PASSWORD = "http.proxyPassword";
		String PROXY_HOST_HTTP = "http.proxyHost";
		String PROXY_PORT_HTTP = "http.proxyPort";
		String PROXY_HOST_HTTPS = "https.proxyHost";
		String PROXY_PORT_HTTPS = "https.proxyPort";
		String PROXY_NONPROXY_HTTP = "http.nonProxyHosts";

		// First set Proxy Settings
		
		System.setProperty(PROXY_USER, proxyUserValue);
		System.setProperty(PROXY_PASSWORD, proxyPasswordValue);
		System.setProperty(PROXY_HOST_HTTP, PROXY_HOST_HTTP_VALUE);
		System.setProperty(PROXY_PORT_HTTP, PROXY_PORT_HTTP_VALUE);
		System.setProperty(PROXY_HOST_HTTPS, PROXY_HOST_HTTPS_VALUE);
		System.setProperty(PROXY_PORT_HTTPS, PROXY_PORT_HTTPS_VALUE);
		System.setProperty(PROXY_NONPROXY_HTTP, proxyNonProxyValue);
	}
	
	
	private void loadProperties() {
		
		Properties prop = new Properties();
		InputStream input = null;
		logger.info("loading proxy properties from input file");
		try {

		      input = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE);


		    // load a properties file
		    prop.load(input);

		    // get the property values for these variables for testing firefox with proxy settings
//		    logger.info(prop.getProperty("PROXY_VALUE"));
//		    logger.info(prop.getProperty("PROXY_PORT"));
//		    logger.info(prop.getProperty("PROXY_USER"));
//		    logger.info(prop.getProperty("PROXY_PASSWORD"));
//		    logger.info(prop.getProperty("PROXY_NONPROXY"));
		    
		    // set properties
		    setProperties(prop);

		} catch (IOException ex) {
		    ex.printStackTrace();
		} finally {
		    if (input != null) {
		        try {
		            input.close();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		    }
		}	
	}
	
	private void setProperties(Properties prop ) {
		logger.info("reading proxy properties from input file");
		proxyValue = prop.getProperty("PROXY_VALUE");
		proxyPort = prop.getProperty("PROXY_PORT");
		proxyUserValue = prop.getProperty("PROXY_USER");
		proxyPasswordValue = prop.getProperty("PROXY_PASSWORD");
		proxyNonProxyValue = prop.getProperty("PROXY_NONPROXY");
		userName = prop.getProperty("USERNAME");
		passwd = prop.getProperty("PASSWORD");
		url = prop.getProperty("URL");
	}


	/**
	 * @return the proxyValue
	 */
	public String getProxyValue() {
		return proxyValue;
	}


	/**
	 * @param proxyValue the proxyValue to set
	 */
	public void setProxyValue(String proxyValue) {
		this.proxyValue = proxyValue;
	}


	/**
	 * @return the proxyPort
	 */
	public String getProxyPort() {
		return proxyPort;
	}


	/**
	 * @param proxyPort the proxyPort to set
	 */
	public void setProxyPort(String proxyPort) {
		this.proxyPort = proxyPort;
	}


	/**
	 * @return the proxyUserValue
	 */
	public String getProxyUserValue() {
		return proxyUserValue;
	}


	/**
	 * @param proxyUserValue the proxyUserValue to set
	 */
	public void setProxyUserValue(String proxyUserValue) {
		this.proxyUserValue = proxyUserValue;
	}


	/**
	 * @return the proxyPasswordValue
	 */
	public String getProxyPasswordValue() {
		return proxyPasswordValue;
	}


	/**
	 * @param proxyPasswordValue the proxyPasswordValue to set
	 */
	public void setProxyPasswordValue(String proxyPasswordValue) {
		this.proxyPasswordValue = proxyPasswordValue;
	}


	/**
	 * @return the proxyNonProxyValue
	 */
	public String getProxyNonProxyValue() {
		return proxyNonProxyValue;
	}


	/**
	 * @param proxyNonProxyValue the proxyNonProxyValue to set
	 */
	public void setProxyNonProxyValue(String proxyNonProxyValue) {
		this.proxyNonProxyValue = proxyNonProxyValue;
	}


	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}


	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}


	/**
	 * @return the passwd
	 */
	public String getPasswd() {
		return passwd;
	}


	/**
	 * @param passwd the passwd to set
	 */
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}


	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}


	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}




}



