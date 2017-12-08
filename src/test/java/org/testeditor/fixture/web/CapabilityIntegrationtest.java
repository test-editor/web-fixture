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


package org.testeditor.fixture.web;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CapabilityIntegrationtest {
    
    private static final Logger logger = LoggerFactory.getLogger(CapabilityIntegrationtest.class);
    
    private static String proxyHttpExpected = "http://mysystem.proxy.server";
    private static String proxySslExpected = "http://mysystem_ssl.proxy.server";
    private static int proxyTypeExpected = 5;
    private static int proxySslPortExpected = 108;
    private static int proxyhttpPortExpected = 101;
    private static String proxyHttp = "";
    private static String proxySsl  = "";
    private static int proxyHttpPort = 0;
    private static int proxySslPort = 0;
    private static int proxyType = 0;
    
    @Test
    public void readCapabilitySuccesful() throws Exception {
        
        // given
        WebDriverFixture fixture = new WebDriverFixture();
        
        // when
        fixture.startBrowser("firefox");
        logger.debug("Firefox started succesfully");

        // then
        readCapabilitiesFromProfile(fixture);
        assertEquals(proxyHttpExpected, proxyHttp);
        assertEquals(proxyhttpPortExpected, proxyHttpPort);
        assertEquals(proxySslExpected, proxySsl);
        assertEquals(proxySslPortExpected, proxySslPort);
        assertEquals(proxyTypeExpected, proxyType);
        logger.debug(" ######## End of Integrationtest ########");
        
    }
   
    private void readCapabilitiesFromProfile(WebDriverFixture fixture) {
        WebDriver driver = fixture.getDriver();
        Capabilities cap = ((RemoteWebDriver) driver).getCapabilities();
        String profilePath = (String) cap.getCapability("moz:profile");
        logger.debug("Firefox Preferences read succesfully");
        FirefoxProfile profile = new FirefoxProfile(new File(profilePath));
        proxyHttp = profile.getStringPreference("network.proxy.http", "");
        logger.debug("Firefox Preference proxy HTTP = {} read succesfully" , proxyHttp);
        proxyHttpPort = profile.getIntegerPreference("network.proxy.http_port", 0);
        logger.debug("Firefox Preference proxy HTTP port = {} read succesfully" , proxyHttpPort);
        proxySsl = profile.getStringPreference("network.proxy.ssl", "");
        logger.debug("Firefox Preference proxy SSL = {}  read succesfully", proxySsl);
        proxySslPort = profile.getIntegerPreference("network.proxy.ssl_port", 0);
        logger.debug("Firefox Preference proxy SSL port = {}  read succesfully", proxySslPort);
        proxyType = profile.getIntegerPreference("network.proxy.type", 0);
        logger.debug("Firefox Preference proxy type = {}  read succesfully", proxyType);
    }

}
