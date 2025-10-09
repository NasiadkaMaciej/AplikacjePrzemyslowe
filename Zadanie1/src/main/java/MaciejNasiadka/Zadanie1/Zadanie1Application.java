package MaciejNasiadka.Zadanie1;

import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Zadanie1Application {

	public static void main(String[] args) {
		SpringApplication.run(Zadanie1Application.class, args);
		System.err.println("Uruchomiono aplikację Zadanie1");
	}

	@Bean
	CommandLineRunner demo(EmployeeService employeeService) {
		return args -> {
			employeeService.addEmployee(new Employee(
			  "Pracownik1", "Nazwisko1", "email1@email.pl", "Firma1", Position.MANAGER, BigDecimal.valueOf(13_500)));
			employeeService.addEmployee(new Employee(
			  "Pracownik2", "Nazwisko2", "email2@email.pl", "Firma1", Position.PROGRAMISTA, BigDecimal.valueOf(9_200)));
			employeeService.addEmployee(new Employee(
			  "Pracownik3", "Nazwisko3", "email3@email.pl", "Firma1", Position.WICEPREZES, BigDecimal.valueOf(19_000)));
			employeeService.addEmployee(new Employee(
			  "Pracownik4", "Nazwisko4", "email4@email.pl", "Firma2", Position.PROGRAMISTA, BigDecimal.valueOf(8_700)));
			employeeService.addEmployee(new Employee(
			  "Pracownik5", "Nazwisko5", "email5@email.pl", "Firma3", Position.STAZYSTA, BigDecimal.valueOf(3_200)));
            employeeService.addEmployee(new Employee(
                    "Pracownik6", "Nazwisko6", "email6@email.pl", "Firma3", Position.STAZYSTA));

			System.out.println("Wszyscy pracownicy:");
			employeeService.findAll().forEach(System.out::println);

			System.out.println("\nPracownicy w TechCorp:");
			employeeService.findByCompany("TechCorp").forEach(System.out::println);

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
		};
	}
}
