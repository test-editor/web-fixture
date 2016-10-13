package org.testeditor.fixture.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class WebDriverFixtureTest  {
	
	private static final Logger logger = LoggerFactory.getLogger(WebDriverFixtureTest.class);
	private static final String PROPERTIES_FILE = "test.properties";
	private String proxyValue = null;
	private String proxyPort = null;
	private String proxyUserValue = null;
	private String proxyPasswordValue = null;
	private String proxyNonProxyValue = null;
	private String userName = null;
	private String passwd = null;
	private String url = null;
	
	/* 	Bitte diesen Test nicht loeschen 
	   	da die Entwicklung der Selenium Treiber für Firefox noch in der Betaphase ist und noch nicht final,
	   	moechte ich diesen Test dafuer nutzen, vorhandene Methoden weiterhin auf Funktionalitaet zu pruefen.  
	*/

	/**
	 * 	Dieser Test testet die vorhandene Funktionalitaet in der Konstellation <br>
	 *	<ul>
	 *    <li>Firefox Portable 45.2.0 ESR</li>
	 *	  <li>Selenium Webdriver Version 2.53.0</li>
	 * 	  <li>Webdrivermanager 1.4.4</li>
	 *    <li>Ngwebdriver 0.9.5</li>
	 *	  <li>ohne Geckodriver</li>
	 *  </ul>
	 *	
	 * @throws InterruptedException
	 */
	//@Test
	public void firefoxPortableStartAndStopTest() throws InterruptedException {

		String pathFirefoxPortable = "c:\\dev\\tools\\firefox\\FirefoxPortable\\firefox.exe";
				
		loadProperties();
		setProxyValuesForFirefox();
		WebDriverFixture fixture = new WebDriverFixture();
		
		fixture.startFireFoxPortable(pathFirefoxPortable);
		fixture.waitSeconds(2);
		fixture.gotToUrl(url);
		fixture.typeInto(userName, "test");
		fixture.typeInto(passwd, "test");
		fixture.waitSeconds(2);
		fixture.closeBrowser();
	}
	
	
	/**
	 * 	Dieser Test testet die vorhandene Funktionalitaet in der Konstellation <br>
	 *	<ul>
	 *    <li>Firefox Portable 49.0.1 </li>
	 *	  <li>Selenium Webdriver Version 3.00 beta4</li>
	 * 	  <li>Webdrivermanager 1.4.4</li>
	 *    <li>Ngwebdriver 0.9.5</li>
	 *	  <li>mit Geckodriver 0.11.1</li>
	 *  </ul>
	 *	
	 * @throws InterruptedException
	 */
	//@Test
	public void newFirefoxPortableStartAndStopTest() throws InterruptedException {
		
		String pathFirefoxPortable = "c:\\dev\\tools\\firefox\\FirefoxPortable_49.01\\FirefoxPortable\\firefox.exe";
		String pathGeckodriver = "c:\\dev\\tools\\firefox\\geckodriver.exe";
		
		// einkommentieren nur wenn mit Selenium Version ab 3.00 beta4 getetestet werden soll,
		// weil Firefox (ab 47) nur noch mit dem GeckoDriver (zur Zeit akt. Version 0.11.1) startet. 
		System.setProperty("webdriver.gecko.driver", pathGeckodriver);
		
		loadProperties();
		setProxyValuesForFirefox();
		WebDriverFixture fixture = new WebDriverFixture();
		
		fixture.startFireFoxPortable(pathFirefoxPortable);
		fixture.waitSeconds(2);
		fixture.gotToUrl(url);
		fixture.typeInto(userName, "test");
		fixture.typeInto(passwd, "test");
		fixture.waitSeconds(2);
		fixture.closeBrowser();
	}
	
	
	private void setProxyValuesForFirefox() {
		logger.info("setting proxy properties");
		String PROXY_HOST_HTTP_VALUE = proxyValue;
		String PROXY_PORT_HTTP_VALUE = proxyPort;
		String PROXY_HOST_HTTPS_VALUE = proxyValue;
		String PROXY_PORT_HTTPS_VALUE = proxyPort;	
		
		String PROXY_USER = "http.proxyUser";
		String PROXY_PASSWORD = "http.proxyPassword";
		String PROXY_HOST_HTTP = "http.proxyHost";
		String PROXY_PORT_HTTP = "http.proxyPort";
		String PROXY_HOST_HTTPS = "https.proxyHost";
		String PROXY_PORT_HTTPS = "https.proxyPort";
		String PROXY_NONPROXY_HTTP = "http.nonProxyHosts";

		// First set Proxy Settings
		
		System.setProperty(PROXY_USER, proxyUserValue);
		System.setProperty(PROXY_PASSWORD, proxyPasswordValue);
		System.setProperty(PROXY_HOST_HTTP, PROXY_HOST_HTTP_VALUE);
		System.setProperty(PROXY_PORT_HTTP, PROXY_PORT_HTTP_VALUE);
		System.setProperty(PROXY_HOST_HTTPS, PROXY_HOST_HTTPS_VALUE);
		System.setProperty(PROXY_PORT_HTTPS, PROXY_PORT_HTTPS_VALUE);
		System.setProperty(PROXY_NONPROXY_HTTP, proxyNonProxyValue);
	}
	
	
	private void loadProperties() {
		
		Properties prop = new Properties();
		InputStream input = null;
		logger.info("loading proxy properties from input file");
		try {

		      input = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE);


		    // load a properties file
		    prop.load(input);

		    // get the property values for these variables for testing firefox with proxy settings
//		    logger.info(prop.getProperty("PROXY_VALUE"));
//		    logger.info(prop.getProperty("PROXY_PORT"));
//		    logger.info(prop.getProperty("PROXY_USER"));
//		    logger.info(prop.getProperty("PROXY_PASSWORD"));
//		    logger.info(prop.getProperty("PROXY_NONPROXY"));
		    
		    // set properties
		    setProperties(prop);

		} catch (IOException ex) {
		    ex.printStackTrace();
		} finally {
		    if (input != null) {
		        try {
		            input.close();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		    }
		}	
	}
	
	private void setProperties(Properties prop ) {
		logger.info("reading proxy properties from input file");
		proxyValue = prop.getProperty("PROXY_VALUE");
		proxyPort = prop.getProperty("PROXY_PORT");
		proxyUserValue = prop.getProperty("PROXY_USER");
		proxyPasswordValue = prop.getProperty("PROXY_PASSWORD");
		proxyNonProxyValue = prop.getProperty("PROXY_NONPROXY");
		userName = prop.getProperty("USERNAME");
		passwd = prop.getProperty("PASSWORD");
		url = prop.getProperty("URL");
	}

}
