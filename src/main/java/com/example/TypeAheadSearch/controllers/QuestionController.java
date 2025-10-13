package com.example.TypeAheadSearch.controllers;

import com.example.TypeAheadSearch.dto.QuestionRequestDTO;
import com.example.TypeAheadSearch.dto.QuestionResponseDTO;
import com.example.TypeAheadSearch.services.AutoCompleteService;
import com.example.TypeAheadSearch.services.IQuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@Slf4j
public class QuestionController {

    private final IQuestionService questionService;
    private final AutoCompleteService autocompleteService;

    @PostMapping
    public ResponseEntity<QuestionResponseDTO> createQuestion(
            @Valid @RequestBody QuestionRequestDTO questionRequestDTO) {
        return new ResponseEntity<>(
                questionService.createQuestion(questionRequestDTO),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<QuestionResponseDTO> getQuestionById(@PathVariable String id) {
        return ResponseEntity.ok(questionService.getQuestionById(id));
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<Void> deleteQuestionById(@PathVariable String id) {
        questionService.deleteQuestionById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<QuestionResponseDTO> updateQuestionById(
            @PathVariable String id,
            @Valid @RequestBody QuestionRequestDTO questionRequestDTO) {
        return ResponseEntity.ok(
                questionService.updateQuestionById(id, questionRequestDTO)
        );
    }

    /**
     * Endpoint: Record a user clicking on a search result
     * Path: POST /api/questions/search/record?query=mongodb
     */
    @PostMapping("/search/record")
    public ResponseEntity<Void> recordSearch(@RequestParam String query) {
        autocompleteService.recordSearch(query);
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint: Get autocomplete suggestions for a prefix
     * Path: GET /api/questions/autocomplete?prefix=mon
     */
    @GetMapping("/autocomplete")
    public ResponseEntity<List<String>> getAutocompleteSuggestions(
            @RequestParam String prefix) {
        List<String> suggestions = autocompleteService.getAutocompleteSuggestions(prefix);
        return ResponseEntity.ok(suggestions);
    }

    /**
     * Endpoint: Rebuild autocomplete cache from database
     * Path: POST /api/questions/rebuild-cache
     * (Protected endpoint in production)
     */
    @PostMapping("/rebuild-cache")
    public ResponseEntity<Void> rebuildCache() {
        autocompleteService.rebuildAutocompleteCache();
        return ResponseEntity.ok().build();
    }
}
