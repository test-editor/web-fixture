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

    private static final String CAPABILITIES = "capabilities";
    private static final String OPTIONS = "options";
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
        checkRedundantElements(elements,currentPlattform);
        return getBrowserSpecificSetting(elements, currentPlattform);
    }
    
    /**
     * This method is for testing purposes.
     * @param fileName 
     *          of JSON test file for browserSetup.json.
     * @return 
     *          A List of BrowserSetupElement
     */
    protected static List<BrowserSetupElement> getBrowserSettings(String fileName) {
        
        BrowserSetupReader reader = new BrowserSetupReader();
        List<BrowserSetupElement> elements = reader.readElements(fileName);
        
        Platform currentPlattform = Platform.getCurrent();
        checkRedundantElements(elements,currentPlattform);
        return getBrowserSpecificSetting(elements, currentPlattform);
    }
    
    protected static void checkRedundantElements(List<BrowserSetupElement> elements, Platform platform) {
        List<BrowserSetting> duplicateSettings = new ArrayList<>();
        
        List<BrowserSetupElement> separatedBrowserSettingsForOs = separateBrowserSettingsForOs(elements, platform);
        List<BrowserSetupElement> separateBrowserSettingsOsUnspecific = separateBrowserSettingsOsUnspecific(elements);
        List<BrowserSetting> capabilitiesOsUnspecific = getBrowserSettingList(separateBrowserSettingsOsUnspecific,
            CAPABILITIES); 
        List<BrowserSetting> capabilitiesOsSpecific = getBrowserSettingList(separatedBrowserSettingsForOs,
            CAPABILITIES); 
        List<BrowserSetting> optionsOsUnspecific = getBrowserSettingList(separateBrowserSettingsOsUnspecific, 
            OPTIONS);  
        List<BrowserSetting> optionsOsSpecific = getBrowserSettingList(separatedBrowserSettingsForOs, 
            OPTIONS);  
        
        // Look for duplicate capability entries and exit test when found. 
        determineDuplicateEntries(duplicateSettings, capabilitiesOsUnspecific, capabilitiesOsSpecific);
        // Look for duplicate option entries and exit test when found.
        determineDuplicateEntries(duplicateSettings, optionsOsUnspecific, optionsOsSpecific);
        
        if (duplicateSettings.size() > 0) {
            throw new RuntimeException("Duplicate entries existing in configuration file " 
                + FILE_NAME + ". Entries are: " + duplicateSettings);
        }
    }
    
    protected static void determineDuplicateEntries(List<BrowserSetting> duplicateSettings, 
            List<BrowserSetting> settingOsUnspecific, List<BrowserSetting> settingOsSpecific) {
        for (BrowserSetting browserSetting : settingOsUnspecific) {
            String capabilityName = browserSetting.getKey();
            for (BrowserSetting browserSettingDuplicate : settingOsSpecific) {
                String duplicateCapabilityName = browserSettingDuplicate.getKey();
                if (capabilityName.equalsIgnoreCase(duplicateCapabilityName)) {
                    duplicateSettings.add(browserSetting);
                    duplicateSettings.add(browserSettingDuplicate);
                } 
            }
        }
    }
    
    protected static List<BrowserSetting> getBrowserSettingList(List<BrowserSetupElement> 
        separateBrowserSettingsOsUnspecific, String browserSettingType) {
        List<BrowserSetting> browserSettingList = new ArrayList<>();
        separateBrowserSettingsOsUnspecific.forEach((bsetting) -> { 
            if (browserSettingType.equals(OPTIONS)) {
                bsetting.getOptions().forEach((option) ->  { 
                    BrowserSetting setting = new BrowserSetting(option.getKey(), option.getValue());
                    browserSettingList.add(setting);
                } 
                );
            
            } else if (browserSettingType.equals(CAPABILITIES)) {
                bsetting.getCapabilities().forEach((capability) ->  { 
                    BrowserSetting setting = new BrowserSetting(capability.getKey(), capability.getValue());
                    browserSettingList.add(setting);
                    } 
                );
            }  
        
        }   
        ); 
        return browserSettingList;
    }
    
    protected static List<BrowserSetupElement> getBrowserSpecificSetting(List<BrowserSetupElement> 
        elements,Platform platform) {
        List<BrowserSetupElement> settings = new ArrayList<>();    
        settings = separateBrowserSettingsForOs(elements, platform);
        settings.addAll(separateBrowserSettingsOsUnspecific(elements));
        return settings;
    }

    protected static List<BrowserSetupElement> separateBrowserSettingsOsUnspecific(List<BrowserSetupElement> elements) {
        List<BrowserSetupElement> settings = new ArrayList<>();
        elements.forEach((browserSetupElement) -> {
            String osName = browserSetupElement.getOsName();
            if (osName == null || osName.isEmpty()) {
                settings.add(browserSetupElement);
            }
        });
        return settings;
    }

    protected static List<BrowserSetupElement> separateBrowserSettingsForOs(List<BrowserSetupElement> elements,
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
