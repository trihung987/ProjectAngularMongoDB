package me.trihung.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopEventDtoDetailed {
    private String name;
    private BigDecimal revenue;
    private long tickets;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    
    // Helper method to calculate status based on dates
    public void calculateStatus() {
        LocalDate now = LocalDate.now();
        if (startDate != null && now.isBefore(startDate)) {
            this.status = "upcoming";
        } else if (endDate != null && now.isAfter(endDate)) {
            this.status = "completed";
        } else {
            this.status = "active";
        }
    }
    
    // Constructor from basic TopEventDto
    public TopEventDtoDetailed(TopEventDto basic) {
        this.name = basic.getName();
        this.revenue = basic.getRevenue();
        this.tickets = basic.getTickets();
        this.status = basic.getStatus();
    }
    
    // Convert to basic TopEventDto
    public TopEventDto toBasic() {
        return new TopEventDto(name, revenue, tickets, status);
    }
}