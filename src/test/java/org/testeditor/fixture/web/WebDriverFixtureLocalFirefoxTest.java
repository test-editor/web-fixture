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
import java.net.URL;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 	Bitte diesen Test nicht loeschen 
 *	da die Entwicklung der Selenium Treiber für Firefox noch in der Betaphase ist und noch nicht final,
 * 	 moechte ich diesen Test dafuer nutzen, vorhandene Methoden weiterhin auf Funktionalitaet zu pruefen.  
 */
public class WebDriverFixtureLocalFirefoxTest  {
	private static final Logger logger = LoggerFactory.getLogger(WebDriverFixtureLocalFirefoxTest.class);
	private static final String WEBSITE = "index.html";
	
	@Before
	public void Setup() {
		Assume.assumeTrue("This is not a Windows OS - ignoring test", HelperTool.isOsWindows());
		Assume.assumeTrue("Geckodriver is not present on test system - ignoring test", HelperTool.isGeckoDriverPresent());
	}
	

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
	 * @throws IOException 
	 */
	@Test
	public void firefoxPortableStartAndStopTest() throws InterruptedException, IOException{

		logger.info("Starting Firefox Portable 45.2.0");
		String pathFirefoxPortable = "c:\\dev\\tools\\firefox\\FirefoxPortable\\firefox.exe";
		URL webSite =  getClass().getClassLoader().getResource(WEBSITE);
		logger.info("Following URL : {} will be opened" , webSite.toString());
		String expectedTitle = "Login Wiki";
		
		BrowserProperties tool = new BrowserProperties();
		tool.initializeProperties();		
		
		WebDriverFixture fixture = new WebDriverFixture();
		
		fixture.startFireFoxPortable(pathFirefoxPortable);
		fixture.waitSeconds(2);
		fixture.goToUrl(webSite.toString());
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
	 * @throws IOException 
	 */
	//@Test
	public void newFirefoxPortableStartAndStopTest() throws InterruptedException, IOException {
		logger.info("Starting Firefox Portable 49.0.1");
		String pathFirefoxPortable = "c:\\dev\\tools\\firefox\\FirefoxPortable_49.01\\FirefoxPortable\\firefox.exe";
		
		URL webSite =  getClass().getClassLoader().getResource(WEBSITE);
		logger.info("Following URL : {} will be opened" , webSite.toString());
		String expectedTitle = "Login Wiki";
		
		// einkommentieren nur wenn mit Selenium Version ab 3.00 beta4 getetestet werden soll,
		// weil Firefox (ab 47) nur noch mit dem GeckoDriver (zur Zeit akt. Version 0.11.1) startet. 
		System.setProperty("webdriver.gecko.driver", HelperTool.getPathgeckodriver());
		
		BrowserProperties tool = new BrowserProperties();
		tool.initializeProperties();
		WebDriverFixture fixture = new WebDriverFixture();
		
		fixture.startFireFoxPortable(pathFirefoxPortable);
		fixture.waitSeconds(2);
		fixture.goToUrl(webSite.toString());
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
	 *    <li>Firefox 49.0.1 </li>
	 *	  <li>Selenium Webdriver Version 3.00 beta4</li>
	 * 	  <li>Webdrivermanager 1.4.4</li>
	 *    <li>Ngwebdriver 0.9.5</li>
	 *	  <li>mit Geckodriver 0.11.1</li>
	 *  </ul>
	 *	
	 * @throws InterruptedException
	 * @throws IOException 
	 */
	//@Test
	public void newFirefoxStartAndStopTest() throws InterruptedException, IOException {
		
		//GIVEN
		logger.info("Starting Firefox 49.0.1");
		//String pathFirefoxPortable = "c:\\dev\\tools\\firefox\\FirefoxPortable_49.01\\FirefoxPortable\\firefox.exe";
		URL webSite =  getClass().getClassLoader().getResource(WEBSITE);
		logger.info("Following URL : {} will be opened" , webSite.toString());
		String expectedTitle = "Login Wiki";
    	// einkommentieren nur wenn mit Selenium Version ab 3.00 beta4 getetestet werden soll,
		// weil Firefox (ab 47) nur noch mit dem GeckoDriver (zur Zeit akt. Version 0.11.1) startet. 
		//System.setProperty("webdriver.gecko.driver", pathGeckodriver);
		BrowserProperties tool = new BrowserProperties();
		tool.initializeProperties();
		WebDriverFixture fixture = new WebDriverFixture();
		
		//WHEN
		fixture.startBrowser("firefox");
		fixture.waitSeconds(2);
		fixture.goToUrl(webSite.toString());
		
		//THEN
		Assert.assertTrue(fixture.getDriver().getTitle().startsWith(expectedTitle));
		fixture.typeInto(tool.getUserName(), "test_1");
		String userName = fixture.getWebElement("[id]" + (tool.getUserName())).getAttribute("value");
		Assert.assertTrue((userName).equals("test_1"));
		fixture.typeInto(tool.getPasswd(), "test");
		String password = fixture.getWebElement("[id]" + (tool.getPasswd())).getAttribute("value");
		Assert.assertTrue((password).equals("test"));
		fixture.closeBrowser();	
		
	}
	
	
}
