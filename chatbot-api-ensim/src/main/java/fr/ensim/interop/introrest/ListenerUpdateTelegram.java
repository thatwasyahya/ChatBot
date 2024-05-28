package fr.ensim.interop.introrest;

import fr.ensim.interop.introrest.model.telegram.ApiResponseUpdateTelegram;
import fr.ensim.interop.introrest.controller.MessageRestController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class ListenerUpdateTelegram implements CommandLineRunner {

	@Value("${telegram.api.url}")
	private String apiUrl;

	@Value("${telegram.bot.token}")
	private String botToken;

	private final RestTemplate restTemplate;
	private final MessageRestController messageController;
	private int offset = 0;
	private static final Logger logger = Logger.getLogger(ListenerUpdateTelegram.class.getName());

	public ListenerUpdateTelegram(RestTemplate restTemplate, MessageRestController messageController) {
		this.restTemplate = restTemplate;
		this.messageController = messageController;
	}

	private String getTelegramApiUrl(String method) {
		return apiUrl + botToken + "/" + method;
	}

	@Override
	public void run(String... args) {
		logger.info("Démarrage du listener d'updates Telegram...");

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				pollUpdates();
			}
		}, 0, 3000);
	}

	public void pollUpdates() {
		String url = getTelegramApiUrl("getUpdates") + "?offset=" + offset;
		logger.info("Polling updates from Telegram API: " + url);

		try {
			ApiResponseUpdateTelegram response = restTemplate.getForObject(url, ApiResponseUpdateTelegram.class);
			if (response != null && response.getResult() != null) {
				response.getResult().forEach(update -> {
					if (update.getMessage() != null) {
						logger.info("Received message: " + update.getMessage().getText());
						messageController.handleMessage(update.getMessage());
					}
					offset = update.getUpdateId() + 1;
				});
			} else {
				logger.warning("No updates received or response is null");
			}
		} catch (RestClientException e) {
			logger.log(Level.SEVERE, "Exception while polling updates", e);
		}
	}
}