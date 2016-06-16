#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package reporting;

/**
 * Class contains colors for report.
 * @author gsikorskiy
 *
 */
public enum ReportColor {
	/**
	 * Font color blue.
	 */
	METHOD("color: ${symbol_pound}0000ff"),
	/**
	 * Font color black.
	 */
	STEP("color: ${symbol_pound}222222"),
	/**
	 * Background color orange.
	 */
	WARNING("background-color: ${symbol_pound}F99654");

	private String color;
	ReportColor(final String color) {
		this.color = color;
	}

	/**
	 * Retrieves color string, that can be pasted to html.
	 * @return something, that can be added to "style="
	 */
	public String getColor() {
		return this.color;
	}
}
