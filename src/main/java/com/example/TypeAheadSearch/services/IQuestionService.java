package com.example.TypeAheadSearch.services;

import com.example.TypeAheadSearch.dto.QuestionRequestDTO;
import com.example.TypeAheadSearch.dto.QuestionResponseDTO;

public interface IQuestionService {

    public QuestionResponseDTO createQuestion(QuestionRequestDTO questionRequestDTO);
    public QuestionResponseDTO getQuestionById(String id);
    public QuestionResponseDTO updateQuestionById(String id, QuestionRequestDTO questionRequestDTO);
    public void deleteQuestionById(String id);
}
