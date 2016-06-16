package common;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class contains global parameters for tests.
 * @author gsikorskiy
 *
 */
public final class Environment {

	private static final int DEFAULT_TIMEOUT = 30;
	private static Logger log = LoggerFactory.getLogger(Environment.class);

	/**
     * Retrieves default timeout, that will be used in tests.
     * Can be set by using -Dtimeout option
     */
	public static final int TIMEOUT;

	/**
	 * Flag means should we make screenshots or not.
     * Can be set by using -Dmake.screenshot option
	 */
	public static final boolean MAKE_SCREENSHOT;

    /**
     * Shows where screenshots will be stored.
     * By default test-output/html/scr/<TODAY>
     * Can be set by using -Dscreenshot.path option
     */
	public static final String SCREENSHOT_PATH;

	/**
     * Retrieves url to grid hub.
     * Can be set by using -Dgrid.hub option
     */
	public static final Optional<URL> GRID_HUB_URL;

	/**
	 * Url of site, that we will test.
     * Can be set by using -Dtest.site option
	 */
	public static final URI SITE_TO_TEST;

	static {
		try {
			SITE_TO_TEST = URI.create(System.getProperty("test.site"));
		} catch (NullPointerException | IllegalArgumentException e) { 
			throw new RuntimeException("URL to site is not set in VM arguments. What should we test?");
		}

		URL grid = null;
		try {
			String url = System.getProperty("grid.hub");
			if (url != null) {
				grid = new URL(url);
			}
		} catch (MalformedURLException e) {
			log.error("Unable to parse grid hub link from vm arguments.");
		}

		GRID_HUB_URL = Optional.ofNullable(grid);
		
		if (GRID_HUB_URL.isPresent()) {
			log.debug("Grid hub link: " + GRID_HUB_URL);
		}

		TIMEOUT = Integer.valueOf(System.getProperty("timeout",
				String.valueOf(DEFAULT_TIMEOUT)));
		log.debug("Default timeout is " + TIMEOUT);

		MAKE_SCREENSHOT = Boolean.valueOf(System.getProperty("make.screenshot", "true"));

		final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
		final String date = dateFormatter.format(new Date());

		SCREENSHOT_PATH = System.getProperty("screenshot.path",
				System.getProperty("user.dir") + "/test-output/html/scr/" + date);

		System.setProperty("org.uncommons.reportng.escape-output", "false");
		System.setProperty("org.uncommons.reportng.stylesheet","src/main/resources/report.css");
	}

	private Environment() { }

}
