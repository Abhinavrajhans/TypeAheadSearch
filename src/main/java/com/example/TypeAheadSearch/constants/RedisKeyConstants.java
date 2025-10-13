package com.example.TypeAheadSearch.constants;

public class RedisKeyConstants {

    // Search count store: hash containing <query, count>
    public static final String SEARCH_COUNT_HASH = "search:count";

    // Autocomplete prefix store: ZSET for each prefix
    // Pattern: "autocomplete:prefix:{prefix}" -> ZSET of (score=count, member=fullQuery)
    public static final String AUTOCOMPLETE_PREFIX_PREFIX = "autocomplete:prefix:";

    // Configuration
    public static final int TOP_K = 10;
    public static final long ZSET_EXPIRY_SECONDS = 86400 * 7; // 7 days
}
