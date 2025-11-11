package app.components;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * async http client for OpenWeather
 */

public class OpenWeatherClient {

    private final String apiKey;
    private final String unitParam;
    private final String langParam;
    private final String baseUrlWeather = "https://api.openweathermap.org/data/2.5/weather?";
    private final HttpClient client = HttpClient.newHttpClient();
    private final static ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public OpenWeatherClient(String apiKey, String unit) {
        this.apiKey = apiKey;
        this.unitParam = "&units=" + unit;
        this.langParam = "&lang=en";
    }

    /**
     * Returns weather data for a specific city.
     *
     * @param cityName the name of the city (e.g. "London")
     * @return CompletableFuture containing a Map with weather data (ResponseBuilder
     *         format);
     *         if {@code cityName} is null or empty, returns completedFuture(null)
     */

    public CompletableFuture<Map<String, Object>> getWeatherByCity(String cityName) {
        if (cityName == null || cityName.isBlank()) {
            CompletableFuture<Map<String, Object>> failed = new CompletableFuture<>();
            failed.completeExceptionally(new IllegalArgumentException("cityName cannot be null or empty"));
            return failed;
        }
        String encodedCity = URLEncoder.encode(cityName, StandardCharsets.UTF_8);
        String cityParam = "q=" + encodedCity;
        String appid = "&appid=" + this.apiKey;
        String uriString = baseUrlWeather + cityParam + appid + this.unitParam + this.langParam;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uriString))
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(body -> {
                    try {
                        return mapper.readValue(body, Map.class);
                    } catch (Exception e) {
                        throw new RuntimeException("Ошибка парсинга JSON", e);
                    }
                });

    }

}
