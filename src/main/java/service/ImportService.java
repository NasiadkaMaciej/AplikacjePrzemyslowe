package service;

import exception.InvalidDataException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import model.Employee;
import model.ImportSummary;
import model.Position;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ImportService {
	private final EmployeeService employeeService;

	@Value("${app.import.csv-file}") private String csvFilePath;

	public ImportService(EmployeeService employeeService) {
		this.employeeService = employeeService;
		System.out.println("ImportService has been created with EmployeeService injected!");
	}

	public ImportSummary importFromCsv(Path csvPath) {
		ImportSummary summary = new ImportSummary();

		try {
			var lines = Files.readAllLines(csvPath);
			boolean isFirstLine = true;
			int lineNumber = 0;

			for (String line : lines) {
				lineNumber++;

				if (isFirstLine) {
					isFirstLine = false;
					continue;
				}

				try {
					Employee employee = parseCsvLine(line);
					employeeService.addEmployee(employee);
					summary.incrementImportedCount();
				} catch (InvalidDataException | IllegalArgumentException e) {
					summary.addError(lineNumber, e.getMessage());
				}
			}
		} catch (IOException e) { summary.addError(0, "Nie można odczytać pliku: " + e.getMessage()); }

		return summary;
	}

	private Employee parseCsvLine(String line) throws InvalidDataException {
		String[] parts = line.split(",");
		if (parts.length < 5) { throw new InvalidDataException("Nieprawidłowa liczba kolumn w linii: " + line); }

		try {
			String firstName = parts[0].trim();
			String lastName = parts[1].trim();
			String email = parts[2].trim();
			String company = parts[3].trim();
			Position position = Position.valueOf(parts[4].trim().toUpperCase());

			if (parts.length > 5 && !parts[5].trim().isEmpty()) {
				BigDecimal salary = new BigDecimal(parts[5].trim());
				return new Employee(firstName, lastName, email, company, position, salary);
			} else {
				return new Employee(firstName, lastName, email, company, position);
			}
		} catch (IllegalArgumentException e) {
			throw new InvalidDataException("Błąd parsowania danych w linii: " + line + " - " + e.getMessage());
		}
	}
}