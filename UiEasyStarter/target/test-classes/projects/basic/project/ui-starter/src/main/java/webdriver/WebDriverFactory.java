package webdriver;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;

import common.Environment;

/**
 * Factory to instantiate a WebDriver object.
 * It returns an instance of the driver (local invocation) or
 * an instance of RemoteWebDriver
 *
 * @author gsikorskiy
 */
public final class WebDriverFactory {

//	private static Logger log = LoggerFactory.getLogger(WebDriverFactory.class);


	/**
	 * Name of attribute to add to context.
	 */
	public static final String DRIVER_ATTRIBUTE_NAME = "driver";

    private WebDriverFactory() {
    }


	/**
	 * Factory method to return a RemoteWebDriver instance given the url of the
	 * Grid hub and a Browser instance.
	 *
	 * @param browser : Browser object containing info around the browser to hit
	 *
	 * @return RemoteWebDriver
	 */
	public static WebDriver getInstance(final Browser browser) {

		WebDriver webDriver = null;

		final BrowserName browserName = browser.getName();
		DesiredCapabilities capability = new DesiredCapabilities();
		capability.setJavascriptEnabled(true);
		capability.setCapability(CapabilityType.TAKES_SCREENSHOT, true);

		if (!Environment.GRID_HUB_URL.isPresent()) {
			return getInstanceOnLocalhost(browserName);
		}

		switch (browserName) {
		case CHROME:
			capability = DesiredCapabilities.chrome();
			capability.setCapability("chrome.switches", Arrays.asList("--ignore-certificate-errors"));
			break;
		case FIREFOX:
			final FirefoxProfile profile = new FirefoxProfile();
			profile.setEnableNativeEvents(false);
            profile.setAssumeUntrustedCertificateIssuer(false);
            capability = DesiredCapabilities.firefox();
            capability.setCapability(FirefoxDriver.PROFILE, profile);
			break;
		case IE:
			capability = DesiredCapabilities.internetExplorer();
			capability
			.setCapability(
					InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,
					true);
			break;
		case OPERA:
			capability = DesiredCapabilities.operaBlink();
			break;
		case SAFARI:
			capability = DesiredCapabilities.safari();
			break;
		case ANDROID:
			capability = DesiredCapabilities.android();
			break;
		case IPHONE:
			capability = DesiredCapabilities.iphone();
			break;
		default:
			capability = DesiredCapabilities.htmlUnit();
		}

		capability = setVersionAndPlatform(capability, browser.getVersion(),
				browser.getPlatform());

		// Create Remote WebDriver
		webDriver = new RemoteWebDriver(Environment.GRID_HUB_URL.get(), capability);

		webDriver.manage().timeouts().implicitlyWait(Environment.TIMEOUT, TimeUnit.SECONDS);
		webDriver.manage().timeouts().pageLoadTimeout(Environment.TIMEOUT, TimeUnit.SECONDS);
		return webDriver;
	}


	/**
	 * Factory method to return a WebDriver instance given the browser to hit.
	 *
	 * @param browser : String representing the local browser to hit
	 *
	 * @return WebDriver instance
	 */
	public static WebDriver getInstanceOnLocalhost(final BrowserName browser) {

		WebDriver webDriver = null;

		switch (browser) {
		case CHROME:
			setChromeDriver();
			webDriver = new ChromeDriver();
			break;
		case FIREFOX:
			final FirefoxProfile profile = new FirefoxProfile();
			profile.setEnableNativeEvents(false);
            profile.setAssumeUntrustedCertificateIssuer(false);
			webDriver = new FirefoxDriver(profile);
			break;
//		case OPERA:
//			webDriver = new OperaDriver();
//			break;
		case IE:
            isSupportedPlatform(browser);
			webDriver = new InternetExplorerDriver();
			break;
		case SAFARI:
            isSupportedPlatform(browser);
            webDriver = new SafariDriver();
            break;
        default:
        	webDriver = new HtmlUnitDriver(true);
		}
		webDriver.manage().timeouts().implicitlyWait(Environment.TIMEOUT, TimeUnit.SECONDS);
		return webDriver;
	}

	/**
	 * Helper method to set version and platform for a specific browser.
	 * @param capability : DesiredCapabilities object coming from the selected
	 * browser
	 * @param version : browser version
	 * @param platform : browser platform
	 * @return DesiredCapabilities
	 */
	private static DesiredCapabilities setVersionAndPlatform(
			final DesiredCapabilities capability,
			final String version,
			final String platform) {

		capability.setPlatform(Platform.ANY);
		for (final Platform isThis : Platform.values()) {
			if (isThis.name().equalsIgnoreCase(platform)) {
				capability.setPlatform(isThis);
			}
		}
		if (version != null) {
			capability.setVersion(version);
		}
		return capability;
	}

	/**
	 * Helper method to set ChromeDriver location into the right sytstem property.
	 */
	private static void setChromeDriver() {
		final Platform current = Platform.getCurrent();
		final String chromeBinary = "src/main/resources/drivers/chrome/chromedriver"
				+ (Platform.WINDOWS.is(current) ? ".exe" : "");
		System.setProperty("webdriver.chrome.driver", chromeBinary);
	}

    private static void isSupportedPlatform(final BrowserName browser) {
        boolean isSupported = true;
        final Platform current = Platform.getCurrent();
        if (browser.equals(BrowserName.IE)) {
            isSupported = Platform.WINDOWS.is(current);
        } else if (browser.equals(BrowserName.SAFARI)) {
            isSupported = Platform.MAC.is(current) || Platform.WINDOWS.is(current);
        }
        assert isSupported : "Platform is not supported by " + browser + " browser";
    }
}
