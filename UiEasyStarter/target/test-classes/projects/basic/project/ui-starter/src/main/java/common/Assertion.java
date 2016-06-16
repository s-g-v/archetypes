package common;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.Reporter;

import reporting.CommonReporter;
import reporting.ScreenshotHTMLReporter;

/**
 * Extension of standard TestNG assertions.
 * @author gsikorskiy
 *
 */
public final class Assertion extends Assert {

	private Assertion() { }

	/**
	 * Check that pictures are equals. Firstly it compares md5.
	 * If it fails, looking for differences.
	 * @param actual - actual picture.
	 * @param expected - expected picture.
	 * @param message - message to write to log if fails.
	 */
	public static void assertEqualsPictures(final File actual,
											final File expected,
											final String message) {
		if (getMD5(actual).equals(getMD5(expected))) {
	    	return;
	    }
		try {
			final BufferedImage actualImage = crop(ImageIO.read(actual));
		   	final BufferedImage expectedImage = crop(ImageIO.read(expected));
		   	assertEqualsPictures(actualImage, expectedImage, message);
		} catch (final IOException e) {
		   	fail(message, e);
		}
	}

	/**
	 * Check that pictures are equals. Firstly it compares md5.
	 * If it fails, looking for differences. Works with BufferedImage files
	 * @param actual - actual picture.
	 * @param expected - expected picture.
	 * @param message - message to write to log if fails.
	 */
	public static void assertEqualsPictures(final BufferedImage actual,
											final BufferedImage expected,
											final String message) {
		if (getMD5(actual).equals(getMD5(expected))) {
	    	return;
	    }
		try {
			final ITestResult tr = Reporter.getCurrentTestResult();

			final StringBuilder fileName = new StringBuilder();
			final SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss");
			final String timestamp = dateFormatter.format(new Date());
			fileName.append(timestamp + " DiffImage ");
			fileName.append(tr.getTestContext().getName() + " ");
			fileName.append(tr.getMethod().getMethodName() + " ");
			fileName.append(ScreenshotHTMLReporter.FILE_EXTENSION);

			final File outputfile = new File(Environment.SCREENSHOT_PATH + File.separator
	    			+ fileName.toString().replaceAll(" ", "_"));

			ImageIO.write(imageDiff(actual, expected), "png", outputfile);
			CommonReporter.printScreenshot("Actual(left) picture has"
					+ " the following differences with expected(right): ", outputfile);
	    } catch (final IOException e) {
	    	fail(message, e);
		} finally {
			//if md5 hash not equals one to another, then show difference and fail.
			//TODO make fail with accuracy in %.
			fail(message);
		}
	}

	private static String getMD5(final BufferedImage img) {
		String md5 = "";
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			ImageIO.write(img, "png", baos);
			md5 = DigestUtils.md5Hex(new ByteArrayInputStream(baos.toByteArray()));
		} catch (final IOException e) {
			fail("Can't get md5 for buffered image. ", e);
		}
		return md5;
	}

	private static String getMD5(final File f) {
		String md5 = "";
		try (FileInputStream fis = new FileInputStream(f)) {
			md5 = DigestUtils.md5Hex(fis);
		} catch (final IOException e) {
			fail("Can't get md5 for file: " + f.getAbsolutePath(), e);
		}
		return md5;
	}

	/**
	 * Shows differences between 2 images.
	 * @param image1 - first image.
	 * @param image2 - second image.
	 * @return - difference between image 2 and 1.
	 */
	public static BufferedImage imageDiff(final BufferedImage image1, final BufferedImage image2) {
		final int width = image1.getWidth();
		final int height = image1.getHeight();
		final int white = 0x00FFFFFF;
		final BufferedImage img = new BufferedImage(2 * width, height, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < width; i++) {
		    for (int j = 0; j < height; j++) {
		    	final int rgb1 = image1.getRGB(i, j);
		    	final int rgb2 = image2.getRGB(i, j);
		    	if (rgb1 == rgb2) {
		    		img.setRGB(i, j, rgb1);
		    	} else {
			    	img.setRGB(i, j, (rgb2 - rgb1) ^ white);
		    	}
		    	img.setRGB(width + i, j, rgb2);
		    }
		}
		return img;
	}

	/**
	 * Removes all white borders.
	 * @param image - image to crop.
	 * @return - image without empty borders
	 */
	public static BufferedImage crop(final BufferedImage image) {
		final int width = image.getWidth();
		final int height = image.getHeight();
		int minW = width;
		int maxW = 0;
		int minH = height;
		int maxH = 0;
		for (int i = 0; i < width; i++) {
		    for (int j = 0; j < height; j++) {
		    	final int rgb = image.getRGB(i, j);
		    	if (rgb != -1 && i < minW) {
		    		minW = i;
		    	}
		    	if (rgb != -1 && i > maxW) {
		    		maxW = i;
		    	}
		    	if (rgb != -1 && j < minH) {
		    		minH = j;
		    	}
		    	if (rgb != -1 && j > maxH) {
		    		maxH = j;
		    	}
		    }
		}
		final BufferedImage img = new BufferedImage(maxW - minW, maxH - minH, BufferedImage.TYPE_INT_RGB);
		int k = 0;
		for (int i = minW; i < maxW; i++) {
			int l = 0;
			for (int j = minH; j < maxH; j++) {
		    	final int rgb = image.getRGB(i, j);
		    	img.setRGB(k, l, rgb);
		    	l++;
	    	}
		    k++;
	    }
		return img;
	}
}
