package app.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.*;

public class ResponseBuilder {

    private final static ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    /**
     * Returns structured map
     * @param inputMap - response from openWeather
     * @return rturn structured Map<String, Object>
     */
    public static Map<String, Object> buildWeatherMap(Map<String, Object> inputMap) {

        Map<String, Object> response = new HashMap<>();
        response.put("errors", "");

        Map<String, Object> weatherMap = new HashMap<>();
        Map<String, Object> temperatureMap = new HashMap<>();
        Map<String, Object> windMap = new HashMap<>();
        Map<String, Object> sysMap = new HashMap<>();
        Map<String, Object> coordMap = new HashMap<>();

        // WEATHER
        try {
            List<Map<String, Object>> weatherList = (List<Map<String, Object>>) inputMap.get("weather");
            if (weatherList != null && !weatherList.isEmpty()) {
                Map<String, Object> first = weatherList.get(0);
                weatherMap.put("main", first.getOrDefault("main", ""));
                weatherMap.put("description", first.getOrDefault("description", ""));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // TEMPERATURE
        try {
            Map<String, Object> mainMap = (Map<String, Object>) inputMap.get("main");
            if (mainMap != null) {
                temperatureMap.put("temp", mainMap.getOrDefault("temp", ""));
                temperatureMap.put("feels_like", mainMap.getOrDefault("feels_like", ""));
                temperatureMap.put("humidity", mainMap.getOrDefault("humidity", ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // WIND
        try {
            Map<String, Object> windInput = (Map<String, Object>) inputMap.get("wind");
            if (windInput != null) {
                windMap.put("speed", windInput.getOrDefault("speed", ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // SYS → sunrise, sunset
        try {
            Map<String, Object> sysInput = (Map<String, Object>) inputMap.get("sys");
            if (sysInput != null) {
                sysMap.put("sunrise", sysInput.getOrDefault("sunrise", ""));
                sysMap.put("sunset", sysInput.getOrDefault("sunset", ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // VISIBILITY
        String visibilityStr = "";
        try {
            Object visibility = inputMap.get("visibility");
            visibilityStr = visibility != null ? visibility.toString() : "";
        } catch (Exception e) {
        }

        // DATETIME (OpenWeather использует "dt")
        Object datetime = inputMap.getOrDefault("dt", "");

        // TIMEZONE
        Object timezone = inputMap.getOrDefault("timezone", "");

        // CITY NAME
        Object cityName = inputMap.getOrDefault("name", "");

        // COORDINATES
        try {
            Map<String, Object> coordMapTmp = (Map<String, Object>) inputMap.get("coord");
            if (coordMapTmp != null) {
                coordMap.put("lat", coordMapTmp.getOrDefault("lat", ""));
                coordMap.put("lon", coordMapTmp.getOrDefault("lon", ""));
            }
        } catch (Exception e) {
            coordMap.put("lat", "");
            coordMap.put("lon", "");
        }

        // BUILD FINAL RESPONSE
        response.put("weather", weatherMap);
        response.put("temperature", temperatureMap);
        response.put("wind", windMap);
        response.put("sys", sysMap);
        response.put("visibility", visibilityStr);
        response.put("datetime", datetime);
        response.put("timezone", timezone);
        response.put("name", cityName);
        response.put("coordinates", coordMap);

        return response;
    }

    /**
     * Returns json String 
     *
     * @param inputMap  - any map
     * @return return json String 
     */
    public static String buildJSON(Map<String, Object> inputMap) {
        try {
            return mapper.writeValueAsString(inputMap);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сериализации в JSON", e);
        }
    }

}
