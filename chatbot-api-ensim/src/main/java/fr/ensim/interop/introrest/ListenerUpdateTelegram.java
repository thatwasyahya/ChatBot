package fr.ensim.interop.introrest;

import fr.ensim.interop.introrest.model.telegram.ApiResponseUpdateTelegram;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class ListenerUpdateTelegram implements CommandLineRunner {

	@Value("${telegram.api.url}")
	private String telegramApiUrl;

	@Value("${telegram.bot.token}")
	private String telegramBotToken;

	@Value("${open.weather.api.url}")
	private String weatherApiUrl;

	@Value("${open.weather.api.token}")
	private String weatherApiToken;

	private final RestTemplate restTemplate = new RestTemplate();
	private int offset = 0;

	private String getTelegramApiUrl(String method) {
		return telegramApiUrl + telegramBotToken + "/" + method;
	}

	@Override
	public void run(String... args) {
		Logger.getLogger("ListenerUpdateTelegram").log(Level.INFO, "Démarrage du listener d'updates Telegram...");

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				pollUpdates();
			}
		}, 0, 5000); // Poll every 5 seconds
	}

	private void pollUpdates() {
		String url = getTelegramApiUrl("getUpdates") + "?offset=" + offset;
		try {
			ApiResponseUpdateTelegram response = restTemplate.getForObject(url, ApiResponseUpdateTelegram.class);

			if (response != null && response.getResult() != null) {
				response.getResult().forEach(update -> {
					if (update.getMessage() != null) {
						String text = update.getMessage().getText();
						String chatId = String.valueOf(update.getMessage().getChat().getId());
						String userName = update.getMessage().getFrom().getFirstName();

						if (text.contains("hello")) {
							sendMessage(chatId, "Hello " + userName + "!");
						} else if (text.contains("meteo")) {
							// Utiliser des valeurs de latitude et longitude par défaut ou demander à l'utilisateur
							double lat = 44.34;
							double lon = 10.99;
							String weatherResponse = getWeather(lat, lon);
							sendMessage(chatId, "Voici la météo actuelle : " + weatherResponse);
						} else if (text.contains("blague")) {
							sendMessage(chatId, "Fetching a joke...");
							sendMessage(chatId, getJoke());
						}

						offset = update.getUpdateId() + 1;
					}
				});
			}
		} catch (RestClientException e) {
			// Handle connection errors
			e.printStackTrace();
		}
	}

	private void sendMessage(String chatId, String text) {
		String url = UriComponentsBuilder.fromHttpUrl(getTelegramApiUrl("sendMessage"))
				.queryParam("chat_id", chatId)
				.queryParam("text", text)
				.toUriString();
		try {
			restTemplate.getForObject(url, String.class);
		} catch (RestClientException e) {
			// Handle connection errors
			e.printStackTrace();
		}
	}

	private String getWeather(double lat, double lon) {
		String url = UriComponentsBuilder.fromHttpUrl(weatherApiUrl + "weather")
				.queryParam("lat", lat)
				.queryParam("lon", lon)
				.queryParam("appid", weatherApiToken)
				.queryParam("units", "metric")
				.toUriString();
		Logger.getLogger("ListenerUpdateTelegram").log(Level.INFO, "Weather API URL: " + url);
		return restTemplate.getForObject(url, String.class);
	}

	private String getJoke() {
		// Implement the joke fetching logic here
		return "Here is a joke!";
	}
}
