package org.testeditor.fixture.web;

import java.io.IOException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * These tests can only executed when firefox is installed on System under Test
 * environment.
 *
 */
public class FireFoxTest {

	private static String proxyHost;
	private static String proxyUser;
	private static String proxyPassword;

	private WebDriverFixture driver;

	@Before
	public void setupTest() throws IOException {

		// This new may fail, if the driver fixture cannot properly connect to
		// the started browser instance resulting in driver == null
		driver = new WebDriverFixture();
	}

	@After
	public void teardown() {
		if (driver != null) {
			driver.closeBrowser();
		}
	}

	// This test is for local proxy based environment, umcomment and test if
	// possible.
	// @Test
	public void googleTestWithProxyCredentials() throws InterruptedException, IOException {

		// given

		// Make sure to set the following environment variables before
		// executing the tests.
		getProxyCredentials();

		driver.startBrowser("firefox");
		driver.goToUrl("https://google.de");
		driver.typeInto("q", LocatorStrategy.NAME, "Test-Editor");
		driver.submit("q", LocatorStrategy.NAME);

		// when
		driver.waitUntilElementFound("res", LocatorStrategy.ID, 2);
		String title = driver.getTitle();

		// then
		Assert.assertEquals("Test-Editor - Google-Suche", title);
	}

	@Test
	public void googleTestWithoutProxyCredentials() throws InterruptedException, IOException {

		// given
		driver.startBrowser("firefox");
		driver.goToUrl("https://google.de");
		driver.typeInto("q", LocatorStrategy.NAME, "Test-Editor");
		driver.submit("q", LocatorStrategy.NAME);

		// when
		driver.waitUntilElementFound("res", LocatorStrategy.ID, 2);
		String title = driver.getTitle();

		// then
		Assert.assertEquals("Test-Editor - Google-Suche", title);
	}

	// @Test
	public void googleTestWithBinary() throws InterruptedException, IOException {

		// given
		driver.startFireFoxPortable("c:/Users/u096310/AppData/Local/Firefox Developer Edition/firefox.exe");
		driver.goToUrl("https://google.de");
		driver.typeInto("q", LocatorStrategy.NAME, "Test-Editor");
		driver.submit("q", LocatorStrategy.NAME);

		// when
		driver.waitUntilElementFound("res", LocatorStrategy.ID, 2);
		String title = driver.getTitle();

		// then
		Assert.assertEquals("Test-Editor - Google-Suche", title);
	}

	private void getProxyCredentials() {
		proxyHost = System.getenv(WebDriverFixture.HTTP_PROXY_HOST);
		proxyUser = System.getenv(WebDriverFixture.HTTP_PROXY_USER);
		proxyPassword = System.getenv(WebDriverFixture.HTTP_PROXY_PASSWORD);
		Assert.assertNotNull(proxyHost);
		Assert.assertFalse(proxyHost.isEmpty());
		Assert.assertNotNull(proxyUser);
		Assert.assertFalse(proxyUser.isEmpty());
		Assert.assertNotNull(proxyPassword);
		Assert.assertFalse(proxyPassword.isEmpty());
	}

}
