/*******************************************************************************
 * Copyright (c) 2012 - 2016 Signal Iduna Corporation and others.
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

import com.google.gson.JsonObject;
import io.github.bonigarcia.wdm.BrowserManager;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.FirefoxDriverManager;
import io.github.bonigarcia.wdm.InternetExplorerDriverManager;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testeditor.fixture.core.TestRunListener;
import org.testeditor.fixture.core.TestRunReportable;
import org.testeditor.fixture.core.TestRunReporter;
import org.testeditor.fixture.core.TestRunReporter.Action;
import org.testeditor.fixture.core.TestRunReporter.SemanticUnit;
import org.testeditor.fixture.core.interaction.FixtureMethod;

/**
 * The {@code WebDriverFixture} class represents methods for automating
 * browser-actions like clicking on gui-widgets by means of a test-driver called
 * {@code WebDriver}.
 */
public class WebDriverFixture implements TestRunListener, TestRunReportable {

    // Proxy keys
    public static final String HTTP_PROXY_HOST = "http.proxyHost";
    public static final String HTTP_PROXY_PASSWORD = "http.proxyPassword";
    public static final String HTTP_PROXY_USER = "http.proxyUser";

    // Proxy values
    private static String httpProxyHost;
    private static String httpProxyPassword;
    private static String httpProxyUser;

    private WebDriver driver;
    private static final Logger logger = LoggerFactory.getLogger(WebDriverFixture.class);

    private String exeuteScript = null;

    /**
     * specifies the time to wait (in seconds) before an action will be
     * performed.
     * 
     * @param timeToWait
     * @throws InterruptedException
     */
    @FixtureMethod
    public void waitSeconds(long timeToWait) throws InterruptedException {
        Thread.sleep(timeToWait * 1000);
    }

    /**
     * @return {@code WebDriver} to perform actions.
     */
    public WebDriver getDriver() {
        return driver;
    }

    /**
     * sets the {@code WebDriver}
     * 
     * @param driver
     */
    @FixtureMethod
    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Sets a String as a script-name for execution.
     * 
     * @param exec
     */
    @FixtureMethod
    public void setExecuteScript(String exec) {
        exeuteScript = exec;
    }

    /**
     * Starts a specified browser. Following Strings are available:<br>
     * 
     * <ul>
     * <li><b>default</b> -opens a specific browser (On Windows -> Internet
     * Explorer, on others -> Firefox), but Firefox will only work for versions
     * > 55.0.3</li>
     * <li><b>firefox</b> - starts a locally installed firefox instance with
     * {@code GeckoDriver}</li>
     * <li><b>ie</b> - opens Microsoft Windows Internet Explorer</li>
     * <li><b>chrome</b> - opens Google Chrome</li>
     * </ul>
     * 
     * @param browser
     *            String literal for used browser
     * @return {@code WebDriver}
     */
    @FixtureMethod
    public WebDriver startBrowser(String browser) {
        logger.info("Starting browser: {}", browser);
        switch (browser) {
        case "default":
            if (SystemUtils.IS_OS_WINDOWS) {
                launchIE();
            } else {
                launchFirefox();
            }
            break;
        case "firefox":
            launchFirefox();
            break;
        case "ie":
            launchIE();
            break;
        case "chrome":
            launchChrome();
            break;
        }
        configureDriver();
        return driver;
    }

    @Override
    public void initWithReporter(TestRunReporter reporter) {
        reporter.addListener(this);
    }

    @Override
    public void reported(SemanticUnit unit, Action action, String msg) {
        if (unit == SemanticUnit.TEST && action == Action.ENTER) {
            runningTest = msg;
        }
        if (screenshotShouldBeMade(unit, action, msg)) {
            screenshot(msg + '.' + action.name());
        }
    }

    private String runningTest = null;
    private static final int SCREENSHOT_FILENAME_MAXLEN = 128;

    private String getCurrentTestCase() {
        return runningTest != null ? runningTest : "UNKNOWN_TEST";
    }

    private String getScreenshotPath() {
        // configurable through maven build?
        return "screenshots";
    }

    private boolean screenshotShouldBeMade(SemanticUnit unit, Action action, String msg) {
        // configurable through maven build?
        return ((action == Action.ENTER) || unit == SemanticUnit.TEST) && driver != null;
    }

    private String reduceToMaxLen(String base, int maxLen) {
        if (base.length() < maxLen) {
            return base;
        } else {
            return base.substring(0, maxLen);
        }
    }

