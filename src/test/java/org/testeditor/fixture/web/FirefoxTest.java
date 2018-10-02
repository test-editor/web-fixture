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

import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testeditor.fixture.core.FixtureException;
import org.testeditor.fixture.core.MaskingString;

/**
 * 
 * These tests can only executed when firefox is installed on System under Test
 * environment.
 *
 */
public class FirefoxTest {

    private WebDriverFixture driver;
    private static final Logger logger = LoggerFactory.getLogger(FirefoxTest.class);

    @Before
    public void setupTest() throws IOException {
        // This new may fail, if the driver fixture cannot properly connect to
        // the started browser instance resulting in driver == null
        driver = new WebDriverFixture();
    }

    @After
    public void teardown() throws FixtureException {
        if (driver != null) {
            driver.closeBrowser();
        }
    }

    @Test
    public void googleTest() throws FixtureException {
        // given
        driver.startBrowser("firefox");
        driver.goToUrl("https://google.de");
        driver.typeInto("q", LocatorStrategy.NAME, "Test-Editor");
        driver.waitSeconds(2);
        driver.submit("q", LocatorStrategy.NAME);
        driver.waitSeconds(2);

        // when
        String title = driver.getTitle();

        // then
        Assert.assertTrue(title.startsWith("Test-Editor - Google"));
        logger.debug(" ######## End of Test googleTest ########");
    }
    
    @Test
    public void googleTestWithSpecialKeys() throws FixtureException {
        // given
        String searchField = "q";
        driver.startBrowser("firefox");
        driver.goToUrl("https://google.de");
        driver.typeInto(searchField, LocatorStrategy.NAME, "Test-Editor");

        // when
        driver.pressSpecialKey("BACK_SPACE");
        driver.pressSpecialKey("HOME");
        // used waits because Firefox since version 62.x is behaving slower on special keys. For robust tests.
        driver.waitSeconds(1);
        driver.pressSpecialKey("DELETE");
        driver.waitSeconds(1);
        driver.pressSpecialKey("DELETE");
        driver.waitSeconds(1);
        driver.pressSpecialKey("DELETE");
        driver.waitSeconds(1);
        driver.pressSpecialKey("DELETE");
        driver.waitSeconds(1);
        driver.pressSpecialKey("DELETE");
        driver.waitSeconds(1);
        String readValue = driver.readValue(searchField, LocatorStrategy.NAME);
        logger.debug(" read value in searchfield : {} " , readValue);

        // then
        Assert.assertEquals("The value does not match the requirement", "Edito", readValue);
        logger.debug(" ######## End of Test googleTestWithSpecialKeys ########");
    }
    
    @Test
    public void testReadValueToRetrieveTextFromValueAttribute() throws FixtureException {
        // given
        String searchField = "q";
        driver.startBrowser("firefox");
        driver.goToUrl("https://google.de");
        driver.typeInto(searchField, LocatorStrategy.NAME, "Test-Editor");
       
        // when
        String readValue = driver.readValue(searchField, LocatorStrategy.NAME);
        logger.debug(" read value in searchfield : {} " , readValue);

        // then
        Assert.assertEquals("The value does not match the requirement", readValue, "Test-Editor");
        logger.debug(" ######## End of Test testReadValueToRetrieveTextFromValueAttribute ########");
    }
    
    @Test
    public void testPresenceOfTextOnWebPageInValueAttribute() throws FixtureException {
        // given
        String searchField = "q";
        driver.startBrowser("firefox");
        driver.goToUrl("https://google.de");
        driver.typeInto(searchField, LocatorStrategy.NAME, "Test-Editor");
       
        // when
        boolean presenceOfText = driver.isTextOnPage("Test-Editor");
        logger.debug(" Is text on page : {} " , presenceOfText);

        // then
        Assert.assertTrue(presenceOfText);
        logger.debug(" ######## End of Test testPresenceOfTextOnWebPageInValueAttribute ########");
    }
    
    @Test
    public void typeIntoTest() throws FixtureException {
        // given
        driver.startBrowser("firefox");
        driver.goToUrl("https://google.de");
        String searchField = "q";
        driver.typeInto(searchField, LocatorStrategy.NAME, "Test-Editor");
        WebElement webElement = driver.getWebElement("q", LocatorStrategy.NAME);
        String valueOfWebElement = driver.getValueOfWebElement(webElement);
        Assert.assertEquals("The given text is not the one we searched for.", "Test-Editor", valueOfWebElement);
        driver.waitSeconds(1);

        // when
        driver.typeInto("q", LocatorStrategy.NAME, "Test-Editor");
        String readValue = driver.readValue(searchField, LocatorStrategy.NAME);
        logger.debug(" read value in searchfield : {} " , readValue);


        // then
        Assert.assertEquals("The given text is not the one we searched for.", "Test-Editor", valueOfWebElement);
        logger.debug(" ######## End of Test typeIntoTest ########");
    }
    
    @Test 
    public void typeSecretIntoTest() throws FixtureException {
        // given
        driver.startBrowser("firefox");
        driver.goToUrl("https://keepass.info/help/kb/testform.html");
        driver.waitUntilElementFound("pwd", LocatorStrategy.NAME, 5);

        // when
        driver.typeSecretInto("pwd", LocatorStrategy.NAME, new MaskingString("Test-Editor"));
        WebElement webElement = driver.getWebElement("pwd", LocatorStrategy.NAME);
        String valueOfWebElement = driver.getValueOfWebElement(webElement);
        logger.debug(" read value in searchfield : {} " , valueOfWebElement);

        // then
        Assert.assertEquals("The given text is not the one we searched for.", "Test-Editor", valueOfWebElement);
        logger.debug(" ######## End of Test typeSecretIntoTest ########");
    }
    
    @Test
    public void typeSecretIntoUnsecretFieldTest() throws FixtureException {
        // given
        driver.startBrowser("firefox");
        driver.goToUrl("https://keepass.info/help/kb/testform.html");
        driver.waitUntilElementFound("user", LocatorStrategy.NAME, 5);
        
        // when then
        Assertions.assertThrows(FixtureException.class, () -> {
            driver.typeSecretInto("user", LocatorStrategy.NAME, new MaskingString("Test-Editor"));
        });
        logger.debug(" ######## End of Test typeSecretIntoUnsecretFieldTest ########");
    }

}



