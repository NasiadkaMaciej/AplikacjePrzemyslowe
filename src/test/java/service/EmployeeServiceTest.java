package service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import model.CompanyStatistics;
import model.Employee;
import model.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EmployeeServiceTest {

	private EmployeeService employeeService;
	private Employee employee1;
	private Employee employee2;
	private Employee employee3;

	@BeforeEach
	void setUp() {
		employeeService = new EmployeeService();

		// Create test employees
		employee1 =
		  new Employee("John", "Doe", "john.doe@company1.com", "Company1", Position.MANAGER, BigDecimal.valueOf(15000));

		employee2 = new Employee(
		  "Jane", "Smith", "jane.smith@company1.com", "Company1", Position.PROGRAMISTA, BigDecimal.valueOf(10000));

		employee3 = new Employee(
		  "Bob", "Brown", "bob.brown@company2.com", "Company2", Position.STAZYSTA, BigDecimal.valueOf(3500));
	}

	@Test
	void addEmployee_ShouldAddEmployeeSuccessfully() {
		employeeService.addEmployee(employee1);

		List<Employee> employees = employeeService.findAll();
		assertEquals(1, employees.size());
		assertEquals(employee1, employees.get(0));
	}

	@Test
	void addEmployee_ShouldThrowException_WhenEmployeeWithSameEmailExists() {
		employeeService.addEmployee(employee1);

		IllegalArgumentException exception =
		  assertThrows(IllegalArgumentException.class, () -> employeeService.addEmployee(employee1));

		assertTrue(exception.getMessage().contains("already exists"));
	}

	@Test
	void addEmployee_ShouldThrowException_WhenEmployeeIsNull() {
		assertThrows(NullPointerException.class, () -> employeeService.addEmployee(null));
	}

	@Test
	void findByCompany_ShouldReturnEmployeesOfThatCompany() {
		employeeService.addEmployee(employee1);
		employeeService.addEmployee(employee2);
		employeeService.addEmployee(employee3);

		List<Employee> company1Employees = employeeService.findByCompany("Company1");
		assertEquals(2, company1Employees.size());
		assertTrue(company1Employees.contains(employee1));
		assertTrue(company1Employees.contains(employee2));
	}

	@Test
	void findByCompany_ShouldReturnEmptyList_WhenNoEmployeesInCompany() {
		employeeService.addEmployee(employee1);

		List<Employee> nonExistentCompanyEmployees = employeeService.findByCompany("NonExistentCompany");
		assertTrue(nonExistentCompanyEmployees.isEmpty());
	}

	@Test
	void findAllSortedByLastName_ShouldReturnSortedEmployees() {
		employeeService.addEmployee(employee3); // Brown
		employeeService.addEmployee(employee1); // Doe
		employeeService.addEmployee(employee2); // Smith

		List<Employee> sortedEmployees = employeeService.findAllSortedByLastName();
		assertEquals(3, sortedEmployees.size());
		assertEquals("Brown", sortedEmployees.get(0).getLastName());
		assertEquals("Doe", sortedEmployees.get(1).getLastName());
		assertEquals("Smith", sortedEmployees.get(2).getLastName());
	}

	@Test
	void groupByPosition_ShouldReturnEmployeesGroupedByPosition() {
		employeeService.addEmployee(employee1); // MANAGER
		employeeService.addEmployee(employee2); // PROGRAMISTA
		employeeService.addEmployee(employee3); // STAZYSTA

		Map<Position, List<Employee>> groupedEmployees = employeeService.groupByPosition();
		assertEquals(3, groupedEmployees.size());

		assertEquals(1, groupedEmployees.get(Position.MANAGER).size());
		assertEquals(1, groupedEmployees.get(Position.PROGRAMISTA).size());
		assertEquals(1, groupedEmployees.get(Position.STAZYSTA).size());

		assertEquals(employee1, groupedEmployees.get(Position.MANAGER).get(0));
		assertEquals(employee2, groupedEmployees.get(Position.PROGRAMISTA).get(0));
		assertEquals(employee3, groupedEmployees.get(Position.STAZYSTA).get(0));
	}

	@Test
	void countByPosition_ShouldReturnCorrectCount() {
		employeeService.addEmployee(employee1); // MANAGER
		employeeService.addEmployee(employee2); // PROGRAMISTA
		employeeService.addEmployee(new Employee("Another",
												 "Programmer",
												 "another@company.com",
												 "Company",
												 Position.PROGRAMISTA,
												 BigDecimal.valueOf(9000))); // Another PROGRAMISTA

		Map<Position, Long> counts = employeeService.countByPosition();
		assertEquals(2, counts.size());

		assertEquals(1L, counts.get(Position.MANAGER));
		assertEquals(2L, counts.get(Position.PROGRAMISTA));
	}

	@Test
	void calculateAverageSalary_ShouldReturnCorrectAverage() {
		employeeService.addEmployee(employee1); // 15000
		employeeService.addEmployee(employee2); // 10000

		Optional<BigDecimal> averageOpt = employeeService.calculateAverageSalary();
		assertTrue(averageOpt.isPresent());

		BigDecimal average = averageOpt.get();
		assertEquals(0, BigDecimal.valueOf(12500).compareTo(average));
	}

	@Test
	void calculateAverageSalary_ShouldReturnEmpty_WhenNoEmployees() {
		Optional<BigDecimal> averageOpt = employeeService.calculateAverageSalary();
		assertTrue(averageOpt.isEmpty());
	}

	@Test
	void findHighestPaidEmployee_ShouldReturnEmployeeWithHighestSalary() {
		employeeService.addEmployee(employee1); // 15000
		employeeService.addEmployee(employee2); // 10000
		employeeService.addEmployee(employee3); // 3500

		Optional<Employee> highestPaidOpt = employeeService.findHighestPaidEmployee();
		assertTrue(highestPaidOpt.isPresent());

		Employee highestPaid = highestPaidOpt.get();
		assertEquals(employee1, highestPaid);
	}

	@Test
	void findHighestPaidEmployee_ShouldReturnEmpty_WhenNoEmployees() {
		Optional<Employee> highestPaidOpt = employeeService.findHighestPaidEmployee();
		assertTrue(highestPaidOpt.isEmpty());
	}

	@Test
	void validateSalaryConsistency_ShouldReturnEmptyListForValidSalaries() {
		employeeService.addEmployee(employee1); // Well-paid MANAGER
		employeeService.addEmployee(employee2); // Well-paid PROGRAMISTA

		List<Employee> underpaidEmployees = employeeService.validateSalaryConsistency();
		assertTrue(underpaidEmployees.isEmpty());
	}

	@Test
	void getCompanyStatistics_ShouldReturnCorrectStatistics() {
		employeeService.addEmployee(employee1); // Company1, 15000, John Doe
		employeeService.addEmployee(employee2); // Company1, 10000, Jane Smith
		employeeService.addEmployee(employee3); // Company2, 3500, Bob Brown

		Map<String, CompanyStatistics> statistics = employeeService.getCompanyStatistics();
		assertEquals(2, statistics.size());

		CompanyStatistics company1Stats = statistics.get("Company1");
		assertNotNull(company1Stats);
		assertEquals(2, company1Stats.getEmployeeCount());
		assertEquals(12500.0, company1Stats.getAverageSalary(), 0.01);
		assertEquals("John Doe", company1Stats.getTopEarnerFullName());

		CompanyStatistics company2Stats = statistics.get("Company2");
		assertNotNull(company2Stats);
		assertEquals(1, company2Stats.getEmployeeCount());
		assertEquals(3500.0, company2Stats.getAverageSalary(), 0.01);
		assertEquals("Bob Brown", company2Stats.getTopEarnerFullName());
	}

	@Test
	void addEmployees_ShouldAddAllEmployees() {
		List<Employee> employees = List.of(employee1, employee2);
		employeeService.addEmployees(employees);

		List<Employee> allEmployees = employeeService.findAll();
		assertEquals(2, allEmployees.size());
		assertTrue(allEmployees.contains(employee1));
		assertTrue(allEmployees.contains(employee2));
	}
}