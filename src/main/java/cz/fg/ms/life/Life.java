package cz.fg.ms.life;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cz.fg.ms.life.exception.ConfigurationException;

/**
 * Game of life implementation
 *
 * @author Martin Siroky
 */
public class Life {

	private static Log LOG = LogFactory.getLog(Life.class.getName());

	private static int DEAD_UNKNOWN = 0; // default value
	private static int LIVE_UNKNOWN = 1;
	private static int DEAD_LIVE = 2;
	private static int LIVE_DEAD = 3;
	private static int DEAD_DEAD = 4;
	private static int LIVE_LIVE = 5;

	/**
	 * Cells - each cell has one of six statuses (for optimization - copy of
	 * array for next step is not needed)
	 */
	private int[][] cells = null;

	/**
	 * Reads configuration from file and initializes the game
	 * 
	 * @param reader
	 *            Reader to read from
	 * @throws ConfigurationException
	 *             In case reading or initialization failed
	 */
	public void init(Reader reader) throws ConfigurationException {
		LOG.info("Reading from configuration file started");

		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(reader);

			List<int[]> rows = new ArrayList<>();
			Integer columnCount = null;
			int rowIndex = 0;

			String line = null;
			StringTokenizer stringTokenizer;

			// read lines from reader
			while ((line = bufferedReader.readLine()) != null) {
				stringTokenizer = new StringTokenizer(line, ",");

				columnCount = checkColumnCount(stringTokenizer, columnCount, rowIndex);

				// adds first and last column for algorithm optimization
				int[] row = new int[columnCount + 2];
				rows.add(row);

				for (int column = 0; column < columnCount; column++) {
					row[column + 1] = "1".equals(stringTokenizer.nextToken()) ? LIVE_UNKNOWN : DEAD_UNKNOWN;
				}

				rowIndex++;
			}

			// adds first and last row for algorithm optimization
			rows.add(0, new int[columnCount + 2]);
			rows.add(new int[columnCount + 2]);
			cells = rows.toArray(new int[0][0]);

			LOG.info(rowIndex + " rows with " + columnCount + " columns were read from configuration file.");
		} catch (IOException e) {
			throw new ConfigurationException("Cannot read from configuration file", e);
		}
	}

	/**
	 * Writes current status of cells to writer
	 * 
	 * @param writer
	 *            Writer to write to
	 * @throws IOException
	 *             in case writing failed
	 */
	public void saveCurrentStatus(Writer writer) throws IOException {
		try {
			for (int row = 1; row < cells.length - 1; row++) {
				if (row > 1) {
					writer.write("\n");
				}
				for (int column = 1; column < cells[row].length - 1; column++) {
					if (column > 1) {
						writer.write(",");
					}
					writer.write(cells[row][column] == LIVE_UNKNOWN ? "1" : "0");
				}

			}

			LOG.info("Current status was saved.");
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}

	/**
	 * Does one step of Game of life - recomputes cells status
	 */
	public void doOneStep() {

		// computes next status of cells
		for (int row = 1; row < cells.length - 1; row++) {
			for (int column = 1; column < cells[row].length - 1; column++) {
				int liveNeighbour = countLiveNeighbour(row, column);

				if (isLive(row, column)) {
					if (liveNeighbour < 2 || liveNeighbour > 3) {
						cells[row][column] = LIVE_DEAD;
					} else {
						cells[row][column] = LIVE_LIVE;
					}
				} else {
					if (liveNeighbour == 3) {
						cells[row][column] = DEAD_LIVE;
					} else {
						cells[row][column] = DEAD_DEAD;
					}
				}
			}
		}

		// switch next status to current status
		for (int row = 1; row < cells.length - 1; row++) {
			for (int column = 1; column < cells[row].length - 1; column++) {
				if (cells[row][column] == LIVE_DEAD || cells[row][column] == DEAD_DEAD) {
					cells[row][column] = DEAD_UNKNOWN;
				}
				if (cells[row][column] == LIVE_LIVE || cells[row][column] == DEAD_LIVE) {
					cells[row][column] = LIVE_UNKNOWN;
				}
			}
		}

		LOG.info("Next step was computed.");
	}

	/**
	 * Prints current status of cells (in human readable form) to output
	 */
	public void printCurrentStatus() {
		for (int row = 1; row < cells.length - 1; row++) {
			for (int column = 1; column < cells[row].length - 1; column++) {
				System.out.print(cells[row][column] == LIVE_UNKNOWN ? "\u2588" : "\u2591");
			}
			System.out.println();
		}
	}

	/**
	 * Counts live neighbour of given cell
	 * 
	 * @param row
	 *            Row of the cell
	 * @param column
	 *            Column of the cell
	 * @return count of live neighbour of given cell
	 */
	private int countLiveNeighbour(int row, int column) {
		int liveNeighbour = 0;
		liveNeighbour += (isLive(row - 1, column - 1) ? 1 : 0);
		liveNeighbour += (isLive(row - 1, column) ? 1 : 0);
		liveNeighbour += (isLive(row - 1, column + 1) ? 1 : 0);
		liveNeighbour += (isLive(row, column - 1) ? 1 : 0);
		liveNeighbour += (isLive(row, column + 1) ? 1 : 0);
		liveNeighbour += (isLive(row + 1, column - 1) ? 1 : 0);
		liveNeighbour += (isLive(row + 1, column) ? 1 : 0);
		liveNeighbour += (isLive(row + 1, column + 1) ? 1 : 0);
		return liveNeighbour;
	}

	/**
	 * Checks if cell is live
	 * 
	 * @param row
	 *            Row of the cell
	 * @param column
	 *            Column of the cell
	 * @return if cell is live
	 */
	private boolean isLive(int row, int column) {
		return cells[row][column] % 2 == 1;
	}

	/**
	 * Checks column count in current row (if count is equal with previous rows)
	 * 
	 * @param stringTokenizer
	 *            Tokenizer to check
	 * @param columnCount
	 *            Previous count of columns
	 * @param rowIndex
	 *            Index of current row
	 * @return Column count of current row
	 * @throws ConfigurationException
	 *             In case column count is not equal
	 */
	private Integer checkColumnCount(StringTokenizer stringTokenizer, Integer columnCount, int rowIndex)
			throws ConfigurationException {
		int tokenCount = stringTokenizer.countTokens();
		if (columnCount != null) {
			if (columnCount != tokenCount) {
				throw new ConfigurationException("Different count of values in row " + rowIndex + " (" + tokenCount
						+ " instead of " + columnCount + ") in configuration file ");
			}
		} else {
			columnCount = tokenCount;
		}
		return columnCount;
	}

}
