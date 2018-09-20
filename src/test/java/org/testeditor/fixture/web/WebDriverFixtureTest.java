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

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.openqa.selenium.WebElement;
import org.testeditor.fixture.core.FixtureException;


public class WebDriverFixtureTest {
    
    @Test
    public void textIsOnPageDynamicElementTest() throws FixtureException {
        // given
        WebDriverFixture driver  = new WebDriverFixture();
        WebElement element = Mockito.mock(WebElement.class);
        Mockito.when(element.getAttribute("value")).thenReturn("foo");
        String expectedValue = "foo";
        
        // when 
        String textOnPage = driver.getValueOfWebElement(element);
        
        //then
        Assert.assertEquals(expectedValue, textOnPage); 
    }
    
    @Test
    public void textIsNotOnPageDynamicElementTest() throws FixtureException {
        // given
        WebDriverFixture driver  = new WebDriverFixture();
        WebElement element = Mockito.mock(WebElement.class);
        Mockito.when(element.getAttribute("value")).thenReturn("");
        
        // when 
        String textOnPage = driver.getValueOfWebElement(element);
        
        //then
        Assert.assertNull(textOnPage); 
    }
    
    @Test
    public void textIsNotOnPageChecksGetText() throws FixtureException {
        // given
        WebDriverFixture driver  = new WebDriverFixture();
        WebElement element = Mockito.mock(WebElement.class);
        Mockito.when(element.getText()).thenReturn("");
        
        // when 
        String textOnPage = driver.getValueOfWebElement(element);
        
        //then
        Assert.assertNull(textOnPage); 
    }
    
    @Test
    public void textIsOnPageChecksGetText() throws FixtureException {
        // given
        WebDriverFixture driver  = new WebDriverFixture();
        WebElement element = Mockito.mock(WebElement.class);
        Mockito.when(element.getText()).thenReturn("foo");
        
        // when 
        String textOnPage = driver.getValueOfWebElement(element);
        
        //then
        Assert.assertEquals("foo", textOnPage); 
    }
    
}
