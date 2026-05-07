package com.pan.graduatedproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatchTranslationResponse {
    private List<String> translations;
    private List<Long> historyIds;
    private int successCount;
    private int totalCount;
}
