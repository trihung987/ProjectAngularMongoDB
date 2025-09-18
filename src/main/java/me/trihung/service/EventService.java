package me.trihung.service;

import java.util.List;

import me.trihung.dto.EventDto;
import me.trihung.dto.EventPageResponse;
import me.trihung.dto.request.EventRequest;

public interface EventService {
    EventDto createEvent(EventRequest eventRequest);
    EventDto saveDraft(EventRequest requestDto);
    List<EventDto> getAllEvents();
    EventDto getEventById(String id);
    void deleteEvent(String id);
    EventPageResponse getEventsPaged(int page, int size, String status, String search, String sortBy, String sortDirection, boolean hasOwner);
}