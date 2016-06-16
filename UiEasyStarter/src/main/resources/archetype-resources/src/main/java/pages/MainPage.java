#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class MainPage extends Page{

	@FindBy(how = How.CLASS_NAME, using = "header")
	private WebElement headMenu;

	public MainPage(final WebDriver webDriver) {
		super(webDriver);
	}

	public String getHeaderText() {
		return headMenu.getText();
	}
}
