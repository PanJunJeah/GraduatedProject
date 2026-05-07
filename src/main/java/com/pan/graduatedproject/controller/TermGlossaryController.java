package com.pan.graduatedproject.controller;

import com.pan.graduatedproject.dto.*;
import com.pan.graduatedproject.entity.User;
import com.pan.graduatedproject.service.TermGlossaryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/glossary")
public class TermGlossaryController {

    private final TermGlossaryService glossaryService;

    public TermGlossaryController(TermGlossaryService glossaryService) {
        this.glossaryService = glossaryService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TermDTO>> addTerm(
            @Valid @RequestBody TermRequest request,
            @AuthenticationPrincipal User user) {
        try {
            TermDTO term = glossaryService.addTerm(request, user.getUsername());
            return ResponseEntity.ok(ApiResponse.success("Term added successfully", term));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TermDTO>>> getTerms(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String sourceLang,
            @RequestParam(required = false) String targetLang) {
        try {
            List<TermDTO> terms;
            if (sourceLang != null && targetLang != null) {
                terms = glossaryService.getTermsByLanguagePair(user.getUsername(), sourceLang, targetLang);
            } else {
                terms = glossaryService.getUserTerms(user.getUsername());
            }
            return ResponseEntity.ok(ApiResponse.success(terms));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TermDTO>> updateTerm(
            @PathVariable Long id,
            @Valid @RequestBody TermRequest request,
            @AuthenticationPrincipal User user) {
        try {
            TermDTO term = glossaryService.updateTerm(id, request, user.getUsername());
            return ResponseEntity.ok(ApiResponse.success("Term updated successfully", term));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTerm(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        try {
            glossaryService.deleteTerm(id, user.getUsername());
            return ResponseEntity.ok(ApiResponse.success("Term deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
