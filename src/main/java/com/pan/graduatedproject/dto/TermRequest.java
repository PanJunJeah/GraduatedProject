package com.pan.graduatedproject.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TermRequest {
    @NotBlank(message = "Source term is required")
    private String sourceTerm;

    @NotBlank(message = "Target term is required")
    private String targetTerm;

    private String sourceLang = "zh";

    private String targetLang = "en";

    private String description;
}
