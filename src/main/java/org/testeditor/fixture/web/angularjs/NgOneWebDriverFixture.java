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

package org.testeditor.fixture.web.angularjs;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testeditor.fixture.web.LocatorStrategy;
import org.testeditor.fixture.web.WebDriverFixture;

import com.paulhammant.ngwebdriver.ByAngular;
import com.paulhammant.ngwebdriver.NgWebDriver;

/**
 * Includes special methods for AngularJS applications
 *
 */
public class NgOneWebDriverFixture extends WebDriverFixture {

    private static final Logger logger = LoggerFactory.getLogger(NgOneWebDriverFixture.class);
    private NgWebDriver ngWebDriver;

    /**
     * @param elementLocator
     * @return {@code WebElement} special handling for Angular tags beginning with
     *         "[model"
     */
    @Override
    protected WebElement getWebElement(String elementLocator, LocatorStrategy locatorStrategy) {
        waitForAngularCompleteOperations();
        if (locatorStrategy.equals(LocatorStrategy.MODEL)) {
            proofIfModel(elementLocator);
            WebElement result = getDriver().findElement(ByAngular.model(elementLocator));
            if (result != null) {
                return result;
            }
        }
        return super.getWebElement(elementLocator, locatorStrategy);
    }

    /**
     * @param elementLocator Locator for Gui-Widget
     * @return {@code WebElement} special handling for Angular tags beginning with
     *         "[model"
     */
    private WebElement proofIfModel(String elementLocator) {
        if (elementLocator.contains("(")) {
            return findIndexedByModel(elementLocator);
        }
        return null;
    }

    /**
     * 
     * @param elementLocator Locator for Gui-Widget
     * @return {@code WebElement} special handling for Angular Gui Widgets
     */
    private WebElement findIndexedByModel(String elementLocator) {
        String substring = elementLocator.substring(elementLocator.indexOf("(") + 1);
        String indexString = substring.substring(0, substring.indexOf(")"));
        Integer index = Integer.valueOf(indexString);
        return getDriver().findElements(ByAngular.model(elementLocator.substring(elementLocator.indexOf(")") + 1)))
                .get(index);
    }

    /**
     * Instantiate a special {@link NgWebDriver} for handling Angular Widgets
     */
    @Override
    protected void configureDriver() {
        super.configureDriver();
        ngWebDriver = new NgWebDriver((JavascriptExecutor) getDriver());
        getDriver().manage().timeouts().setScriptTimeout(60, TimeUnit.SECONDS);
    }

    /**
     * Method to wait that angular operations are completed before looking up new
     * operations.
     */
    protected void waitForAngularCompleteOperations() {
        logger.trace("Wait until angular operations are completed.");
        ngWebDriver.waitForAngularRequestsToFinish();
        logger.trace("Angular operations completed.");
    }

}
