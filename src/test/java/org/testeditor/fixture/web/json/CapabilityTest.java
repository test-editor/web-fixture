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

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CapabilityTest {
    private static final Logger logger = LoggerFactory.getLogger(CapabilityTest.class);
    
    @Test
    public void capabilityTestPositive() throws Exception {
        
        final BrowserSetting capabilityInt = new BrowserSetting("This is an Integer", 1); 
        final BrowserSetting capabilityString = new BrowserSetting("This is a String", "world"); 
        final BrowserSetting capabilityDouble = new BrowserSetting("This is a Double", 0.2);
        final BrowserSetting capabilityBoolean = new BrowserSetting("This is a Boolean", true);
        
        assertTrue((Integer) capabilityInt.getValue() == 1, "Value is not an integer");
        assertTrue(capabilityString.getValue().equals("world"), "Value is not a String") ;
        assertTrue((Double) capabilityDouble.getValue() == 0.2, "Value is not a double");
        assertTrue((Boolean) capabilityBoolean.getValue() == true, "Value is not a boolean");
        logger.debug(" ######## End of Test capabilityTestPositive ########");
    }
    
}
