package com.pan.graduatedproject.service;

import com.pan.graduatedproject.dto.TermDTO;
import com.pan.graduatedproject.dto.TermRequest;
import com.pan.graduatedproject.entity.TermGlossary;
import com.pan.graduatedproject.entity.User;
import com.pan.graduatedproject.repository.TermGlossaryRepository;
import com.pan.graduatedproject.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TermGlossaryService {

    private final TermGlossaryRepository glossaryRepository;
    private final UserRepository userRepository;

    public TermGlossaryService(TermGlossaryRepository glossaryRepository, UserRepository userRepository) {
        this.glossaryRepository = glossaryRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TermDTO addTerm(TermRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (glossaryRepository.existsByUserIdAndSourceTermAndTargetLang(
                user.getId(), request.getSourceTerm(), request.getTargetLang())) {
            throw new RuntimeException("Term already exists in glossary");
        }

        TermGlossary term = new TermGlossary();
        term.setUser(user);
        term.setSourceLang(request.getSourceLang());
        term.setTargetLang(request.getTargetLang());
        term.setSourceTerm(request.getSourceTerm());
        term.setTargetTerm(request.getTargetTerm());
        term.setDescription(request.getDescription());

        TermGlossary saved = glossaryRepository.save(term);
        return toDTO(saved);
    }

    public List<TermDTO> getUserTerms(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return glossaryRepository.findByUserId(user.getId())
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<TermDTO> getTermsByLanguagePair(String username, String sourceLang, String targetLang) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return glossaryRepository.findByUserIdAndSourceLangAndTargetLang(user.getId(), sourceLang, targetLang)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TermDTO updateTerm(Long termId, TermRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TermGlossary term = glossaryRepository.findById(termId)
                .orElseThrow(() -> new RuntimeException("Term not found"));

        if (!term.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to update this term");
        }

        term.setSourceLang(request.getSourceLang());
        term.setTargetLang(request.getTargetLang());
        term.setSourceTerm(request.getSourceTerm());
        term.setTargetTerm(request.getTargetTerm());
        term.setDescription(request.getDescription());

        TermGlossary updated = glossaryRepository.save(term);
        return toDTO(updated);
    }

    @Transactional
    public void deleteTerm(Long termId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TermGlossary term = glossaryRepository.findById(termId)
                .orElseThrow(() -> new RuntimeException("Term not found"));

        if (!term.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to delete this term");
        }

        glossaryRepository.delete(term);
    }

    private TermDTO toDTO(TermGlossary term) {
        TermDTO dto = new TermDTO();
        dto.setId(term.getId());
        dto.setSourceLang(term.getSourceLang());
        dto.setTargetLang(term.getTargetLang());
        dto.setSourceTerm(term.getSourceTerm());
        dto.setTargetTerm(term.getTargetTerm());
        dto.setDescription(term.getDescription());
        dto.setCreatedAt(term.getCreatedAt());
        return dto;
    }
}
