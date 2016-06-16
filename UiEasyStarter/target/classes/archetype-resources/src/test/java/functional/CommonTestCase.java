#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package functional;

import static reporting.CommonReporter.stepDescription;

import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import webdriver.Browser;
import webdriver.BrowserName;
import webdriver.WebDriverFactory;
import webdriver.WebDriverStorage;

/**
 * Methods, that are common for all tests.
 * @author gsikorskiy
 *
 */
public class CommonTestCase {

	/**
	 * Method to initialize driver before test. It uses TestNG @Parameters feature.
	 * @param browserName - name of browser to test site. Default is Firefox.
	 * @param browserVersion - version of browser to test site. Default is not assigned.
	 * @param platform - platform to test site. Default if Linux.
	 */
	@Parameters({"BrowserName", "Version", "Platform"})
	@BeforeTest
	protected final void initDriver(@Optional("FIREFOX") final BrowserName browserName,
						   @Optional("") final String browserVersion,
						   @Optional("LINUX") final Platform platform) {

		final Browser browser = new Browser(browserName);
		browser.setVersion(browserVersion);
		browser.setPlatform(platform.name());

		WebDriverStorage.addDriver(browser);
	}

	/**
	 * Adds webdriver to current test context.
	 * @param context - test context.
	 */
	@BeforeClass(alwaysRun = true)
	protected final void setUpDriverToContext(final ITestContext context) {
		context.setAttribute(WebDriverFactory.DRIVER_ATTRIBUTE_NAME, this.getWebDriver());
	}

	/**
	 * Close browser after test.
	 */
	@AfterTest(alwaysRun = true)
	protected final void tearDown() {
		stepDescription("Cleaning up after test.");
		this.getWebDriver().quit();
	}

	private WebDriver getWebDriver() {
		return WebDriverStorage.getDriver();
	}
}
