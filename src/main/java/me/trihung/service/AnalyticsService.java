package me.trihung.service;

import me.trihung.dto.EventTypeRevenueDto;
import me.trihung.dto.RevenueDataDto;
import me.trihung.dto.TopEventDto;

import java.util.List;

public interface AnalyticsService {

    List<RevenueDataDto> getRevenueData(int year);

    List<EventTypeRevenueDto> getEventTypeRevenue(int year, String eventType);

    List<TopEventDto> getTopEvents(int year);
}