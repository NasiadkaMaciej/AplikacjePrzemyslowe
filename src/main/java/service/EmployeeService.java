package service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import model.CompanyStatistics;
import model.Employee;
import model.Position;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

	private final Map<String, Employee> employeesByEmail = new LinkedHashMap<>();

	public void addEmployee(Employee employee) {
		String key = employee.getEmail();
		if (employeesByEmail.containsKey(key)) {
			throw new IllegalArgumentException("Employee with email %s already exists".formatted(key));
		}
		employeesByEmail.put(key, employee);
	}

	public List<Employee> findAll() { return List.copyOf(employeesByEmail.values()); }

	public List<Employee> findByCompany(String company) {
		return employeesByEmail.values()
		  .stream()
		  .filter(employee -> employee.getCompany().equalsIgnoreCase(company))
		  .toList();
	}

	public List<Employee> findAllSortedByLastName() {
		return employeesByEmail.values()
		  .stream()
		  .sorted(Comparator.comparing(Employee::getLastName)
					.thenComparing(Employee::getFirstName)
					.thenComparing(Employee::getEmail))
		  .toList();
	}

	public Map<Position, List<Employee>> groupByPosition() {
		Map<Position, List<Employee>> grouped = employeesByEmail.values().stream().collect(
		  Collectors.groupingBy(Employee::getPosition, () -> new EnumMap<>(Position.class), Collectors.toList()));
		grouped.replaceAll((position, list) -> List.copyOf(list));
		return Collections.unmodifiableMap(grouped);
	}

	public Map<Position, Long> countByPosition() {
		Map<Position, Long> counts = employeesByEmail.values().stream().collect(
		  Collectors.groupingBy(Employee::getPosition, () -> new EnumMap<>(Position.class), Collectors.counting()));
		return Collections.unmodifiableMap(counts);
	}

	public Optional<BigDecimal> calculateAverageSalary() {
		if (employeesByEmail.isEmpty()) { return Optional.empty(); }
		BigDecimal sum =
		  employeesByEmail.values().stream().map(Employee::getSalary).reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal average = sum.divide(BigDecimal.valueOf(employeesByEmail.size()), 2, RoundingMode.HALF_UP);
		return Optional.of(average);
	}

	public Optional<Employee> findHighestPaidEmployee() {
		return employeesByEmail.values().stream().max(Comparator.comparing(Employee::getSalary));
	}

	public List<Employee> validateSalaryConsistency() {
		return employeesByEmail.values()
		  .stream()
		  .filter(employee -> employee.getSalary().compareTo(employee.getPosition().getBaseSalary()) < 0)
		  .collect(Collectors.toList());
	}

	public Map<String, CompanyStatistics> getCompanyStatistics() {
		return employeesByEmail.values().stream().collect(Collectors.groupingBy(
		  Employee::getCompany, Collectors.collectingAndThen(Collectors.toList(), companyEmployees -> {
			  long count = companyEmployees.size();

			  double averageSalary =
				companyEmployees.stream().mapToDouble(e -> e.getSalary().doubleValue()).average().orElse(0.0);

			  String topEarnerName = companyEmployees.stream()
									   .max(Comparator.comparing(Employee::getSalary))
									   .map(emp -> emp.getFirstName() + " " + emp.getLastName())
									   .orElse("N/A");

			  return new CompanyStatistics(count, averageSalary, topEarnerName);
		  })));
	}

	public void addEmployees(List<Employee> newEmployees) {
		for (Employee employee : newEmployees) {
			addEmployee(employee);
		}
	}
}