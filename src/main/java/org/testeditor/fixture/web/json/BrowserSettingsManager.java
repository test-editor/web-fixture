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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return checkedElementsForDuplicate(elements,currentPlattform);
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
        return checkedElementsForDuplicate(elements,currentPlattform);
    }
    
    protected static List<BrowserSetupElement> checkedElementsForDuplicate(List<BrowserSetupElement> elements,
            Platform platform) {
        List<BrowserSetupElement> separatedBrowserSettingsForOs = separateBrowserSettingsForOs(elements, platform);
        List<BrowserSetupElement> separateBrowserSettingsOsUnspecific = separateBrowserSettingsOsUnspecific(elements);
        Map<String,Object> capabilitiesOsUnspecific = separateCapabilities(separateBrowserSettingsOsUnspecific); 
        separateCapabilities(capabilitiesOsUnspecific,separatedBrowserSettingsForOs); 
        Map<String,Object> optionsOsUnspecific = separateOptions(separateBrowserSettingsOsUnspecific);  
        separateOptions(optionsOsUnspecific,separatedBrowserSettingsForOs); 
        separatedBrowserSettingsForOs.addAll(separateBrowserSettingsOsUnspecific);
        return separatedBrowserSettingsForOs;
    }
    
    // just not deleted for your opinion. One method instead of 2 for options and capabilities
    //    protected static Map<String,Object> separateBrowserSettings(List<BrowserSetupElement> 
    //        separateBrowserSettingsOsUnspecific, String browserSettingType) {
    //        Map<String,Object> browserSettings = new HashMap();
    //        separateBrowserSettingsOsUnspecific.forEach((bsetting) -> { 
    //            if (browserSettingType.equals(OPTIONS)) {
    //                bsetting.getOptions().forEach((option) ->  { 
    //                    browserSettings.put(option.getKey(), option.getValue());
    //                } 
    //                );
    //            
    //            } else if (browserSettingType.equals(CAPABILITIES)) {
    //                bsetting.getCapabilities().forEach((capability) ->  { 
    //                    browserSettings.put(capability.getKey(), capability.getValue());
    //                    } 
    //                );
    //            }  
    //        
    //        }   
    //        ); 
    //        return browserSettings;
    //    }
    
    protected static Map<String,Object> separateOptions(List<BrowserSetupElement> 
        separateBrowserSettingsOsUnspecific) {
        Map<String,Object> browserSettings = new HashMap();
        separateBrowserSettingsOsUnspecific.forEach((bsetting) -> { 
            bsetting.getOptions().forEach((option) ->  { 
                browserSettings.put(option.getKey(), option.getValue());
            } 
            );
        });
        return browserSettings;
    }
    
    protected static Map<String,Object> separateOptions(Map<String,Object> osUnspecificSettings,
            List<BrowserSetupElement> separateBrowserSettingsOsspecific) {
        Map<String,Object> browserSettings = new HashMap();
        separateBrowserSettingsOsspecific.forEach((bsetting) -> { 
                bsetting.getOptions().forEach((option) ->  {
                    String key = option.getKey();
                    if (osUnspecificSettings.containsKey(key)) {
                        throw new RuntimeException("Duplicate entries existing in configuration file " 
                            + FILE_NAME + ". Entries are: [" + key + " - " + option.getValue() + ", " 
                            + key + " - " + osUnspecificSettings.get(key) + "]");
                    } else {
                        browserSettings.put(option.getKey(), option.getValue());
                    }
                } 
                );
        }   
        ); 
        return browserSettings;
    }
    
    protected static Map<String,Object> separateCapabilities(List<BrowserSetupElement> 
        separateBrowserSettingsOsUnspecific) {
        Map<String,Object> browserSettings = new HashMap();
        separateBrowserSettingsOsUnspecific.forEach((bsetting) -> { 
            bsetting.getCapabilities().forEach((capability) ->  { 
                browserSettings.put(capability.getKey(), capability.getValue());
            } 
            );
        });
        return browserSettings;
    }
    
    // just not deleted for your opinion. One method instead of 2 for options and capabilities    
    //    protected static Map<String,Object> separateBrowserSettings(Map<String,Object> osUnspecificSettings,
    //            List<BrowserSetupElement> separateBrowserSettingsOsspecific, String browserSettingType) {
    //        Map<String,Object> browserSettings = new HashMap();
    //        separateBrowserSettingsOsspecific.forEach((bsetting) -> { 
    //            if (browserSettingType.equals(OPTIONS)) {
    //                bsetting.getOptions().forEach((option) ->  {
    //                    String key = option.getKey();
    //                    if (osUnspecificSettings.containsKey(key)) {
    //                        throw new RuntimeException("Duplicate entries existing in configuration file " 
    //                            + FILE_NAME + ". Entries are: [" + key + " - " + option.getValue() + ", " 
    //                            + key + " - " + osUnspecificSettings.get(key) + "]");
    //                    } else {
    //                        browserSettings.put(option.getKey(), option.getValue());
    //                    }
    //                } 
    //                );
    //        
    //            } else if (browserSettingType.equals(CAPABILITIES)) {
    //                bsetting.getCapabilities().forEach((capability) ->  { 
    //                    String key = capability.getKey();
    //                    if (osUnspecificSettings.containsKey(key)) {
    //                        throw new RuntimeException("Duplicate entries existing in configuration file " 
    //                            + FILE_NAME + ". Entries are: [" + key + " - " + capability.getValue() + ", " 
    //                            + key + " - " + osUnspecificSettings.get(key) + "]");
    //                    } else {
    //                        browserSettings.put(capability.getKey(), capability.getValue());
    //                    }
    //                } 
    //                );
    //            }  
    //    
    //        }   
    //        ); 
    //        return browserSettings;
    //    }
    
    protected static Map<String,Object> separateCapabilities(Map<String,Object> osUnspecificSettings,
            List<BrowserSetupElement> separateBrowserSettingsOsspecific) {
        Map<String,Object> browserSettings = new HashMap();
        separateBrowserSettingsOsspecific.forEach((bsetting) -> { 
                bsetting.getCapabilities().forEach((capability) ->  { 
                    String key = capability.getKey();
                    if (osUnspecificSettings.containsKey(key)) {
                        throw new RuntimeException("Duplicate entries existing in configuration file " 
                            + FILE_NAME + ". Entries are: [" + key + " - " + capability.getValue() + ", " 
                            + key + " - " + osUnspecificSettings.get(key) + "]");
                    } else {
                        browserSettings.put(capability.getKey(), capability.getValue());
                    }
                } 
                );
        }   
        ); 
        return browserSettings;
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
