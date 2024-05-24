package fr.ensim.interop.introrest.controller;

import fr.ensim.interop.introrest.service.BlagueService;
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
	private final BlagueService blagueService;

	public MessageRestController(BlagueService blagueService) {
		this.blagueService = blagueService;
	}

	private String getTelegramApiUrl(String method) {
		return telegramApiUrl + telegramBotToken + "/" + method;
	}

	@PostMapping("/sendMessage")
	public String sendMessage(@RequestParam String chatId, @RequestParam String text) {
		String url = UriComponentsBuilder.fromHttpUrl(getTelegramApiUrl("sendMessage"))
				.queryParam("chat_id", chatId)
				.queryParam("text", text)
				.toUriString();

		url = decodeUrl(url);

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

		url = decodeUrl(url);

		return restTemplate.getForObject(url, String.class);
	}

	@GetMapping("/joke")
	public String getJoke() {
		return blagueService.getRandomJoke();
	}

	private String decodeUrl(String url) {
		try {
			return java.net.URLDecoder.decode(url, java.nio.charset.StandardCharsets.UTF_8.toString());
		} catch (java.io.UnsupportedEncodingException e) {
			throw new RuntimeException("Failed to decode URL", e);
		}
	}
}
