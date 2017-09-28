package org.testeditor.fixture.web.drivermanager;

import io.github.bonigarcia.wdm.BrowserManager;
import io.github.bonigarcia.wdm.InternetExplorerDriverManager;
import java.io.IOException;
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
    private static String ieFlakinessCapability;

    @BeforeClass
    public static void setupClass() throws IOException {
        Assume.assumeTrue("This is not a Windows OS - ignoring test", SystemUtils.IS_OS_WINDOWS);
        setupTestParameter();
        BrowserManager browserManager = InternetExplorerDriverManager.getInstance();
        if (proxyHost != null && proxyHost.isEmpty()) {
            browserManager.proxy(proxyHost);
            browserManager.proxyUser(proxyUser);
            browserManager.proxyPass(proxyPassword);
        }
        browserManager.setup();
    }

    @Before
    public void setupTest() throws IOException {
        DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
        if (ieFlakinessCapability != null && !ieFlakinessCapability.isEmpty()) {
            capabilities.setCapability(ieFlakinessCapability, true);
        }
        capabilities.setCapability("requireWindowFocus", true);

        // New fails if IE can not be started.
        driver = new InternetExplorerDriver(capabilities);
    }

    @After
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void smokeIntegrationTest() throws InterruptedException {

        // given (through class setup)

        // when
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

        // then
        Assert.assertEquals(resultSiteTitle, title);

    }

    public void waitSeconds(long timeToWait) throws InterruptedException {
        Thread.sleep(timeToWait * 1000);
    }

    /**
     * To execute these Tests it is necessary to provide a file named
     * "ie_test.properties" specified in the variable "testPropertyPath" below.
     * The content of the property file should contain following key value
     * pairs. See Wiki for "testing new web-fixure".
     * 
     * <pre>
     * PROXY_HOST=your.proxy.system.here
     * PROXY_USER=yourProxyUsernameHere
     * PROXY_PASSWORD=yourProxyPasswordHere
     * IE_FLAKINESS=quick-search-query
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
    public static void setupTestParameter() throws IOException {
        proxyHost = System.getenv("PROXY_HOST");
        proxyUser = System.getenv("PROXY_USER");
        proxyPassword = System.getenv("PROXY_PASSWORD");
        ieFlakinessCapability = System.getenv("IE_FLAKINESS");
        testSiteUrl = System.getenv("TEST_SITE_URL");
        searchfieldID = System.getenv("SEARCH_FIELD_ID");
        searchString = System.getenv("SEARCH_STRING");
        firstSiteTitle = System.getenv("FIRST_SITE_TITLE");
        xPathResult = System.getenv("XPATH_RESULT");
        resultSiteTitle = System.getenv("RESULT_SITE_TITLE");
    }
}
