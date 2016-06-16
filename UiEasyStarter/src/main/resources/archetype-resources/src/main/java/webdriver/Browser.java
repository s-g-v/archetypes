#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package webdriver;

/**
 * It represents a browser. It contains name, version and platform fields.
 *
 * @author gsikorskiy
 */
public class Browser {

	private BrowserName name;
	private String version;
	private String platform;

	/**
	 * Creates simple instance of browser with certain name.
	 * @param name - element of BrowserName enum
	 */
	public Browser(final BrowserName name) {
		this.name = name;
	}

	/**
	 * Retrieves name in lower case.
	 * @return string with browser's name
	 */
	public final BrowserName getName() {
		return name;
	}

	/**
	 * Retrieves version.
	 * @return string with browser's version
	 */
	public final String getVersion() {
		return version;
	}

	/**
	 * Retrieves platform.
	 * @return string with browser's platform
	 */
	public final String getPlatform() {
		return platform;
	}

	/**
	 * Allows to set desired browser platform.
	 * @param platform - name of platform
	 */
	public final void setPlatform(final String platform) {
		this.platform = platform;
	}

	/**
	 * Allows to set desired browser version.
	 * @param version - version as string
	 */
	public final void setVersion(final String version) {
		this.version = version;
	}

	@Override
	public final String toString() {
		return getName() + "_" + version + "_" + platform;
	}

	@Override
	public final boolean equals(final Object o) {
		boolean result = false;
		if (o != null && o instanceof Browser) {
			Browser br = (Browser) o;
			result = (br.getName().equals(this.name)
					&& br.getPlatform().equals(platform)
					&& br.getVersion().equals(version));
		}
		return result;
	}

	@Override
	public final int hashCode() {
		return super.hashCode();
	}
}
