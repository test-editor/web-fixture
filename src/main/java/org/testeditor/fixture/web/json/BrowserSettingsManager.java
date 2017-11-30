/*******************************************************************************
 * Copyright (c) 2012 - 2017 Signal Iduna Corporation and others.
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

package org.testeditor.fixture.web.json;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.Platform;

public class BrowserSettingsManager {

    private static final String FILE_NAME = "browserSetup.json";

    /**
     * 
     * @return a List of BrowserSetupElements like :
     * 
     * <code>
     *  "os" : "LINUX",
        "browser" : "firefox",
        "options" : {
            "network.proxy.type" : 5 ,
            "network.proxy.http" : "http://mysystem.proxy.server" ,
            "network.proxy.http_port" : 100 ,
            "network.proxy.ssl" : "http://mysystem.proxy.server" ,
            "network.proxy.ssl_port" : 100 
        }

     * </code>
     */
    public static List<BrowserSetupElement> getBrowserSettings() {
        
        BrowserSetupReader reader = new BrowserSetupReader();
        List<BrowserSetupElement> elements = reader.readElements(FILE_NAME);

        Platform currentPlattform = Platform.getCurrent();
        return getBrowserSpecificSetting(elements, currentPlattform);

    }
    
    
    private static List<BrowserSetupElement> getBrowserSpecificSetting(List<BrowserSetupElement> 
        elements,Platform platform) {
        List<BrowserSetupElement> settings = new ArrayList<>();    
        settings = separateBrowserSettingsForOs(elements, platform);
        separateBrowserSettingsOsUnspecific(settings, elements);
        return settings;
    }

    private static void separateBrowserSettingsOsUnspecific(List<BrowserSetupElement> settings,
            List<BrowserSetupElement> elements) {
        elements.forEach((browserSetupElement) -> {
            String osName = browserSetupElement.getOsName();
            if (osName == null || osName.isEmpty()) {
                settings.add(browserSetupElement);
            }
        });
        
    }

    private static List<BrowserSetupElement> separateBrowserSettingsForOs(List<BrowserSetupElement> elements,
            Platform platform) {
        List<BrowserSetupElement> settings = new ArrayList<>();
        elements.forEach((browserSetupElement) -> {
            String osName = browserSetupElement.getOsName();
            if (osName != null && osName.equalsIgnoreCase(platform.name())) {
                settings.add(browserSetupElement);
            }
        });
        return settings;
    }

}
