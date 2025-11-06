package config;

import com.google.gson.Gson;
import java.net.http.HttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

	@Bean
	public HttpClient httpClient() {
		System.out.println("Creating HttpClient bean...");
		return HttpClient.newHttpClient();
	}

	@Bean
	public Gson gson() {
		System.out.println("Creating Gson bean...");
		return new Gson();
	}
}