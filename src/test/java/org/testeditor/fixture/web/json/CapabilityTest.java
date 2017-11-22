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

import org.junit.Assert;
import org.junit.Test;

public class CapabilityTest {
    
    @Test
    public void capabilityTest() throws Exception {
        
        BrowserSetting capabilityInt = new BrowserSetting("This is an Integer", 1); 
        BrowserSetting capabilityString = new BrowserSetting("This is a String", "world"); 
        BrowserSetting capabilityDouble = new BrowserSetting("This is a Double", 0.2);
        BrowserSetting capabilityBoolean = new BrowserSetting("This is a Boolean", true);
        
        Assert.assertTrue("Value is not an integer", (Integer) capabilityInt.getValue() == 1);
        Assert.assertTrue("Value is not a String", capabilityString.getValue().equals("world")) ;
        Assert.assertTrue("Value is not a double", (Double) capabilityDouble.getValue() == 0.2);
        Assert.assertTrue("Value is not a boolean", (Boolean) capabilityBoolean.getValue() == true);
    }

}
