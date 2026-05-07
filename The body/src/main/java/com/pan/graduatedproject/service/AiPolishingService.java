package com.pan.graduatedproject.service;

import com.pan.graduatedproject.dto.PolishRequest;
import com.pan.graduatedproject.dto.PolishResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AiPolishingService {

    private final RestTemplate restTemplate;

    @Value("${ai.service.url}")
    private String aiServiceUrl;

    public AiPolishingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public PolishResponse polish(PolishRequest request) {
        try {
            String url = aiServiceUrl + "/api/polish";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("text", request.getText());
            requestBody.put("style", request.getStyle());
            requestBody.put("language", request.getLanguage());

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                if ("success".equals(body.get("status"))) {
                    PolishResponse polishResponse = new PolishResponse();
                    polishResponse.setPolishedText((String) body.get("data"));
                    polishResponse.setStyle((String) body.get("style"));
                    polishResponse.setLanguage((String) body.get("language"));
                    return polishResponse;
                }
            }
            throw new RuntimeException("Polishing service returned invalid response");
        } catch (Exception e) {
            throw new RuntimeException("Failed to polish: " + e.getMessage(), e);
        }
    }
}
