#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package functional;

import static reporting.CommonReporter.methodDescription;
import static reporting.CommonReporter.printInfo;

import org.testng.annotations.Test;

import common.Environment;
import pages.MainPage;
import pages.Page;

public class MainPageTest extends CommonTestCase{

	@Test
	public void open() {
		methodDescription("Open main page");
		final MainPage main = Page.open(Environment.SITE_TO_TEST.toString(), MainPage.class);
		printInfo(main.getHeaderText());
	}
}
