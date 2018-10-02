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

package org.testeditor.fixture.web.io;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileReaderTest {

    static final String lineSeparator = System.getProperty("line.separator");
    private static final Logger logger = LoggerFactory.getLogger(FileReaderTest.class);
        
    String result = "firefox-cap1 = \"abcdüöß\"" + lineSeparator  
            + "firefox-cap2 = 8" + lineSeparator  
            + "firefox-cap3 = true";

    @Test
    public void testReadFile() throws Exception {
        FileReader reader = new FileReader();
        String fileContentAsString = reader.getFileContentAsString("utf8EncodedTextWithUmlaut.txt");
        Assert.assertEquals(result, fileContentAsString);
        logger.debug(" ######## End of Test testReadFile ########");
    }

}
