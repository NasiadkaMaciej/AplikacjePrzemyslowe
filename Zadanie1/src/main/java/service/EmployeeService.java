package MaciejNasiadka.Zadanie1;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
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
}