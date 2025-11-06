package service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.google.gson.Gson;
import exception.ApiException;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.util.List;
import model.Employee;
import model.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ApiServiceTest {

	@Mock private HttpClient mockHttpClient;
	@Mock private HttpResponse<String> mockResponse;

	private ApiService apiService;
	private Gson gson;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		gson = new Gson();
		apiService = new ApiService(mockHttpClient, gson, "https://jsonplaceholder.typicode.com/users");
	}

	@Test
	void fetchEmployeesFromApi_ShouldReturnEmployees_WhenApiCallSucceeds()
	  throws IOException, InterruptedException, ApiException {
		// Prepare mock response
		String jsonResponse = """
        [
          {
            "id": 1,
            "name": "John Doe",
            "email": "john@example.com",
            "company": {
              "name": "Test Company"
            }
          },
          {
            "id": 2,
            "name": "Jane Smith",
            "email": "jane@example.com",
            "company": {
              "name": "Another Company"
            }
          }
        ]
        """;

		// Configure mock behavior
		when(mockResponse.statusCode()).thenReturn(200);
		when(mockResponse.body()).thenReturn(jsonResponse);
		when(mockHttpClient.send(any(HttpRequest.class), any(BodyHandler.class))).thenReturn(mockResponse);

		// Execute the method
		List<Employee> employees = apiService.fetchEmployeesFromApi();

		// Verify results
		assertEquals(2, employees.size());

		Employee employee1 = employees.get(0);
		assertEquals("John", employee1.getFirstName());
		assertEquals("Doe", employee1.getLastName());
		assertEquals("john@example.com", employee1.getEmail());
		assertEquals("Test Company", employee1.getCompany());
		assertEquals(Position.PROGRAMISTA, employee1.getPosition());

		Employee employee2 = employees.get(1);
		assertEquals("Jane", employee2.getFirstName());
		assertEquals("Smith", employee2.getLastName());
		assertEquals("jane@example.com", employee2.getEmail());
		assertEquals("Another Company", employee2.getCompany());
		assertEquals(Position.PROGRAMISTA, employee2.getPosition());
	}

	@Test
	void fetchEmployeesFromApi_ShouldThrowApiException_WhenStatusCodeIsNotOk()
	  throws IOException, InterruptedException {
		// Configure mock behavior
		when(mockResponse.statusCode()).thenReturn(404);
		when(mockHttpClient.send(any(HttpRequest.class), any(BodyHandler.class))).thenReturn(mockResponse);

		// Execute and verify
		ApiException exception = assertThrows(ApiException.class, () -> apiService.fetchEmployeesFromApi());
		assertTrue(exception.getMessage().contains("API returned status code"));
	}

	@Test
	void fetchEmployeesFromApi_ShouldThrowApiException_WhenIOExceptionOccurs()
	  throws IOException, InterruptedException {
		// Configure mock to throw IOException
		when(mockHttpClient.send(any(HttpRequest.class), any(BodyHandler.class)))
		  .thenThrow(new IOException("Network error"));

		// Execute and verify
		ApiException exception = assertThrows(ApiException.class, () -> apiService.fetchEmployeesFromApi());
		assertTrue(exception.getMessage().contains("Failed to fetch data from API"));
	}

	@Test
	void fetchEmployeesFromApi_ShouldHandleEmptyResponse() throws IOException, InterruptedException, ApiException {
		// Prepare empty JSON array response
		String emptyJson = "[]";

		// Configure mock behavior
		when(mockResponse.statusCode()).thenReturn(200);
		when(mockResponse.body()).thenReturn(emptyJson);
		when(mockHttpClient.send(any(HttpRequest.class), any(BodyHandler.class))).thenReturn(mockResponse);

		// Execute the method
		List<Employee> employees = apiService.fetchEmployeesFromApi();

		// Verify results
		assertTrue(employees.isEmpty());
	}
}