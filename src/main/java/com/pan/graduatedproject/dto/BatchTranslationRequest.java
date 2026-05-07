package com.pan.graduatedproject.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class BatchTranslationRequest {
    @NotEmpty(message = "Texts list cannot be empty")
    private List<String> texts;

    private String sourceLang = "zh";

    private String targetLang = "en";
}
