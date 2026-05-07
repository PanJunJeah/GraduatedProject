package com.pan.graduatedproject.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class TranslationMemoryService {

    private final RestTemplate restTemplate;

    @Value("${qdrant.host}")
    private String host;

    @Value("${qdrant.port}")
    private int port;

    @Value("${qdrant.collection.name}")
    private String collectionName;

    private static final int VECTOR_SIZE = 64;

    public TranslationMemoryService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void initCollection() {
        try {
            String url = getBaseUrl() + "/collections/" + collectionName;
            Map<?, ?> existing = restTemplate.getForObject(url, Map.class);

            if (existing == null) {
                throw new RuntimeException("Empty response");
            }
        } catch (Exception ignored) {
            try {
                String createUrl = getBaseUrl() + "/collections/" + collectionName;
                Map<String, Object> body = new HashMap<>();
                Map<String, Object> vectors = new HashMap<>();
                Map<String, Object> params = new HashMap<>();
                params.put("size", VECTOR_SIZE);
                params.put("distance", "Cosine");
                vectors.put("size", VECTOR_SIZE);
                vectors.put("distance", "Cosine");
                body.put("vectors", vectors);
                restTemplate.put(createUrl, body);
            } catch (Exception e) {
                System.err.println("Failed to initialize Qdrant collection: " + e.getMessage());
            }
        }
    }

    public void addTranslation(String sourceText, String translatedText, String sourceLang, String targetLang) {
        try {
            String url = getBaseUrl() + "/collections/" + collectionName + "/points?wait=true";

            Map<String, Object> payload = new HashMap<>();
            payload.put("source_text", sourceText);
            payload.put("translated_text", translatedText);
            payload.put("source_lang", sourceLang);
            payload.put("target_lang", targetLang);

            Map<String, Object> point = new HashMap<>();
            point.put("id", UUID.randomUUID().toString());
            point.put("vector", generateFakeVector(sourceText));
            point.put("payload", payload);

            Map<String, Object> body = new HashMap<>();
            body.put("points", List.of(point));

            restTemplate.put(url, body);

        } catch (Exception e) {
            System.err.println("Failed to add translation to memory: " + e.getMessage());
        }
    }

    public List<Map<String, String>> searchSimilar(String query, String sourceLang, String targetLang, int limit) {
        List<Map<String, String>> results = new ArrayList<>();
        try {
            String url = getBaseUrl() + "/collections/" + collectionName + "/points/search";

            Map<String, Object> body = new HashMap<>();
            body.put("vector", generateFakeVector(query));
            body.put("limit", limit);
            body.put("with_payload", true);

            Map<?, ?> response = restTemplate.postForObject(url, body, Map.class);
            if (response == null) {
                return results;
            }

            Object resultObj = response.get("result");
            if (!(resultObj instanceof List<?> list)) {
                return results;
            }

            for (Object itemObj : list) {
                if (!(itemObj instanceof Map<?, ?> item)) {
                    continue;
                }
                Object payloadObj = item.get("payload");
                if (!(payloadObj instanceof Map<?, ?> payload)) {
                    continue;
                }

                Map<String, String> out = new HashMap<>();
                out.put("sourceText", String.valueOf(payload.get("source_text")));
                out.put("translatedText", String.valueOf(payload.get("translated_text")));
                out.put("score", String.valueOf(item.get("score")));
                results.add(out);
            }
        } catch (Exception e) {
            System.err.println("Failed to search translation memory: " + e.getMessage());
        }
        return results;
    }

    private List<Double> generateFakeVector(String text) {
        Random random = new Random(text.hashCode());
        List<Double> vector = new ArrayList<>();
        for (int i = 0; i < VECTOR_SIZE; i++) {
            vector.add(random.nextDouble());
        }
        return vector;
    }

    private String getBaseUrl() {
        return "http://" + host + ":" + port;
    }
}
