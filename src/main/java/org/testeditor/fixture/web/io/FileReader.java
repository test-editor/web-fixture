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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.CharStreams;

public class FileReader {
    
    private static final Logger logger = LoggerFactory.getLogger(FileReader.class);
    public static final String PATH_TO_BROWSER_JSON_FILE = "TEST_EDITOR_BROWSER_SETUP_PATH";

    /**
     * Opens a file defined as fileName and read the content line by line.
     * 
     * @return File content as String
     * @throws IOException 
     */
    public String getFileContentAsString(String fileName) throws IOException {
        String result = null;
        String browserSetupFilePath = System.getenv(PATH_TO_BROWSER_JSON_FILE);
        // First try to resolve the file through environment variable
        result = getResourceOverEnvironmentVariable(browserSetupFilePath);
        if (StringUtils.isNotBlank(result)) {
            logger.debug("Browser capabilities read from file : {}", browserSetupFilePath);
        } else {
            // Get file from resources folder
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(fileName);
            logger.debug("Browser capabilities read from file : {} in \"src/test/resources\" folder", fileName);
            try {
                result = CharStreams.toString(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                
            } catch (IOException e) {
                logger.info("The file with the name {} can not be read in the resource folder."
                        + " Exception occured: {}" , fileName, e);
                //  throw new FixtureException("file could not be found in resource folder",
                //  FixtureException.keyValues("fileName", fileName));
            } catch (NullPointerException e) {
                logger.error("The file with the name {} can not be found in the resource folder.", fileName);
                result = "";
            }
        }
        return result;
    }
    
    /**
     * For reading content of a JSON-File where the path is defined through the environment variable 
     * <code>TEST_EDITOR_BROWSER_SETUP_PATH</code>.<br>
     * Usage under Linux : Set TEST_EDITOR_BROWSER_SETUP_PATH=~/your_prefered_folder/fileName.json
     * Usage under Windows : Set TEST_EDITOR_BROWSER_SETUP_PATH=C:/your_prefered_folder/fileName.json
     * 
     * @return The content of the the file with the path defined in an environment variable 
     * named <code>TEST_EDITOR_BROWSER_SETUP_PATH</code>
     * @throws IOException 
     *          When File can not be read
     * @throws FileNotFoundException 
     *          When File is not present on given path. 
     */
    protected String getResourceOverEnvironmentVariable(String path) throws IOException {
        String fileContent = null;
        if (StringUtils.isNotBlank(path)) {
            fileContent = FileUtils.readFileToString(new File(path), StandardCharsets.UTF_8);
        } else {
            logger.debug("Can not find environment variable '{}' with path entry for browser "
                    + "settings JSON file, trying local resource folder src/test/resources", PATH_TO_BROWSER_JSON_FILE);
        }
        return fileContent;
    }
    
}
