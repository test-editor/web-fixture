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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;
import static org.testeditor.fixture.core.TestRunReporter.Action.ENTER;
import static org.testeditor.fixture.core.TestRunReporter.Action.LEAVE;
import static org.testeditor.fixture.core.TestRunReporter.SemanticUnit.CLEANUP;
import static org.testeditor.fixture.core.TestRunReporter.SemanticUnit.COMPONENT;
import static org.testeditor.fixture.core.TestRunReporter.SemanticUnit.MACRO;
import static org.testeditor.fixture.core.TestRunReporter.SemanticUnit.MACRO_LIB;
import static org.testeditor.fixture.core.TestRunReporter.SemanticUnit.SETUP;
import static org.testeditor.fixture.core.TestRunReporter.SemanticUnit.SPECIFICATION_STEP;
import static org.testeditor.fixture.core.TestRunReporter.SemanticUnit.STEP;
import static org.testeditor.fixture.core.TestRunReporter.SemanticUnit.TEST;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.verification.VerificationMode;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.testeditor.fixture.core.FixtureException;
import org.testeditor.fixture.core.TestRunReporter.Action;
import org.testeditor.fixture.core.TestRunReporter.SemanticUnit;
import org.testeditor.fixture.core.TestRunReporter.Status;
import org.testeditor.fixture.core.artifacts.TestArtifact;
import org.testeditor.fixture.core.artifacts.TestArtifactRegistry;

@RunWith(Parameterized.class)
public class WebDriverFixtureScreenshotsTest {

    @Parameters(name = "{0}, {1}: {2}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] { 
            { CLEANUP, ENTER, never() }, 
            { COMPONENT, ENTER, never() }, 
            { MACRO, ENTER, never() },
            { MACRO_LIB, ENTER, never() },
            { SETUP, ENTER, never() },
            { SPECIFICATION_STEP, ENTER, never() },
            { STEP, ENTER, never() },
            { CLEANUP, LEAVE, never() },
            { COMPONENT, LEAVE, never() }, 
            { MACRO, LEAVE, never() },
            { MACRO_LIB, LEAVE, never() },
            { SETUP, LEAVE, never() },
            { SPECIFICATION_STEP, LEAVE, never() },
            
            { TEST, ENTER, times(1) },
            { STEP, LEAVE, times(1) },
            { TEST, LEAVE, times(1) } });
    }
    
    @Rule public TemporaryFolder tempFolder = new TemporaryFolder();

    private final SemanticUnit unit;
    private final Action action;
    private final VerificationMode expectedInvocations;

    public WebDriverFixtureScreenshotsTest(SemanticUnit unit, Action action, VerificationMode expectedInvocations) {
        this.unit = unit;
        this.action = action;
        this.expectedInvocations = expectedInvocations;
    }

    @Test
    public void textIsOnPageDynamicElementTest() throws FixtureException, WebDriverException, IOException {
        // given
        TestArtifactRegistry mockRegistry = mock(TestArtifactRegistry.class);
        WebDriverFixture driverUnderTest = new WebDriverFixture(() -> mockRegistry);
        WebDriver mockedInternalDriver = mock(WebDriver.class, withSettings().extraInterfaces(TakesScreenshot.class));
        when(((TakesScreenshot)mockedInternalDriver).getScreenshotAs(OutputType.FILE)).thenReturn(tempFolder.newFile());
        driverUnderTest.setDriver(mockedInternalDriver);

        // when
        driverUnderTest.reported(unit, action, "", "", Status.OK, Collections.emptyMap());

        // then
        verify(mockRegistry, expectedInvocations).register(any(TestArtifact.class), anyString());
    }

}
