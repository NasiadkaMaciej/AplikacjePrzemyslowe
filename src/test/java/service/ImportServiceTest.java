package service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import model.Employee;
import model.ImportSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ImportServiceTest {

	@TempDir Path tempDir;

	private EmployeeService employeeService;
	private ImportService importService;
	private Path csvFile;

	@BeforeEach
	void setUp() {
		employeeService = mock(EmployeeService.class);
		importService = new ImportService(employeeService);
	}

	@Test
	void importFromCsv_ShouldImportValidEmployees() throws IOException {
		// Create CSV file with valid data
		String csvContent = "firstName,lastName,email,company,position,salary\n"
							+ "John,Doe,john@example.com,Company1,MANAGER,15000\n"
							+ "Jane,Smith,jane@example.com,Company1,PROGRAMISTA,10000\n";

		csvFile = createTempCsvFile(csvContent);

		// Execute import
		ImportSummary summary = importService.importFromCsv(csvFile);

		// Verify results
		assertEquals(2, summary.getImportedCount());
		assertTrue(summary.getErrors().isEmpty());

		// Verify employeeService.addEmployee was called twice
		verify(employeeService, times(2)).addEmployee(any(Employee.class));
	}

	@Test
	void importFromCsv_ShouldHandleInvalidPosition() throws IOException {
		// Create CSV file with invalid position
		String csvContent = "firstName,lastName,email,company,position,salary\n"
							+ "John,Doe,john@example.com,Company1,INVALID_POSITION,15000\n";

		csvFile = createTempCsvFile(csvContent);

		// Execute import
		ImportSummary summary = importService.importFromCsv(csvFile);

		// Verify results
		assertEquals(0, summary.getImportedCount());
		assertEquals(1, summary.getErrors().size());
		assertTrue(summary.getErrors().get(0).contains("Invalid position"));

		// Verify employeeService.addEmployee was not called
		verify(employeeService, never()).addEmployee(any(Employee.class));
	}

	@Test
	void importFromCsv_ShouldHandleNegativeSalary() throws IOException {
		// Create CSV file with negative salary
		String csvContent = "firstName,lastName,email,company,position,salary\n"
							+ "John,Doe,john@example.com,Company1,MANAGER,-5000\n";

		csvFile = createTempCsvFile(csvContent);

		// Execute import
		ImportSummary summary = importService.importFromCsv(csvFile);

		// Verify results
		assertEquals(0, summary.getImportedCount());
		assertEquals(1, summary.getErrors().size());
		assertTrue(summary.getErrors().get(0).contains("Salary must be positive"));

		// Verify employeeService.addEmployee was not called
		verify(employeeService, never()).addEmployee(any(Employee.class));
	}

	@Test
	void importFromCsv_ShouldHandleInvalidSalaryFormat() throws IOException {
		// Create CSV file with invalid salary format
		String csvContent = "firstName,lastName,email,company,position,salary\n"
							+ "John,Doe,john@example.com,Company1,MANAGER,NotANumber\n";

		csvFile = createTempCsvFile(csvContent);

		// Execute import
		ImportSummary summary = importService.importFromCsv(csvFile);

		// Verify results
		assertEquals(0, summary.getImportedCount());
		assertEquals(1, summary.getErrors().size());
		assertTrue(summary.getErrors().get(0).contains("Invalid salary format"));

		// Verify employeeService.addEmployee was not called
		verify(employeeService, never()).addEmployee(any(Employee.class));
	}

	@Test
	void importFromCsv_ShouldHandleInvalidNumberOfFields() throws IOException {
		// Create CSV file with invalid number of fields
		String csvContent = "firstName,lastName,email,company,position,salary\n"
							+ "John,Doe,john@example.com,Company1\n"; // Missing fields

		csvFile = createTempCsvFile(csvContent);

		// Execute import
		ImportSummary summary = importService.importFromCsv(csvFile);

		// Verify results
		assertEquals(0, summary.getImportedCount());
		assertEquals(1, summary.getErrors().size());
		assertTrue(summary.getErrors().get(0).contains("Invalid number of fields"));

		// Verify employeeService.addEmployee was not called
		verify(employeeService, never()).addEmployee(any(Employee.class));
	}

	@Test
	void importFromCsv_ShouldHandleNonExistentFile() {
		// Try to import from a non-existent file
		Path nonExistentFile = tempDir.resolve("non-existent.csv");

		// Execute import
		ImportSummary summary = importService.importFromCsv(nonExistentFile);

		// Verify results
		assertEquals(0, summary.getImportedCount());
		assertEquals(1, summary.getErrors().size());
		assertTrue(summary.getErrors().get(0).contains("File reading error"));

		// Verify employeeService.addEmployee was not called
		verify(employeeService, never()).addEmployee(any(Employee.class));
	}

	@Test
	void importFromCsv_ShouldHandleMixedValidAndInvalidData() throws IOException {
		// Create CSV file with mixed valid and invalid data
		String csvContent = "firstName,lastName,email,company,position,salary\n"
							+ "John,Doe,john@example.com,Company1,MANAGER,15000\n" +		  // Valid
							"Jane,Smith,jane@example.com,Company1,INVALID_POSITION,10000\n" + // Invalid position
							"Alice,White,alice@example.com,Company2,STAZYSTA,3500\n";		  // Valid

		csvFile = createTempCsvFile(csvContent);

		// Execute import
		ImportSummary summary = importService.importFromCsv(csvFile);

		// Verify results
		assertEquals(2, summary.getImportedCount());
		assertEquals(1, summary.getErrors().size());

		// Verify employeeService.addEmployee was called twice (for valid entries)
		verify(employeeService, times(2)).addEmployee(any(Employee.class));
	}

	private Path createTempCsvFile(String content) throws IOException {
		Path file = tempDir.resolve("test.csv");
		Files.writeString(file, content);
		return file;
	}
}