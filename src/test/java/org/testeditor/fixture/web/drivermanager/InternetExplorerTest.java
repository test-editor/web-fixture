package org.testeditor.fixture.web.drivermanager;

import io.github.bonigarcia.wdm.BrowserManager;
import io.github.bonigarcia.wdm.InternetExplorerDriverManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import org.apache.commons.lang3.SystemUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

public class InternetExplorerTest {

    private WebDriver driver;

    private static String proxyHost;
    private static String proxyUser;
    private static String proxyPassword;
    private static String testSiteUrl;
    private static String searchfieldID;
    private static String searchString;
    private static String firstSiteTitle;
    private static String xPathResult;
    private static String resultSiteTitle;

    @BeforeClass
    public static void setupClass() throws IOException {
        assumeWindowsPresent();
        BrowserManager browserManager = InternetExplorerDriverManager.getInstance();
        browserManager.proxy(proxyHost);
        browserManager.proxyUser(proxyUser);
        browserManager.proxyPass(proxyPassword);
        browserManager.setup();
    }

    @Before
    public void setupTest() throws IOException {
        DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
        capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
        capabilities.setCapability("requireWindowFocus", true);

        driver = new InternetExplorerDriver(capabilities);
    }

    @After
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void test() throws InterruptedException {
        driver.get(testSiteUrl);
        String title = driver.getTitle();
        Assert.assertEquals(firstSiteTitle, title);
        WebElement webElement = driver.findElement(By.id(searchfieldID));
        webElement.sendKeys(searchString);
        webElement.sendKeys(Keys.ENTER);
        waitSeconds(1);
        webElement = driver.findElement(By.xpath(xPathResult));
        webElement.click();
        waitSeconds(1);
        title = driver.getTitle();
        Assert.assertEquals(resultSiteTitle, title);

    }

    public void waitSeconds(long timeToWait) throws InterruptedException {
        Thread.sleep(timeToWait * 1000);
    }

    /**
     * To execute these Tests it is necessary to provide a file named
     * "ie_test.properties" specified in the variable "testPropertyPath" below.
     * The content of the property file should contain following key value
     * pairs.
     * 
     * <pre>
     * PROXY_HOST=your.proxy.system.here
     * PROXY_USER=yourProxyUsernameHere
     * PROXY_PASSWORD=yourProxyPasswordHere
     * TEST_SITE_URL=yourTestSiteUrlHere
     * SEARCH_FIELD_ID=yourSearchFieldToTypeInHere
     * SEARCH_STRING=yourSearchStringHere
     * FIRST_SITE_TITLE=yourFirstWebsiteTitle
     * XPATH_RESULT=yourXpathEntryResult
     * RESULT_SITE_TITLE=yourWebsiteTitleAfterSearch
     * </pre>
     * 
     * @throws IOException
     */
    public static void assumeWindowsPresent() throws IOException {
        Assume.assumeTrue("This is not a Windows OS - ignoring test", SystemUtils.IS_OS_WINDOWS);
        String userHome = System.getProperty("user.home");
        String testPropertyPath = "/.m2/integrationsTest/ie_test.properties";

        Properties prop = new Properties();
        InputStream inputStream = null;

        inputStream = new FileInputStream(userHome + testPropertyPath);
        InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");

        prop.load(reader);

        proxyHost = prop.getProperty("PROXY_HOST");
        proxyUser = prop.getProperty("PROXY_USER");
        proxyPassword = prop.getProperty("PROXY_PASSWORD");
        xPathResult = prop.getProperty("XPATH_RESULT");
        searchfieldID = prop.getProperty("SEARCH_FIELD_ID");
        searchString = prop.getProperty("SEARCH_STRING");
        testSiteUrl = prop.getProperty("TEST_SITE_URL");
        firstSiteTitle = prop.getProperty("FIRST_SITE_TITLE");
        resultSiteTitle = prop.getProperty("RESULT_SITE_TITLE");

        Assert.assertNotNull(proxyHost);
        Assert.assertNotNull(proxyUser);
        Assert.assertNotNull(proxyPassword);
        Assert.assertNotNull(xPathResult);
        Assert.assertNotNull(searchfieldID);
        Assert.assertNotNull(searchString);
        Assert.assertNotNull(testSiteUrl);
        Assert.assertNotNull(firstSiteTitle);
        Assert.assertNotNull(resultSiteTitle);
    }
}
