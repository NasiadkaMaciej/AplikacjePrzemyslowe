package MaciejNasiadka.Zadanie1;

import exception.ApiException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import model.CompanyStatistics;
import model.ImportSummary;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import service.ApiService;
import service.ImportService;

@SpringBootApplication
public class Main {

	public static final String RESET = "\u001B[0m";
	public static final String GREEN = "\u001B[32m";
	public static final String RED = "\u001B[31m";

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
		System.err.println("Uruchomiono aplikację Zadanie1");
	}

	@Bean
	CommandLineRunner demo(EmployeeService employeeService) {
		return args -> {
			System.out.println(GREEN + "--- Podstawowe operacje na pracownikach ---" + RESET);
			// Dodaj przykładowych pracowników
			employeeService.addEmployee(new Employee("Pracownik1",
													 "Nazwisko1",
													 "Pracownik1@Firma1.pl",
													 "Firma1",
													 Position.MANAGER,
													 BigDecimal.valueOf(13_500)));
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
			employeeService.addEmployee(new Employee("Pracownik5",
													 "Nazwisko5",
													 "Pracownik5@Firma3.pl",
													 "Firma3",
													 Position.STAZYSTA,
													 BigDecimal.valueOf(3_200)));
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

			System.out.println(GREEN + "\n--- Import i API ---" + RESET);

			// Inicjalizacja dodatkowych serwisów
			ImportService importService = new ImportService(employeeService);
			ApiService apiService = new ApiService("https://jsonplaceholder.typicode.com/users");

			// Import z CSV
			Path csvPath = Paths.get("employees.csv");
			ImportSummary importSummary = importService.importFromCsv(csvPath);
			System.out.println("Podsumowanie importu: " + importSummary);

			// Pobieranie danych z API
			try {
				List<Employee> apiEmployees = apiService.fetchEmployeesFromApi();
				employeeService.addEmployees(apiEmployees);
				System.out.println("Zaimportowano " + apiEmployees.size() + " pracowników z API");
			} catch (ApiException e) { System.err.println("API error: " + e.getMessage()); }

			// Walidacja spójności wynagrodzeń
			List<Employee> underpaidEmployees = employeeService.validateSalaryConsistency();
			System.out.println("\nŻle wynagradzani pracownicy:");
			underpaidEmployees.forEach(System.out::println);

			// Statystyki firma
			Map<String, CompanyStatistics> companyStats = employeeService.getCompanyStatistics();
			System.out.println("\nStatystyki firmy:");
			companyStats.forEach((company, stats) -> { System.out.println(company + ": " + stats); });
		};
	}
}