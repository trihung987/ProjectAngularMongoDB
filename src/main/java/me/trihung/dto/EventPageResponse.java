package me.trihung.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventPageResponse {
    private List<EventDto> events;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
}
