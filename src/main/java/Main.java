import exception.ApiException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import model.CompanyStatistics;
import model.Employee;
import model.ImportSummary;
import model.Position;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import service.ApiService;
import service.EmployeeService;
import service.ImportService;

@SpringBootApplication
@ComponentScan(basePackages = { "service", "config" })
@ImportResource("classpath:employees-beans.xml")
public class Main implements CommandLineRunner {

	public static final String RESET = "\u001B[0m";
	public static final String GREEN = "\u001B[32m";
	public static final String RED = "\u001B[31m";

	private final EmployeeService employeeService;
	private final ImportService importService;
	private final ApiService apiService;
	private final List<Employee> xmlEmployees;

	public Main(EmployeeService employeeService,
				ImportService importService,
				ApiService apiService,
				@Qualifier("xmlEmployees") List<Employee> xmlEmployees) {
		this.employeeService = employeeService;
		this.importService = importService;
		this.apiService = apiService;
		this.xmlEmployees = xmlEmployees;
	}

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
		System.err.println("Uruchomiono aplikację Zadanie1");
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("\n========================================");
		System.out.println("Starting Employee Management Application");
		System.out.println("========================================\n");

		System.out.println(GREEN + "--- Dodawanie pracowników z XML ---" + RESET);
		System.out.println("Ładowanie " + xmlEmployees.size() + " pracowników z konfiguracji XML...");
		employeeService.addEmployees(xmlEmployees);
		System.out.println("Dodano pracowników z XML:");
		xmlEmployees.forEach(System.out::println);

		System.out.println(GREEN + "\n--- Podstawowe operacje na pracownikach ---" + RESET);
		employeeService.addEmployee(new Employee(
		  "Pracownik1", "Nazwisko1", "Pracownik1@Firma1.pl", "Firma1", Position.MANAGER, BigDecimal.valueOf(13_500)));
		employeeService.addEmployee(new Employee("Pracownik2",
												 "Nazwisko2",
												 "Pracownik2@Firma1.pl",
												 "Firma1",
												 Position.PROGRAMISTA,
												 BigDecimal.valueOf(9_200)));
		employeeService.addEmployee(new Employee("Pracownik3",
												 "Nazwisko3",
												 "Pracownik3@Firma1.pl",
												 "Firma1",
												 Position.WICEPREZES,
												 BigDecimal.valueOf(19_000)));
		employeeService.addEmployee(new Employee("Pracownik4",
												 "Nazwisko4",
												 "Pracownik4@Firma2.pl",
												 "Firma2",
												 Position.PROGRAMISTA,
												 BigDecimal.valueOf(8_700)));
		employeeService.addEmployee(new Employee(
		  "Pracownik5", "Nazwisko5", "Pracownik5@Firma3.pl", "Firma3", Position.STAZYSTA, BigDecimal.valueOf(3_200)));
		employeeService.addEmployee(
		  new Employee("Pracownik6", "Nazwisko6", "Pracownik6@Firma3.pl", "Firma3", Position.STAZYSTA));

		System.out.println("Wszyscy pracownicy:");
		employeeService.findAll().forEach(System.out::println);

		System.out.println("\nPracownicy w Firma4:");
		employeeService.findByCompany("Firma4").forEach(System.out::println);

		System.out.println("\nPracownicy posortowani według nazwiska:");
		employeeService.findAllSortedByLastName().forEach(System.out::println);

		System.out.println("\nGrupowanie według stanowiska:");
		employeeService.groupByPosition().forEach(
		  (position, employees) -> System.out.println(position.getDisplayName() + " -> " + employees));

		System.out.println("\nLiczba pracowników na stanowisku:");
		employeeService.countByPosition().forEach(
		  (position, count) -> System.out.println(position.getDisplayName() + " -> " + count));

		employeeService.calculateAverageSalary().ifPresent(
		  average -> System.out.println("\nŚrednie wynagrodzenie: " + average));

		employeeService.findHighestPaidEmployee().ifPresent(
		  highest -> System.out.println("Najwyższe wynagrodzenie: " + highest));

		System.out.println(GREEN + "\n--- Import z CSV ---" + RESET);
		Path csvPath = Paths.get("employees.csv");
		ImportSummary importSummary = importService.importFromCsv(csvPath);
		System.out.println("Podsumowanie importu: " + importSummary);

		System.out.println(GREEN + "\n--- Pobieranie danych z API ---" + RESET);
		try {
			List<Employee> apiEmployees = apiService.fetchEmployeesFromApi();
			employeeService.addEmployees(apiEmployees);
			System.out.println("Zaimportowano " + apiEmployees.size() + " pracowników z API");
		} catch (ApiException e) { System.err.println("API error: " + e.getMessage()); }

		System.out.println(GREEN + "\n--- Walidacja wynagrodzeń ---" + RESET);
		List<Employee> underpaidEmployees = employeeService.validateSalaryConsistency();
		System.out.println("Źle wynagradzani pracownicy:");
		underpaidEmployees.forEach(System.out::println);

		System.out.println(GREEN + "\n--- Statystyki firm ---" + RESET);
		Map<String, CompanyStatistics> companyStats = employeeService.getCompanyStatistics();
		System.out.println("Statystyki firmy:");
		companyStats.forEach((company, stats) -> { System.out.println(company + ": " + stats); });

		System.out.println("\n========================================");
		System.out.println("Application finished successfully!");
		System.out.println("========================================\n");
	}
}