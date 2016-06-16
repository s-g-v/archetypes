package pages;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import common.Environment;
import reporting.CommonReporter;
import webdriver.WebDriverStorage;

/**
 * Abstract representation of a Page in the UI. Page object pattern
 *
 * @author gsikorskiy
 */
public abstract class Page {

	private static final Logger log = LoggerFactory.getLogger(Page.class);
	private final WebDriver webDriver;

	/**
	 * Constructor injecting the WebDriver interface.
	 * @param webDriver - constructor should have webDriver parameter for PageFactory.initElements.
	 */
	public Page(final WebDriver webDriver) {
		this.webDriver = webDriver;
	}

	/**
	 * Wrapper to open pages in tests.
	 * @param <E> - heir of Page class
	 * @param url - address of page.
	 * @param page - Page object class.
	 * @return page with initialized web elements
	 */
	public static final <E extends Page> E open(String url, final Class<E> page) {
		log.info("Open page: " + url);
		WebDriver wd = WebDriverStorage.getDriver();
		wd.get(url);
		final E result = PageFactory.initElements(wd, page);
		if (!wd.getCurrentUrl().contains(url)) {
			throw new RuntimeException("Expected url: " + url + "\nBut opened: " + wd.getCurrentUrl());
		}
		return result;
	}

	/**
	 * Allows to open pages that have URL static field in PageObject.class
	 * @param <E> - heir of Page class
	 * @param page - Page object class.
	 * @return page with initialized web elements
	 */
	public static final <E extends Page> E open(final Class<E> page) {
		return open(getStaticUrl(page).get(), page);
	}
	
	/**
	 * Method to take current page screenshot.
	 * @return - file to save.
	 */
	public final File takeScreenshot() {
		return ((TakesScreenshot) this.webDriver).getScreenshotAs(OutputType.FILE);
	}

	/**
	 * Saves screenshot to file @param name.
	 * @param name - filename or full path.
	 */
	public final void saveScreenshot(final String name) {
		final File saved = (name.contains(File.separator))
				? new File(name)
				: new File(Environment.SCREENSHOT_PATH, name);
		try {
			FileUtils.copyFile(this.takeScreenshot(), saved);
			CommonReporter.printScreenshot("Saved screenshot: ", saved);
		} catch (final IOException e) {
			throw new RuntimeException("Can't save screenshot for the page: "
					+ this.webDriver.getTitle(), e);
		}
	}

	/**
	 * Describes ExpectedCondition when element with locator can have different texts.
	 * @param by - locator of element.
	 * @param text - list of possible texts, written on element.
	 * @return ExpectedCondition for WebDriverWait.
	 */
	protected final ExpectedCondition<WebElement> anyTextToBePresentInElementLocated(final By by,
			final List<String> text) {

		return new ExpectedCondition<WebElement>() {
			@Override
			public WebElement apply(final WebDriver driver) {
				try {
					driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
					final List<WebElement> candidates = driver.findElements(by);
					for (final WebElement item : candidates) {
						for (final String msg : text) {
							Page.this.log.debug("Looking for " + msg);
							if (item.isDisplayed() && item.getText().contains(msg)) {
								return item;
							}
						}
					}
					return null;
				} catch (final StaleElementReferenceException e) {
					return this.apply(driver);
				} finally {
					driver.manage().timeouts().
					implicitlyWait(Environment.TIMEOUT, TimeUnit.SECONDS);
				}

			}
		};
	}

	/**
	 * Searches element if we know its locator and several possible texts on it.
	 * Timeout for search is default framework timeout.
	 * @param by - locator to search.
	 * @param messages - possible text messages.
	 * @return - WebElement or appropriate exception if element not found.
	 */
	protected final WebElement findElementWithAnyMessage(final By by, final List<String> messages) {
		return new WebDriverWait(this.webDriver, Environment.TIMEOUT).
				withMessage("Couldn't find element by \"" + by + "\" with any text from " + messages).
				until(ExpectedConditions.refreshed(
								this.anyTextToBePresentInElementLocated(by, messages)));
	}

	/**
	 * Allows to get displayed element from the list of elemnents,
	 * found by one locator.
	 * @param items - list of element with same locator.
	 * @return - displayed element
	 */
	protected final WebElement getDisplayed(final List<WebElement> items) {
		WebElement result = null;
		for (final WebElement item : items) {
			if (item.isDisplayed()) {
				result = item;
				break;
			}
		}
		if (result == null) {
			throw new StaleElementReferenceException("Couldn't find displayed element in the list.");
		}
		return result;
	}
	
	private static <T> Optional<String> getStaticUrl(final Class<T> page) {
		String url = null;
		try {
			url = (String) page.getDeclaredField("URL").get(null);
		} catch (IllegalArgumentException | IllegalAccessException
				| NoSuchFieldException | SecurityException e) {
			log.error("Page object " + page.getSimpleName() + " doesn't contain public static field URL.\n"
					+ "Try to open page by open(url, page.class).");
		}
		return Optional.of(url);
	}
}
