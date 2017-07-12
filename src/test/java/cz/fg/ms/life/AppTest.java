package cz.fg.ms.life;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cz.fg.ms.life.exception.ExitException;

/**
 * Test for App class
 * 
 * @see cz.fg.ms.life.App
 * 
 * @author Martin Siroky
 */
public class AppTest {

	private PrintStream originalOut;
	private SecurityManager originalSecurityManager;

	@Before
	public void setUp() {
		originalOut = System.out;
		originalSecurityManager = System.getSecurityManager();
	}

	@After
	public void teadDown() {
		System.setSecurityManager(originalSecurityManager);
		System.setOut(originalOut);
	}

	@Test
	public void testMainEmptyArgs() {
		testPrintUsageInfo(new String[] {});
	}

	@Test
	public void testMainPrintHelp() {
		testPrintUsageInfo(new String[] { "-h" });
	}

	private void testPrintUsageInfo(String[] args) {
		ByteArrayOutputStream outContent = null;
		try {
			outContent = new ByteArrayOutputStream();
			System.setOut(new PrintStream(outContent));

			TestingSecurityManager sm = new TestingSecurityManager();
			System.setSecurityManager(sm);

			try {
				App.main(args);
				fail("Should have thrown exception");
			} catch (ExitException e) {
				assertEquals(1, e.getStatus());
			}

			assertTrue(outContent.toString().startsWith("Game of life demo usage"));
		} finally {
			IOUtils.closeQuietly(outContent);
		}
	}

	@Test
	public void testMainWrongFirstArg() {
		ByteArrayOutputStream outContent = null;
		try {
			outContent = new ByteArrayOutputStream();
			System.setOut(new PrintStream(outContent));

			TestingSecurityManager sm = new TestingSecurityManager();
			System.setSecurityManager(sm);

			try {
				App.main(new String[] { "nonExistingFile", "10" });
				fail("Should have thrown exception");
			} catch (ExitException e) {
				assertEquals(1, e.getStatus());
			}

			assertTrue(outContent.toString().matches("(?s)File .* does not exist.*"));
		} finally {
			IOUtils.closeQuietly(outContent);
		}
	}

	@Test
	public void testMainWrongSecondArg() {
		ByteArrayOutputStream outContent = null;
		try {
			outContent = new ByteArrayOutputStream();
			System.setOut(new PrintStream(outContent));

			TestingSecurityManager sm = new TestingSecurityManager();
			System.setSecurityManager(sm);

			try {
				App.main(new String[] {
						new File(URLDecoder.decode(AppTest.class.getResource("/example01.txt").getFile(), "UTF-8"))
								.getAbsolutePath(),
						"b" });
				fail("Should have thrown exception");
			} catch (UnsupportedEncodingException e) {
				fail("Error getting testing file");
			} catch (ExitException e) {
				assertEquals(1, e.getStatus());
			}

			assertTrue(outContent.toString().matches("(?s)String .* is not a number.*"));
		} finally {
			IOUtils.closeQuietly(outContent);
		}
	}

	@Test
	public void testMainSuccess() {
		InputStream originalIn = System.in;

		ByteArrayOutputStream outContent = null;
		ByteArrayInputStream inContent = null;
		try {
			outContent = new ByteArrayOutputStream();
			System.setOut(new PrintStream(outContent));
			inContent = new ByteArrayInputStream("q".getBytes());
			System.setIn(inContent);

			try {
				App.main(new String[] {
						new File(URLDecoder.decode(AppTest.class.getResource("/example01.txt").getFile(), "UTF-8"))
								.getAbsolutePath(),
						"10" });

			} catch (UnsupportedEncodingException e) {
				fail("Error getting testing file");
			} catch (ExitException e) {
				fail("Should not thrown exception");
			}

			assertTrue(outContent.toString().matches("(?s).*Step 1:.*Step 10:.*"));
		} finally {
			IOUtils.closeQuietly(outContent);
			IOUtils.closeQuietly(inContent);

			System.setIn(originalIn);
		}
	}

}
