package app.components;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class CashWeather {

    public static int cashSize = 10;
    private static long cashTimeInvalid = 600_000;

    private static final Map<String, Map<String, Object>> cache = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Map<String, Object>> eldest) {
            return size() > cashSize;
        }
    };

    public static synchronized Map<String, Object> get(String cityName) {
        Map<String, Object> cityMap = cache.get(cityName);
        if (cityMap == null) {
            return null;
        }

        Long timestamp = (Long) cityMap.get("timestamp");
        if (System.currentTimeMillis() >= timestamp + cashTimeInvalid) {
            cache.remove(cityName);
            return null;
        }

        return (Map<String, Object>) cityMap.get("data");
    }

    public static synchronized Map<String, Map<String, Object>> get() {
        return cache;
    }

    public static synchronized void set(Map<String, Object> data) {
        if (data == null || !data.containsKey("name")) {
            throw new IllegalArgumentException("Invalid data: missing 'name' field");
        }

        Map<String, Object> tmpData = new HashMap<>();
        tmpData.put("data", data);
        tmpData.put("timestamp", System.currentTimeMillis());

        String name = data.get("name").toString();
        cache.put(name, tmpData);
    }

    /**
     * Очистить весь кэш.
     */
    public static synchronized void clear() {
        cache.clear();
    }

    /**
     * Проверить размер кэша (для отладки).
     */
    public static synchronized int size() {
        return cache.size();
    }
}
