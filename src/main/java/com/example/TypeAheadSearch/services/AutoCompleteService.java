package com.example.TypeAheadSearch.services;

import com.example.TypeAheadSearch.dto.QuestionResponseDTO;
import com.example.TypeAheadSearch.repositories.QuestionRepository;
import com.example.TypeAheadSearch.repositories.RedisSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutoCompleteService {

    private final RedisSearchRepository redisSearchRepository;
    private final QuestionRepository questionRepository;

    /**
     * Handle a search query: update counts and autocomplete data
     */
    public void recordSearch(String query) {
        log.info("Recording search for query: {}", query);

        // Step 1: Increment search count
        Long newCount = redisSearchRepository.incrementSearchCount(query);

        // Step 2: Update all prefixes with this query
        List<String> prefixes = redisSearchRepository.getAllPrefixes(query);
        for (String prefix : prefixes) {
            redisSearchRepository.addToAutocompletePrefix(prefix, query, newCount);
        }

        log.debug("Recorded search for '{}' with count {}", query, newCount);
    }

    /**
     * Get top K autocomplete suggestions for a given prefix
     */
    public List<String> getAutocompleteSuggestions(String prefix) {
        log.info("Fetching autocomplete suggestions for prefix: {}", prefix);

        if (prefix == null || prefix.trim().isEmpty()) {
            return Collections.emptyList();
        }

        return redisSearchRepository.getTopKQueriesForPrefix(prefix.toLowerCase());
    }

    /**
     * Rebuild autocomplete cache from MongoDB
     * Call this on application startup or after Redis cache flush
     */
    public void rebuildAutocompleteCache() {
        log.info("Starting autocomplete cache rebuild from database");

        // Clear existing autocomplete data
        redisSearchRepository.clearAllAutocompleteData();

        // Fetch all questions from MongoDB
        var allQuestions = questionRepository.findAll();
        List<String> queryStrings = allQuestions.stream()
                .map(q -> q.getTitle().toLowerCase())
                .collect(Collectors.toList());

        // Rebuild Redis data
        redisSearchRepository.rebuildAutocompleteFromQueries(queryStrings);

        log.info("Autocomplete cache rebuild completed");
    }
}
