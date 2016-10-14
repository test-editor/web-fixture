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

import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 	Bitte diesen Test nicht loeschen 
 *	da die Entwicklung der Selenium Treiber für Firefox noch in der Betaphase ist und noch nicht final,
 * 	 moechte ich diesen Test dafuer nutzen, vorhandene Methoden weiterhin auf Funktionalitaet zu pruefen.  
 */
public class WebDriverFixtureTest  {
		


	private static final Logger logger = LoggerFactory.getLogger(WebDriverFixtureTest.class);
	private static final String WEBSITE = "index.html";
	
	/**
	 * 	Dieser Test testet die vorhandene Funktionalitaet in der Konstellation <br>
	 *	<ul>
	 *    <li>Firefox Portable 45.2.0 ESR</li>
	 *	  <li>Selenium Webdriver Version 2.53.0</li>
	 * 	  <li>Webdrivermanager 1.4.4</li>
	 *    <li>Ngwebdriver 0.9.5</li>
	 *	  <li>ohne Geckodriver</li>
	 *  </ul>
	 *	
	 * @throws InterruptedException
	 */
	//@Test
	public void firefoxPortableStartAndStopTest() throws InterruptedException{

		logger.info("Starting Firefox Portable 45.2.0");
		String pathFirefoxPortable = "c:\\dev\\tools\\firefox\\FirefoxPortable\\firefox.exe";
		URL webSite =  getClass().getClassLoader().getResource(WEBSITE);
		logger.info("Following URL : {} will be opened" , webSite.toString());
		String expectedTitle = "Login Wiki";
		
		HelperTool tool = new HelperTool();
		tool.initializeProperties();		
		
		WebDriverFixture fixture = new WebDriverFixture();
		
		fixture.startFireFoxPortable(pathFirefoxPortable);
		fixture.waitSeconds(2);
		fixture.gotToUrl(webSite.toString());
		Assert.assertTrue(fixture.getDriver().getTitle().startsWith(expectedTitle));
		fixture.typeInto(tool.getUserName(), "test_1");
		String userName = fixture.getWebElement("[id]" + (tool.getUserName())).getAttribute("value");
		Assert.assertTrue((userName).equals("test_1"));
		fixture.typeInto(tool.getPasswd(), "test");
		String password = fixture.getWebElement("[id]" + (tool.getPasswd())).getAttribute("value");
		Assert.assertTrue((password).equals("test"));
		fixture.closeBrowser();
	}
	
	
	/**
	 * 	Dieser Test testet die vorhandene Funktionalitaet in der Konstellation <br>
	 *	<ul>
	 *    <li>Firefox Portable 49.0.1 </li>
	 *	  <li>Selenium Webdriver Version 3.00 beta4</li>
	 * 	  <li>Webdrivermanager 1.4.4</li>
	 *    <li>Ngwebdriver 0.9.5</li>
	 *	  <li>mit Geckodriver 0.11.1</li>
	 *  </ul>
	 *	
	 * @throws InterruptedException
	 */
	//@Test
	public void newFirefoxPortableStartAndStopTest() throws InterruptedException {
		logger.info("Starting Firefox Portable 49.0.1");
		String pathFirefoxPortable = "c:\\dev\\tools\\firefox\\FirefoxPortable_49.01\\FirefoxPortable\\firefox.exe";
		String pathGeckodriver = "c:\\dev\\tools\\firefox\\geckodriver.exe";
		
		// einkommentieren nur wenn mit Selenium Version ab 3.00 beta4 getetestet werden soll,
		// weil Firefox (ab 47) nur noch mit dem GeckoDriver (zur Zeit akt. Version 0.11.1) startet. 
		System.setProperty("webdriver.gecko.driver", pathGeckodriver);
		
		HelperTool tool = new HelperTool();
		tool.initializeProperties();
		WebDriverFixture fixture = new WebDriverFixture();
		
		fixture.startFireFoxPortable(pathFirefoxPortable);
		fixture.waitSeconds(2);
		fixture.gotToUrl(tool.getUrl());
		fixture.typeInto(tool.getUserName(), "test");
		fixture.typeInto(tool.getPasswd(), "test");
		fixture.waitSeconds(2);
		fixture.closeBrowser();
	}
	
}
