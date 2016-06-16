package reporting;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Reporter;

/**
 * Class contains simple methods to write information to test report
 * with using different styles and colors.
 * @author gsikorskiy
 *
 */
public final class CommonReporter {
	private static int stepId = 0;
	private static Logger log = LoggerFactory.getLogger(CommonReporter.class);

	private CommonReporter() {
	}

	/**
	 * Allows to write test method description to report.
	 * Test method description will be written in blue color with using expand lists.
	 * @param descr - description of test method
	 */
	public static void methodDescription(final String descr) {
		reportDropdownBlock(descr, ReportColor.METHOD.getColor()); //Blue color
	}

	/**
	 * Allows to write test step description to report.
	 * Test step description will be written in black color with using expand lists.
	 * @param descr - description of test step
	 */
	public static void stepDescription(final String descr) {
		reportDropdownBlock(descr, ReportColor.STEP.getColor()); //Almost black color
	}

	/**
	 * Allows to write test info description to report.
	 * Simple test information to write to report
	 * @param descr - info to add to report
	 */
	public static void printInfo(final String descr) {
		log.info(descr);
	}

	/**
	 * Allows to add pictures to report.
	 * @param descr - info to add to report
	 * @param picture - file with image
	 */
	public static void printScreenshot(final String descr, final File picture) {
		Reporter.log(descr + "<br><img src=\"" + picture.getAbsolutePath() + "\">");
	}

	private static void reportDropdownBlock(final String text,
			final String color) {
		Reporter.setEscapeHtml(false);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd_HH:mm:ss:SSS");
		String timestamp = simpleDateFormat.format(new Date());
		if (stepId > 0) {
			Reporter.log("</div>");
		}
		StringBuilder logEvent = new StringBuilder();
		logEvent.append("<a href=\"");
		logEvent.append("javascript:toggleElement('step-" + stepId
				+ "', 'block')\" ");
		logEvent.append("title=\"Click to expand/collapse\" ");
		logEvent.append("style=\"" + color + "\">");
		logEvent.append("<b>");
		logEvent.append(timestamp + " " + text);
		logEvent.append("</b></a><br />");
		logEvent.append("<div class=\"testStep\" id=\"step-" + stepId + "\">");
		Reporter.log(logEvent.toString());
		stepId++;
	}
}
