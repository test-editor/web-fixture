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

import java.util.List;

public class BrowserSetupElement {

    private String browserSetupElementName;
    private String osName ;
    private String osVersion;
    private String browserName;
    private String browserVersion;
    
    List<BrowserSetting> capabilities ;
    List<BrowserSetting> options;
    
    public String getBrowserSetupName() {
        return browserSetupElementName;
    }
    
    public void setBrowserSetupName(String browserSetupName) {
        this.browserSetupElementName = browserSetupName;
    }
    
    public String getOsName() {
        return osName;
    }
    
    public void setOsName(String osName) {
        this.osName = osName;
    }
    
    public String getOsVersion() {
        return osVersion;
    }
    
    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }
    
    public String getBrowserName() {
        return browserName;
    }
    
    public void setBrowserName(String browserName) {
        this.browserName = browserName;
    }
    
    public String getBrowserVersion() {
        return browserVersion;
    }
    
    public void setBrowserVersion(String browserVersion) {
        this.browserVersion = browserVersion;
    }
    
    public List<BrowserSetting> getCapabilities() {
        return capabilities;
    }
    
    public void setCapabilities(List<BrowserSetting> capabilities) {
        this.capabilities = capabilities;
    }
    
    public List<BrowserSetting> getOptions() {
        return options;
    }
    
    public void setOptions(List<BrowserSetting> options) {
        this.options = options;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "BrowserSetupElement [browserSetupElementName=" + browserSetupElementName + ", osName=" + osName
                + ", osVersion=" + osVersion + ", browserName=" + browserName + ", browserVersion=" + browserVersion
                + ", capabilitiesAsJsonObject=" + ", capabilities=" + capabilities
                + ", options=" + options + "]";
    }
    
    
    
}