package cane.brothers.russianpost.client.data;


/**
 * POJO класс для проверки контрольной суммы.
 */
public class CheckTotal {

	private int allLines = 0;
	private int gapLines = 0;
	private int workingLines = 0;
	private int duplicatedLines = 0;
	private int errorLines = 0;

	// количество баркодов которые были удалены
	private int removeLines = 0;

	public CheckTotal() {
	}

	public CheckTotal(int all, int gap, int work, int dup, int err, int remove) {
		allLines = all;
		gapLines = gap;
		workingLines = work;
		duplicatedLines = dup;
		errorLines = err;
		removeLines = remove;
	}

	public int getAllLines() {
		return allLines;
	}

	public void setAllLines(int allLines) {
		this.allLines = allLines;
	}

	public int getGapLines() {
		return gapLines;
	}

	public void setGapLines(int gapLines) {
		this.gapLines = gapLines;
	}

	public int getWorkingLines() {
		return workingLines;
	}

	public void setWorkingLines(int workingLines) {
		this.workingLines = workingLines;
	}

	public int getDuplicatedLines() {
		return duplicatedLines;
	}

	public void setDuplicatedLines(int duplicatedLines) {
		this.duplicatedLines = duplicatedLines;
	}

	public int getErrorLines() {
		return errorLines;
	}

	public void setErrorLines(int errorLines) {
		this.errorLines = errorLines;
	}

	public int getRemoveLines() {
		return removeLines;
	}

	public void setRemoveLines(int removeLines) {
		this.removeLines = removeLines;
	}
}
