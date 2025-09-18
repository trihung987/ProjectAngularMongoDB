package me.trihung.repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.aggregation.ConvertOperators;
import org.springframework.data.mongodb.core.aggregation.DateOperators;
import org.springframework.data.mongodb.core.aggregation.LiteralOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import me.trihung.dto.EventTypeRevenueDto;
import me.trihung.dto.OrderDto;
import me.trihung.dto.RevenueDataDto;
import me.trihung.dto.TopEventDto;
import me.trihung.dto.TopEventDtoDetailed;
import me.trihung.entity.Order;
import me.trihung.entity.User;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class CustomOrderRepositoryImpl implements CustomOrderRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Page<OrderDto> findOrderDtosByOwner(User owner, Pageable pageable) {
        log.debug("Finding orders for user with ID: {}", owner.getId());
        
        try {
            // Try the simple Spring Data MongoDB method first
            Page<Order> orderPage = findByOwner(owner, pageable);
            
            log.debug("Found {} orders using simple repository method", orderPage.getTotalElements());
            
            // Convert to DTOs
            List<OrderDto> orderDtos = orderPage.getContent().stream()
                    .map(this::convertOrderToDto)
                    .collect(Collectors.toList());
            
            log.debug("Converted {} orders to DTOs", orderDtos.size());
            
            return new PageImpl<>(orderDtos, pageable, orderPage.getTotalElements());
            
        } catch (Exception e) {
            log.error("Error with simple repository method, trying manual query for user {}: {}", 
                     owner.getId(), e.getMessage(), e);
            return findOrderDtosByOwnerManual(owner, pageable);
        }
    }
    
    private Page<Order> findByOwner(User owner, Pageable pageable) {
        // Use MongoTemplate for a manual query as backup
        org.springframework.data.mongodb.core.query.Query query = 
            new org.springframework.data.mongodb.core.query.Query();
        
        // Try multiple criteria to handle different DBRef formats
        Criteria ownerCriteria = new Criteria().orOperator(
            Criteria.where("owner.$id").is(owner.getId()),
            Criteria.where("owner.id").is(owner.getId()),
            Criteria.where("owner").is(owner.getId())
        );
        
        query.addCriteria(ownerCriteria);
        query.with(pageable);
        
        List<Order> orders = mongoTemplate.find(query, Order.class);
        long total = mongoTemplate.count(
            new org.springframework.data.mongodb.core.query.Query()
                .addCriteria(ownerCriteria), 
            Order.class);
        
        log.debug("Found {} orders for user {} using manual query", total, owner.getId());
        
        return new PageImpl<>(orders, pageable, total);
    }
    
    private Page<OrderDto> findOrderDtosByOwnerManual(User owner, Pageable pageable) {
        log.debug("Using manual MongoDB query to find orders");
        
        try {
            // Manual query approach with multiple criteria
            org.springframework.data.mongodb.core.query.Query query = 
                new org.springframework.data.mongodb.core.query.Query();
            
            Criteria ownerCriteria = new Criteria().orOperator(
                Criteria.where("owner.$id").is(owner.getId()),
                Criteria.where("owner.id").is(owner.getId()),
                Criteria.where("owner").is(owner.getId())
            );
            
            query.addCriteria(ownerCriteria);
            query.with(pageable);
            
            List<Order> orders = mongoTemplate.find(query, Order.class);
            long total = mongoTemplate.count(
                new org.springframework.data.mongodb.core.query.Query()
                    .addCriteria(ownerCriteria), 
                Order.class);
            
            log.debug("Manual query found {} orders", total);
            
            // Convert to DTOs
            List<OrderDto> orderDtos = orders.stream()
                    .map(this::convertOrderToDto)
                    .collect(Collectors.toList());
            
            return new PageImpl<>(orderDtos, pageable, total);
            
        } catch (Exception e) {
            log.error("Manual query also failed for user {}: {}", owner.getId(), e.getMessage(), e);
            return new PageImpl<>(List.of(), pageable, 0);
        }
    }
    
    private OrderDto convertOrderToDto(Order order) {
        log.debug("Converting order {} to DTO", order.getId());
        
        OrderDto dto = OrderDto.builder()
                .id(order.getId())
                .quantity(order.getQuantity())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .ownerId(order.getOwner() != null ? order.getOwner().getId() : null)
                .build();
        
        // Get zone information if available
        if (order.getZone() != null) {
            dto.setZoneId(order.getZone().getId());
            dto.setPriceZone(order.getZone().getPrice());
            dto.setNameZone(order.getZone().getName());
            
            // Get event information if zone has eventId
            if (order.getZone().getEventId() != null) {
                try {
                    org.springframework.data.mongodb.core.query.Query eventQuery = 
                        new org.springframework.data.mongodb.core.query.Query();
                    eventQuery.addCriteria(Criteria.where("id").is(order.getZone().getEventId()));
                    
                    me.trihung.entity.Event event = mongoTemplate.findOne(eventQuery, me.trihung.entity.Event.class);
                    if (event != null) {
                        dto.setNameEvent(event.getEventName());
                    }
                } catch (Exception e) {
                    log.warn("Could not fetch event data for zone {}: {}", order.getZone().getId(), e.getMessage());
                }
            }
        }
        
        return dto;
    }

    @Override
    public List<RevenueDataDto> findRevenueDataByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Finding revenue data from {} to {}", startDate, endDate);
        
        try {
            MatchOperation matchOperation = Aggregation.match(
                    Criteria.where("createdAt").gte(startDate).lte(endDate)
            );

            // Lookup zones to get eventId
            LookupOperation lookupZone = LookupOperation.newLookup()
                    .from("zones")
                    .localField("zone.$id")
                    .foreignField("_id")
                    .as("zoneData");

            AggregationOperation unwindZone = Aggregation.unwind("zoneData");

            // Use addFields to convert string totalAmount to double before aggregation
            AggregationOperation addFieldsOperation = Aggregation.addFields()
                    .addField("numericAmount").withValue(ConvertOperators.ToDouble.toDouble("$totalAmount"))
                    .build();

            // Project necessary fields with numeric amount
            ProjectionOperation projectFields = Aggregation.project()
                    .andInclude("createdAt")
                    .and(DateOperators.DateToString.dateOf("createdAt").toString("%Y-%m")).as("month")
                    .and("zoneData.eventId").as("eventId")
                    .and("numericAmount").as("amount");

            GroupOperation groupOperation = Aggregation.group("month")
                    .sum("amount").as("revenue")
                    .count().as("orders")
                    .addToSet("eventId").as("uniqueEventIds"); // Count unique events, not zones

            // Project to final format
            ProjectionOperation projectionOperation = Aggregation.project()
                    .and("_id").as("month")
                    .and("revenue").as("revenue")
                    .and("orders").as("orders")
                    .and(ArrayOperators.Size.lengthOfArray("uniqueEventIds")).as("events");

            SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Direction.ASC, "month"));

            Aggregation aggregation = Aggregation.newAggregation(
                    matchOperation,
                    lookupZone,
                    unwindZone,
                    addFieldsOperation,
                    projectFields,
                    groupOperation,
                    projectionOperation,
                    sortOperation
            );

            AggregationResults<RevenueDataDto> results = mongoTemplate.aggregate(
                    aggregation, "orders", RevenueDataDto.class);

            List<RevenueDataDto> resultList = results.getMappedResults();
            log.debug("Found {} revenue data points", resultList.size());
            
            return resultList;
            
        } catch (Exception e) {
            log.error("Error in findRevenueDataByDateRange: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public List<EventTypeRevenueDto> findEventTypeRevenue(LocalDateTime startDate, LocalDateTime endDate, String eventType) {
        log.debug("Finding event type revenue from {} to {}, eventType: {}", startDate, endDate, eventType);
        
        try {
            // Match orders by date range first
            MatchOperation matchOperation = Aggregation.match(
                    Criteria.where("createdAt").gte(startDate).lte(endDate)
            );

            // Add field to convert string totalAmount to double  
            AggregationOperation addFieldsStringToNumber = Aggregation.addFields()
                    .addField("numericAmount").withValue(ConvertOperators.ToDouble.toDouble("$totalAmount"))
                    .build();

            // Project necessary fields with numeric amount
            ProjectionOperation addFields = Aggregation.project()
                    .andInclude("createdAt", "quantity")
                    .and("zone.$id").as("zoneId")
                    .and("numericAmount").as("totalAmount");

            // Lookup to join with zones
            LookupOperation lookupZone = LookupOperation.newLookup()
                    .from("zones")
                    .localField("zoneId")
                    .foreignField("_id")
                    .as("zoneData");

            AggregationOperation unwindZone = Aggregation.unwind("zoneData");

            // Lookup to join with events
            LookupOperation lookupEvent = LookupOperation.newLookup()
                    .from("events")
                    .localField("zoneData.eventId")
                    .foreignField("_id")
                    .as("eventData");

            AggregationOperation unwindEvent = Aggregation.unwind("eventData");

            // Apply event type filter if specified
            MatchOperation eventTypeMatch = null;
            if (eventType != null && !eventType.isEmpty()) {
                eventTypeMatch = Aggregation.match(
                        Criteria.where("eventData.eventCategory").is(eventType)
                );
            }

            // Group by event category
            GroupOperation groupOperation = Aggregation.group("eventData.eventCategory")
                    .sum("quantity").as("value")
                    .sum("totalAmount").as("revenue");

            // Project to match EventTypeRevenueDto structure
            ProjectionOperation projectionOperation = Aggregation.project()
                    .and("_id").as("name")
                    .and("value").as("value")
                    .and("revenue").as("revenue");

            // Build aggregation pipeline dynamically
            List<AggregationOperation> operations = Arrays.asList(
                    matchOperation,
                    addFieldsStringToNumber,
                    addFields,
                    lookupZone,
                    unwindZone,
                    lookupEvent,
                    unwindEvent
            );

            // Add event type filter if needed
            if (eventTypeMatch != null) {
                operations = new java.util.ArrayList<>(operations);
                operations.add(eventTypeMatch);
            }

            // Add final operations
            operations = new java.util.ArrayList<>(operations);
            operations.add(groupOperation);
            operations.add(projectionOperation);

            Aggregation aggregation = Aggregation.newAggregation(operations);

            AggregationResults<EventTypeRevenueDto> results = mongoTemplate.aggregate(
                    aggregation, "orders", EventTypeRevenueDto.class);

            List<EventTypeRevenueDto> resultList = results.getMappedResults();
            log.debug("Found {} event type revenue entries", resultList.size());
            
            return resultList;
            
        } catch (Exception e) {
            log.error("Error in findEventTypeRevenue: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public List<TopEventDto> findTopEvents(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        log.debug("Finding top events from {} to {}, limit: {}", startDate, endDate, pageable.getPageSize());
        
        try {
            // Match orders by date range
            MatchOperation matchOperation = Aggregation.match(
                    Criteria.where("createdAt").gte(startDate).lte(endDate)
            );

            // Add field to convert string totalAmount to double
            AggregationOperation addFieldsStringToNumber = Aggregation.addFields()
                    .addField("numericAmount").withValue(ConvertOperators.ToDouble.toDouble("$totalAmount"))
                    .build();

            // Project necessary fields with numeric amount
            ProjectionOperation addFields = Aggregation.project()
                    .andInclude("createdAt", "quantity")
                    .and("zone.$id").as("zoneId")
                    .and("numericAmount").as("totalAmount");

            // Lookup operations to join with zones and events
            LookupOperation lookupZone = LookupOperation.newLookup()
                    .from("zones")
                    .localField("zoneId")
                    .foreignField("_id")
                    .as("zoneData");

            AggregationOperation unwindZone = Aggregation.unwind("zoneData");

            LookupOperation lookupEvent = LookupOperation.newLookup()
                    .from("events")
                    .localField("zoneData.eventId")
                    .foreignField("_id")
                    .as("eventData");

            AggregationOperation unwindEvent = Aggregation.unwind("eventData");

            // Group by event to calculate totals
            GroupOperation groupOperation = Aggregation.group("eventData._id")
                    .first("eventData.eventName").as("name")
                    .sum("totalAmount").as("revenue")
                    .sum("quantity").as("tickets")
                    .first("eventData.startDate").as("startDate")
                    .first("eventData.startTime").as("startTime")
                    .first("eventData.endDate").as("endDate")
                    .first("eventData.endTime").as("endTime");

            // Project with improved status calculation
            ProjectionOperation projectionOperation = Aggregation.project()
                    .and("name").as("name")
                    .and("revenue").as("revenue")
                    .and("tickets").as("tickets")
                    .and("startDate").as("startDate")
                    .and("endDate").as("endDate")
                    .and(LiteralOperators.Literal.asLiteral("active")).as("status"); // Use simple status for now

            // Sort by revenue descending
            SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Direction.DESC, "revenue"));

            // Add pagination
            AggregationOperation skipOperation = Aggregation.skip((long) pageable.getOffset());
            AggregationOperation limitOperation = Aggregation.limit(pageable.getPageSize());

            Aggregation aggregation = Aggregation.newAggregation(
                    matchOperation,
                    addFieldsStringToNumber,
                    addFields,
                    lookupZone,
                    unwindZone,
                    lookupEvent,
                    unwindEvent,
                    groupOperation,
                    projectionOperation,
                    sortOperation,
                    skipOperation,
                    limitOperation
            );

            AggregationResults<TopEventDtoDetailed> results = mongoTemplate.aggregate(
                    aggregation, "orders", TopEventDtoDetailed.class);

            List<TopEventDtoDetailed> detailedList = results.getMappedResults();
            
            // Calculate proper status for each event and convert to basic TopEventDto
            List<TopEventDto> resultList = detailedList.stream()
                    .peek(TopEventDtoDetailed::calculateStatus)
                    .map(TopEventDtoDetailed::toBasic)
                    .collect(Collectors.toList());
            
            log.debug("Found {} top events", resultList.size());
            
            return resultList;
            
        } catch (Exception e) {
            log.error("Error in findTopEvents: {}", e.getMessage(), e);
            // Fallback to simpler aggregation if the complex one fails
            return findTopEventsSimple(startDate, endDate, pageable);
        }
    }
    
    // Fallback method with simpler status logic
    private List<TopEventDto> findTopEventsSimple(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        try {
            log.debug("Using simplified top events query as fallback");
            
            // Match orders by date range
            MatchOperation matchOperation = Aggregation.match(
                    Criteria.where("createdAt").gte(startDate).lte(endDate)
            );

            // Add field to convert string totalAmount to double
            AggregationOperation addFieldsStringToNumber = Aggregation.addFields()
                    .addField("numericAmount").withValue(ConvertOperators.ToDouble.toDouble("$totalAmount"))
                    .build();

            // Project necessary fields with numeric amount
            ProjectionOperation addFields = Aggregation.project()
                    .andInclude("createdAt", "quantity")
                    .and("zone.$id").as("zoneId")
                    .and("numericAmount").as("totalAmount");

            // Lookup operations to join with zones and events
            LookupOperation lookupZone = LookupOperation.newLookup()
                    .from("zones")
                    .localField("zoneId")
                    .foreignField("_id")
                    .as("zoneData");

            AggregationOperation unwindZone = Aggregation.unwind("zoneData");

            LookupOperation lookupEvent = LookupOperation.newLookup()
                    .from("events")
                    .localField("zoneData.eventId")
                    .foreignField("_id")
                    .as("eventData");

            AggregationOperation unwindEvent = Aggregation.unwind("eventData");

            // Group by event to calculate totals
            GroupOperation groupOperation = Aggregation.group("eventData._id")
                    .first("eventData.eventName").as("name")
                    .sum("totalAmount").as("revenue")
                    .sum("quantity").as("tickets");

            // Project with simple status
            ProjectionOperation projectionOperation = Aggregation.project()
                    .and("name").as("name")
                    .and("revenue").as("revenue")
                    .and("tickets").as("tickets")
                    .and(LiteralOperators.Literal.asLiteral("active")).as("status");

            // Sort by revenue descending
            SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Direction.DESC, "revenue"));

            // Add pagination
            AggregationOperation skipOperation = Aggregation.skip((long) pageable.getOffset());
            AggregationOperation limitOperation = Aggregation.limit(pageable.getPageSize());

            Aggregation aggregation = Aggregation.newAggregation(
                    matchOperation,
                    addFieldsStringToNumber,
                    addFields,
                    lookupZone,
                    unwindZone,
                    lookupEvent,
                    unwindEvent,
                    groupOperation,
                    projectionOperation,
                    sortOperation,
                    skipOperation,
                    limitOperation
            );

            AggregationResults<TopEventDto> results = mongoTemplate.aggregate(
                    aggregation, "orders", TopEventDto.class);

            return results.getMappedResults();
            
        } catch (Exception e) {
            log.error("Even simplified top events query failed: {}", e.getMessage(), e);
            return List.of();
        }
    }

    // Helper method to convert aggregation result to OrderDto
    private OrderDto convertToOrderDto(OrderDtoAggregate aggregate) {
        return OrderDto.builder()
                .id(aggregate.getId())
                .zoneId(aggregate.getZoneId())
                .ownerId(aggregate.getOwnerId())
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