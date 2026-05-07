package com.pan.graduatedproject.controller;

import com.pan.graduatedproject.dto.*;
import com.pan.graduatedproject.entity.User;
import com.pan.graduatedproject.service.AiPolishingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/polish")
public class PolishController {

    private final AiPolishingService polishingService;

    public PolishController(AiPolishingService polishingService) {
        this.polishingService = polishingService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PolishResponse>> polish(
            @Valid @RequestBody PolishRequest request,
            @AuthenticationPrincipal User user) {
        try {
            PolishResponse response = polishingService.polish(request);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
