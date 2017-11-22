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
            List<Capability> capabilities = new ArrayList<Capability>();
            List<Option> options = new ArrayList<Option>();
            JsonObject jsonObject = browserSetupElement.getCapabilitiesAsJsonObject();
            setOptions(browserSetupElement, options, jsonObject);  
            setCapabilities(browserSetupElement, capabilities, jsonObject);  
            setOsName(browserSetupElement, jsonObject);
            setOsVersion(browserSetupElement, jsonObject);
            setBrowserName(browserSetupElement, jsonObject);
            setBrowserVersion(browserSetupElement, jsonObject);
        }
    }

    private void setCapabilities(BrowserSetupElement browserSetupElement, List<Capability> capabilities,
            JsonObject jsonObject) {
        JsonElement capabilitiesAsJSonElement = jsonObject.get("capabilities");
        if (capabilitiesAsJSonElement != null) {
            Set<Map.Entry<String, JsonElement>> capabilityEntries = capabilitiesAsJSonElement.getAsJsonObject()
                    .entrySet();
            for (Map.Entry<String, JsonElement> capabilityEntry : capabilityEntries) {
                Capability capabilitiy = createCapability(capabilityEntry);
                
                capabilities.add(capabilitiy);
                browserSetupElement.setCapabilities(capabilities);
            }
        }
    }

    private void setOptions(BrowserSetupElement browserSetupElement, List<Option> options, JsonObject jsonObject) {
        JsonElement optionsAsJSonElement = jsonObject.get("options");
        if (optionsAsJSonElement != null) {
            Set<Map.Entry<String, JsonElement>> optionEntries = optionsAsJSonElement.getAsJsonObject()
                    .entrySet();
            for (Map.Entry<String, JsonElement> optionEntry : optionEntries) {
                
                Option option = createOption(optionEntry);
                if (option != null) {
                    options.add(option);
                    browserSetupElement.setOptions(options);
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

    private Option createOption(Entry<String, JsonElement> optionEntry) {
        Option option = null;
        String key = optionEntry.getKey();
        JsonElement value = optionEntry.getValue();
        JsonPrimitive optionAsJsonPrimitive = value.getAsJsonPrimitive();
        if (optionAsJsonPrimitive.isString()) {
            option = new Option(key, value.getAsString(), String.class);
            
        } else if (optionAsJsonPrimitive.isBoolean()) {
            option = new Option(key, value.getAsBoolean(), Boolean.TYPE);
            
        } else if (optionAsJsonPrimitive.isNumber()) {
            option = new Option(key, value.getAsInt(), Integer.TYPE);
        }
        return option;
    }

    private Capability createCapability(Entry<String, JsonElement> capabilityEntry) {
        Capability capability = null;
        String key = capabilityEntry.getKey();
        JsonElement value = capabilityEntry.getValue();
        JsonPrimitive capabilityAsJsonPrimitive = value.getAsJsonPrimitive();
        if (capabilityAsJsonPrimitive.isString()) {
            capability = new Capability(key, value.getAsString(), String.class);
        } else if (capabilityAsJsonPrimitive.isBoolean()) {
            capability = new Capability(key, value.getAsBoolean(), Boolean.TYPE);
            
        } else if (capabilityAsJsonPrimitive.isNumber()) {
            capability = new Capability(key, value.getAsInt(), Integer.TYPE);
        }
        return capability;
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
            browserSetupElement.setCapabilitiesAsJsonObject(value.getAsJsonObject());
            browserSetupElements.add(browserSetupElement);
        }
        return browserSetupElements;
    }

}

