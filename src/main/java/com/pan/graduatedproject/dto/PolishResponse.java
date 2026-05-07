package com.pan.graduatedproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolishResponse {
    private String polishedText;
    private String style;
    private String language;
}
