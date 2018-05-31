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

package org.testeditor.fixture.web.angularjs.materialdesign;

import java.util.List;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testeditor.fixture.core.FixtureException;
import org.testeditor.fixture.core.interaction.FixtureMethod;
import org.testeditor.fixture.web.LocatorStrategy;
import org.testeditor.fixture.web.angularjs.NgOneWebDriverFixture;

import com.google.gson.JsonObject;
import com.paulhammant.ngwebdriver.ByAngular;
import com.paulhammant.ngwebdriver.ByAngularCssContainingText;

/**
 * Includes special methods for AngularJS applications with Google's Material
 * Design Specification.
 */
public class NgMdWebDriverFixture extends NgOneWebDriverFixture {

    private static final Logger logger = LoggerFactory.getLogger(NgMdWebDriverFixture.class);

    /**
     * 
     * @param elementLocator Locator for Selection
     * @param value name of the entry to be selected
     */
    @FixtureMethod
    public void selectElementInSelection(String elementLocator, LocatorStrategy locatorStrategy, String value)
            throws FixtureException {
        clickOn(elementLocator, locatorStrategy);
        wrappedSleep(300, "select element in selection interrupted", keyValues("elementLocator", elementLocator,
                "locatorStrategy", locatorStrategy.toString(), "value", value));
        waitForAngularCompleteOperations();
        ByAngularCssContainingText cssContainingText = ByAngular.cssContainingText("md-option", value);
        List<WebElement> options = getDriver().findElements(cssContainingText);
        for (WebElement element : options) {
            if (element.isDisplayed()) {
                element.click();
            }
        }
        logger.trace("Selected value {} in selection {} using {}", value, elementLocator, locatorStrategy);
    }

    /**
     * @return the Options of a selection Field as a {@code JsonObject}
     * @param elementLocator Locator for Selection
     */
    @FixtureMethod
    public JsonObject getOptionsInSelection(String elementLocator, LocatorStrategy locatorStrategy)
            throws FixtureException {
        JsonObject availableOptions = new JsonObject();
        clickOn(elementLocator, locatorStrategy);
        wrappedSleep(300, "get option in selection interrupted",
                keyValues("elementLocator", elementLocator, "locatorStrategy", locatorStrategy.toString()));
        waitForAngularCompleteOperations();
        ByAngularCssContainingText cssContainingText = ByAngular.cssContainingText("md-option", "");
        List<WebElement> options = getDriver().findElements(cssContainingText);
        for (WebElement element : options) {
            if (element.isDisplayed()) {
                availableOptions.addProperty(element.getText(), element.getText());
            }
        }
        Actions actions = new Actions(getDriver());
        actions.sendKeys(Keys.ESCAPE).perform();
        return availableOptions;
    }

}
