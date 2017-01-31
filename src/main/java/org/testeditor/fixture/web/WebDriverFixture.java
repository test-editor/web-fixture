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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.MarionetteDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testeditor.fixture.core.TestRunListener;
import org.testeditor.fixture.core.TestRunReportable;
import org.testeditor.fixture.core.TestRunReporter;
import org.testeditor.fixture.core.TestRunReporter.Action;
import org.testeditor.fixture.core.TestRunReporter.SemanticUnit;
import org.testeditor.fixture.core.interaction.FixtureMethod;

import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.InternetExplorerDriverManager;
import io.github.bonigarcia.wdm.MarionetteDriverManager;

/**
 * The {@code WebDriverFixture} class represents methods for automating 
 * browser-actions like clicking on gui-widgets by means of a test-driver 
 * called {@code WebDriver}.
 */
public class WebDriverFixture implements TestRunListener, TestRunReportable {

	public static final String HTTP_NON_PROXY_HOSTS = "http.nonProxyHosts";
	public static final String HTTP_PROXY_PORT = "http.proxyPort";
	public static final String HTTP_PROXY_HOST = "http.proxyHost";
	public static final String HTTP_PROXY_PASSWORD = "http.proxyPassword";
	public static final String HTTP_PROXY_USER = "http.proxyUser";
	public static final String HTTPS_PROXY_HOST = "https.proxyHost";
	public static final String HTTPS_PROXY_PORT = "https.proxyPort";
	private WebDriver driver;
	private static final Logger logger = LoggerFactory.getLogger(WebDriverFixture.class);
	
	private String exeuteScript = null;

	/**
	 * specifies the time to wait (in seconds) before an action will be performed.
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
	 * @param driver
	 */
	@FixtureMethod
	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

	/**
	 * sets a String as a script-name for execution.
	 * @param exec
	 */
	@FixtureMethod
	public void setExecuteScript(String exec) {
		exeuteScript = exec;
	}

