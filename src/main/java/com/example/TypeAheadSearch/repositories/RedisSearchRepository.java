package com.example.TypeAheadSearch.repositories;

import com.example.TypeAheadSearch.constants.RedisKeyConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RedisSearchRepository implements IRedisSearchRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Increment search count for a query and return the new count
     */
    public Long incrementSearchCount(String query) {
        String countKey = RedisKeyConstants.SEARCH_COUNT_HASH;
        Long newCount = redisTemplate.opsForHash().increment(countKey, query, 1);
        log.debug("Incremented search count for query '{}' to {}", query, newCount);
        return newCount;
    }

    /**
     * Get current search count for a query
     */
    public Long getSearchCount(String query) {
        Object count = redisTemplate.opsForHash().get(RedisKeyConstants.SEARCH_COUNT_HASH, query);
        return count != null ? Long.parseLong(count.toString()) : 0L;
    }

    /**
     * Add or update a query in the autocomplete ZSET for a given prefix
     * Score is the search count (higher score = higher popularity)
     */
    public void addToAutocompletePrefix(String prefix, String query, Long score) {
        String prefixKey = RedisKeyConstants.AUTOCOMPLETE_PREFIX_PREFIX + prefix;

        redisTemplate.opsForZSet().add(prefixKey, query, score.doubleValue());

        // Trim to keep only top K
        Long size = redisTemplate.opsForZSet().size(prefixKey);
        if (size != null && size > RedisKeyConstants.TOP_K) {
            redisTemplate.opsForZSet().removeRange(prefixKey, 0, size - RedisKeyConstants.TOP_K - 1);
        }

        // Set expiry on the ZSET
        redisTemplate.expire(prefixKey, RedisKeyConstants.ZSET_EXPIRY_SECONDS, java.util.concurrent.TimeUnit.SECONDS);

        log.debug("Added '{}' to prefix '{}' with score {}", query, prefix, score);
    }

    /**
     * Fetch top K queries for a given prefix (sorted by score descending)
     */
    public List<String> getTopKQueriesForPrefix(String prefix) {
        String prefixKey = RedisKeyConstants.AUTOCOMPLETE_PREFIX_PREFIX + prefix;

        Set<Object> results = redisTemplate.opsForZSet()
                .reverseRange(prefixKey, 0, RedisKeyConstants.TOP_K - 1);

        if (results == null || results.isEmpty()) {
            log.debug("No autocomplete results found for prefix '{}'", prefix);
            return Collections.emptyList();
        }

        List<String> queries = results.stream()
                .map(Object::toString)
                .collect(Collectors.toList());

        log.debug("Fetched {} results for prefix '{}'", queries.size(), prefix);
        return queries;
    }

    /**
     * Get all prefixes of a given query
     * E.g., "mongodb" -> ["m", "mo", "mon", "mong", "mongo", "mongod", "mongodb"]
     */
    public List<String> getAllPrefixes(String query) {
        List<String> prefixes = new ArrayList<>();
        for (int i = 1; i <= query.length(); i++) {
            prefixes.add(query.substring(0, i).toLowerCase());
        }
        return prefixes;
    }

    /**
     * Batch add queries to all their respective prefixes
     * Used during Redis recovery/rebuild from database
     */
    public void rebuildAutocompleteFromQueries(List<String> queries) {
        Map<String, Long> queryCounts = new HashMap<>();

        // First, build the search count map
        for (String query : queries) {
            queryCounts.put(query, queryCounts.getOrDefault(query, 0L) + 1);
        }

        // Store counts in Redis hash
        for (Map.Entry<String, Long> entry : queryCounts.entrySet()) {
            redisTemplate.opsForHash().put(
                    RedisKeyConstants.SEARCH_COUNT_HASH,
                    entry.getKey(),
                    entry.getValue()
            );
        }

        // For each query, add it to all prefix ZSETs
        for (Map.Entry<String, Long> entry : queryCounts.entrySet()) {
            String query = entry.getKey();
            Long count = entry.getValue();

            List<String> prefixes = getAllPrefixes(query);
            for (String prefix : prefixes) {
                addToAutocompletePrefix(prefix, query, count);
            }
        }

        log.info("Rebuilt autocomplete data for {} unique queries", queryCounts.size());
    }

    /**
     * Clear all autocomplete data (for maintenance/testing)
     */
    public void clearAllAutocompleteData() {
        Set<String> keys = redisTemplate.keys(RedisKeyConstants.AUTOCOMPLETE_PREFIX_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info("Cleared {} autocomplete prefix keys", keys.size());
        }
    }
}
