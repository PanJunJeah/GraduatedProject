package com.pan.graduatedproject.service;

import com.pan.graduatedproject.dto.TranslationRequest;
import com.pan.graduatedproject.dto.TranslationResponse;
import com.pan.graduatedproject.dto.HistoryDTO;
import com.pan.graduatedproject.entity.TranslationHistory;
import com.pan.graduatedproject.entity.User;
import com.pan.graduatedproject.repository.TranslationHistoryRepository;
import com.pan.graduatedproject.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TranslationService {

    private final AiTranslationService aiTranslationService;
    private final TranslationHistoryRepository historyRepository;
    private final UserRepository userRepository;

    public TranslationService(AiTranslationService aiTranslationService,
                              TranslationHistoryRepository historyRepository,
                              UserRepository userRepository) {
        this.aiTranslationService = aiTranslationService;
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
    }

    public TranslationResponse translate(TranslationRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String translatedText = aiTranslationService.translate(
                request.getSourceText(),
                request.getSourceLang(),
                request.getTargetLang()
        );

        TranslationHistory history = new TranslationHistory();
        history.setUser(user);
        history.setSourceText(request.getSourceText());
        history.setTranslatedText(translatedText);
        history.setSourceLang(request.getSourceLang());
        history.setTargetLang(request.getTargetLang());
        history.setTranslationType(request.getTranslationType());
        history.setContext(request.getContext());

        TranslationHistory savedHistory = historyRepository.save(history);

        TranslationResponse response = new TranslationResponse();
        response.setTranslatedText(translatedText);
        response.setSourceLang(request.getSourceLang());
        response.setTargetLang(request.getTargetLang());
        response.setHistoryId(savedHistory.getId());

        return response;
    }

    public List<HistoryDTO> getHistory(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<TranslationHistory> histories = historyRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        return histories.stream().map(this::toHistoryDTO).collect(Collectors.toList());
    }

    private HistoryDTO toHistoryDTO(TranslationHistory history) {
        HistoryDTO dto = new HistoryDTO();
        dto.setId(history.getId());
        dto.setSourceText(history.getSourceText());
        dto.setTranslatedText(history.getTranslatedText());
        dto.setSourceLang(history.getSourceLang());
        dto.setTargetLang(history.getTargetLang());
        dto.setTranslationType(history.getTranslationType());
        dto.setCreatedAt(history.getCreatedAt());
        return dto;
    }
}
