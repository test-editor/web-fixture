# Browser Setup for Testing
It is possible to activate browser-specific settings before test execution.  
E.g. when a special proxy setting is needed for Firefox on a System Under Test (SUT) environment under Linux which is different to the proxy setting under Windows, it is possible to create a JSON-File called **browserSetup.json** in the directory **src/test/resources** of the actual test project. These specified settings will be used for test execution.

## Documentation for Options
For [Firefox Options](https://seleniumhq.github.io/selenium/docs/api/rb/Selenium/WebDriver/Firefox/Options.html)
there is also the possibility to configure all browser preferences with the help of options, just enter the key and the value for the needed preference in the options section and there you go. 

## Documentation for Capabilities
See [DesiredCapabilities](https://github.com/SeleniumHQ/selenium/wiki/DesiredCapabilities)


## Example structure of the JSON-File   
![BrowserSetup](/images/browserSetup.png)

### Browser Setup Elements
The **browserSetup.json** file consists of several different browser setup elements marked blue in the screenshot.  
See  
![allBrowserSetupElements](/images/allBrowserSetupElements.png)

  
Each element consists of    
* an arbitrarily chosen element name  
![elementName](/images/elementName.png)
* an optional browser setting entry for the platform  
![osName](/images/osEntry.png) 
* an optional browser setting entry for the browser to test   
![browserEntry](/images/browserEntry.png)  
* an optional browser setting entry for options   
![options](/images/options.png)       


### Browser Settings
#### Key "os"  
When a test should run with OS-specific settings the key for "os" must be set. For the specific values please refer to the OS-specific values on the Selenium web site under [DesiredCapabilities].(https://github.com/SeleniumHQ/selenium/wiki/DesiredCapabilities)  
When a test should run without OS-specific settings, the specified "Browser Settings" under this "Browser Setup Element" is valid for all OS (currently WINDOWS, LINUX, MAC). It is also possible to mix both options for os-specific and not os-specific. An error will occur during a test on a given operating system, if duplicate entries for the same key exist. In the screenshot we have an example for setting the SSL proxy URL and the SSL proxy port for all OS (see screenshot BrowserSetup-Element "firefox-section-Without-os")  
![BrowserSetup-Element](/images/allBrowserSetupElements.png)  
and for the Linux OS the proxy type varies (1 instead of 5 see screenshot proxyType) in contrast to Windows OS proxy type.  
![proxyType](/images/proxyType.png) 
  
#### Key "browser"
When a test should run with browser-specific settings the key for "browser" must be set.  
For the specific browser names, please refer to the Selenium web site under [DesiredCapabilities](https://github.com/SeleniumHQ/selenium/wiki/DesiredCapabilities)  
When no browser entry is specified, that means all options and capabilities are defined for all browser vendors.  
 
#### Key "options"
When a test should run with browser-specific option settings the key for "options" must be set.  
For the browser-specific values please refer to the particular browser vendor. 


#### Key "capabilities"
Capabilities existed in the past but were dropped in favor of options. Every Capability can now be specified as an Option see above.  

Capabilities are deprecated and will be removed in Selenium-Java-API later.  see [Capabilities](https://github.com/seleniumhq/selenium/commit/a4ac624ec94d70834198009e50491e4dca4e841b) 
