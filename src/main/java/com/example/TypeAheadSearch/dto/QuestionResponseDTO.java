package com.example.TypeAheadSearch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponseDTO implements Serializable {

    private String id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
}
