package me.trihung.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrderPageResponse {
    private List<OrderDto> content;
    private long totalElements;
    private int totalPages;
    private int page;
    private int size;
}
