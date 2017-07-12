package cz.fg.ms.life;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cz.fg.ms.life.exception.ConfigurationException;

/**
 * Simple Game of life demo
 * (<a href="https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life">wikipedia
 * Game of life</a>)
 * 
 * @author Martin Siroky
 */
public class App {

	private static Log LOG = LogFactory.getLog(App.class.getName());

	private static File configuration;
	private static int steps = 0;

	/**
	 * Main method
	 * 
	 * @param args
	 *            First argument - path to file with configuration which
	 *            contains comma separated lines with 1 and 0 values (1=live
	 *            cell, 0=dead cell)
	 * 
	 *            Second argument - number of steps to compute and show
	 */
	public static void main(String[] args) {
		BufferedReader consoleReader = null;
		FileReader fileReader = null;
		try {
			processArguments(args);

			Life life = new Life();
			fileReader = new FileReader(configuration);
			life.init(fileReader);
			life.printCurrentStatus();

			// prints first steps (based on second arg)
			int step = 0;
			while (step < steps) {
				life.doOneStep();
				System.out.println("Step " + (step + 1) + ":");
				life.printCurrentStatus();
				step++;
			}

			// prints next step when user press enter
			consoleReader = new BufferedReader(new InputStreamReader(System.in));
			while (true) {
				System.out.print("Type 'q' to quit. Press enter to show next step");
				if ("q".equals(consoleReader.readLine())) {
					break;
				}

				life.doOneStep();
				System.out.println("Step " + (step + 1) + ":");
				life.printCurrentStatus();
				step++;
			}

			LOG.info("Exit 0");
		} catch (ConfigurationException e) {
			if (e.getMessage() != null) {
				System.out.println(e.getMessage() + "\n");
			}

			LOG.error(e.getMessage(), e);
			System.exit(1);
		} catch (IOException e) {
			System.out.println("Error reading input");
			LOG.error("Unexpected error", e);
			System.exit(1);
		} finally {
			IOUtils.closeQuietly(fileReader);
			IOUtils.closeQuietly(consoleReader);
		}
	}

	/**
	 * Processes command-line arguments
	 * 
	 * @param args
	 *            Arguments
	 * @throws ConfigurationException
	 *             In case when some argument is wrong
	 */
	private static void processArguments(String[] args) throws ConfigurationException {
		if (args.length != 2 || (args.length > 0 && "-h".equals(args[0]))) {
			printUsageInfo();

			throw new ConfigurationException();
		}

		configuration = new File(args[0]);
		if (!configuration.exists()) {
			throw new ConfigurationException("File " + args[0] + " does not exist");
		}

		if (configuration.isDirectory()) {
			throw new ConfigurationException("File " + args[0] + " is directory");
		}

		if (!configuration.canRead()) {
			throw new ConfigurationException("File " + args[0] + " is not readable");
		}

		try {
			steps = Integer.parseInt(args[1]);
		} catch (NumberFormatException ex) {
			throw new ConfigurationException("String " + args[1] + " is not a number");
		}
	}

	/**
	 * Prints basic usage info to output
	 */
	private static void printUsageInfo() {
		System.out.println(
				"Game of life demo usage: life [FILE] [STEPS]\nwhere\n\tFILE is file with initial fields configuration\n\tSTEPS is number of steps to go");
	}
}
