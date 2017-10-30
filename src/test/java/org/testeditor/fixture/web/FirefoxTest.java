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

import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * These tests can only executed when firefox is installed on System under Test
 * environment.
 *
 */
public class FirefoxTest {

    private WebDriverFixture driver;

    @Before
    public void setupTest() throws IOException {
        // This new may fail, if the driver fixture cannot properly connect to
        // the started browser instance resulting in driver == null
        driver = new WebDriverFixture();
    }

    @After
    public void teardown() {
        if (driver != null) {
            driver.closeBrowser();
        }
    }

    @Test
    public void googleTest() throws InterruptedException, IOException {
        // given
        driver.startBrowser("firefox");
        driver.goToUrl("https://google.de");
        driver.typeInto("q", LocatorStrategy.NAME, "Test-Editor");
        driver.submit("q", LocatorStrategy.NAME);

        // when
        driver.waitUntilElementFound("res", LocatorStrategy.ID, 2);
        String title = driver.getTitle();

        // then
        Assert.assertEquals("Test-Editor - Google-Suche", title);
    }

}
