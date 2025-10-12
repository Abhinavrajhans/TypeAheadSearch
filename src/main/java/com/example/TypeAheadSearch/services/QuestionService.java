package com.example.TypeAheadSearch.services;

import com.example.TypeAheadSearch.adapters.QuestionAdapter;
import com.example.TypeAheadSearch.dto.QuestionRequestDTO;
import com.example.TypeAheadSearch.dto.QuestionResponseDTO;
import com.example.TypeAheadSearch.execptions.ResourceNotFoundException;
import com.example.TypeAheadSearch.models.Question;
import com.example.TypeAheadSearch.repositories.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionService implements IQuestionService {

    private final QuestionRepository questionRepository;

    @Override
    @CacheEvict(cacheNames = "questions", allEntries = true)
    public QuestionResponseDTO createQuestion(QuestionRequestDTO questionRequestDTO) {
        log.info("Creating question with title: {}", questionRequestDTO.getTitle());
        Question question = QuestionAdapter.toEntity(questionRequestDTO);
        Question saved = questionRepository.save(question);
        log.info("Question created successfully with id: {}", saved.getId());
        return QuestionAdapter.toDTO(saved);
    }

    @Override
    @Cacheable(cacheNames = "questions", key = "#id")
    public QuestionResponseDTO getQuestionById(String id) {
        log.info("Fetching question with id: {}", id);
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));
        log.info("Question found: {}", id);
        return QuestionAdapter.toDTO(question);
    }

    @Override
    @CacheEvict(cacheNames = "questions", allEntries = true)
    public void deleteQuestionById(String id) {
        log.info("Deleting question with id: {}", id);
        if (!questionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Question not found with id: " + id);
        }
        questionRepository.deleteById(id);
        log.info("Question deleted successfully with id: {}", id);
    }

    @Override
    @CacheEvict(cacheNames = "questions", allEntries = true)
    public QuestionResponseDTO updateQuestionById(String id, QuestionRequestDTO questionRequestDTO) {
        log.info("Updating question with id: {}", id);
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));

        log.info("Question found, updating fields");
        question.setTitle(questionRequestDTO.getTitle());
        question.setContent(questionRequestDTO.getContent());

        Question updated = questionRepository.save(question);
        log.info("Question updated successfully with id: {}", id);
        return QuestionAdapter.toDTO(updated);
    }
}
