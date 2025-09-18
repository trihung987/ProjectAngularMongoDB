package me.trihung.repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import me.trihung.dto.EventTypeRevenueDto;
import me.trihung.dto.OrderDto;
import me.trihung.dto.RevenueDataDto;
import me.trihung.dto.TopEventDto;
import me.trihung.entity.Order;
import me.trihung.entity.User;

@Repository
public class CustomOrderRepositoryImpl implements CustomOrderRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Page<OrderDto> findOrderDtosByOwner(User owner, Pageable pageable) {
        // Lookup operations to join with Zone and Event collections
        LookupOperation lookupZone = LookupOperation.newLookup()
                .from("zones")
                .localField("zone.$id")
                .foreignField("_id")
                .as("zoneData");

        LookupOperation lookupEvent = LookupOperation.newLookup()
                .from("events")
                .localField("zoneData.eventId")
                .foreignField("_id")
                .as("eventData");

        // Match operation to filter by owner
        MatchOperation matchOperation = Aggregation.match(
                Criteria.where("owner.$id").is(owner.getId())
        );

        // Unwind operations
        AggregationOperation unwindZone = Aggregation.unwind("zoneData");
        AggregationOperation unwindEvent = Aggregation.unwind("eventData");

        // Project to OrderDto structure
        ProjectionOperation projectionOperation = Aggregation.project()
                .and("_id").as("id")
                .and("zoneData._id").as("zoneId")
                .and("owner.$id").as("ownerId")
                .and("quantity").as("quantity")
                .and("totalAmount").as("totalAmount")
                .and("createdAt").as("createdAt")
                .and("zoneData.price").as("priceZone")
                .and("zoneData.name").as("nameZone")
                .and("eventData.eventName").as("nameEvent");

        // Add skip and limit for pagination
        AggregationOperation skipOperation = Aggregation.skip((long) pageable.getOffset());
        AggregationOperation limitOperation = Aggregation.limit(pageable.getPageSize());

