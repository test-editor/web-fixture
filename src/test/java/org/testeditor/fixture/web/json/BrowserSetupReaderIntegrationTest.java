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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
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
        List<BrowserSetting> capabilities = firstBrowserSetupElement.getCapabilities();
        List<BrowserSetting> options = firstBrowserSetupElement.getOptions();
        String osName = firstBrowserSetupElement.getOsName();
        String osVersion = firstBrowserSetupElement.getOsVersion();
        
        // Second Element
        BrowserSetupElement secondBrowserSetupElement = browserSetupElements.get(2);
        String browserName2 = secondBrowserSetupElement.getBrowserName();
        String browserSetupName2 = secondBrowserSetupElement.getBrowserSetupName();
        String browserVersion2 = secondBrowserSetupElement.getBrowserVersion();
        List<BrowserSetting> capabilities2 = secondBrowserSetupElement.getCapabilities();
        List<BrowserSetting> options2 = secondBrowserSetupElement.getOptions();
        String osName2 = secondBrowserSetupElement.getOsName();
        String osVersion2 = secondBrowserSetupElement.getOsVersion();
        
        
        // then
        
        // First element
        Assert.assertEquals(null, browserName);
        Assert.assertEquals("proxy section", browserSetupName);
        Assert.assertEquals(null, browserVersion);
        Assert.assertEquals(1, capabilities.size());
        Assert.assertEquals("httpProxyUrl", capabilities.get(0).getKey());
        Assert.assertEquals("http://mysystem.proxy.server:100", capabilities.get(0).getValue());
        Assert.assertTrue(options.isEmpty());
        Assert.assertEquals(null, osName);
        Assert.assertEquals(null, osVersion);
        
        // Second Element
        Assert.assertEquals("chrome", browserName2);
        Assert.assertEquals("chromium 52 under window 10", browserSetupName2);
        Assert.assertEquals("52.0", browserVersion2);
        Assert.assertEquals(3, capabilities2.size());
        Assert.assertEquals("chromium-cap1", capabilities2.get(0).getKey());
        Assert.assertEquals("value1", capabilities2.get(0).getValue());
        Assert.assertEquals(80, capabilities2.get(1).getValue());
        Assert.assertEquals(false, capabilities2.get(2).getValue());
        Assert.assertTrue(options2.isEmpty());
        Assert.assertEquals("VISTA", osName2);
        Assert.assertEquals("10.0", osVersion2);
        logger.debug(" ######## End of Test integrationTest ########");
    }

}
