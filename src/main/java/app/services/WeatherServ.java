package app.services;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import app.components.CashWeather;
import app.components.OpenWeatherClient;
import app.components.ResponseBuilder;

public class WeatherServ {

    private static final Map<String, WeatherServ> INSTANCES = new ConcurrentHashMap<>();

    private final String apiKey;
    private final boolean pollingMode;
    private final int pollingPeriod;
    private final String[] cities;
    private final OpenWeatherClient client;
    private ScheduledExecutorService scheduler;

    private WeatherServ(String apiKey, boolean pollingMode, int pollingPeriod, String[] cities) {
        this.apiKey = apiKey;
        this.pollingMode = pollingMode;
        this.pollingPeriod = pollingPeriod;
        this.cities = cities;
        this.client = new OpenWeatherClient(apiKey, "metric");
        if (pollingMode) {
            startPollingWeather();
        }
    }

    /**
     * Initializes or returns an existing instance of {@link WeatherServ}.
     * <p>
     * This method ensures that only one instance exists per {@code apiKey}.
     * If an instance with the same key was already created, it will be returned
     * instead of a new one.
     *
     * @param apiKey        your OpenWeather API key
     * @param pollingMode   {@code true} to enable polling mode (auto-refresh),
     *                      {@code false} for on-demand mode
     * @param pollingPeriod refresh period in minutes (used only if polling mode is
     *                      enabled)
     * @param cities        list of cities whose data will be updated in polling
     *                      mode
     * @return a new or existing {@link WeatherServ} instance
     */

    public static synchronized WeatherServ init(String apiKey, boolean pollingMode, int pollingPeriod,
            String[] cities) {
        if (INSTANCES.containsKey(apiKey)) {
            return INSTANCES.get(apiKey);
        }
        WeatherServ newInstance = new WeatherServ(apiKey, pollingMode, pollingPeriod, cities);
        INSTANCES.put(apiKey, newInstance);
        return newInstance;
    }

    public static synchronized void rm(String apiKey) {
        INSTANCES.remove(apiKey);
    }

    public String getWeather(String cityName) {
        Map<String, Object> cached = CashWeather.get(cityName);
        if (cached != null) {
            return ResponseBuilder.buildJSON(cached);
        }

        CompletableFuture<Map<String, Object>> future = client.getWeatherByCity(cityName);
        Map<String, Object> rawResponse = future.join();
        Map<String, Object> response = ResponseBuilder.buildWeatherMap(rawResponse);
        CashWeather.set(response);
        return ResponseBuilder.buildJSON(response);
    }

    private void startPollingWeather() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        Runnable pollingTask = () -> {
            for (String city : cities) {
                try {
                    CompletableFuture<Map<String, Object>> f = client.getWeatherByCity(city);
                    Map<String, Object> raw = f.join();
                    Map<String, Object> parsed = ResponseBuilder.buildWeatherMap(raw);
                    CashWeather.set(parsed);
                    System.out.println("[Polling] Updated " + city);
                } catch (Exception e) {
                    System.err.println("[Polling] Error for " + city + ": " + e.getMessage());
                }
            }
        };
        scheduler.scheduleAtFixedRate(pollingTask, 0, this.pollingPeriod, TimeUnit.MINUTES);
    }

    public void stopPolling() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
            System.out.println("[Polling] stopped");
        }
    }

    public boolean isPollingMode() {
        return pollingMode;
    }

    public String getApiKey() {
        return apiKey;
    }
}
