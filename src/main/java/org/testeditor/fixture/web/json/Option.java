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

import java.lang.reflect.Type;

public class Option {

    private String key;
    private Object value;
    private Type type;
    
    /**
     * 
     * @param key
     * @param value
     * @param type
     */
    public Option(String key , Object value, Type type) {
        this.key = key;
        this.value = value;
        this.type = type;
    }
    
    public String getKey() {
        return key;
    }
    
    public Object getValue() {
        return value;
    }

    public Type getType() {
        return type;
    }
    
    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return key + " - " + value;
    }

}
