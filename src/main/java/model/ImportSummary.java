package model;

import java.util.ArrayList;
import java.util.List;

public class ImportSummary {
	private int importedCount;
	private List<String> errors;

	public ImportSummary() {
		this.importedCount = 0;
		this.errors = new ArrayList<>();
	}

	public void incrementImportedCount() { this.importedCount++; }

	public void addError(int lineNumber, String errorMessage) {
		errors.add("Line " + lineNumber + ": " + errorMessage);
	}

	public int getImportedCount() { return importedCount; }

	public List<String> getErrors() { return errors; }

	@Override
	public String toString() {
		return "ImportSummary{"
		  + "importedCount=" + importedCount + ", errors=" + errors + '}';
	}
}