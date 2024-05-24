package fr.ensim.interop.introrest.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Random;

@RestController
public class MessageRestController {


	@Value("${telegram.api.url}")
	private String telegramApiUrl;

	@Value("${telegram.bot.token}")
	private String telegramBotToken;

	@Value("${open.weather.api.url}")
	private String weatherApiUrl;

	@Value("${open.weather.api.token}")
	private String weatherApiToken;

	private final RestTemplate restTemplate = new RestTemplate();
	private final List<String> jokes = List.of(
			"Joke 1",
			"Joke 2",
			"Joke 3",
			"Joke 4",
			"Joke 5"
	);

	private String getTelegramApiUrl(String method) {
		return telegramApiUrl + telegramBotToken + "/" + method;
	}

	@PostMapping("/sendMessage")
	public String sendMessage(@RequestParam String chatId, @RequestParam String text) {
		String url = UriComponentsBuilder.fromHttpUrl(getTelegramApiUrl("sendMessage"))
				.queryParam("chat_id", chatId)
				.queryParam("text", text)
				.toUriString();
		return restTemplate.getForObject(url, String.class);
	}

	@GetMapping("/weather")
	public String getWeather(@RequestParam double lat, @RequestParam double lon) {
		String url = UriComponentsBuilder.fromHttpUrl(weatherApiUrl + "weather")
				.queryParam("lat", lat)
				.queryParam("lon", lon)
				.queryParam("appid", weatherApiToken)
				.queryParam("units", "metric")
				.toUriString();
		return restTemplate.getForObject(url, String.class);
	}

	@GetMapping("/joke")
	public String getJoke() {
		Random random = new Random();
		return jokes.get(random.nextInt(jokes.size()));
	}
}
