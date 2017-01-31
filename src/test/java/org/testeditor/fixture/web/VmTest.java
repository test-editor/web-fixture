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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

public class VmTest {
	
	
	/**
	 * Prerequisites for execution of tests
	 */
	@Before
	public void setup() {
		Assume.assumeTrue("This is not a Windows OS - ignoring test", HelperTool.isOsWindows()); 
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
	public void firefoxPortableStartAndStopTest() throws InterruptedException, IOException {

		String pathFirefoxPortable = "c:\\dev\\tools\\firefox\\FirefoxPortable\\firefox.exe";
				
		BrowserProperties tool = new BrowserProperties();
		tool.initializeProperties();
		WebDriverFixture fixture = new WebDriverFixture();
		
		fixture.startFireFoxPortable(pathFirefoxPortable);
		fixture.waitSeconds(2);
		fixture.goToUrl(tool.getUrl());
		fixture.typeInto(tool.getUserName(), "test");
		String filename = fixture.screenshot("test");
		fixture.typeInto(tool.getPasswd(), "test");
		fixture.waitSeconds(2);
		fixture.closeBrowser();

		assertTrue("Screenshot missing", new File(filename).exists());
	}
	
}
