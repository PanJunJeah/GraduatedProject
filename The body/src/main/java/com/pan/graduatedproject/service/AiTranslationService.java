package com.pan.graduatedproject.service;

import com.pan.graduatedproject.dto.TranslationRequest;
import com.pan.graduatedproject.dto.TranslationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AiTranslationService {

    private final RestTemplate restTemplate;

    @Value("${ai.service.url}")
    private String aiServiceUrl;

    public AiTranslationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String translate(String text, String srcLang, String tgtLang) {
        try {
            String url = aiServiceUrl + "/api/translate";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("text", text);
            requestBody.put("src_lang", srcLang);
            requestBody.put("tgt_lang", tgtLang);

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
                    return (String) body.get("data");
                }
            }
            throw new RuntimeException("Translation service returned invalid response");
        } catch (Exception e) {
            throw new RuntimeException("Failed to translate: " + e.getMessage(), e);
        }
    }
}
