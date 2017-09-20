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
 * The HelperTool is just a container for convenience methods to proof
 * prerequisites for different testcases.
 */
public class HelperTool {

    private static String userHome = System.getProperty("user.home");
    private static String osWindows = "win64";
    private static String osLinux = "linux64";
    private static String osMac = "macos";
    private static String geckodriverVersion = "0.19.0";
    private static String testPropertyPath = "/.m2/repository/webdriver/geckodriver";

    // This is the standard path where
    private static final String pathGeckodriver = userHome + testPropertyPath;

    /**
     * @return true if the path to geckodriver is available, false otherwise.
     */
    public static boolean isGeckoDriverPresent() {
        StringBuilder builder = new StringBuilder(pathGeckodriver);
        if (isOsWindows()) {
            builder.append("/").append(osWindows).append("/").append(geckodriverVersion).append("/").append("geckodriver.exe");
        } else if (isOsLinux()) {
            builder.append("/").append(osLinux).append("/").append(geckodriverVersion).append("/").append("geckodriver");
        } else if (isOsMac()) {
            builder.append("/").append(osMac).append("/").append(geckodriverVersion).append("/").append("geckodriver");
        }
        return new File(builder.toString()).exists();
    }

    /**
     * @return true if OS = WINDOWS, false otherwise
     */
    public static boolean isOsWindows() {
        return SystemUtils.IS_OS_WINDOWS;
    }

    /**
     * @return true if OS = WINDOWS, false otherwise
     */
    public static boolean isOsLinux() {
        return SystemUtils.IS_OS_LINUX;
    }

    /**
     * @return true if OS = WINDOWS, false otherwise
     */
    public static boolean isOsMac() {
        return SystemUtils.IS_OS_MAC;
    }

    /**
     * @return String reperesenting the path to geckodriver.exe
     */
    public static String getPathgeckodriver() {
        return pathGeckodriver;
    }

}
