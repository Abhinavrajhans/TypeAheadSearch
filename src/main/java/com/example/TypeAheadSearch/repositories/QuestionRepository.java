package com.example.TypeAheadSearch.repositories;

import com.example.TypeAheadSearch.models.Question;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends MongoRepository<Question,String> {
}