	/**
	 * Starts a specified browser. 
	 * Following Strings are available:<br>
	 * 
	 * <ul>
     *    <li><b>default</b> -opens a specific browser (On Windows -> Internet Explorer, on others -> 
	 * 	      Firefox), but Firefox will only work for versions > 47.0.1</li>
     *    <li><b>firefox</b> - starts a locally  installed firefox instance without {@code MarionetteDriver}</li>
     *    <li><b>modernfirefox</b> - opens Firefox with {@code MarionetteDriver}</li>
     *    <li><b>ie</b> - opens Microsoft Windows Internet Explorer with</li>
     *    <li><b>chrome</b> - opens Google Chrome</li>
     * </ul>
	 * 
	 * <b>default</b> - opens a specific browser (On Windows -> Internet Explorer, on others -> 
	 * 	Firefox), but Firefox will only work for versions > 47.0.1 <br>
	 * <b>firefox</b> - starts a locally  installed firefox instance without {@code MarionetteDriver}<br>
	 * <b>modernfirefox</b> - opens Firefox with {@code MarionetteDriver} <br>
	 * <b>ie</b> - opens Microsoft Windows Internet Explorer<br>
	 * <b>chrome</b> - opens Google Chrome <br>
	 * 
	 * @param browser String literal for used browser 
	 * @return  {@code WebDriver}
	 */
	@FixtureMethod
	public WebDriver startBrowser(String browser) {
		logger.info("Starting browser: {}", browser);
		switch (browser) {
		case "default":
			if (SystemUtils.IS_OS_WINDOWS) {
				launchIE();
			} else {
				launchFirefoxMarionetteDriver();
			}
			break;
		case "firefox":
			launchFirefox();
			break;
		case "modernfirefox":
			launchFirefoxMarionetteDriver();
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
		StringBuffer finalFilenameBuffer = new StringBuffer();
		int lenOfFixedElements = timeStr.length() + additionalGraphicType.length() + 1/* hyphen */;
		finalFilenameBuffer //
				.append(getScreenshotPath()) //
				.append('/').append(reduceToMaxLen(testcase, SCREENSHOT_FILENAME_MAXLEN))//
				.append('/').append(timeStr).append('-')
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
	 * starts Firefox Portable.
	 * This works for Versions < 47.0. preferably Firefox ESR 45.4.0
	 * @param browserPath
	 * @return  {@code WebDriver}
	 */
	@FixtureMethod
	public WebDriver startFireFoxPortable(String browserPath) {
		logger.info("Starting firefox portable: {}", browserPath);
		FirefoxBinary binary = new FirefoxBinary(new File(browserPath));
		FirefoxProfile profile = getFireFoxProfile();
		driver = new FirefoxDriver(binary, profile);
		configureDriver();
		return driver;
	}

	/**
	 * launches Windows Internet Explorer with IEDriverServerManager
	 * must be downloaded and present locally before test execution. 
	 */
	private void launchIE() {
		InternetExplorerDriverManager.getInstance().setup();
		driver = new InternetExplorerDriver();
	}

	/**
	 * launches Google Chrome with the ChromeDriverManager
	 * must be downloaded and present locally before test execution.
	 */
	private void launchChrome() {
		ChromeDriverManager.getInstance().setup();
		driver = new ChromeDriver();
		registerShutdownHook(driver);
	}

	/**
	 * launches Firefox with the deprecated MarionetteDriverManager 
	 * (actual driver named geckodriver should be used) 
	 * must be downloaded and present locally before test execution.
	 */
	private void launchFirefoxMarionetteDriver() {
//		Authenticator.setDefault(new Authenticator() {
//	          @Override
//	         public PasswordAuthentication getPasswordAuthentication() {
//	               if(getRequestorType() == Authenticator.RequestorType.PROXY) 
//	                   return new PasswordAuthentication("your_proxy_username", "your_proxy_password".toCharArray());
//	               else
//	                  return super.getPasswordAuthentication();
//	         }});
		
		MarionetteDriverManager.getInstance().setup("v0.10.0");
		driver = new MarionetteDriver();
		registerShutdownHook(driver);
	}

	/**
	 * ShutdownHook for teardown of started Browsermanager 
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
	 * Specifies behavior of Browser.
	 * Prerequisite for a test to be executed is, that browsers are opened maximized
	 * to prevent failures.
	 */
	protected void configureDriver() {
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		//maximizing works for Firefox tested with FF <= 49.0.1
		driver.manage().window().maximize();
	}

	/**
	 * launches Firefox with a specific profile
	 */
	private void launchFirefox() {
		FirefoxProfile profile = getFireFoxProfile();
		driver = new FirefoxDriver(profile);
	}

	/**
	 * creates a new Profile for testing with proxy settings
	 * @return FirefoxProfile
	 */
	private FirefoxProfile getFireFoxProfile() {
		File profFile = new File(System.getProperty("java.io.tmpdir"), "selenium");
		if (profFile.exists()) {
			profFile.delete();
		}
		profFile.mkdir();
		logger.debug("Creating firefox profile in: {}", profFile);
		FirefoxProfile profile = new FirefoxProfile(profFile);
		profile.setAcceptUntrustedCertificates(true);
		profile.setAssumeUntrustedCertificateIssuer(false);
		profile.setPreference("security.mixed_content.block_active_content", false);
		profile.setPreference("security.mixed_content.block_display_content", true);
		
		logger.debug("proxyHost:\"" + System.getProperty(HTTP_PROXY_HOST) + "\"");
		logger.debug("proxyPort:\"" + System.getProperty(HTTP_PROXY_PORT) + "\"");
		logger.debug("nonProxyHosts:\"" + System.getProperty(HTTP_NON_PROXY_HOSTS) + "\"");
		
		if (System.getProperty(HTTP_PROXY_HOST) != null) {
			logger.info("Setting up proxy: {} on port {} with nonProxyHosts {} ", System.getProperty(HTTP_PROXY_HOST), System.getProperty(HTTP_PROXY_PORT), System.getProperty(HTTP_NON_PROXY_HOSTS) );
			profile.setPreference("network.proxy.type", 1);
			profile.setPreference("network.proxy.user_name", System.getProperty(HTTP_PROXY_USER));
			profile.setPreference("network.proxy.password", System.getProperty(HTTP_PROXY_PASSWORD));
			profile.setPreference("network.proxy.http", System.getProperty(HTTP_PROXY_HOST));
			profile.setPreference("network.proxy.http_port", System.getProperty(HTTP_PROXY_PORT));
			profile.setPreference("network.proxy.ssl", System.getProperty(HTTPS_PROXY_HOST));
			profile.setPreference("network.proxy.ssl_port", System.getProperty(HTTPS_PROXY_PORT));
			profile.setPreference("network.proxy.no_proxies_on", System.getProperty(HTTP_NON_PROXY_HOSTS));
		}
		return profile;
	}

	/**
	 * opens a specified URL in the browser
	 * @param url - an URL-String which represents the Web-Site to open in the browser.
	 * example: url = "http://www.google.com"
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
		driver.close();
	}

	/**
	 * press enter on a specified Gui-Widget
	 * @param elementLocator Locator for Gui-Widget
	 */
	@FixtureMethod
	public void pressEnterOn(String elementLocator) {
		WebElement element = getWebElement(elementLocator);
		element.submit();
	}

	/**
	 * type into text fields on a specified Gui-Widget
	 * @param elementLocator Locator for Gui-Widget
	 * @param value String which is set into the textfield
	 */
	@FixtureMethod
	public void typeInto(String elementLocator, String value) {
		WebElement element = getWebElement(elementLocator);
		element.sendKeys(value);
	}

	/**
	 * empties the textfield 
	 * @param elementLocator Locator for Gui-Widget
	 */
	@FixtureMethod
	public void clear(String elementLocator) {
		WebElement element = getWebElement(elementLocator);
		element.clear();
	}

	/**
	 * click on a specified Gui-Widget
	 * @param elementLocator Locator for Gui-Widget
	 */
	@FixtureMethod
	public void clickOn(String elementLocator) {
		WebElement element = getWebElement(elementLocator);
		element.click();
	}

	/**
	 * 
	 * @param elementLocator  Locator for Gui-Widget
	 * @return value of a label
	 */
	@FixtureMethod
	public String readValue(String elementLocator) {
		WebElement element = getWebElement(elementLocator);
		return element.getText();
	}
	
	/**
	 * 
	 * @param elementLocator  Locator for Gui-Widget
	 * @param value to be selected in a Selectionbox
	 * @throws InterruptedException
	 */
	@FixtureMethod
	public void selectElementInSelection(String elementLocator, String value) throws InterruptedException{
		clickOn(elementLocator);
		Thread.sleep(300);
		WebElement element = getWebElement(elementLocator);
	    new Select(element).selectByVisibleText(value);
		//logger.trace("Selected value {} in selection {}", value, elementLocator);
	}
	
	/**
	 * 
	 * @param elementLocator Locator for Gui-Widget
	 * @return a Map of the values in a Selectionbox
	 * @throws InterruptedException
	 */
	@FixtureMethod
	public Map<String, String>  getOptionsInSelection(String elementLocator) throws InterruptedException {
		clickOn(elementLocator);
		Thread.sleep(300);
		Map<String, String> namesOfAllSelectedOptions = new HashMap<String, String>();
		Select selection = new Select(getWebElement(elementLocator));
		List<WebElement> allSelectedOptions;allSelectedOptions = selection.getAllSelectedOptions();
		for (WebElement webElement : allSelectedOptions) {
			namesOfAllSelectedOptions.put(webElement.getText(), webElement.getText());
		}
		return namesOfAllSelectedOptions;
	}
	
	
	/**
	 * 
	 * @param elementLoacator Locator for Gui-Widget
	 * @return true if a checkable Gui-Widget is checked, false otherwise.
	 */
	@FixtureMethod
	public Boolean checkEnabled(String elementLoacator) {
		WebElement element = getWebElement(elementLoacator);
		return element.isEnabled();
	}

	/**
	 * @param elementLocator Locator for Gui-Widget
	 * @return {@code WebElement} where the Locator String begins in a specific manner. 
	 */
	protected WebElement getWebElement(String elementLocator) {
		if (exeuteScript != null) {
			executeScript();
		}
		logger.info("Lookup element {}", elementLocator);
		WebElement result = null;
		if (elementLocator.startsWith("[xpath]")) {
			result = driver.findElement(By.xpath(extractLocatorStringFrom(elementLocator)));
		}
		if (elementLocator.startsWith("[name]")) {
			result = driver.findElement(By.name(extractLocatorStringFrom(elementLocator)));
		}
		if (elementLocator.startsWith("[link]")) {
			result = driver.findElement(By.linkText(extractLocatorStringFrom(elementLocator)));
		}
		if (elementLocator.startsWith("[id]")) {
			result = driver.findElement(By.id(extractLocatorStringFrom(elementLocator)));
		}
		if (elementLocator.startsWith("[css]")) {
			result = driver.findElement(By.cssSelector(extractLocatorStringFrom(elementLocator)));
		}
		if (result == null) {
			result = driver.findElement(By.name(elementLocator));
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

	/**
	 * extracts the Locator
	 * @param elementLocator Locator for Gui-Widget
	 * @return substring of given elementLocator to the first index of an "]"
	 */
	protected String extractLocatorStringFrom(String elementLocator) {
		return elementLocator.substring(elementLocator.indexOf(']') + 1);
	}
	
}
