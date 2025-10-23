package service;

import exception.InvalidDataException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import model.Employee;
import model.ImportSummary;
import model.Position;
import service.EmployeeService;

public class ImportService {

	private final EmployeeService employeeService;

	public ImportService(EmployeeService employeeService) { this.employeeService = employeeService; }

	public ImportSummary importFromCsv(Path filePath) {
		ImportSummary summary = new ImportSummary();

		try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
			String line;
			int lineNumber = 0;

			// Skip header
			reader.readLine();
			lineNumber++;

			while ((line = reader.readLine()) != null) {
				lineNumber++;

				// Skip empty lines
				if (line.trim().isEmpty()) { continue; }

				try {
					Employee employee = parseEmployee(line.trim());
					employeeService.addEmployee(employee);
					summary.incrementImportedCount();
				} catch (InvalidDataException | IllegalArgumentException e) {
					summary.addError(lineNumber, e.getMessage());
				}
			}
		} catch (IOException e) { summary.addError(0, "File reading error: " + e.getMessage()); }

		return summary;
	}

	private Employee parseEmployee(String line) throws InvalidDataException {
		String[] parts = line.split(",");

		if (parts.length != 6) { throw new InvalidDataException("Invalid number of fields: " + parts.length); }

		String firstName = parts[0].trim();
		String lastName = parts[1].trim();
		String email = parts[2].trim();
		String company = parts[3].trim();
		Position position;

		try {
			position = Position.valueOf(parts[4].trim().toUpperCase());
		} catch (IllegalArgumentException e) { throw new InvalidDataException("Invalid position: " + parts[4].trim()); }

		BigDecimal salary;
		try {
			double salaryValue = Double.parseDouble(parts[5].trim());

			if (salaryValue <= 0) { throw new InvalidDataException("Salary must be positive"); }
			salary = BigDecimal.valueOf(salaryValue);
		} catch (NumberFormatException e) {
			throw new InvalidDataException("Invalid salary format: " + parts[5].trim());
		}

		return new Employee(firstName, lastName, email, company, position, salary);
	}
}