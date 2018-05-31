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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.testeditor.fixture.core.FixtureException;
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
     * @throws FixtureException
     */
    public List<BrowserSetupElement> readElements(String fileName) throws FixtureException {
        FileReader reader = new FileReader();
        List<BrowserSetupElement> allSetupElements = new ArrayList<>();
        String jsonString = reader.getFileContentAsString(fileName);
        if (jsonString != null && !jsonString.isEmpty()) {
            Gson gson = new GsonBuilder().create();
            JsonObject obj = gson.fromJson(jsonString, JsonObject.class);
            allSetupElements = getAllSetupElements(obj);
        }
        return allSetupElements;
        
    }

    private void completeElementSettings(BrowserSetupElement browserSetupElement ,
            JsonObject jsonObject) {
        List<BrowserSetting> capabilities = new ArrayList<BrowserSetting>();
        List<BrowserSetting> options = new ArrayList<BrowserSetting>();
        setBrowserSetting(browserSetupElement, options, jsonObject.get(optionsString));
        browserSetupElement.setOptions(options);
        setBrowserSetting(browserSetupElement, capabilities, jsonObject.get(capabilitiesString));
        browserSetupElement.setCapabilities(capabilities);
        setOsNameIfPresent(browserSetupElement, jsonObject);
        setOsVersionIfPresent(browserSetupElement, jsonObject);
        setBrowserNameIfPresent(browserSetupElement, jsonObject);
        setBrowserVersionIfPresent(browserSetupElement, jsonObject);
    }


    private void setBrowserSetting(BrowserSetupElement browserSetupElement, List<BrowserSetting> settings,
            JsonElement optionsAsJSonElement) {
        if (optionsAsJSonElement != null) {
            Set<Map.Entry<String, JsonElement>> settingEntries = optionsAsJSonElement.getAsJsonObject()
                    .entrySet();
            for (Map.Entry<String, JsonElement> settingEntry : settingEntries) {
                BrowserSetting setting = createBrowserSettting(settingEntry);
                if (setting != null) {
                    settings.add(setting);
                }
            }
        }
    }
    
    private void setBrowserVersionIfPresent(BrowserSetupElement browserSetupElement, JsonObject jsonObject) {
        JsonElement browserVersion = jsonObject.get("browser-version");
        if (browserVersion != null) {
            browserSetupElement.setBrowserVersion(browserVersion.getAsString());
        }
    }

    private void setBrowserNameIfPresent(BrowserSetupElement browserSetupElement, JsonObject jsonObject) {
        JsonElement browser = jsonObject.get("browser");
        if (browser != null) {
            browserSetupElement.setBrowserName(browser.getAsString());
        }
    }

    private void setOsVersionIfPresent(BrowserSetupElement browserSetupElement, JsonObject jsonObject) {
        JsonElement osVersion = jsonObject.get("os-version");
        if (osVersion != null) {
            browserSetupElement.setOsVersion(osVersion.getAsString());
        }
    }

    private void setOsNameIfPresent(BrowserSetupElement browserSetupElement, JsonObject jsonObject) {
        JsonElement os = jsonObject.get("os");
        if (os != null) {
            browserSetupElement.setOsName(os.getAsString());
        }
    }

    private BrowserSetting createBrowserSettting(Entry<String, JsonElement> browserSettingEntry) {
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
    
    private List<BrowserSetupElement> getAllSetupElements(JsonObject jsonObject) {
        BrowserSetupElement browserSetupElement;
        List<BrowserSetupElement> browserSetupElements = new ArrayList<BrowserSetupElement>();
        Set<Map.Entry<String, JsonElement>> setupEntries = jsonObject.entrySet();
        for (Map.Entry<String, JsonElement> setupEntry : setupEntries) {
            browserSetupElement = new BrowserSetupElement();
            browserSetupElement.setBrowserSetupName(setupEntry.getKey());
            JsonElement value = setupEntry.getValue();
            completeElementSettings(browserSetupElement, value.getAsJsonObject());
            browserSetupElements.add(browserSetupElement);
        }
        return browserSetupElements;
    }

}
