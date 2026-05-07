package com.pan.graduatedproject.repository;

import com.pan.graduatedproject.entity.TranslationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TranslationHistoryRepository extends JpaRepository<TranslationHistory, Long> {
    List<TranslationHistory> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<TranslationHistory> findByUserIdAndSourceLangOrderByCreatedAtDesc(Long userId, String sourceLang);
    List<TranslationHistory> findByUserIdAndTargetLangOrderByCreatedAtDesc(Long userId, String targetLang);
}
