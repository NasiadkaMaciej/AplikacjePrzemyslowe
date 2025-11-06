package service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import exception.ApiException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import model.Employee;
import model.Position;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ApiService {
	private final HttpClient httpClient;
	private final Gson gson;
	private final String apiUrl;

	public ApiService(HttpClient httpClient, Gson gson, @Value("${app.api.url}") String apiUrl) {
		this.httpClient = httpClient;
		this.gson = gson;
		this.apiUrl = apiUrl;
		System.out.println("ApiService has been created with dependencies injected!");
		System.out.println("API URL: " + apiUrl);
	}

	public List<Employee> fetchEmployeesFromApi() throws ApiException {
		try {
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).GET().build();

			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() != 200) {
				throw new ApiException("API returned status code: " + response.statusCode());
			}

			return parseApiResponse(response.body());
		} catch (IOException | InterruptedException e) {
			throw new ApiException("Failed to fetch data from API: " + e.getMessage(), e);
		}
	}

	private List<Employee> parseApiResponse(String jsonResponse) {
		List<Employee> employees = new ArrayList<>();
		JsonArray jsonArray = gson.fromJson(jsonResponse, JsonArray.class);

		for (int i = 0; i < jsonArray.size(); i++) {
			JsonObject userJson = jsonArray.get(i).getAsJsonObject();

			String firstName = userJson.get("name").getAsString().split(" ")[0];
			String lastName = userJson.get("name").getAsString().split(" ").length > 1
								? userJson.get("name").getAsString().split(" ")[1]
								: "";
			String email = userJson.get("email").getAsString();
			String company = userJson.getAsJsonObject("company").get("name").getAsString();
			// Pracownicy z API to programiści
			employees.add(new Employee(firstName, lastName, email, company, Position.PROGRAMISTA));
		}

		return employees;
	}
}