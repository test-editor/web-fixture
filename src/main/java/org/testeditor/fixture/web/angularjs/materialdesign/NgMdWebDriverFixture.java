/*******************************************************************************
 * Copyright (c) 2012 - 2016 Signal Iduna Corporation and others.
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testeditor.fixture.core.interaction.FixtureMethod;
import org.testeditor.fixture.web.angularjs.NgOneWebDriverFixture;

import com.paulhammant.ngwebdriver.ByAngular;
import com.paulhammant.ngwebdriver.ByAngularCssContainingText;

public class NgMdWebDriverFixture extends NgOneWebDriverFixture {

	private Logger logger = LogManager.getLogger(NgMdWebDriverFixture.class);

	@FixtureMethod
	public void selectElementInSelection(String elementLocator, String value) throws InterruptedException {
		clickOn(elementLocator);
		Thread.sleep(300);
		waitForAngularCompleteOperations();
		ByAngularCssContainingText cssContainingText = ByAngular.cssContainingText("md-option", value);
		List<WebElement> options = getDriver().findElements(cssContainingText);
		for (WebElement element : options) {
			if (element.isDisplayed()) {
				element.click();
			}
		}
		logger.trace("Selected value {} in slection {}", value, elementLocator);
	}

	@FixtureMethod
	public Map<String, String> getOptionsInSelection(String elementLocator) throws InterruptedException {
		Map<String, String> availableOptions = new HashMap<String, String>();
		clickOn(elementLocator);
		Thread.sleep(300);
		waitForAngularCompleteOperations();
		ByAngularCssContainingText cssContainingText = ByAngular.cssContainingText("md-option", "");
		List<WebElement> options = getDriver().findElements(cssContainingText);
		for (WebElement element : options) {
			if (element.isDisplayed()) {
				availableOptions.put(element.getText(), element.getText());
			}
		}
		Actions actions = new Actions(getDriver());
		actions.sendKeys(Keys.ESCAPE).perform();
		return availableOptions;
	}

	@FixtureMethod
	public void selectCurrentDate() throws InterruptedException {
		clickOn("[xpath]/html/body/div[*]/md-dialog/md-dialog-actions/button[2]");
	}

}
