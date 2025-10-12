package com.example.TypeAheadSearch.controllers;

import com.example.TypeAheadSearch.dto.QuestionRequestDTO;
import com.example.TypeAheadSearch.dto.QuestionResponseDTO;
import com.example.TypeAheadSearch.services.IQuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final IQuestionService questionService;

    @PostMapping
    public ResponseEntity<QuestionResponseDTO> createQuestion(@Valid @RequestBody QuestionRequestDTO questionRequestDTO)
    {
        return new ResponseEntity<>(questionService.createQuestion(questionRequestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<QuestionResponseDTO> getQuestionById(@PathVariable String id){
        return ResponseEntity.ok(questionService.getQuestionById(id));
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<Void> deleteQuestionById(@PathVariable String id){
        questionService.deleteQuestionById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<QuestionResponseDTO>  updateQuestionById(@PathVariable String id, @Valid @RequestBody QuestionRequestDTO questionRequestDTO){
        return ResponseEntity.ok(questionService.updateQuestionById(id, questionRequestDTO));
    }


}
