package com.pan.graduatedproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TranslationResponse {
    private String translatedText;
    private String sourceLang;
    private String targetLang;
    private Long historyId;
}
