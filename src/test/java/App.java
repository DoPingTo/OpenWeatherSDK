import app.components.SimpleServer;
import app.services.WeatherServ;

public class App {
    public static void main(String[] args) {
        String apiKey = System.getenv("apiKey");
        SimpleServer server = new SimpleServer();
        server.run();
        String[] cities = {"London", "Moscow", "Krasnodar","Jeddah", "Jerusalem", "Los Angeles",
                        "Marigot", "Maseru", "Niamey", "Nouakchott", "Novosibirsk", "New York",
                    "Oslo", "Phnom Penh", "Quezon City", "Rawalpindi", "Rio de Janeiro", "Santa Cruz de la Sierra", "South Tangerang",
                "Ulan Bator", "Yokohama", "Xi'an", "Sofia"};
        WeatherServ serv =  WeatherServ.init(apiKey, false,  10 ,cities);
        for (String city : cities) {
                String rsp = serv.getWeather(city);
                System.out.println(rsp);
        }
    }
}
