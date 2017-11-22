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

import org.junit.Assert;
import org.junit.Test;

public class BrowserSetupReaderTest {

    @Test
    public void testAllBrowserElements() {
        BrowserSetupReader browserSetupReader = new BrowserSetupReader();
        ArrayList<BrowserSetupElement> browserSetupElements = browserSetupReader.readElements("browserSetup.json");
        
        String expected = "[BrowserSetupElement "
                + "[browserSetupElementName=proxy section, osName=null, osVersion=null, "
                + "browserName=null, browserVersion=null, "
                + "capabilitiesAsJsonObject={\"capabilities\":{\"httpProxyUrl\":\"http://mysystem.proxy.server:100\"}}, "
                + "capabilities=[httpProxyUrl - http://mysystem.proxy.server:100], options=null], "
                + "BrowserSetupElement "
                + "[browserSetupElementName=windows-section, osName=WINDOWS, osVersion=null, "
                + "browserName=null, browserVersion=null, "
                + "capabilitiesAsJsonObject={\"os\":\"WINDOWS\","
                + "\"options\":{\"windows-option\":\"value1\",\"windows-option2\":80,\"windows-option3\":true},"
                + "\"capabilities\":{}}, "
                + "capabilities=null, "
                + "options=[windows-option - value1, windows-option2 - 80, windows-option3 - true]], "
                + "BrowserSetupElement "
                + "[browserSetupElementName=chromium 52 under window 10, osName=WINDOWS, osVersion=10.0, "
                + "browserName=chrome, browserVersion=52.0, "
                + "capabilitiesAsJsonObject={\"os\":\"WINDOWS\",\"os-version\":\"10.0\","
                + "\"browser\":\"chrome\",\"browser-version\":\"52.0\","
                + "\"capabilities\":{\"chromium-cap1\":\"value1\",\"chromium-cap2\":80,\"chromium-cap3\":false}}, "
                + "capabilities=[chromium-cap1 - value1, chromium-cap2 - 80, chromium-cap3 - false], options=null], "
                + "BrowserSetupElement "
                + "[browserSetupElementName=general firefox section, osName=null, osVersion=null, "
                + "browserName=firefox, browserVersion=null, "
                + "capabilitiesAsJsonObject={\"browser\":\"firefox\","
                + "\"capabilities\":{\"firefox-cap1\":\"value1\",\"firefox-cap2\":80,\"firefox-cap3\":true}}, "
                + "capabilities=[firefox-cap1 - value1, firefox-cap2 - 80, firefox-cap3 - true], options=null]]";
        
        String actual = browserSetupElements.toString();
        Assert.assertEquals(expected, actual);
    }

}
