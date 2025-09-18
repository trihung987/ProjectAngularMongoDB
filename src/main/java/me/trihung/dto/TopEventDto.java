package me.trihung.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopEventDto {
    private String name;
    private BigDecimal revenue;
    private long tickets;
    private String status;

}