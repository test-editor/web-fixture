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


package org.testeditor.fixture.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testeditor.fixture.core.FixtureException;
import org.testeditor.fixture.web.io.FileReader;
import org.testeditor.fixture.web.json.BrowserSetting;
import org.testeditor.fixture.web.json.BrowserSetupElement;
import org.testeditor.fixture.web.json.BrowserSetupReader;

public class CapabilityIntegrationtest {
    
    private static final String BROWSER_SETUP_JSON = "browserSetup.json";

    private static final Logger logger = LoggerFactory.getLogger(CapabilityIntegrationtest.class);
    
    private static String proxyHttpExpected ;
    private static String proxySslExpected ;
    private static int proxyTypeExpected ;
    private static int proxySslPortExpected ;
    private static int proxyhttpPortExpected ;
    private static String proxyHttp = null;
    private static String proxySsl  = null;
    private static int proxyHttpPort ;
    private static int proxySslPort ;
    private static int proxyType ;
    private static String PROXY_HTTP_KEY = "network.proxy.http";
    private static String PROXY_SSL_KEY = "network.proxy.ssl";
    private static String PROXY_TYPE = "network.proxy.type";
    private static String PROXY_SSL_PORT = "network.proxy.ssl_port";
    private static String PROXY_HTTP_PORT = "network.proxy.http_port";
    
    
    @Test
    public void readCapabilitySuccesful() throws Exception {
        
        // given
        getBrowserSetupFile();
        WebDriverFixture fixture = new WebDriverFixture();
        
        // when
        fixture.startBrowser("firefox");
        logger.debug("Firefox started successfully");

        // then
        readCapabilitiesFromProfile(fixture);
        assertEquals(proxyHttpExpected, proxyHttp);
        assertEquals(proxyhttpPortExpected, proxyHttpPort);
        assertEquals(proxySslExpected, proxySsl);
        assertEquals(proxySslPortExpected, proxySslPort);
        assertEquals(proxyTypeExpected, proxyType);
        logger.debug(" ######## End of Test readCapabilitySuccesful ########");
        
    }

    private void getBrowserSetupFile() throws FixtureException, IOException {
        List<BrowserSetupElement> elements = null;
        // check if environment variable exists 
        String browserSetupPath = System.getenv(FileReader.PATH_TO_BROWSER_JSON_FILE);
        if (StringUtils.isNotBlank(browserSetupPath)) {
            logger.debug("External browserSetup file : {} used !", browserSetupPath);
            BrowserSetupReader reader = new BrowserSetupReader();
            elements = reader.readElements(browserSetupPath);
        }
        // check if browserSetup.json file exists on 'src/test/resources' path
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(BROWSER_SETUP_JSON);
        if (StringUtils.isBlank(browserSetupPath) && inputStream != null) {
            logger.debug("Internal browserSetup file browserSetup.json at path src/test/resources is used !");
            BrowserSetupReader reader = new BrowserSetupReader();
            elements = reader.readElements(BROWSER_SETUP_JSON);
        } else if (StringUtils.isBlank(browserSetupPath) && inputStream == null) {
            // There is no file to proof please check the prerequisites !
            throw new IllegalArgumentException("There is no browserSetup file to execute the test");
        }  
        
        for (BrowserSetupElement browserSetupElement : elements) {
            if (browserSetupElement.getBrowserName().equalsIgnoreCase("firefox") 
                    && (browserSetupElement.getOsName() == null || browserSetupElement.getOsName().equals("LINUX"))) {
                List<BrowserSetting> options = browserSetupElement.getOptions();
                for (BrowserSetting browserOption : options) {
                    String key = browserOption.getKey();
                    if (key.equals(PROXY_HTTP_KEY)) {
                        proxyHttpExpected = (String) browserOption.getValue();
                    }
                    if (key.equals(PROXY_SSL_KEY)) {
                        proxySslExpected = (String) browserOption.getValue();
                    }
                    if (key.equals(PROXY_HTTP_PORT)) {
                        proxyhttpPortExpected = (int) browserOption.getValue();
                    }
                    if (key.equals(PROXY_SSL_PORT)) {
                        proxySslPortExpected = (int) browserOption.getValue();
                    }
                    if (key.equals(PROXY_TYPE)) {
                        proxyTypeExpected = (int) browserOption.getValue();
                    }
                }
            }
        }
    }
   
    private void readCapabilitiesFromProfile(WebDriverFixture fixture) {
        WebDriver driver = fixture.getDriver();
        Capabilities cap = ((RemoteWebDriver) driver).getCapabilities();
        String profilePath = (String) cap.getCapability("moz:profile");
        logger.debug("Firefox Preferences read successfully");
        FirefoxProfile profile = new FirefoxProfile(new File(profilePath));
        proxyHttp = profile.getStringPreference("network.proxy.http", "");
        logger.debug("Firefox Preference proxy HTTP = {} read successfully" , proxyHttp);
        proxyHttpPort = profile.getIntegerPreference("network.proxy.http_port", 0);
        logger.debug("Firefox Preference proxy HTTP port = {} read successfully" , proxyHttpPort);
        proxySsl = profile.getStringPreference("network.proxy.ssl", "");
        logger.debug("Firefox Preference proxy SSL = {}  read successfully", proxySsl);
        proxySslPort = profile.getIntegerPreference("network.proxy.ssl_port", 0);
        logger.debug("Firefox Preference proxy SSL port = {}  read successfully", proxySslPort);
        proxyType = profile.getIntegerPreference("network.proxy.type", 0);
        logger.debug("Firefox Preference proxy type = {}  read successfully", proxyType);
    }

}
