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

public class VmTest {
	
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
	public void firefoxPortableStartAndStopTest() throws InterruptedException {

		String pathFirefoxPortable = "c:\\dev\\tools\\firefox\\FirefoxPortable\\firefox.exe";
				
		HelperTool tool = new HelperTool();
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