    private String constructScreenshotFilename(String filenameBase, String testcase) {
        String additionalGraphicType = ".png";
        String escapedBaseName = filenameBase.replaceAll("[^a-zA-Z0-9.-]", "_").replaceAll("_+", "_")
                .replaceAll("_+\\.", ".").replaceAll("\\._+", ".");
        String timeStr = new SimpleDateFormat("HHmmss.SSS").format(new Date());
        String dateStr = new SimpleDateFormat("YYYYMMdd").format(new Date());
        StringBuffer finalFilenameBuffer = new StringBuffer();
        int lenOfFixedElements = timeStr.length() + additionalGraphicType.length() + 1/* hyphen */;
        finalFilenameBuffer //
                .append(getScreenshotPath()) //
                .append('/').append(reduceToMaxLen(testcase, SCREENSHOT_FILENAME_MAXLEN))//
                .append('/').append(dateStr) //
                .append('/').append(timeStr).append('-') //
                .append(reduceToMaxLen(escapedBaseName, SCREENSHOT_FILENAME_MAXLEN - lenOfFixedElements))//
                .append(additionalGraphicType);
        return finalFilenameBuffer.toString();
    }

    /**
     * Write a screenshot of the current ui into a file, based on the basic
     * filenameBase provided. The final filename is constructed using the
     * testcase a hash of the fixture itself and a shortened timestamp.
     * 
     * @param filenameBase
     *            user definable part of the final filename
     */
    @FixtureMethod
    public String screenshot(String filenameBase) {
        String testcase = getCurrentTestCase();
        String finalFilename = constructScreenshotFilename(filenameBase, testcase);
        if (driver != null) {
            try {
                File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                FileUtils.copyFile(scrFile, new File(finalFilename));
                logger.info("Wrote screenshot to file='{}'.", finalFilename);
            } catch (IOException e) {
                logger.error("Could not write screenshot to file='{}'.", finalFilename);
            }
        } else {
            logger.warn("Driver not set (yet). Could not write screenshot to file='{}'.", finalFilename);
        }
        return finalFilename;
    }

    /**
     * starts Firefox Portable. This works for Versions < 47.0. preferably
     * Firefox ESR 45.4.0
     * 
     * @param browserPath
     * @return {@code WebDriver}
     */
    @FixtureMethod
    public WebDriver startFireFoxPortable(String browserPath) {
        logger.info("Starting firefox portable: {}", browserPath);
        setupDrivermanager(FirefoxDriverManager.getInstance());
        File file = new File(browserPath);
        boolean exists = file.exists();
        FirefoxBinary binary = new FirefoxBinary(new File(browserPath));
        FirefoxOptions options = new FirefoxOptions();
        options.setBinary(binary);
        driver = new FirefoxDriver(options);
        return driver;
    }

    /**
     * launches Google Chrome with the ChromeDriverManager, this will be
     * downloaded automatically and will be saved in the directory
     * /~USER_HOME/m2/repository/webdriver/ in every according OS.
     */
    private void launchChrome() {
        setupDrivermanager(ChromeDriverManager.getInstance());
        driver = new ChromeDriver();
        registerShutdownHook(driver);
    }

    /**
     * launches Firefox with FirefoxDriverManager, this will be downloaded
     * automatically and will be saved in the directory
     * /~USER_HOME/m2/repository/webdriver/ in every according OS.
     */
    private void launchFirefox() {
        setupDrivermanager(FirefoxDriverManager.getInstance());
        driver = new FirefoxDriver();
        registerShutdownHook(driver);
    }

    /**
     * launches Windows Internet Explorer with IEDriverServerManager, this will
     * be downloaded automatically and will be saved in the directory
     * /~USER_HOME/m2/repository/webdriver/ in every according OS.
     */
    private void launchIE() {
        setupDrivermanager(InternetExplorerDriverManager.getInstance());
        DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
        setCapabilities(capabilities);
        driver = new InternetExplorerDriver(capabilities);
        registerShutdownHook(driver);
    }

    private void setCapabilities(DesiredCapabilities capabilities) {
        capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
        capabilities.setCapability("requireWindowFocus", true);
    }

    private void setupDrivermanager(BrowserManager manager) {
        if (areProxyCredentialsAvailable()) {
            settingProxyCredentials(manager);
        }
        manager.setup();
    }

    private boolean areProxyCredentialsAvailable() {
        String proxyHost = System.getenv(HTTP_PROXY_HOST);
        boolean proxyCredentialsAvailable = false;
        if (proxyHost != null && !proxyHost.isEmpty()) {
            httpProxyHost = proxyHost;
            httpProxyUser = System.getenv(HTTP_PROXY_USER);
            httpProxyPassword = System.getenv(HTTP_PROXY_PASSWORD);
            proxyCredentialsAvailable = true;
        }
        return proxyCredentialsAvailable;
    }

