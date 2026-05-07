package com.pan.graduatedproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TermDTO {
    private Long id;
    private String sourceLang;
    private String targetLang;
    private String sourceTerm;
    private String targetTerm;
    private String description;
    private LocalDateTime createdAt;
}
