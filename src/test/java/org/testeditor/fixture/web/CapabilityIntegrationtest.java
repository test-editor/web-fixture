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

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.RemoteWebDriver;

public class CapabilityIntegrationtest {
    
    private static String proxyHttpExpected = "http://mysystem.proxy.server";
    private static String proxySslExpected = "http://mysystem_ssl.proxy.server";
    private static int proxyTypeExpected = 5;
    private static int proxySslPortExpected = 108;
    private static String proxyHttp = "";
    private static String proxySsl  = "";
    private static int proxySslPort = 0;
    private static int proxyType = 0;
    
    @Test
    public void readCapabilitySuccesful() throws Exception {
        
        // given
        WebDriverFixture fixture = new WebDriverFixture();
        
        // when
        fixture.startBrowser("firefox");

        // then
        readCapabilitiesFromProfile(fixture);
        Assert.assertEquals(proxyHttpExpected, proxyHttp);
        Assert.assertEquals(proxySslExpected, proxySsl);
        Assert.assertEquals(proxySslPortExpected, proxySslPort);
        Assert.assertEquals(proxyTypeExpected, proxyType);
        
    }
   
    public void readCapabilitiesFromProfile(WebDriverFixture fixture) {
        WebDriver driver = fixture.getDriver();
        Capabilities cap = ((RemoteWebDriver) driver).getCapabilities();
        String profilePath = (String) cap.getCapability("moz:profile");
        FirefoxProfile profile = new FirefoxProfile(new File(profilePath));
        proxyHttp = profile.getStringPreference("network.proxy.http", "");
        proxySsl = profile.getStringPreference("network.proxy.ssl", "");
        proxySslPort = profile.getIntegerPreference("network.proxy.ssl_port", 0);
        proxyType = profile.getIntegerPreference("network.proxy.type", 0);
    }

}


