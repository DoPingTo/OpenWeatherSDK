

import app.services.WeatherServ;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WeatherServTest {

    @Test
    void testSingletonBehavior() {
        WeatherServ ws1 = WeatherServ.init("KEY_123", false, 10, new String[]{"London"});
        WeatherServ ws2 = WeatherServ.init("KEY_123", true, 10, new String[]{"Paris"});
        
        assertSame(ws1, ws2, "Instances with the same API key should be identical");
    }

    @Test
    void testDifferentApiKeysCreateDifferentInstances() {
        WeatherServ ws1 = WeatherServ.init("KEY_1", false, 10, new String[]{"London"});
        WeatherServ ws2 = WeatherServ.init("KEY_2", false, 10, new String[]{"London"});
        assertNotSame(ws1, ws2, "Different API keys should produce different instances");
    }

    @Test
    void testCacheTimeoutBehavior() throws InterruptedException {
        var ws = WeatherServ.init("KEY_X", false, 10, new String[]{"London"});
        ws.getWeather("London");
        var first = ws.getWeather("London");
        assertNotNull(first);
        var second = ws.getWeather("London");
        assertNotNull(second);
    }
}
