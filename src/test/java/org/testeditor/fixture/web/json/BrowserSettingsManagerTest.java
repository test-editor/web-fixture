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

package org.testeditor.fixture.web.json;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.openqa.selenium.Platform;
import org.testeditor.fixture.core.FixtureException;

public class BrowserSettingsManagerTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void failureInDuplicateCapability() throws FixtureException {
        // given + when
        thrown.expect(FixtureException.class);
        thrown.expectMessage("Duplicate entries existing in configuration file");
        thrown.expect(fixtureExceptionMatcher("file", "browserSetup.json", //
                "valueCapability", 5, //
                "valueBrowserSettingsMap", 8, //
                "offendingKey", "my.cap"));
        BrowserSettingsManager manager = new BrowserSettingsManager();
        manager.getBrowserSettings("browserSetupWithFailureCapability.json");

        // then
        // expected RuntimeException will be thrown.
    }

    @Test
    public void failureInDuplicateOption() throws FixtureException {

        // given
        // when
        thrown.expect(FixtureException.class);
        thrown.expectMessage("Duplicate entries existing in configuration file");
        thrown.expect(fixtureExceptionMatcher("file", "browserSetup.json", //
                "valueOption", 5, //
                "valueBrowserSettingsMap", 8, //
                "offendingKey", "network.proxy.type"));

        BrowserSettingsManager manager = new BrowserSettingsManager();
        manager.getBrowserSettings("browserSetupWithFailureOption.json");

        // then
        // expected RuntimeException will be thrown.
    }

    @Test
    public void successfulWindowsTestForOptions() throws FixtureException {

        // given
        BrowserSettingsManager manager = new BrowserSettingsManager();
        BrowserSettingsManager spyManager = Mockito.spy(manager);
        when(spyManager.getCurrentPlatform()).thenReturn(Platform.VISTA);

        // when
        List<BrowserSetupElement> browserSettings = (spyManager.getBrowserSettings("browserSetupWithOptions.json"));

        // then
        assertEquals(3, browserSettings.size());
        assertEquals("firefox", browserSettings.get(1).getBrowserName());
        assertEquals("windows-firefox-section", browserSettings.get(1).getBrowserSetupName());
        assertEquals(null, browserSettings.get(2).getOsName());
        assertEquals("firefox-section-without-os", browserSettings.get(2).getBrowserSetupName());
    }

    @Test
    public void successfulLinuxTestForOptions() throws FixtureException {

        // given
        BrowserSettingsManager manager = new BrowserSettingsManager();
        BrowserSettingsManager spyManager = Mockito.spy(manager);
        when(spyManager.getCurrentPlatform()).thenReturn(Platform.LINUX);

        // when
        List<BrowserSetupElement> browserSettings = (spyManager.getBrowserSettings("browserSetupWithOptions.json"));

        // then
        assertEquals(2, browserSettings.size());
        assertEquals("linux-firefox-section", browserSettings.get(0).getBrowserSetupName());
        assertEquals("firefox-section-without-os", browserSettings.get(1).getBrowserSetupName());
        assertEquals("firefox", browserSettings.get(1).getBrowserName());
        assertEquals("firefox", browserSettings.get(0).getBrowserName());
    }

    @Test
    public void successfulMacTestForOptions() throws FixtureException {

        // given
        BrowserSettingsManager manager = new BrowserSettingsManager();
        BrowserSettingsManager spyManager = spy(manager);
        when(spyManager.getCurrentPlatform()).thenReturn(Platform.MAC);

        // when
        List<BrowserSetupElement> browserSettings = (spyManager.getBrowserSettings("browserSetupWithOptions.json"));

        // then
        assertEquals(2, browserSettings.size());
        assertEquals("mac-firefox-section", browserSettings.get(0).getBrowserSetupName());
        assertEquals("firefox-section-without-os", browserSettings.get(1).getBrowserSetupName());
        assertEquals("firefox", browserSettings.get(0).getBrowserName());
        assertEquals("firefox", browserSettings.get(1).getBrowserName());
    }

    private Matcher<FixtureException> fixtureExceptionMatcher(final Object... objects) {
        return new BaseMatcher<FixtureException>() {
            @Override
            public boolean matches(Object item) {
                if (item instanceof FixtureException) {
                    Map<String, Object> keyValues = ((FixtureException) item).getKeyValueStore();
                    boolean result = true;
                    for (int i = 0; i < objects.length / 2; i++) {
                        result = result && keyValues.containsKey(objects[i * 2])
                                && keyValues.get(objects[i * 2]).equals(objects[i * 2 + 1]);
                    }
                    return result;
                } else {
                    return false;
                }
            }

            @Override
            public void describeTo(Description description) {
                // empty for now
            }
        };
    }

}
