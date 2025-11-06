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
	private final List<Employee> employees = new ArrayList<>();

	public EmployeeService() { System.out.println("EmployeeService has been created by Spring!"); }

	public void addEmployee(Employee employee) {
		// Validate null
		if (employee == null) { throw new NullPointerException("Employee cannot be null"); }

		// Validate duplicate email
		boolean emailExists = employees.stream().anyMatch(e -> e.getEmail().equalsIgnoreCase(employee.getEmail()));

		if (emailExists) {
			throw new IllegalArgumentException("Employee with email " + employee.getEmail() + " already exists");
		}

		employees.add(employee);
	}

	public void addEmployees(List<Employee> employeeList) {
		if (employeeList == null) { throw new NullPointerException("Employee list cannot be null"); }
		employeeList.forEach(this::addEmployee);
	}

	public List<Employee> findAll() { return new ArrayList<>(employees); }

	public List<Employee> findByCompany(String company) {
		return employees.stream().filter(e -> e.getCompany().equalsIgnoreCase(company)).collect(Collectors.toList());
	}

	public List<Employee> findAllSortedByLastName() {
		return employees.stream().sorted(Comparator.comparing(Employee::getLastName)).collect(Collectors.toList());
	}

	public Map<Position, List<Employee>> groupByPosition() {
		return employees.stream().collect(Collectors.groupingBy(Employee::getPosition));
	}

	public Map<Position, Long> countByPosition() {
		return employees.stream().collect(Collectors.groupingBy(Employee::getPosition, Collectors.counting()));
	}

	public Optional<BigDecimal> calculateAverageSalary() {
		List<BigDecimal> salaries = employees.stream().map(Employee::getSalary).collect(Collectors.toList());

		if (salaries.isEmpty()) { return Optional.empty(); }

		BigDecimal sum = salaries.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

		return Optional.of(sum.divide(BigDecimal.valueOf(salaries.size()), 2, RoundingMode.HALF_UP));
	}

	public Optional<Employee> findHighestPaidEmployee() {
		return employees.stream().max(Comparator.comparing(Employee::getSalary));
	}

	public List<Employee> validateSalaryConsistency() {
		return employees.stream()
		  .filter(e -> e.getSalary().compareTo(e.getPosition().getBaseSalary()) < 0)
		  .collect(Collectors.toList());
	}

	public Map<String, CompanyStatistics> getCompanyStatistics() {
		return employees.stream()
		  .collect(Collectors.groupingBy(Employee::getCompany))
		  .entrySet()
		  .stream()
		  .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
			  List<Employee> companyEmployees = entry.getValue();
			  long employeeCount = companyEmployees.size();

			  BigDecimal sum =
				companyEmployees.stream().map(Employee::getSalary).reduce(BigDecimal.ZERO, BigDecimal::add);

			  double averageSalary =
				sum.divide(BigDecimal.valueOf(companyEmployees.size()), 2, RoundingMode.HALF_UP).doubleValue();

			  String topEarnerFullName = companyEmployees.stream()
										   .max(Comparator.comparing(Employee::getSalary))
										   .map(e -> e.getFirstName() + " " + e.getLastName())
										   .orElse("N/A");

			  return new CompanyStatistics(employeeCount, averageSalary, topEarnerFullName);
		  }));
	}
}