package MaciejNasiadka.Zadanie1;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Objects;

public class Employee {

	private final String firstName;
	private final String lastName;
	private final String email;
	private final String company;
	private final Position position;
	private final BigDecimal salary;

	public Employee(
	  String firstName, String lastName, String email, String company, Position position, BigDecimal salary) {
		this.firstName = requireText(firstName, "firstName");
		this.lastName = requireText(lastName, "lastName");
		this.email = requireText(email, "email").toLowerCase(Locale.ROOT);
		this.company = requireText(company, "company");
		this.position = Objects.requireNonNull(position, "position");
		this.salary = validateSalary(Objects.requireNonNull(salary, "salary"), position);
	}

	public Employee(String firstName, String lastName, String email, String company, Position position) {
		this(firstName, lastName, email, company, position, position.getBaseSalary());
	}

	private static String requireText(String value, String fieldName) {
		if (value == null || value.isBlank()) { throw new IllegalArgumentException(fieldName + " must not be blank"); }
		return value.trim();
	}

	private static BigDecimal validateSalary(BigDecimal salary, Position position) {
		if (salary.compareTo(position.getBaseSalary()) < 0) {
			throw new IllegalArgumentException("Salary must be at least base salary for position " +
											   position.getDisplayName());
		}
		return salary;
	}

	public String getFirstName() { return firstName; }

	public String getLastName() { return lastName; }

	public String getEmail() { return email; }

	public String getCompany() { return company; }

	public Position getPosition() { return position; }

	public BigDecimal getSalary() { return salary; }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Employee employee)) return false;
		return email.equals(employee.email);
	}

	@Override
	public int hashCode() {
		return Objects.hash(email);
	}

	@Override
	public String toString() {
		return "Employee{"
		  + "firstName='" + firstName + '\'' + ", lastName='" + lastName + '\'' + ", email='" + email + '\'' +
		  ", company='" + company + '\'' + ", position=" + position.getDisplayName() + ", salary=" + salary + '}';
	}
}