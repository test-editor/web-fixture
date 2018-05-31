/*******************************************************************************
 * Copyright (c) 2012 - 2018 Signal Iduna Corporation and others.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.Platform;
import org.testeditor.fixture.core.FixtureException;
import org.testeditor.fixture.web.WebDriverFixture;

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
    public List<BrowserSetupElement> getBrowserSettings() throws FixtureException {
        BrowserSetupReader reader = new BrowserSetupReader();
        List<BrowserSetupElement> elements = reader.readElements(FILE_NAME);
        Platform currentPlattform = getCurrentPlatform();
        return getBrowserElementsWithoutDuplicateEntries(elements, currentPlattform);
    }

    /**
     * This method is for testing purposes.
     * 
     * @param fileName of JSON test file for browserSetup.json.
     * @return A List of BrowserSetupElement
     */
    protected List<BrowserSetupElement> getBrowserSettings(String fileName) throws FixtureException {
        BrowserSetupReader reader = new BrowserSetupReader();
        List<BrowserSetupElement> elements = reader.readElements(fileName);
        Platform currentPlattform = getCurrentPlatform();
        return getBrowserElementsWithoutDuplicateEntries(elements, currentPlattform);
    }

    protected Platform getCurrentPlatform() {
        return Platform.getCurrent();
    }

    protected List<BrowserSetupElement> getBrowserElementsWithoutDuplicateEntries(List<BrowserSetupElement> elements,
            Platform platform) throws FixtureException {
        List<BrowserSetupElement> browserSetupElementsOsSpecific = getBrowserSetupElementsOsSpecific(elements,
                platform);
        List<BrowserSetupElement> browserSetupElementOsUnspecific = getBrowserSetupElementsOsUnspecific(elements);
        Map<String, Object> capabilities = copyCapabilitiesIntoMap(new HashMap<>(), browserSetupElementOsUnspecific);
        copyCapabilitiesIntoMap(capabilities, browserSetupElementsOsSpecific);
        Map<String, Object> options = copyOptionsIntoMap(new HashMap<>(), browserSetupElementOsUnspecific);
        copyOptionsIntoMap(options, browserSetupElementsOsSpecific);
        browserSetupElementsOsSpecific.addAll(browserSetupElementOsUnspecific);
        return browserSetupElementsOsSpecific;
    }

    protected Map<String, Object> copyOptionsIntoMap(Map<String, Object> browserSettingsMap,
            List<BrowserSetupElement> browserSetupElements) throws FixtureException {
        for (BrowserSetupElement bsetting : browserSetupElements) {
            for (BrowserSetting option : bsetting.getOptions()) {
                String key = option.getKey();
                if (browserSettingsMap.containsKey(key)) {
                    throw new FixtureException("Duplicate entries existing in configuration file",
                            WebDriverFixture.keyValues("file", FILE_NAME, //
                                    "offendingKey", key, //
                                    "valueOption", option.getValue(), //
                                    "valueBrowserSettingsMap", browserSettingsMap.get(key)));
                } else {
                    browserSettingsMap.put(key, option.getValue());
                }
            }
        }
        return browserSettingsMap;
    }

    protected Map<String, Object> copyCapabilitiesIntoMap(Map<String, Object> browserSettingsMap,
            List<BrowserSetupElement> separateBrowserSettingsOsspecific) throws FixtureException {
        for (BrowserSetupElement bsetting : separateBrowserSettingsOsspecific) {
            for (BrowserSetting capability : bsetting.getCapabilities()) {
                String key = capability.getKey();
                if (browserSettingsMap.containsKey(key)) {
                    throw new FixtureException("Duplicate entries existing in configuration file",
                            WebDriverFixture.keyValues("file", FILE_NAME, //
                                    "offendingKey", key, //
                                    "valueCapability", capability.getValue(), //
                                    "valueBrowserSettingsMap", browserSettingsMap.get(key)));
                } else {
                    browserSettingsMap.put(key, capability.getValue());
                }
            }
        }
        return browserSettingsMap;
    }
    
    protected List<BrowserSetupElement> getBrowserSetupElementsOsUnspecific(List<BrowserSetupElement> elements) {
        List<BrowserSetupElement> settings = new ArrayList<>();
        elements.forEach((browserSetupElement) -> {
            String osName = browserSetupElement.getOsName();
            if (osName == null || osName.isEmpty()) {
                settings.add(browserSetupElement);
            }
        });
        return settings;
    }

    protected List<BrowserSetupElement> getBrowserSetupElementsOsSpecific(List<BrowserSetupElement> elements,
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
