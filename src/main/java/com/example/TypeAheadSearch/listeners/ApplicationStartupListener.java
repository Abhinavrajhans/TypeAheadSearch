package com.example.TypeAheadSearch.listeners;

import com.example.TypeAheadSearch.services.AutoCompleteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationStartupListener {

    private final AutoCompleteService autocompleteService;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("Application startup: Rebuilding autocomplete cache");
        autocompleteService.rebuildAutocompleteCache();
    }
}