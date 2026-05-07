package com.pan.graduatedproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryDTO {
    private Long id;
    private String sourceText;
    private String translatedText;
    private String sourceLang;
    private String targetLang;
    private String translationType;
    private LocalDateTime createdAt;
}
