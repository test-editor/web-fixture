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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.testeditor.fixture.web.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class BrowserSetupReader {
    
    private static final String optionsString = "options";
    private static final String capabilitiesString = "capabilities"; 

    /**
     * Reads all available elements. An element is a {@link BrowserSetupElement} like operating system "os" with the 
     * value "WINDOWS" or "LINUX"s or browser specific "capabilities" or "options". These elements can be used for
     * setting up a browser before starting a test.
     * @param fileName of the BrowserElements
     * @return All Browser or OS specific settings for starting a browser in a test environment.  
     */
    public ArrayList<BrowserSetupElement> readElements(String fileName) {
        FileReader reader = new FileReader();
        String jsonString = reader.getFileContentAsString(fileName);
        Gson gson = new GsonBuilder().create();
        JsonObject obj = gson.fromJson(jsonString, JsonObject.class);
        ArrayList<BrowserSetupElement> allSetupElements = getAllSetupElements(obj);
        extractElements(allSetupElements);
        return allSetupElements;
    }
    
    private void extractElements(ArrayList<BrowserSetupElement> allSetupElements) {
       
        for (BrowserSetupElement browserSetupElement : allSetupElements) {
            List<BrowserSetting> capabilities = new ArrayList<BrowserSetting>();
            List<BrowserSetting> options = new ArrayList<BrowserSetting>();
            JsonObject jsonObject = browserSetupElement.getSettingsAsJsonObject();
            setBrowserSetting(browserSetupElement, options, jsonObject, optionsString);  
            setBrowserSetting(browserSetupElement, capabilities, jsonObject, capabilitiesString);  
            setOsName(browserSetupElement, jsonObject);
            setOsVersion(browserSetupElement, jsonObject);
            setBrowserName(browserSetupElement, jsonObject);
            setBrowserVersion(browserSetupElement, jsonObject);
        }
    }

    private void setBrowserSetting(BrowserSetupElement browserSetupElement, List<BrowserSetting> settings, 
            JsonObject jsonObject, String settingType) {
        JsonElement optionsAsJSonElement = jsonObject.get(settingType);
        if (optionsAsJSonElement != null) {
            Set<Map.Entry<String, JsonElement>> settingEntries = optionsAsJSonElement.getAsJsonObject()
                    .entrySet();
            for (Map.Entry<String, JsonElement> settingEntry : settingEntries) {
                BrowserSetting setting = createSettting(settingEntry);
                if (setting != null) {
                    settings.add(setting);
                    if (settingType.equals(capabilitiesString)) {
                        browserSetupElement.setCapabilities(settings);
                    } else if (settingType.equals(optionsString)) {
                        browserSetupElement.setOptions(settings);
                    }
                }
            }
        }
    }
    
    private void setBrowserVersion(BrowserSetupElement browserSetupElement, JsonObject jsonObject) {
        JsonElement browserVersion = jsonObject.get("browser-version");
        if (browserVersion != null) {
            browserSetupElement.setBrowserVersion(browserVersion.getAsString());
        }
    }

    private void setBrowserName(BrowserSetupElement browserSetupElement, JsonObject jsonObject) {
        JsonElement browser = jsonObject.get("browser");
        if (browser != null) {
            browserSetupElement.setBrowserName(browser.getAsString());
        }
    }

    private void setOsVersion(BrowserSetupElement browserSetupElement, JsonObject jsonObject) {
        JsonElement osVersion = jsonObject.get("os-version");
        if (osVersion != null) {
            browserSetupElement.setOsVersion(osVersion.getAsString());
        }
    }

    private void setOsName(BrowserSetupElement browserSetupElement, JsonObject jsonObject) {
        JsonElement os = jsonObject.get("os");
        if (os != null) {
            browserSetupElement.setOsName(os.getAsString());
        }
    }

    private BrowserSetting createSettting(Entry<String, JsonElement> browserSettingEntry) {
        BrowserSetting browserSetting = null;
        String key = browserSettingEntry.getKey();
        JsonElement value = browserSettingEntry.getValue();
        JsonPrimitive browserSettingAsJsonPrimitive = value.getAsJsonPrimitive();
        if (browserSettingAsJsonPrimitive.isString()) {
            browserSetting = new BrowserSetting(key, value.getAsString());
        } else if (browserSettingAsJsonPrimitive.isBoolean()) {
            browserSetting = new BrowserSetting(key, value.getAsBoolean());
        } else if (browserSettingAsJsonPrimitive.isNumber()) {
            browserSetting = new BrowserSetting(key, value.getAsInt());
        }
        return browserSetting;
    }
    
    private ArrayList<BrowserSetupElement> getAllSetupElements(JsonObject jsonObject) {
        BrowserSetupElement browserSetupElement;
        ArrayList<BrowserSetupElement> browserSetupElements = new ArrayList<BrowserSetupElement>();
        ArrayList<JsonObject> allJsonObjects = new ArrayList<JsonObject>();
        Set<Map.Entry<String, JsonElement>> setupEntries = jsonObject.entrySet();
        for (Map.Entry<String, JsonElement> setupEntry : setupEntries) {
            browserSetupElement = new BrowserSetupElement(); 
            browserSetupElement.setBrowserSetupName(setupEntry.getKey());
            JsonElement value = setupEntry.getValue();
            allJsonObjects.add(value.getAsJsonObject());
            browserSetupElement.setSettingsAsJsonObject(value.getAsJsonObject());
            browserSetupElements.add(browserSetupElement);
        }
        return browserSetupElements;
    }

}

