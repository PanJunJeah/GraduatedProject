package com.pan.graduatedproject.controller;

import com.pan.graduatedproject.dto.*;
import com.pan.graduatedproject.entity.User;
import com.pan.graduatedproject.service.BatchTranslationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/translation/batch")
public class BatchTranslationController {

    private final BatchTranslationService batchTranslationService;

    public BatchTranslationController(BatchTranslationService batchTranslationService) {
        this.batchTranslationService = batchTranslationService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BatchTranslationResponse>> batchTranslate(
            @Valid @RequestBody BatchTranslationRequest request,
            @AuthenticationPrincipal User user) {
        try {
            BatchTranslationResponse response = batchTranslationService.batchTranslate(request, user.getUsername());
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
