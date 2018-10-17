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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testeditor.fixture.core.FixtureException;


public class BrowserSetupReaderIntegrationTest {
    
    private static final Logger logger = LoggerFactory.getLogger(BrowserSetupReaderIntegrationTest.class);

    @Test
    public void integrationTest() throws FixtureException {
        
        // given
        BrowserSetupReader browserSetupReader = new BrowserSetupReader();
        
        // when
        List<BrowserSetupElement> browserSetupElements = browserSetupReader.readElements("browserSetupTest.json");
        
        // First element
        BrowserSetupElement firstBrowserSetupElement = browserSetupElements.get(0);
        String browserName = firstBrowserSetupElement.getBrowserName();
        String browserSetupName = firstBrowserSetupElement.getBrowserSetupName();
        String browserVersion = firstBrowserSetupElement.getBrowserVersion();
        final List<BrowserSetting> capabilities = firstBrowserSetupElement.getCapabilities();
        final List<BrowserSetting> options = firstBrowserSetupElement.getOptions();
        final String osName = firstBrowserSetupElement.getOsName();
        final String osVersion = firstBrowserSetupElement.getOsVersion();
        
        // Second Element
        BrowserSetupElement secondBrowserSetupElement = browserSetupElements.get(2);
        final String browserName2 = secondBrowserSetupElement.getBrowserName();
        final String browserSetupName2 = secondBrowserSetupElement.getBrowserSetupName();
        final String browserVersion2 = secondBrowserSetupElement.getBrowserVersion();
        final List<BrowserSetting> capabilities2 = secondBrowserSetupElement.getCapabilities();
        final List<BrowserSetting> options2 = secondBrowserSetupElement.getOptions();
        final String osName2 = secondBrowserSetupElement.getOsName();
        final String osVersion2 = secondBrowserSetupElement.getOsVersion();
        
        
        // then
        
        // First element
        assertEquals(null, browserName);
        assertEquals("proxy section", browserSetupName);
        assertEquals(null, browserVersion);
        assertEquals(1, capabilities.size());
        assertEquals("httpProxyUrl", capabilities.get(0).getKey());
        assertEquals("http://mysystem.proxy.server:100", capabilities.get(0).getValue());
        assertTrue(options.isEmpty());
        assertEquals(null, osName);
        assertEquals(null, osVersion);
        
        // Second Element
        assertEquals("chrome", browserName2);
        assertEquals("chromium 52 under window 10", browserSetupName2);
        assertEquals("52.0", browserVersion2);
        assertEquals(3, capabilities2.size());
        assertEquals("chromium-cap1", capabilities2.get(0).getKey());
        assertEquals("value1", capabilities2.get(0).getValue());
        assertEquals(80, capabilities2.get(1).getValue());
        assertEquals(false, capabilities2.get(2).getValue());
        assertTrue(options2.isEmpty());
        assertEquals("VISTA", osName2);
        assertEquals("10.0", osVersion2);
        logger.debug(" ######## End of Test integrationTest ########");
    }

}
