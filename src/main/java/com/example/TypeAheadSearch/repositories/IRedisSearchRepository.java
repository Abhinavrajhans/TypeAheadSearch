package com.example.TypeAheadSearch.repositories;


import java.util.List;

public interface IRedisSearchRepository {

    /**
     * Increment search count for a query and return the new count
     */
    Long incrementSearchCount(String query);

    /**
     * Get current search count for a query
     */
    Long getSearchCount(String query);

    /**
     * Add or update a query in the autocomplete ZSET for a given prefix
     */
    void addToAutocompletePrefix(String prefix, String query, Long score);

    /**
     * Fetch top K queries for a given prefix (sorted by score descending)
     */
    List<String> getTopKQueriesForPrefix(String prefix);

    /**
     * Get all prefixes of a given query
     */
    List<String> getAllPrefixes(String query);

    /**
     * Batch add queries to all their respective prefixes
     */
    void rebuildAutocompleteFromQueries(List<String> queries);

    /**
     * Clear all autocomplete data
     */
    void clearAllAutocompleteData();
}