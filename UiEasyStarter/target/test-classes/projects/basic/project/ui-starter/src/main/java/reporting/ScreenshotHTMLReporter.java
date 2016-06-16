package reporting;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.VelocityContext;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.internal.IResultListener;
import org.uncommons.reportng.HTMLReporter;

import webdriver.WebDriverFactory;

import common.Environment;

/**
 * Enhanced HTML reporter with supporting screenshots after test fails.
 * @author gsikorskiy
 *
 */
public class ScreenshotHTMLReporter extends HTMLReporter implements	IResultListener {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * Default screenshot extension.
	 */
	public static final String FILE_EXTENSION = ".png";

	@Override
	protected final VelocityContext createContext() {
		final VelocityContext context = super.createContext();
		return context;
	}

	@Override
	public final void onTestFailure(final ITestResult result) {
		if (Environment.MAKE_SCREENSHOT) {
			this.log.debug("Make screenshot on test failure.");
			this.makeScreenshot(result);
		}
	}

	@Override
	public void onTestStart(final ITestResult result) {
	}

	@Override
	public void onTestSuccess(final ITestResult result) {
	}

	@Override
	public void onTestSkipped(final ITestResult result) {
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(final ITestResult result) {
	}

	@Override
	public void onStart(final ITestContext context) {
	}

	@Override
	public void onFinish(final ITestContext context) {
	}

	@Override
	public void onConfigurationSuccess(final ITestResult result) {
	}

	@Override
	public void onConfigurationFailure(final ITestResult result) {
	}

	@Override
	public void onConfigurationSkip(final ITestResult result) {
	}

	private void makeScreenshot(final ITestResult tr) {
		final String outputDir = this.compileScreenshotFolder(tr);
		this.log.debug("Screenshot will be saved in " + outputDir);

		final WebDriver driver = (WebDriver) tr.getTestContext()
				.getAttribute(WebDriverFactory.DRIVER_ATTRIBUTE_NAME);
		final String fileName = this.compileScreenshotName(tr, driver).replace(" ", "_");

		try {
			final File f = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			final File saved = new File(outputDir, fileName);
			FileUtils.copyFile(f, saved);
			CommonReporter.printScreenshot("Screenshot for " + tr.getName() + " "
					+ driver.getCurrentUrl(), saved);
		} catch (final IOException e) {
			this.log.error("Cann't copy screenshot to target folder.");
			e.printStackTrace();
		}
	}

	private String compileScreenshotFolder(final ITestResult tr) {
		final StringBuilder dir = new StringBuilder();
		dir.append(Environment.SCREENSHOT_PATH + "/");
		dir.append(tr.getTestContext().getName() + "/");
		return dir.toString();
	}

	private String compileScreenshotName(final ITestResult tr, final WebDriver driver) {
		final StringBuilder fileName = new StringBuilder();
		//TODO Enable when screenshots should be unique
//		final SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss");
//		final String timestamp = dateFormatter.format(new Date());
//		fileName.append(timestamp + " ");
		fileName.append(tr.getMethod().getMethodName() + " ");
		fileName.append(this.extractBrowserName(driver));
		fileName.append(FILE_EXTENSION);
		return fileName.toString();
	}

	private String extractBrowserName(final WebDriver driver) {
		final int beginIndex = driver.toString().indexOf(":") + 1;
		final int endIndex = driver.toString().indexOf("(");
		final String browser = driver.toString().substring(beginIndex, endIndex);
		return browser;
	}
}
