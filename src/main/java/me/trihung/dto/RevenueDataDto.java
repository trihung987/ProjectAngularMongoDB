package me.trihung.dto;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor 
@Builder
public class RevenueDataDto {
    private String month; 
    private BigDecimal revenue; // Change Long to Double
    private Long orders;
    private Long events;  
}