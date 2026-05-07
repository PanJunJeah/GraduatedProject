package com.pan.graduatedproject.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String TRANSLATION_CACHE_PREFIX = "translation:";
    private static final long CACHE_EXPIRE_MINUTES = 30;

    public CacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String getCachedTranslation(String sourceText, String sourceLang, String targetLang) {
        try {
            String key = buildCacheKey(sourceText, sourceLang, targetLang);
            Object cached = redisTemplate.opsForValue().get(key);
            return cached != null ? cached.toString() : null;
        } catch (Exception e) {
            System.err.println("Cache get error: " + e.getMessage());
            return null;
        }
    }

    public void cacheTranslation(String sourceText, String translatedText, String sourceLang, String targetLang) {
        try {
            String key = buildCacheKey(sourceText, sourceLang, targetLang);
            redisTemplate.opsForValue().set(key, translatedText, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        } catch (Exception e) {
            System.err.println("Cache set error: " + e.getMessage());
        }
    }

    public void invalidateCache(String sourceText, String sourceLang, String targetLang) {
        try {
            String key = buildCacheKey(sourceText, sourceLang, targetLang);
            redisTemplate.delete(key);
        } catch (Exception e) {
            System.err.println("Cache invalidate error: " + e.getMessage());
        }
    }

    private String buildCacheKey(String text, String sourceLang, String targetLang) {
        return TRANSLATION_CACHE_PREFIX + sourceLang + ":" + targetLang + ":" + text.hashCode();
    }
}