        // Create aggregation pipeline
        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                lookupZone,
                unwindZone,
                lookupEvent,
                unwindEvent,
                projectionOperation,
                skipOperation,
                limitOperation
        );

        AggregationResults<OrderDtoAggregate> results = mongoTemplate.aggregate(
                aggregation, "orders", OrderDtoAggregate.class);
        
        // Convert to OrderDto with proper UUID conversion
        List<OrderDto> orders = results.getMappedResults().stream()
                .map(this::convertToOrderDto)
                .collect(Collectors.toList());

        // Count total for pagination
        Aggregation countAggregation = Aggregation.newAggregation(
                matchOperation,
                Aggregation.count().as("total")
        );
        
        AggregationResults<CountResult> countResults = mongoTemplate.aggregate(
                countAggregation, "orders", CountResult.class);
        
        long total = countResults.getMappedResults().isEmpty() ? 0 : 
                     countResults.getMappedResults().get(0).getTotal();

        return new PageImpl<>(orders, pageable, total);
    }

    @Override
    public List<RevenueDataDto> findRevenueDataByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        MatchOperation matchOperation = Aggregation.match(
                Criteria.where("createdAt").gte(startDate).lte(endDate)
        );

        // Group by year-month format
        ProjectionOperation addDateFields = Aggregation.project()
                .andInclude("totalAmount", "createdAt", "zone")
                .and("$dateToString")
                .withFormat("%Y-%m")
                .withDate("$createdAt")
                .as("month");

        GroupOperation groupOperation = Aggregation.group("month")
                .sum("totalAmount").as("revenue")
                .count().as("orders")
                .addToSet("zone.$id").as("uniqueZoneIds");

        // Project to add events count (approximated by unique zones)
        ProjectionOperation projectionOperation = Aggregation.project()
                .and("_id").as("month")
                .and("revenue").as("revenue")
                .and("orders").as("orders")
                .and("$size", "$uniqueZoneIds").as("events");

        SortOperation sortOperation = Aggregation.sort().on("month", org.springframework.data.domain.Sort.Direction.ASC);

        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                addDateFields,
                groupOperation,
                projectionOperation,
                sortOperation
        );

        AggregationResults<RevenueDataDto> results = mongoTemplate.aggregate(
                aggregation, "orders", RevenueDataDto.class);

        return results.getMappedResults();
    }

    @Override
    public List<EventTypeRevenueDto> findEventTypeRevenue(LocalDateTime startDate, LocalDateTime endDate, String eventType) {
        // Lookup to join with zones and events
        LookupOperation lookupZone = LookupOperation.newLookup()
                .from("zones")
                .localField("zone.$id")
                .foreignField("_id")
                .as("zoneData");

        LookupOperation lookupEvent = LookupOperation.newLookup()
                .from("events")
                .localField("zoneData.eventId")
                .foreignField("_id")
                .as("eventData");

        AggregationOperation unwindZone = Aggregation.unwind("zoneData");
        AggregationOperation unwindEvent = Aggregation.unwind("eventData");

        // Match operation for date range and optional event type
        Criteria criteria = Criteria.where("createdAt").gte(startDate).lte(endDate);
        if (eventType != null && !eventType.isEmpty()) {
            criteria = criteria.and("eventData.eventCategory").is(eventType);
        }
        MatchOperation matchOperation = Aggregation.match(criteria);

        // Group by event category
        GroupOperation groupOperation = Aggregation.group("eventData.eventCategory")
                .sum("quantity").as("value")
                .sum("totalAmount").as("revenue");

        // Project to match EventTypeRevenueDto structure
        ProjectionOperation projectionOperation = Aggregation.project()
                .and("_id").as("name")
                .and("value").as("value")
                .and("revenue").as("revenue");

        Aggregation aggregation = Aggregation.newAggregation(
                lookupZone,
                unwindZone,
                lookupEvent,
                unwindEvent,
                matchOperation,
                groupOperation,
                projectionOperation
        );

        AggregationResults<EventTypeRevenueDto> results = mongoTemplate.aggregate(
                aggregation, "orders", EventTypeRevenueDto.class);

        return results.getMappedResults();
    }

    @Override
    public List<TopEventDto> findTopEvents(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        // Lookup operations
        LookupOperation lookupZone = LookupOperation.newLookup()
                .from("zones")
                .localField("zone.$id")
                .foreignField("_id")
                .as("zoneData");

        LookupOperation lookupEvent = LookupOperation.newLookup()
                .from("events")
                .localField("zoneData.eventId")
                .foreignField("_id")
                .as("eventData");

        AggregationOperation unwindZone = Aggregation.unwind("zoneData");
        AggregationOperation unwindEvent = Aggregation.unwind("eventData");

        // Match operation for date range
        MatchOperation matchOperation = Aggregation.match(
                Criteria.where("createdAt").gte(startDate).lte(endDate)
        );

        // Group by event
        GroupOperation groupOperation = Aggregation.group("eventData._id")
                .first("eventData.eventName").as("name")
                .sum("totalAmount").as("revenue")
                .sum("quantity").as("tickets")
                .first("eventData.startDate").as("startDate")
                .first("eventData.startTime").as("startTime");

        // Project to add status logic
        ProjectionOperation projectionOperation = Aggregation.project()
                .and("name").as("name")
                .and("revenue").as("revenue")
                .and("tickets").as("tickets")
                .and("$literal", "active").as("status"); // Simplified status logic for now

        // Sort by revenue descending
        SortOperation sortOperation = Aggregation.sort().on("revenue", org.springframework.data.domain.Sort.Direction.DESC);

        // Add pagination
        AggregationOperation limitOperation = Aggregation.limit(pageable.getPageSize());
        AggregationOperation skipOperation = Aggregation.skip((long) pageable.getOffset());

        Aggregation aggregation = Aggregation.newAggregation(
                lookupZone,
                unwindZone,
                lookupEvent,
                unwindEvent,
                matchOperation,
                groupOperation,
                projectionOperation,
                sortOperation,
                skipOperation,
                limitOperation
        );

        AggregationResults<TopEventDto> results = mongoTemplate.aggregate(
                aggregation, "orders", TopEventDto.class);

        return results.getMappedResults();
    }

    // Helper method to convert aggregation result to OrderDto
    private OrderDto convertToOrderDto(OrderDtoAggregate aggregate) {
        return OrderDto.builder()
                .id(UUID.fromString(aggregate.getId()))
                .zoneId(UUID.fromString(aggregate.getZoneId()))
                .ownerId(UUID.fromString(aggregate.getOwnerId()))
                .quantity(aggregate.getQuantity())
                .totalAmount(aggregate.getTotalAmount())
                .createdAt(aggregate.getCreatedAt())
                .priceZone(aggregate.getPriceZone())
                .nameZone(aggregate.getNameZone())
                .nameEvent(aggregate.getNameEvent())
                .build();
    }

    // Helper classes for aggregation results
    private static class CountResult {
        private long total;

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }
    }

    // Helper class for OrderDto aggregation results
    private static class OrderDtoAggregate {
        private String id;
        private String zoneId;
        private String ownerId;
        private Integer quantity;
        private java.math.BigDecimal totalAmount;
        private LocalDateTime createdAt;
        private java.math.BigDecimal priceZone;
        private String nameZone;
        private String nameEvent;

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getZoneId() { return zoneId; }
        public void setZoneId(String zoneId) { this.zoneId = zoneId; }
        public String getOwnerId() { return ownerId; }
        public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public java.math.BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(java.math.BigDecimal totalAmount) { this.totalAmount = totalAmount; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public java.math.BigDecimal getPriceZone() { return priceZone; }
        public void setPriceZone(java.math.BigDecimal priceZone) { this.priceZone = priceZone; }
        public String getNameZone() { return nameZone; }
        public void setNameZone(String nameZone) { this.nameZone = nameZone; }
        public String getNameEvent() { return nameEvent; }
        public void setNameEvent(String nameEvent) { this.nameEvent = nameEvent; }
    }
}