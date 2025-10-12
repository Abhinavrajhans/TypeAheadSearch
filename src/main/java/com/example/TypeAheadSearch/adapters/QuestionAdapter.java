package com.example.TypeAheadSearch.adapters;

import com.example.TypeAheadSearch.dto.QuestionRequestDTO;
import com.example.TypeAheadSearch.dto.QuestionResponseDTO;
import com.example.TypeAheadSearch.models.Question;

public class QuestionAdapter {

    public static Question toEntity(QuestionRequestDTO questionRequestDTO)
    {
        return Question.builder()
                .title(questionRequestDTO.getTitle())
                .content(questionRequestDTO.getContent())
                .build();
    }

    public static QuestionResponseDTO toDTO(Question question){
        return QuestionResponseDTO.builder()
                .id(question.getId())
                .title(question.getTitle())
                .content(question.getContent())
                .createdAt(question.getCreatedAt())
                .build();
    }
}

