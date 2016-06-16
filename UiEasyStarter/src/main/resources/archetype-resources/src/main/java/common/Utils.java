#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package common;

import static reporting.CommonReporter.printInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

/**
 * Simple utils.
 * @author gsikorskiy
 *
 */
public final class Utils {

	/**
	 * Multiplier to convert seconds to milliseconds.
	 */
	public static final int MILLI = 1000;

	private Utils() { }

	/**
	 *Simple CURL command.
	 * @param url - url to process
	 * @return response from server
	 */
	public static String curl(final String url) {
		final StringBuilder result = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(
						new URL(url).openStream(), "UTF-8"))) {
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
		        result.append(line);
		    }
		} catch (final MalformedURLException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return result.toString();
	}

	/**
	 * Forced thread to sleep.
	 * @param seconds - how much sleep
	 */
	@Deprecated
	public static void sleep(final int seconds) {
		try {
			Thread.sleep(MILLI * seconds);
		} catch (final InterruptedException e) {
			printInfo("Looks that something iterrupted our sleep.");
			e.printStackTrace();
		}
	}

    /**
     * Retrieves pseudo random number in specified range.
     * @param min - min border
     * @param max - max border
     * @return - number
     */
    public static int generate(final int min, final int max) {
		return  new Random(System.currentTimeMillis()).nextInt(max - min) + min;
	}
}
