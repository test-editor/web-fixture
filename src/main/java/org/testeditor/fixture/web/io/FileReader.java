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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testeditor.fixture.core.FixtureException;
import org.testeditor.fixture.web.WebDriverFixture;

import com.google.common.io.CharStreams;

public class FileReader {
    
    private static final Logger logger = LoggerFactory.getLogger(WebDriverFixture.class);

    /**
     * Opens a file defined as fileName and read the content line by line.
     * 
     * @return File content as String
     */
    public String getFileContentAsString(String fileName) throws FixtureException {

        // Get file from resources folder
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);
       
        String result = null;
        try {
            result = CharStreams.toString(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.info("The file with the name {} can not be read in the resource folder. {}" , fileName, e);
            throw new FixtureException("file could not be found in resource folder",
                    FixtureException.keyValues("fileName", fileName));
        } catch (NullPointerException e) {
            logger.info("The file with the name {} can not be found in the resource folder.", fileName);
            result = "";
        }
        return result;
    }

}
