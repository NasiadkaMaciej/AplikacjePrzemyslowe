package service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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

public class ApiService {
	private final String apiUrl;
	private final HttpClient client;
	private final Gson gson;

	public ApiService(String apiUrl) {
		this.apiUrl = apiUrl;
		this.client = createHttpClient();
		this.gson = new Gson();
	}

	protected HttpClient createHttpClient() { return HttpClient.newHttpClient(); }

	public List<Employee> fetchEmployeesFromApi() throws ApiException {
		try {
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).GET().build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() != 200) {
				throw new ApiException("API request failed with status code: " + response.statusCode());
			}

			return parseEmployeesFromJson(response.body());

		} catch (IOException | InterruptedException e) { throw new ApiException("Error during API request", e); }
	}

	private List<Employee> parseEmployeesFromJson(String json) throws ApiException {
		List<Employee> employees = new ArrayList<>();

		try {
			JsonArray jsonArray = gson.fromJson(json, JsonArray.class);

			for (JsonElement element : jsonArray) {
				JsonObject userObject = element.getAsJsonObject();

				// Extract and split the full name
				String fullName = userObject.get("name").getAsString();
				String[] nameParts = fullName.split(" ", 2);
				String firstName = nameParts[0];
				String lastName = nameParts.length > 1 ? nameParts[1] : "";

				String email = userObject.get("email").getAsString();
				String company = userObject.getAsJsonObject("company").get("name").getAsString();

				// Pracownicy z API to programiści
				Position position = Position.PROGRAMISTA;

				employees.add(new Employee(firstName, lastName, email, company, position));
			}

			return employees;

		} catch (Exception e) { throw new ApiException("Error parsing JSON response", e); }
	}
}