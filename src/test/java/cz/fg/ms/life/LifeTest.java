package cz.fg.ms.life;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import cz.fg.ms.life.Life;
import cz.fg.ms.life.exception.ConfigurationException;

/**
 * Test for Life class
 * 
 * @see cz.fg.ms.life.Life
 * 
 * @author Martin Siroky
 */
public class LifeTest {

	@Test
	public void testInit() {
		Life life = new Life();

		StringReader reader = null;
		try {
			reader = new StringReader("1,1,1,0,0,0\n1,1,1\n0,0,0,1");
			life.init(reader);

			fail("ConfigurationException should be thrown");
		} catch (Throwable e) {
			assertEquals(ConfigurationException.class, e.getClass());
		} finally {
			IOUtils.closeQuietly(reader);
		}

	}

	@Test
	public void testDoOneStep01variant() {
		testDoOneStep("1,1,0,0\n1,1,0,0\n0,0,1,1\n0,0,1,1", "1,1,0,0\n1,0,0,0\n0,0,0,1\n0,0,1,1");
	}

	@Test
	public void testDoOneStep02variant() {
		testDoOneStep("1,1\n1,1", "1,1\n1,1");
	}

	@Test
	public void testDoOneStep03variant() {
		testDoOneStep("0,0,0\n1,1,1\n0,0,0", "0,1,0\n0,1,0\n0,1,0");
	}

	@Test
	public void testDoOneStep04variant() {
		testDoOneStep("1,1,0,1,1\n1,1,0,1,1", "1,1,0,1,1\n1,1,0,1,1");
	}

	@Test
	public void testDoOneStep05variant() {
		testDoOneStep("0,0,1,0\n1,0,0,1\n1,0,0,1\n0,1,0,0", "0,0,0,0\n0,1,1,1\n1,1,1,0\n0,0,0,0");
	}

	@Test
	public void testPrintCurrentStatus() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			System.setOut(new PrintStream(out, true, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			fail("Error printing current status");
		}

		Life life = new Life();

		StringReader reader = null;
		try {
			reader = new StringReader("1,1,0,1,1\n1,1,0,1,1");
			life.init(reader);
		} catch (ConfigurationException e) {
			fail("Error reading configuration");
		} finally {
			IOUtils.closeQuietly(reader);
		}

		life.printCurrentStatus();

		String lineSeparator = System.lineSeparator();
		try {
			assertEquals("\u2588\u2588\u2591\u2588\u2588" + lineSeparator + "\u2588\u2588\u2591\u2588\u2588"
					+ lineSeparator, out.toString("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			fail("Encoding problem");
		}
	}

	private void testDoOneStep(String initValue, String expectedAfterOneStep) {
		Life life = new Life();

		StringReader reader = null;
		try {
			reader = new StringReader(initValue);
			life.init(reader);
		} catch (ConfigurationException e) {
			fail("Error reading configuration");
		} finally {
			IOUtils.closeQuietly(reader);
		}

		life.doOneStep();

		StringWriter writer = null;
		try {
			writer = new StringWriter();
			life.saveCurrentStatus(writer);
			assertEquals(expectedAfterOneStep, writer.toString());
		} catch (IOException e) {
			fail("Error reading configuration");
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}

}
