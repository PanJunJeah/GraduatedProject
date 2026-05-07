package com.pan.graduatedproject.repository;

import com.pan.graduatedproject.entity.TermGlossary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TermGlossaryRepository extends JpaRepository<TermGlossary, Long> {
    List<TermGlossary> findByUserId(Long userId);
    List<TermGlossary> findByUserIdAndSourceLangAndTargetLang(Long userId, String sourceLang, String targetLang);
    Optional<TermGlossary> findByUserIdAndSourceTermAndTargetLang(Long userId, String sourceTerm, String targetLang);
    boolean existsByUserIdAndSourceTermAndTargetLang(Long userId, String sourceTerm, String targetLang);
}
