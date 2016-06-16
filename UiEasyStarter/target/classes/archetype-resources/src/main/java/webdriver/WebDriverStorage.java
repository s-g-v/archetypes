#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package webdriver;

import org.openqa.selenium.WebDriver;

/**
 * It represents a browser. It contains name, version and platform fields.
 *
 * @author gsikorskiy
 */
public class WebDriverStorage {

	private static final ThreadLocal<WebDriver> DRIVER_STORAGE = new ThreadLocal<WebDriver>();

	private WebDriverStorage() { }
	
	public static void addDriver(final Browser browser) {
		WebDriverStorage.DRIVER_STORAGE.set(WebDriverFactory.getInstance(browser));
	}
	
	public static WebDriver getDriver() {
		return WebDriverStorage.DRIVER_STORAGE.get();
	}
}
