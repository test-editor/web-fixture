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

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testeditor.fixture.web.io.FileReaderTest;

public class CapabilityTest {
    private static final Logger logger = LoggerFactory.getLogger(FileReaderTest.class);
    
    @Test
    public void capabilityTestPositive() throws Exception {
        
        BrowserSetting capabilityInt = new BrowserSetting("This is an Integer", 1); 
        BrowserSetting capabilityString = new BrowserSetting("This is a String", "world"); 
        BrowserSetting capabilityDouble = new BrowserSetting("This is a Double", 0.2);
        BrowserSetting capabilityBoolean = new BrowserSetting("This is a Boolean", true);
        
        Assert.assertTrue("Value is not an integer", (Integer) capabilityInt.getValue() == 1);
        Assert.assertTrue("Value is not a String", capabilityString.getValue().equals("world")) ;
        Assert.assertTrue("Value is not a double", (Double) capabilityDouble.getValue() == 0.2);
        Assert.assertTrue("Value is not a boolean", (Boolean) capabilityBoolean.getValue() == true);
        logger.debug(" ######## End of Test capabilityTestPositive ########");
    }
    
}
