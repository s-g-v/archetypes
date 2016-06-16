package reporting;

import java.io.IOException;

import org.testng.ITestResult;
import org.testng.Reporter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

/**
 * Write test reports.
 * @author gsikorskiy
 *
 */
public class ReportAppender extends AppenderBase<ILoggingEvent> {

	private PatternLayoutEncoder encoder;

	@Override
	public final void start() {
		if (this.encoder == null) {
			addError("No encoder set for the appender named [" + name + "].");
			return;
		}
		try {
			encoder.init(System.out);
			Reporter.log("<script type=\"text/javascript\" src=\"reportng.js\"></script>");
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.start();
	}

	@Override
	protected final void append(final ILoggingEvent event) {
		String body = this.encoder.getLayout().doLayout(event);
		if (body.contains("http:") || body.contains("https:")) {
			body = decorateLink(body);
		}

		if (event.getLevel() == Level.WARN) {
			body = decorateWarn(body);
			ITestResult tr = Reporter.getCurrentTestResult();
			tr.setAttribute("warning", true);
		}
		Reporter.log(body + "<br />");
	}

	/**
	 * Allows to get layout encoder.
	 * @return current encoder
	 */
	public final PatternLayoutEncoder getEncoder() {
		return encoder;
	}

	/**
	 * Setter for layout encoder.
	 * @param encoder - uses to change system encoder
	 */
	public final void setEncoder(final PatternLayoutEncoder encoder) {
		this.encoder = encoder;
	}

	/**
	 * Method to add appropriate html tags to links in the report.
	 * @param text - message to search link
	 * @return - complete element tag with text
	 */
	private static String decorateLink(final String text) {
		int httpIndex = text.indexOf("http");
		String[] delimiters = {" ", ","};
		int endIndex = -1;
		for (String item : delimiters) {
			endIndex = text.indexOf(item, httpIndex);
			if (endIndex != -1) {
				break;
			}
		}
		String link = text.substring(httpIndex);
		if (endIndex != -1) {
			link = text.substring(httpIndex, endIndex - 1);
		}
		StringBuilder decoratedLink = new StringBuilder();
		decoratedLink.append("<a href=\"");
		decoratedLink.append(link);
		decoratedLink.append("\">");
		decoratedLink.append(link);
		decoratedLink.append("</a>");
		return text.replace(link, decoratedLink);
	}

	/**
	 * Method to set color ORANGE for warnings.
	 * @param text - warning message
	 * @return - html element with orange background
	 */
	private static String decorateWarn(final String text) {
		StringBuilder decoratedWarn = new StringBuilder();
		decoratedWarn.append("<div style=\"" + ReportColor.WARNING.getColor() + "\">");
		decoratedWarn.append(text);
		decoratedWarn.append("</div>");
		return decoratedWarn.toString();
	}
}
