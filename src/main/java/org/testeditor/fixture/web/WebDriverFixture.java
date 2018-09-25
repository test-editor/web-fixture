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

package org.testeditor.fixture.web;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.UnexpectedTagNameException;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testeditor.fixture.core.FixtureException;
import org.testeditor.fixture.core.MaskingString;
import org.testeditor.fixture.core.TestRunListener;
import org.testeditor.fixture.core.TestRunReportable;
import org.testeditor.fixture.core.TestRunReporter;
import org.testeditor.fixture.core.TestRunReporter.Action;
import org.testeditor.fixture.core.TestRunReporter.SemanticUnit;
import org.testeditor.fixture.core.TestRunReporter.Status;
import org.testeditor.fixture.core.artifacts.TestArtifact;
import org.testeditor.fixture.core.artifacts.TestArtifactRegistry;
import org.testeditor.fixture.core.interaction.FixtureMethod;
import org.testeditor.fixture.web.json.BrowserSetting;
import org.testeditor.fixture.web.json.BrowserSettingsManager;
import org.testeditor.fixture.web.json.BrowserSetupElement;

import com.google.gson.JsonObject;

import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.DriverManagerType;
import io.github.bonigarcia.wdm.FirefoxDriverManager;
import io.github.bonigarcia.wdm.InternetExplorerDriverManager;
import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * The {@code WebDriverFixture} class represents methods for automating
 * browser-actions like clicking on gui-widgets by means of a test-driver called
 * {@code WebDriver}.
 */
public class WebDriverFixture implements TestRunListener, TestRunReportable {

    // Proxy keys
    public static final String HTTP_PROXY_HOST = "http_proxyHost";
    public static final String HTTP_PROXY_PASSWORD = "http_proxyPassword";
    public static final String HTTP_PROXY_USER = "http_proxyUser";

    // Proxy values
    private static String httpProxyHost;
    private static String httpProxyPassword;
    private static String httpProxyUser;

    private WebDriver driver;
    private static final Logger logger = LoggerFactory.getLogger(WebDriverFixture.class);

    private String executeScript = null;
    
    private final Supplier<TestArtifactRegistry> testArtifactRegistrySupplier;
    
    public WebDriverFixture() {
        this(TestArtifactRegistry::getInstance);
    }
    
    public WebDriverFixture(Supplier<TestArtifactRegistry> testArtifactRegistry) {
        this.testArtifactRegistrySupplier = testArtifactRegistry;
    }    

