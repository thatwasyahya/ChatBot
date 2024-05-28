package fr.ensim.interop.introrest.controller;

import fr.ensim.interop.introrest.model.telegram.Message;
import fr.ensim.interop.introrest.model.weather.WeatherResponse;
import fr.ensim.interop.introrest.model.blague.BlagueResponse;
import fr.ensim.interop.introrest.model.news.NewsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;

@RestController
public class MessageRestController {

    @Value("${telegram.api.url}")
    private String apiUrl;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${open.weather.api.url}")
    private String weatherApiUrl;

    @Value("${open.weather.api.token}")
    private String weatherApiToken;

    @Value("${blagues.api.url}")
    private String blaguesApiUrl;

    @Value("${blagues.api.token}")
    private String blaguesApiToken;

    @Value("${rapidapi.news.url}")
    private String newsApiUrl;

    @Value("${rapidapi.news.key}")
    private String newsApiKey;

    private final RestTemplate restTemplate;
    private static final Logger logger = Logger.getLogger(MessageRestController.class.getName());

    public MessageRestController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void handleMessage(Message message) {
        String text = message.getText();
        String chatId = message.getChat().getId().toString();
        String responseText = "";

        logger.info("Handling message: " + text);

        if (text.startsWith("hello")) {
            responseText = "Hello Mr. " + message.getFrom().getFirstName();
        } else if (text.toLowerCase().startsWith("meteo")) {
            responseText = handleMeteoCommand(text);
        } else if (text.toLowerCase().startsWith("blague")) {
            responseText = getBlague();
        } else if (text.toLowerCase().startsWith("actualité")) {
            responseText = getActualite();
        } else {
            responseText = "Command not recognized.";
        }

        logger.info("Response text: " + responseText);
        sendMessage(chatId, responseText);
    }

    private String handleMeteoCommand(String text) {
        String[] parts = text.split(" ");
        if (parts.length < 2) {
            return "Veuillez fournir une ville après le mot 'meteo'. Exemple: meteo Paris";
        }
        String city = parts[1];
        return getWeather(city);
    }
    @GetMapping("/weather")
    private String getWeather(String city) {
        try {
            String url = weatherApiUrl + "weather?q=" + city + "&appid=" + weatherApiToken + "&units=metric";
            ResponseEntity<WeatherResponse> response = restTemplate.getForEntity(url, WeatherResponse.class);
            if (response.getBody() != null) {
                WeatherResponse weatherResponse = response.getBody();
                if (weatherResponse.getWeather() != null && !weatherResponse.getWeather().isEmpty()) {
                    String description = weatherResponse.getWeather().get(0).getDescription();
                    double temperatureCelsius = weatherResponse.getMain().getTemp();
                    return "La météo à " + city + " est " + description + " avec une température de " + String.format("%.2f", temperatureCelsius) + "°C.";
                } else {
                    return "Impossible de récupérer la description de la météo pour " + city + ".";
                }
            }
        } catch (Exception e) {
            return "Ville " + city + " non trouvée ou erreur lors de la récupération des données météo.";
        }
        return "Erreur inconnue lors de la récupération des données météo.";
    }




    @GetMapping("/joke")
    private String getBlague() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + blaguesApiToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = blaguesApiUrl + "random";
        logger.info("Fetching joke from: " + url);
        ResponseEntity<BlagueResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, BlagueResponse.class);
        if (response.getBody() != null) {
            return response.getBody().getJoke() + " " + response.getBody().getAnswer();
        }
        return "Pas de blague trouvée.";
    }

    private String getActualite() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", newsApiKey);
        headers.set("X-RapidAPI-Host", "google-news13.p.rapidapi.com");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = newsApiUrl + "&lr=fr-FR";
        logger.info("Fetching news from: " + url);
        ResponseEntity<NewsResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, NewsResponse.class);
        if (response.getBody() != null && !response.getBody().getArticles().isEmpty()) {
            return response.getBody().getArticles().get(0).getTitle();
        }
        return "Pas d'actualités trouvées.";
    }

    @PostMapping("/sendMessage")
    private void sendMessage(String chatId, String text) {
        String url = apiUrl + botToken + "/sendMessage?chat_id=" + chatId + "&text=" + text;
        logger.info("Sending message to: " + url);
        restTemplate.getForObject(url, String.class);
    }
}
