package com.pan.graduatedproject.service;

import com.pan.graduatedproject.dto.BatchTranslationRequest;
import com.pan.graduatedproject.dto.BatchTranslationResponse;
import com.pan.graduatedproject.dto.TranslationRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BatchTranslationService {

    private final RestTemplate restTemplate;
    private final TranslationService translationService;

    @Value("${ai.service.url}")
    private String aiServiceUrl;

    public BatchTranslationService(RestTemplate restTemplate, TranslationService translationService) {
        this.restTemplate = restTemplate;
        this.translationService = translationService;
    }

    public BatchTranslationResponse batchTranslate(BatchTranslationRequest request, String username) {
        List<String> translations = new ArrayList<>();
        List<Long> historyIds = new ArrayList<>();
        int successCount = 0;

        try {
            String url = aiServiceUrl + "/api/translate/batch";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("texts", request.getTexts());
            requestBody.put("src_lang", request.getSourceLang());
            requestBody.put("tgt_lang", request.getTargetLang());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                if ("success".equals(body.get("status"))) {
                    @SuppressWarnings("unchecked")
                    List<String> results = (List<String>) body.get("data");

                    for (int i = 0; i < results.size(); i++) {
                        String translatedText = results.get(i);
                        translations.add(translatedText);
                        successCount++;

                        try {
                            TranslationRequest translationRequest = new TranslationRequest();
                            translationRequest.setSourceText(request.getTexts().get(i));
                            translationRequest.setSourceLang(request.getSourceLang());
                            translationRequest.setTargetLang(request.getTargetLang());
                            translationRequest.setTranslationType("batch");

                            var translationResponse = translationService.translate(translationRequest, username);
                            historyIds.add(translationResponse.getHistoryId());
                        } catch (Exception e) {
                            historyIds.add(-1L);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Batch translation via AI service failed, falling back to individual translation: " + e.getMessage());

            for (String text : request.getTexts()) {
                try {
                    TranslationRequest translationRequest = new TranslationRequest();
                    translationRequest.setSourceText(text);
                    translationRequest.setSourceLang(request.getSourceLang());
                    translationRequest.setTargetLang(request.getTargetLang());
                    translationRequest.setTranslationType("batch");

                    var translationResponse = translationService.translate(translationRequest, username);
                    translations.add(translationResponse.getTranslatedText());
                    historyIds.add(translationResponse.getHistoryId());
                    successCount++;
                } catch (Exception ex) {
                    translations.add("");
                    historyIds.add(-1L);
                }
            }
        }

        return new BatchTranslationResponse(translations, historyIds, successCount, request.getTexts().size());
    }
}