    /**
     * To download the correct version of the WebDriver-Server for each browser
     * manufacturer for using W3C WebDriver-compatible clients to interact with,
     * we need proxy credentials if "System under Test" is located behind a
     * proxy. Because of this reason there is a pleasant way to download the
     * corresponding WebDriver-Server with the help of the <a href=
     * "https://github.com/bonigarcia/webdrivermanager">WebDriverManager</a>
     * developed by Boni Garcia, which provides an ability performing a download
     * in an automated way. The proxy credentials should be provided as "System
     * Environment Variables" before the execution of tests.
     * <p>
     * 
     * There are 3 variables to be provided so far.<br>
     * 
     * Here is an example for naming the proxy variables e.g. on a linux
     * system<br>
     * 
     * <pre>
     * 1.)  export http.proxyHost     = my.proxy.url
     * 2.)  export http.proxyPassword = myProxyPassword
     * 3.)  export http.proxyUser     = myProxyUser
     * </pre>
     * 
     * @param browserManager
     *            The specific BrowserManager of a browser.
     */
    private void settingProxyCredentials(BrowserManager browserManager) {
        browserManager.proxy(httpProxyHost);
        browserManager.proxyUser(httpProxyUser);
        browserManager.proxyPass(httpProxyPassword);
    }

    /**
     * ShutdownHook for teardown of started Browsermanager
     * 
     * @param driver
     *            Webdriver to be used
     */
    private void registerShutdownHook(final WebDriver driver) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                driver.quit();
            }
        });
    }

    /**
     * Specifies behavior of Browser. Prerequisite for a test to be executed is,
     * that browsers are opened maximized to prevent failures.
     */
    protected void configureDriver() {
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        // because executing tests results in problems on travis build
        DisplayMode displayMode = getDisplayMode();
        int width = displayMode.getWidth();
        int height = displayMode.getHeight();
        logger.debug("Screen-Width: " + width + " Screen-Height: " + height) ;
        driver.manage().window().setSize(new Dimension(width,height));
    }
    
    
    private DisplayMode getDisplayMode() {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        return gd.getDisplayMode();
    }

    /**
     * opens a specified URL in the browser
     * 
     * @param url
     *            - an URL-String which represents the Web-Site to open in the
     *            browser. example: url = "http://www.google.com"
     */
    @FixtureMethod
    public void goToUrl(String url) {
        driver.get(url);
    }

    /**
     * close the Browser window
     */
    @FixtureMethod
    public void closeBrowser() {
        // drive.close() leads to exception with Selenium V. 3.5.3 and Firefox
        // 55.0.3 preferred method -> just driver.quit();
        driver.quit();
        driver = null;
    }

    /**
     * This method waits explicitly for a WebElement in the DOM-Object until the
     * given timeOut is reached before an Exception is thrown.
     * 
     * @param elementLocator
     *            Locator for Gui-Widget
     * @param locatorType
     *            Type of locator for Gui-Widget
     * @param timeOutInSeconds
     *            The max timeout in seconds when an element is expected before
     *            a NotFoundException will be thrown.
     */
    @FixtureMethod
    public void waitUntilElementFound(String elementLocator, LocatorStrategy locatorType, int timeOutInSeconds) {
        WebDriverWait webDriverWait = new WebDriverWait(driver, timeOutInSeconds);
        webDriverWait.until(ExpectedConditions.presenceOfElementLocated(getBy(elementLocator, locatorType)));
    }

    /**
     * press enter on a specified Gui-Widget
     * 
     * @param elementLocator
     *            Locator for Gui-Widget
     * @param locatorType
     *            Type of locator for Gui-Widget
     */
    @FixtureMethod
    public void pressEnterOn(String elementLocator, LocatorStrategy locatorType) {
        WebElement element = getWebElement(elementLocator, locatorType);
        element.submit();
    }

    /**
     * type into text fields on a specified Gui-Widget
     * 
     * @param elementLocator
     *            Locator for Gui-Widget
     * @param locatorType
     *            Type of locator for Gui-Widget
     * @param value
     *            String which is set into the textfield
     */
    @FixtureMethod
    public void typeInto(String elementLocator, LocatorStrategy locatorType, String value) {
        WebElement element = getWebElement(elementLocator, locatorType);
        element.sendKeys(value);
    }

    /**
     * empties the textfield
     * 
     * @param elementLocator
     *            Locator for Gui-Widget
     * @param locatorType
     *            Type of locator for Gui-Widget
     */
    @FixtureMethod
    public void clear(String elementLocator, LocatorStrategy locatorType) {
        WebElement element = getWebElement(elementLocator, locatorType);
        element.clear();
    }

    /**
     * click on a specified Gui-Widget
     * 
     * @param elementLocator
     *            Locator for Gui-Widget
     * @param locatorType
     *            Type of locator for Gui-Widget
     */
    @FixtureMethod
    public void clickOn(String elementLocator, LocatorStrategy locatorType) {
        WebElement element = getWebElement(elementLocator, locatorType);
        element.click();
    }

    /**
     * @return The title of the actual accessed html web site.
     */
    @FixtureMethod
    public String getTitle() {
        return driver.getTitle();
    }

    /**
     * Submits WebElements like forms ore whole websites
     * 
     * @param elementLocator
     *            Locator for Gui-Widget
     * @param locatorType
     *            Type of locator for Gui-Widget
     */
    @FixtureMethod
    public void submit(String elementLocator, LocatorStrategy locatorType) {
        WebElement element = getWebElement(elementLocator, locatorType);
        element.submit();
    }

    /**
     * 
     * @param elementLocator
     *            Locator for Gui-Widget
     * @param locatorType
     *            Type of locator for Gui-Widget
     * @return value of a label
     */
    @FixtureMethod
    public String readValue(String elementLocator, LocatorStrategy locatorType) {
        WebElement element = getWebElement(elementLocator, locatorType);
        return element.getText();
    }

    /**
     * 
     * @param elementLocator
     *            Locator for Gui-Widget
     * @param locatorType
     *            Type of locator for Gui-Widget
     * @param value
     *            to be selected in a Selectionbox
     * @throws InterruptedException
     */
    @FixtureMethod
    public void selectElementInSelection(String elementLocator, LocatorStrategy locatorType, String value)
            throws InterruptedException {
        clickOn(elementLocator, locatorType);
        Thread.sleep(300);
        WebElement element = getWebElement(elementLocator, locatorType);
        new Select(element).selectByVisibleText(value);
    }

    /**
     * 
     * @param elementLocator
     *            Locator for Gui-Widget
     * @param locatorType
     *            Type of locator for Gui-Widget
     * @return a JsonObject of the values in a Selectionbox
     * @throws InterruptedException
     */
    @FixtureMethod
    public JsonObject getOptionsInSelection(String elementLocator, LocatorStrategy locatorType)
            throws InterruptedException {
        JsonObject availableOptions = new JsonObject();
        clickOn(elementLocator, locatorType);
        Thread.sleep(300);

        Select selection = new Select(getWebElement(elementLocator, locatorType));
        for (WebElement webElement : selection.getAllSelectedOptions()) {
            availableOptions.addProperty(webElement.getText(), webElement.getText());
        }
        return availableOptions;
    }

    /**
     * 
     * @param elementLoacator
     *            Locator for Gui-Widget
     * @param locatorType
     *            Type of locator for Gui-Widget
     * @return true if a checkable Gui-Widget is checked, false otherwise.
     */
    @FixtureMethod
    public Boolean checkEnabled(String elementLoacator, LocatorStrategy locatorType) {
        WebElement element = getWebElement(elementLoacator, locatorType);
        return element.isEnabled();
    }

    /**
     * @param elementLocator
     *            Locator for Gui-Widget
     * @param locatorType
     *            Type of locator for Gui-Widget
     * @return {@code WebElement} where the Locator String begins in a specific
     *         manner.
     */
    protected WebElement getWebElement(String elementLocator, LocatorStrategy locatorType) {
        if (exeuteScript != null) {
            executeScript();
        }

        logger.info("Lookup element {} type {}", elementLocator, locatorType.name());
        return driver.findElement(getBy(elementLocator, locatorType));
    }

    public By getBy(String elementLocator, LocatorStrategy locatorType) {
        By result = null;
        switch (locatorType) {
        case XPATH:
            result = By.xpath(elementLocator);
            break;
        case LINK:
            result = By.linkText(elementLocator);
            break;
        case ID:
            result = By.id(elementLocator);
            break;
        case CSS:
            result = By.cssSelector(elementLocator);
            break;
        default:
            result = By.name(elementLocator);
            break;
        }
        return result;
    }

    /**
     * executes a given Javascript file. Name or respectively path of script
     * must be set before within method {@code setExecuteScript()}.
     */
    private void executeScript() {
        try {
            logger.info("execute script begin {}", exeuteScript);
            BufferedReader br = new BufferedReader(new FileReader(exeuteScript));
            StringBuilder sb = new StringBuilder();
            while (br.ready()) {
                sb.append(br.readLine()).append("\n");
            }
            br.close();
            logger.info("execute script end {}", exeuteScript);
            ((JavascriptExecutor) driver).executeScript(sb.toString());
        } catch (IOException e) {
            logger.error("Can't read java script", e);
            throw new RuntimeException(e);
        }
    }

}
