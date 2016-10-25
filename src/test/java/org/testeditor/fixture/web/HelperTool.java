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

import java.io.File;

import org.apache.commons.lang3.SystemUtils;

/**
 * The HelperTool is just a container for convenience methods to proof prerequisites for 
 * different testcases.
 */
public class HelperTool {
	
   private static final String pathGeckodriver = "c:\\dev\\tools\\firefox\\geckodriver.exe";

	/**
	 * @return true if the path to geckodriver is available, false otherwise.
	 */
   public static boolean isGeckoDriverPresent() {
		return new File(pathGeckodriver).exists();
	}

	/**
	 * @return true if OS = WINDOWS, false otherwise
	 */
	public static boolean isOsWindows() {
		return SystemUtils.IS_OS_WINDOWS;
	}

	/**
	 * @return String reperesenting the path to geckodriver.exe 
	 */
	public static String getPathgeckodriver() {
		return pathGeckodriver;
	}

}
