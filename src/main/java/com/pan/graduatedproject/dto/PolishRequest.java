package com.pan.graduatedproject.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PolishRequest {
    @NotBlank(message = "Text is required")
    private String text;

    private String style = "formal";

    private String language = "zh";
}
