package com.pan.graduatedproject.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TranslationRequest {
    @NotBlank(message = "Source text is required")
    private String sourceText;

    private String sourceLang = "zh";

    private String targetLang = "en";

    private String context;

    private String translationType = "translate";
}
