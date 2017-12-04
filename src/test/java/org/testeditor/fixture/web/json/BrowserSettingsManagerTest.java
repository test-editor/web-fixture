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


package org.testeditor.fixture.web.json;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.spy;

import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.openqa.selenium.Platform;


public class BrowserSettingsManagerTest {
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    
    @Test
    public void failureInDuplicateCapability() {
        
        // given
        BrowserSettingsManager manager = new BrowserSettingsManager();
        
        // when
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Duplicate entries existing in configuration file browserSetup.json. Entries are: "
                + "[my.cap - 5, my.cap - 8]");
        manager.getBrowserSettings("browserSetupWithFailureCapability.json");
        
        // then
        // expected RuntimeException will be thrown.
    }
    
    @Test
    public void failureInDuplicateOption() {
        
        // given
        BrowserSettingsManager manager = new BrowserSettingsManager();
               
        // when
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Duplicate entries existing in configuration file browserSetup.json. Entries are: "
                + "[network.proxy.type - 5, network.proxy.type - 8]");
        manager.getBrowserSettings("browserSetupWithFailureOption.json");
        
        // then
        // expected RuntimeException will be thrown.
    }
    
    @Test
    public void successfulWindowsTestForOptions() {
        
        // given
        BrowserSettingsManager manager = new BrowserSettingsManager();
        BrowserSettingsManager spyManager = Mockito.spy(manager);
        when(spyManager.getCurrentPlatform()).thenReturn(Platform.VISTA);
      
        // when
        List<BrowserSetupElement> browserSettings = (spyManager.getBrowserSettings(
            "browserSetupWithOptions.json"));
    
        // then
        Assert.assertEquals(3, browserSettings.size());
        Assert.assertEquals("firefox", browserSettings.get(1).getBrowserName());
        Assert.assertEquals("windows-firefox-section",browserSettings.get(1).getBrowserSetupName());
        Assert.assertEquals(null, browserSettings.get(2).getOsName());
        Assert.assertEquals("firefox-section-without-os",browserSettings.get(2).getBrowserSetupName());
    } 
    
    @Test
    public void successfulLinuxTestForOptions() {
        
        // given
        BrowserSettingsManager manager = new BrowserSettingsManager();
        BrowserSettingsManager spyManager = Mockito.spy(manager);
        when(spyManager.getCurrentPlatform()).thenReturn(Platform.LINUX);
      
        // when
        List<BrowserSetupElement> browserSettings = (spyManager.getBrowserSettings(
            "browserSetupWithOptions.json"));
        
        // then
        Assert.assertEquals(2, browserSettings.size());
        Assert.assertEquals("linux-firefox-section",browserSettings.get(0).getBrowserSetupName());
        Assert.assertEquals("firefox-section-without-os",browserSettings.get(1).getBrowserSetupName());
        Assert.assertEquals("firefox", browserSettings.get(1).getBrowserName());
        Assert.assertEquals("firefox", browserSettings.get(0).getBrowserName());
    }
    
    @Test
    public void successfulMacTestForOptions() {
        
        // given
        BrowserSettingsManager manager = new BrowserSettingsManager();
        BrowserSettingsManager spyManager = spy(manager);
        when(spyManager.getCurrentPlatform()).thenReturn(Platform.MAC);
      
        // when
        List<BrowserSetupElement> browserSettings = (spyManager.getBrowserSettings(
            "browserSetupWithOptions.json"));
        
        // then
        Assert.assertEquals(2, browserSettings.size());
        Assert.assertEquals("mac-firefox-section",browserSettings.get(0).getBrowserSetupName());
        Assert.assertEquals("firefox-section-without-os",browserSettings.get(1).getBrowserSetupName());
        Assert.assertEquals("firefox", browserSettings.get(0).getBrowserName());
        Assert.assertEquals("firefox", browserSettings.get(1).getBrowserName());
    }

}