    /**
     * specifies the time to wait (in seconds) before an action will be performed.
     * 
     * @param timeToWait
     * @throws InterruptedException
     */
    @FixtureMethod
    public void waitSeconds(long timeToWait) throws FixtureException {
        wrappedSleep(timeToWait * 1000, "wait interrupted", FixtureException.keyValues());
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
    public void setDriver(WebDriver driver) throws FixtureException {
        if (driver == null) {
            throw new FixtureException("WebDriver cannot be null", new IllegalArgumentException());
        } else {
            this.driver = driver;
        }
    }

    /**
     * Sets a String as a script-name for execution.
     * 
     * @param exec
     */
    @FixtureMethod
    public void setExecuteScript(String exec) throws FixtureException {
        executeScript = exec;
    }

    /**
     * Starts a specified browser. Following Strings are available:<br>
     * 
     * <ul>
     * <li><b>default</b> -opens a specific browser (On Windows -> Internet
     * Explorer, on others -> Firefox), but Firefox will only work for versions >
     * 55.0.3</li>
     * <li><b>firefox</b> - starts a locally installed firefox instance with
     * {@code GeckoDriver}</li>
     * <li><b>ie</b> - opens Microsoft Windows Internet Explorer</li>
     * <li><b>chrome</b> - opens Google Chrome</li>
     * </ul>
     * 
     * @param browser String literal for used browser
     * @return {@code WebDriver}
     */
    @FixtureMethod
    public WebDriver startBrowser(String browser) throws FixtureException {
        logger.info("Starting browser: {}", browser);
        switch (browser) {
            case "default":
                if (SystemUtils.IS_OS_WINDOWS) {
                    launchInternetExplorer();
                } else {
                    launchFirefox();
                }
                break;
            case "firefox":
                launchFirefox();
                break;
            case "ie":
                launchInternetExplorer();
                break;
            case "chrome":
                launchChrome();
                break;
            default:
                throw new FixtureException("Unknown browser.", FixtureException.keyValues("browser", browser));
        }
        configureDriver();
        return driver;
    }

    @Override
    public void initWithReporter(TestRunReporter reporter) {
        reporter.addListener(this);
    }

    @Override
    public void reported(SemanticUnit unit, Action action, String msg, String id, Status status,
            Map<String, String> variables) {
        if (unit == SemanticUnit.TEST && action == Action.ENTER) {
            runningTest = msg;
        }
        if (screenshotShouldBeMade(unit, action, msg)) {
            String screenshotFileName = makeScreenshot(msg + '.' + id);
            testArtifactRegistrySupplier.get().register(new TestArtifact("screenshot", screenshotFileName), id);
        }
    }

    @Override
    public void reportAssertionExit(AssertionError e) {
        makeScreenshot("ASSERTION-ERROR");
    }

    @Override
    public void reportExceptionExit(Exception e) {
        makeScreenshot("EXCEPTION");
    }

    @Override
    public void reportFixtureExit(FixtureException e) {
        makeScreenshot("FIXTURE-EXCEPTION");
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
        return (((action == Action.LEAVE) && unit == SemanticUnit.STEP) || unit == SemanticUnit.TEST) && driver != null;
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
     * filenameBase provided. The final filename is constructed using the testcase a
     * hash of the fixture itself and a shortened timestamp.
     * 
     * @param filenameBase user definable part of the final filename
     */
    @FixtureMethod
    public String screenshot(String filenameBase) throws FixtureException {
        return makeScreenshot(filenameBase);
    }

    protected String makeScreenshot(String filenameBase) {
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
     * starts Firefox Portable. This works for Versions < 47.0. preferably Firefox
     * ESR 45.4.0
     * 
     * @param browserPath
     * @return {@code WebDriver}
     */
    @FixtureMethod
    public WebDriver startFireFoxPortable(String browserPath) throws FixtureException {
        logger.info("Starting firefox portable: {}", browserPath);
        setupDrivermanager(FirefoxDriverManager.getInstance(DriverManagerType.FIREFOX));
        FirefoxBinary binary = new FirefoxBinary(new File(browserPath));
        FirefoxOptions options = new FirefoxOptions();
        options.setBinary(binary);
        FirefoxDriver firefoxdriver = new FirefoxDriver(options);
        driver = firefoxdriver;
        logBrowserVersion(options, firefoxdriver);
        return driver;
    }

    /**
     * launches Google Chrome with the ChromeDriverManager, this will be downloaded
     * automatically and will be saved in the directory
     * /~USER_HOME/m2/repository/webdriver/ in every according OS.
     * 
     * @throws FixtureException
     */
    private void launchChrome() throws FixtureException {
        setupDrivermanager(ChromeDriverManager.getInstance(DriverManagerType.CHROME));
        ChromeOptions options = populateBrowserSettingsForChrome();
        ChromeDriver chromeDriver = new ChromeDriver(options);
        driver = chromeDriver;
        logBrowserVersion(options, chromeDriver);
        registerShutdownHook(driver);
    }

    /**
     * launches Firefox with FirefoxDriverManager, this will be downloaded
     * automatically and will be saved in the directory
     * /~USER_HOME/m2/repository/webdriver/ in every according OS.
     * 
     * @throws FixtureException
     */
    private void launchFirefox() throws FixtureException {
        setupDrivermanager(FirefoxDriverManager.getInstance(DriverManagerType.FIREFOX));
        FirefoxOptions options = populateBrowserSettingsForFirefox();
        FirefoxDriver firefoxdriver = new FirefoxDriver(options);
        driver = firefoxdriver;
        logBrowserVersion(options, firefoxdriver);
        registerShutdownHook(driver);
    }

    /**
     * launches Windows Internet Explorer with IEDriverServerManager, this will be
     * downloaded automatically and will be saved in the directory
     * /~USER_HOME/m2/repository/webdriver/ in every according OS.
     * 
     * @throws FixtureException
     */
    private void launchInternetExplorer() throws FixtureException {
        setupDrivermanager(InternetExplorerDriverManager.getInstance(DriverManagerType.IEXPLORER));
        InternetExplorerOptions options = populateBrowserSettingsForInternetExplorer();
        InternetExplorerDriver iedriver = new InternetExplorerDriver(options);
        driver = iedriver;
        logBrowserVersion(options, iedriver);
        registerShutdownHook(driver);
    }
    
    private void logBrowserVersion(Capabilities capabilities, RemoteWebDriver remoteDriver) {
        String browserName = capabilities.getBrowserName();
        String version = remoteDriver.getCapabilities().getVersion();
        logger.info("*******************************************************************");
        logger.info("Browser \"{}\" with version \"{}\" successfully started ..." , browserName, version);
    }

    private ChromeOptions populateBrowserSettingsForChrome() throws FixtureException {
        List<BrowserSetting> options = new ArrayList<>();
        // specifying capabilities and options with the aid of the browser type.
        populateWithBrowserSpecificSettings(BrowserType.CHROME, options);
        ChromeOptions chromeOptions = new ChromeOptions();
        // Specific method because a ChromeOption is just a String like
        // "allow-outdated-plugins" or "load-extension=/path/to/unpacked_extension"
        populateChromeOption(options, chromeOptions);
        return chromeOptions;
    }

    private FirefoxOptions populateBrowserSettingsForFirefox() throws FixtureException {
        List<BrowserSetting> options = new ArrayList<>();
        // specifying capabilities and options with the aid of the browser type.
        populateWithBrowserSpecificSettings(BrowserType.FIREFOX, options);
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        // Specific method because Firefox Options consists of key value pairs
        // with different data types.
        populateFirefoxOption(options, firefoxOptions);
        return firefoxOptions;
    }

    private InternetExplorerOptions populateBrowserSettingsForInternetExplorer() throws FixtureException {
        List<BrowserSetting> options = new ArrayList<>();
        // specifying capabilities and options with the aid of the browser type.
        populateWithBrowserSpecificSettings(BrowserType.IE, options);
        // Specific method because an InternetExplorerOption is just a key value like
        // "ignoreProtectedModeSettings = true" or "requireWindowFocus = true"
        InternetExplorerOptions ieOptions = new InternetExplorerOptions();
        populateIeOptions(options, ieOptions);
        return ieOptions;
    }

    private void populateChromeOption(List<BrowserSetting> options, ChromeOptions chromeOptions) {
        if (options != null) {
            options.forEach((option) -> {
                chromeOptions.addArguments((String) option.getValue());
            });
        }
    }

    private void populateIeOptions(List<BrowserSetting> options, InternetExplorerOptions ieOptions) {
        if (options != null) {
            options.forEach((option) -> {
                ieOptions.setCapability(option.getKey(), option.getValue());
            });
        }
    }

    private void populateFirefoxOption(List<BrowserSetting> options, FirefoxOptions firefoxOptions) {
        if (options != null) {
            options.forEach((option) -> {
                Object value = option.getValue();
                switch (value.getClass().getSimpleName()) {
                    case "String":
                        firefoxOptions.addPreference(option.getKey(), (String) option.getValue());
                        break;

                    case "Integer":
                        firefoxOptions.addPreference(option.getKey(), (Integer) option.getValue());
                        break;

                    case "Boolean":
                        firefoxOptions.addPreference(option.getKey(), (Boolean) option.getValue());
                        break;

                    default:
                        logger.error("Only Strings, Integer or Boolean values are allowed for Option values.");
                        throw new RuntimeException(
                                "Only Strings, Integer or Boolean values are allowed for Option values but was: "
                                        + value.getClass().getSimpleName());

                }

            }

            );
        }
    }

    private void populateWithBrowserSpecificSettings(String browserName, List<BrowserSetting> options)
            throws FixtureException {
        BrowserSettingsManager manager = new BrowserSettingsManager();
        List<BrowserSetupElement> browserSettings = manager.getBrowserSettings();
        browserSettings.forEach((setting) -> {
            if (setting.getBrowserName().equalsIgnoreCase(browserName)) {
                options.addAll(setting.getOptions());
            }
        });

    }

    private void setupDrivermanager(WebDriverManager manager) {
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
     * manufacturer for using W3C WebDriver-compatible clients to interact with, we
     * need proxy credentials if "System under Test" is located behind a proxy.
     * Because of this reason there is a pleasant way to download the corresponding
     * WebDriver-Server with the help of the <a href=
     * "https://github.com/bonigarcia/webdrivermanager">WebDriverManager</a>
     * developed by Boni Garcia, which provides an ability performing a download in
     * an automated way. The proxy credentials should be provided as "System
     * Environment Variables" before the execution of tests.
     * <p>
     * 
     * There are 3 variables to be provided so far.<br>
     * 
     * Here is an example for naming the proxy variables e.g. on a linux system<br>
     * 
     * <pre>
     * 1.)  export http.proxyHost     = my.proxy.url
     * 2.)  export http.proxyPassword = myProxyPassword
     * 3.)  export http.proxyUser     = myProxyUser
     * </pre>
     * 
     * @param browserManager The specific BrowserManager of a browser.
     */
    private void settingProxyCredentials(WebDriverManager browserManager) {
        logger.debug("Proxy Credentials for Browsermanager (proxyHost: {} ; proxyUser: {})", httpProxyHost,
                httpProxyUser);
        browserManager.proxy(httpProxyHost);
        browserManager.proxyUser(httpProxyUser);
        browserManager.proxyPass(httpProxyPassword);
    }

    /**
     * ShutdownHook for teardown of started Browsermanager
     * 
     * @param driver Webdriver to be used
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
        logger.debug("Screen-Width: " + width + " Screen-Height: " + height);
        driver.manage().window().setSize(new Dimension(width, height));
    }

    private DisplayMode getDisplayMode() {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        return gd.getDisplayMode();
    }

    /**
     * opens a specified URL in the browser
     * 
     * @param url - an URL-String which represents the Web-Site to open in the
     *            browser. example: url = "http://www.google.com"
     * @throws FixtureException Exception occurs when browser can not open the given URL. 
     */
    @FixtureMethod
    public void goToUrl(String url) throws FixtureException {
        driver.get(url);
    }

    /**
     * close the Browser window
     */
    @FixtureMethod
    public void closeBrowser() throws FixtureException {
        // drive.close() leads to exception with Selenium V. 3.5.3 and Firefox
        // 55.0.3 preferred method -> just driver.quit();
        driver.quit();
        driver = null;
    }

    /**
     * This method waits explicitly for a WebElement in the DOM-Object until the
     * given timeOut is reached before an Exception is thrown.
     * 
     * @param elementLocator Locator for Gui-Widget
     * @param locatorType Type of locator for Gui-Widget
     * @param timeOutInSeconds The max timeout in seconds when an element is
     *            expected before a NotFoundException will be thrown.
     * @throws FixtureException Exception occurs when wait until an element is 
     *         found on a Web-Element can not be performed. 
     */
    @FixtureMethod
    public void waitUntilElementFound(String elementLocator, LocatorStrategy locatorType, int timeOutInSeconds)
            throws FixtureException {
        WebDriverWait webDriverWait = new WebDriverWait(driver, timeOutInSeconds);
        try {
            webDriverWait.until(ExpectedConditions.presenceOfElementLocated(getBy(elementLocator, locatorType)));
        } catch (TimeoutException e) {
            throw new FixtureException("timeout during wait for element", //
                    FixtureException.keyValues("elementLocator", elementLocator, //
                            "locatorType", locatorType.toString(), //
                            "timeout", Long.valueOf(timeOutInSeconds)),
                    e);
        }
    }

    /**
     * First empties the input field and then types given text into input fields on a specified Gui-Widget
     * 
     * @param elementLocator Locator for Gui-Widget
     * @param locatorType Type of locator for Gui-Widget
     * @param value String which is set into the textfield
     * @throws FixtureException Exception occurs when sendKeys can not be performed on a Web-Element. 
     */
    @FixtureMethod
    public void typeInto(String elementLocator, LocatorStrategy locatorType, String value) throws FixtureException {
        WebElement element = getWebElement(elementLocator, locatorType);
        try {
            element.clear();
            element.sendKeys(value);
        } catch (IllegalArgumentException e) {
            throw new FixtureException("string to be typed into element cannot be null", //
                    FixtureException.keyValues("elementLocator", elementLocator, //
                            "locatorType", locatorType.toString(), //
                            "value", value.toString()), //
                    e);
        }
    }
    
    /**
     * First empties the input field and then types given text obfuscated into input fields on a specified Gui-Widget.
     * The specialty about the text which is typed in is, that it will be obfuscated in log files
     * 
     * @param elementLocator Locator for Gui-Widget.
     * @param locatorType Type of locator for Gui-Widget.
     * @param value A masked String which is set into the textfield where the type is password.
     * @throws FixtureException Exception occurs when a secret text will be typed into a 
     *         web input field which HTML-Atrribute is not type="password". 
     */
    @FixtureMethod
    public void typeSecretInto(String elementLocator, LocatorStrategy locatorType, //
            MaskingString value) throws FixtureException {
        WebElement element = getWebElement(elementLocator, locatorType);
        if (element.getAttribute("type").equals("password")) {
            try {
                element.clear();
                element.sendKeys(value.get());
            } catch (IllegalArgumentException e) {
                throw new FixtureException("string to be typed into element cannot be null", //
                        FixtureException.keyValues("elementLocator", elementLocator, //
                                "locatorType", locatorType.toString()), 
                        e);
            }
            
        } else {
            throw new FixtureException("The searched Web-Element is not of type [password] further processing aborted."
                    + "Please prefer an input field of type password ", //
                    FixtureException.keyValues("elementLocator", elementLocator, //
                            "locatorType", locatorType.toString()));
        }
    }  
    
    /**
     * First empties the input field and then types given text not obfuscated into a non secure input field
     * on a specified Gui-Widget. The specialty about the text which is typed in is, 
     * that it will be not obfuscated in log files
     * 
     * @param elementLocator Locator for Gui-Widget as {@link String}.
     * @param locatorType Type of locator for Gui-Widget as enum {@link LocatorStrategy}.
     * @param value A masked {@link String} which is set into the textfield.
     * @throws FixtureException Exception occurs when sendKeys can not be performed on a Web-Element. 
     */
    @FixtureMethod
    public void typeSecretIntoUnsecureField(String elementLocator, LocatorStrategy locatorType, //
            MaskingString value) throws FixtureException {
        WebElement element = getWebElement(elementLocator, locatorType);
        try {
            element.clear();
            element.sendKeys(value.get());
        } catch (IllegalArgumentException e) {
            throw new FixtureException("string to be typed into element cannot be null", //
                        FixtureException.keyValues("elementLocator", elementLocator, //
                                "locatorType", locatorType.toString()), 
                        e);
        }
    }   
    
    /**
     * Clears the given input field.
     * 
     * @param elementLocator Locator for Gui-Widget
     * @param locatorType Type of locator for Gui-Widget
     * @throws FixtureException  Exception occurs when input field could not be emptied. 
     */
    @FixtureMethod
    public void clear(String elementLocator, LocatorStrategy locatorType) throws FixtureException {
        try {
            WebElement element = getWebElement(elementLocator, locatorType);
            element.clear();
        } catch (Exception e) {
            throw new FixtureException("Could not find Web Element: " + elementLocator , //
                    FixtureException.keyValues("elementLocator", 
                    elementLocator, "locatorType", locatorType.toString()), e);
        }
    } 
 

    /**
     * click on a specified Gui-Widget
     * 
     * @param elementLocator Locator for Gui-Widget
     * @param locatorType Type of locator for Gui-Widget
     */
    @FixtureMethod
    public void clickOn(String elementLocator, LocatorStrategy locatorType) throws FixtureException {
        WebElement element = getWebElement(elementLocator, locatorType);
        try {
            element.click();
        } catch (StaleElementReferenceException e) {
            throw new FixtureException("element to click seems to no longer exist", //
                    FixtureException.keyValues("elementLocator", elementLocator, //
                            "locatorType", locatorType.toString(), //
                            "element", element.toString(), //
                            "pageSource", getPageSource()),
                    e);
        }
    }

    /**
     * @return The title of the actual accessed html web site.
     */
    @FixtureMethod
    public String getTitle() throws FixtureException {
        return driver.getTitle();
    }

    /**
     * Submits WebElements like forms ore whole websites
     * 
     * @param elementLocator Locator for Gui-Widget
     * @param locatorType Type of locator for Gui-Widget
     */
    @FixtureMethod
    public void submit(String elementLocator, LocatorStrategy locatorType) throws FixtureException {
        WebElement element = getWebElement(elementLocator, locatorType);
        try {
            element.submit();
        } catch (NoSuchElementException e) {
            throw new FixtureException("element seems not to be part of a form and cannot be submitted", //
                    FixtureException.keyValues("elementLocator", elementLocator, //
                            "locatorType", locatorType.toString(), //
                            "element", element.toString(), //
                            "pageSource", getPageSource()),
                    e);
        }
    }

    /**
     * Returns the innerText content of an HTML-Element e.g. {@code <button>My Button</button>}
     * returns the text "My Button". If the value will not be found, a FixtureException will be thrown.
     * The expectation is: the value has to be available on the HTML site, because the user typed this value 
     * before this action.          
     * @param elementLocator Locator for Gui-Widget
     * @param locatorType Type of locator for Gui-Widget
     * @return innerText of a HTML-Element
     * @throws FixtureException
     */
    @FixtureMethod
    public String readValue(String elementLocator, LocatorStrategy locatorType) throws FixtureException {
        WebElement element = getWebElement(elementLocator, locatorType);
        String value = getValueOfWebElement(element);
        if (value == null) {
            throw new FixtureException("No value found for given locator " + elementLocator, //
                    FixtureException.keyValues("elementLocator", elementLocator, //
                            "locatorType", locatorType.toString(), //
                            "element", element.toString(), //
                            "value", value));
        }
        return value;
    }
    
    protected String getValueOfWebElement(WebElement element) {
        String value = null;
        String readValueThroughText = element.getText();
        // Selenium getText() vs. getAttribute("value") see https://sqa.stackexchange.com/questions/24463/selenium-webdriver-gettext-vs-getattribute
        if ((readValueThroughText != null) && (!readValueThroughText.isEmpty())) {
            value = readValueThroughText;
        } else { 
            String readValueThroughAttribute = element.getAttribute("value");
            if ((readValueThroughAttribute  != null) && (!readValueThroughAttribute.isEmpty())) {
                value = readValueThroughAttribute;
            } else {
                value = null;
            }
        }
        return value;
    }

    /**
     * 
     * @param elementLocator Locator for Gui-Widget
     * @param locatorType Type of locator for Gui-Widget
     * @param value to be selected in a Selectionbox
     * @throws InterruptedException
     */
    @FixtureMethod
    public void selectElementInSelection(String elementLocator, LocatorStrategy locatorType, String value)
            throws FixtureException {
        clickOn(elementLocator, locatorType);
        wrappedSleep(300, "sleep after click on element was interrupted", FixtureException.keyValues("elementLocator",
                elementLocator, "locatorType", locatorType.toString(), "value", value));
        WebElement element = getWebElement(elementLocator, locatorType);
        try {
            new Select(element).selectByVisibleText(value);
        } catch (NoSuchElementException e) {
            throw new FixtureException(
                    "element could not be selected by visible text (option not part of this selection)", //
                    FixtureException.keyValues("elementLoactor", elementLocator, //
                            "locatorType", locatorType.toString(), //
                            "value", value, "element", element.toString()),
                    e);
        } catch (UnexpectedTagNameException e) {
            throw new FixtureException("element expected to be tag SELECT", //
                    FixtureException.keyValues("elementLoactor", elementLocator, //
                            "locatorType", locatorType.toString(), //
                            "value", value, //
                            "element", element.toString()),
                    e);
        }
    }

    /**
     * 
     * @param elementLocator Locator for Gui-Widget
     * @param locatorType Type of locator for Gui-Widget
     * @return a JsonObject of the values in a Selectionbox
     * @throws InterruptedException
     */
    @FixtureMethod
    public JsonObject getOptionsInSelection(String elementLocator, LocatorStrategy locatorType)
            throws FixtureException {
        JsonObject availableOptions = new JsonObject();
        clickOn(elementLocator, locatorType);
        wrappedSleep(300, "sleep after get options in selection was interrupted",
                FixtureException.keyValues("elementLocator", elementLocator, "locatorType", locatorType.toString()));
        Select selection = new Select(getWebElement(elementLocator, locatorType));
        for (WebElement webElement : selection.getAllSelectedOptions()) {
            availableOptions.addProperty(webElement.getText(), webElement.getText());
        }
        return availableOptions;
    }

    /**
     * 
     * @param elementLocator Locator for Gui-Widget
     * @param locatorType Type of locator for Gui-Widget
     * @return true if a checkable Gui-Widget is checked, false otherwise.
     */
    @FixtureMethod
    public Boolean checkEnabled(String elementLocator, LocatorStrategy locatorType) throws FixtureException {
        WebElement element = getWebElement(elementLocator, locatorType);
        return element.isEnabled();
    }

    /**
     * @param elementLocator Locator for Gui-Widget
     * @param locatorType Type of locator for Gui-Widget
     * @return {@code WebElement} where the Locator String begins in a specific
     *         manner.
     */
    protected WebElement getWebElement(String elementLocator, LocatorStrategy locatorType) throws FixtureException {
        if (executeScript != null) {
            executeScript();
        }

        logger.info("Lookup element {} type {}", elementLocator, locatorType.name());
        WebElement result = null;
        try {
            result = driver.findElement(getBy(elementLocator, locatorType));
        } catch (NoSuchElementException exception) {
            throw new FixtureException("element could not be located on the current page", //
                    FixtureException.keyValues("elementLocator", elementLocator, //
                            "locatorType", locatorType.toString(), "pageSource", getPageSource(), "url",
                            driver.getCurrentUrl(), "title", driver.getTitle()));
        }
        return result;
    }

    private By getBy(String elementLocator, LocatorStrategy locatorType) {
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
     * executes a given Javascript file. Name or respectively path of script must be
     * set before using method {@code setExecuteScript()}.
     */
    private void executeScript() throws FixtureException {
        try {
            logger.info("execute script begin {}", executeScript);
            BufferedReader br = new BufferedReader(new FileReader(executeScript));
            StringBuilder sb = new StringBuilder();
            while (br.ready()) {
                sb.append(br.readLine()).append("\n");
            }
            br.close();
            logger.info("execute script end {}", executeScript);
            ((JavascriptExecutor) driver).executeScript(sb.toString());
        } catch (IOException e) {
            logger.error("Can't read java script", e);
            throw new FixtureException("could not execute javascript", //
                    FixtureException.keyValues("executeScript", executeScript), e);
        }
    }

    protected void wrappedSleep(long ms, String msg, Map<String, Object> keyValueMap) throws FixtureException {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            keyValueMap.put("timeout", Long.valueOf(ms));
            throw new FixtureException(msg, keyValueMap, e);
        }
    }
    
    /**
     * A special keyboard key is pressed.
     * 
     * @param specialKey
     *            the key to press (@see org.openqa.selenium.Keys)
     * @throws FixtureException
     *             if key is invalid
     */
    @FixtureMethod
    public void pressSpecialKey(String specialKey) 
            throws FixtureException {
        if (specialKey == null || specialKey.trim().isEmpty()) {
            throw new FixtureException("Invalid or empty key!");
        }

        try {
            Keys seleniumKey = Keys.valueOf(specialKey.trim().toUpperCase());
            new Actions(driver).sendKeys(seleniumKey).build().perform();
            logger.debug("press special key performed with key : {}", specialKey);
        } catch (IllegalArgumentException e) {
            
            throw new FixtureException("Key cannot be converted", 
                    FixtureException.keyValues("key", specialKey, "allowed-keys", join(Keys.values())), e);
        }
    }
    
    private String join(Keys[] keys) {
        ArrayList<Keys> allKeyentries = new ArrayList<>(Arrays.asList(keys));
        return allKeyentries.stream()
                 .map(n -> n.name())
                 .collect(Collectors.joining("\", \""));
    }

    /**
     * Checks if a specified text appears on a web site. Even just typed text artifacts 
     * which are currently inserted into the DOM. 
     * The expectation is: The user just wants to search for a specified text on a web site 
     * without an error being reported if the text is not found on the web site. With this
     * knowledge the user can decide what to do in the test. Using an assertion or just   
     * assigning the result of this method to a variable for later usage.
     * 
     * @param textTobeFound
     * @return true if searched text is found on a HTML Page and in DOM , false otherwise
     * @throws FixtureException
     */
    @FixtureMethod
    public boolean isTextOnPage(String textTobeFound) throws FixtureException {
        // First looking in the page source for fix Web Elements
        logger.debug("Text \"{}\" is searched on web page", textTobeFound);
        if (checkForConstantWebElements(textTobeFound)) {
            return true;
        }
        // Now check for dynamic Web Elements in DOM 
        return checkForDynamicWebElements(textTobeFound, getAllWebElementsOnPage());
    }

    protected List<WebElement> getAllWebElementsOnPage() {
        return driver.findElements(By.xpath("//*"));
    }

    protected boolean checkForConstantWebElements(String textTobeFound) {
        String pageSource = getPageSource();
        return pageSource.contains(textTobeFound); 
    }

    protected String getPageSource() {
        return driver.getPageSource();
    }

    protected boolean checkForDynamicWebElements(String textTobeFound, List<WebElement> elements) {
        boolean textOnpage = false;
        for (WebElement element :elements) {
            String valueOfWebElement = getValueOfWebElement(element);
            if (valueOfWebElement != null && valueOfWebElement.contains(textTobeFound)) {
                textOnpage = true;
                break;
            } 
        }
        return textOnpage;
    }
    
}
