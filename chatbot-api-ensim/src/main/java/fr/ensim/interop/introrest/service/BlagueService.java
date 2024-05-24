package fr.ensim.interop.introrest.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.logging.Logger;

@Service
public class BlagueService {

    @Value("${blagues.api.url}")
    private String blaguesApiUrl;

    @Value("${blagues.api.token}")
    private String blaguesApiToken;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final Logger logger = Logger.getLogger(BlagueService.class.getName());

    public String getRandomJoke() {
        String url = blaguesApiUrl + "random";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + blaguesApiToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                logger.warning("Failed to get a joke: " + response.getStatusCode());
                return "Failed to get a joke.";
            }
        } catch (Exception e) {
            logger.severe("Exception while getting a joke: " + e.getMessage());
            return "Exception while getting a joke.";
        }
    }
}
