package com.pan.graduatedproject.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "translation_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranslationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String sourceText;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String translatedText;

    @Column(name = "source_lang", nullable = false, length = 10)
    private String sourceLang;

    @Column(name = "target_lang", nullable = false, length = 10)
    private String targetLang;

    @Column(name = "translation_type", length = 20)
    private String translationType;

    @Column(columnDefinition = "TEXT")
    private String context;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
