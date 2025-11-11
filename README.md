# 🌦️ OpenWeather SDK (Java)

A lightweight and easy-to-use Java SDK for accessing the [OpenWeather API](https://openweathermap.org/api).  
The SDK provides both **on-demand** and **polling** modes for retrieving current weather information, with built-in caching and singleton instance management.

---

## 🚀 Features

✅ Retrieve **current weather** data by city name  
✅ Built-in **in-memory cache** (10-minute TTL)  
✅ Stores data for **up to 10 cities** simultaneously  
✅ **Polling mode** for automatic background updates  
✅ Thread-safe and lightweight design  
✅ **Singleton** behavior per API key (no duplicate instances)  
✅ Clean JSON output with essential weather fields only  
✅ Optional lightweight **HTTP server** for cache inspection (debugging)

---

## 🧩 Architecture Overview

```
app/
├── components/
│   ├── CashWeather.java        → In-memory cache with 10-minute expiration
│   ├── OpenWeatherClient.java  → Low-level async HTTP client for OpenWeather API
│   ├── ResponseBuilder.java    → Converts raw API response to normalized JSON
│   ├── SimpleServer.java       → Optional embedded HTTP server for debugging
└── services/
    └── WeatherServ.java        → Main SDK service (Singleton, cache + polling)
```

---

## ⚙️ How to Use

Create a `WeatherServ` instance with required parameters:

| Parameter | Description |
|------------|-------------|
| `apiKey` | Your OpenWeather API key |
| `pollingMode` | `true` for polling mode (auto-refresh), `false` for on-demand |
| `pollingPeriod` | Refresh interval in minutes (used only in polling mode) |
| `cities` | List of cities to track (only used in polling mode) |

Returns a new or existing **WeatherServ** instance (singleton per API key).

---

### 🧠 Components

- **CashWeather** – local cache (default: 10 cities, 10 min TTL).  
  You can modify cache size or expiration time if needed.

- **OpenWeatherClient** – asynchronous, low-level HTTP client for interacting with OpenWeather API.

- **WeatherServ** – main entry point of the SDK; provides high-level methods and manages polling.

- **SimpleServer** *(optional)* – lightweight HTTP server to inspect cached data in real time.

---

## 💡 Example

```java
import app.components.SimpleServer;
import app.services.WeatherServ;

public class App {
    public static void main(String[] args) {
        String apiKey = System.getenv("API_KEY");

        // Optional debug server to inspect cache
        SimpleServer server = new SimpleServer();
        server.run();

        String[] cities = {
            "London", "Moscow", "Krasnodar", "Los Angeles",
            "New York", "Oslo", "Rio de Janeiro"
        };

        WeatherServ sdk = WeatherServ.init(apiKey, false, 10, cities);

        for (String city : cities) {
            String response = sdk.getWeather(city);
            System.out.println(response);
        }
    }
}
```

---

## 🧪 Testing

To run integration tests:

```bash
mvn test
```

or from IDE (JUnit 5 supported).  
Make sure to set your `API_KEY` as an environment variable.

---

## 📦 Building

To package the SDK into a `.jar`:

```bash
mvn clean package
```

The JAR will be located at:
```
target/openweather-client-1.0.0.jar
```

You can then include it in your project:

```xml
<dependency>
  <groupId>com.openweather.sdk</groupId>
  <artifactId>openweather-client</artifactId>
  <version>1.0.0</version>
</dependency>
```

---

## 🧱 Design Principles

### 1. **Thread Safety**
- Shared resources such as cache and polling scheduler are synchronized or concurrent-safe.  
- Multiple SDK instances (with different API keys) can safely coexist.

### 2. **Singleton Pattern**
- Only one `WeatherServ` instance is allowed per API key.  
- Prevents redundant API requests and ensures consistent caching behavior.

### 3. **Asynchronous I/O**
- Uses Java’s `HttpClient.sendAsync()` and `CompletableFuture` for non-blocking operations.  
- Ideal for applications handling multiple city lookups simultaneously.

### 4. **Resilient Error Handling**
- All methods throw clear and descriptive exceptions.  
- JSON parsing and network errors are caught and rethrown as runtime exceptions.

### 5. **Extensibility**
- Modular design: low-level HTTP client (`OpenWeatherClient`) is decoupled from `WeatherServ`.  
- Can be extended for additional OpenWeather endpoints (e.g., forecasts, air pollution, geocoding).

---

## 🛡️ Error Handling

All SDK methods throw exceptions with descriptive messages in case of:
- Invalid API key  
- Invalid or empty city name  
- Network or JSON parsing errors

---

## 🧰 Requirements

- Java 11+  
- Maven 3.8+  
- Active OpenWeather API key

---

## 👨‍💻 Author & License

Developed by **[Your Name]**  
Licensed under the **MIT License**  
© 2025 OpenWeather SDK
