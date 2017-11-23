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

public class BrowserSetting {
    
    private String key;
    private Object value;
   
    /**
     * 
     * @param key
     * @param value
     */
    public BrowserSetting(String key , Object value) {
        this.key = key;
        this.value = value;
    }
    
    public String getKey() {
        return key;
    }
    
    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return key + " - " + value;
    }

}


