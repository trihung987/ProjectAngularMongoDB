package me.trihung.repository;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import me.trihung.entity.Reservation;
import me.trihung.entity.User;
import me.trihung.entity.Zone;
import me.trihung.enums.ReservationStatus;

@Repository
public class CustomReservationRepositoryImpl implements CustomReservationRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public int countActiveReservations(String zoneId) {
        MatchOperation matchOperation = Aggregation.match(
                Criteria.where("zone.$id").is(zoneId)
                        .and("expiresAt").gt(LocalDateTime.now())
        );

        GroupOperation groupOperation = Aggregation.group()
                .sum("quantity").as("totalQuantity");

        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                groupOperation
        );

        AggregationResults<CountResult> results = mongoTemplate.aggregate(
                aggregation, "reservations", CountResult.class);

        return results.getMappedResults().isEmpty() ? 0 : 
               (int) results.getMappedResults().get(0).getTotalQuantity();
    }

    @Override
    public boolean tryInsertReservation(String id, Zone zone, User owner, int quantity,
                                      LocalDateTime createdAt, LocalDateTime expiresAt, ReservationStatus status) {
        // Check availability first
        int activeReservations = countActiveReservations(zone.getId());
        int availableTickets = zone.getMaxTickets() - zone.getSoldTickets() - activeReservations;
        
        if (availableTickets >= quantity) {
            // Create and insert the reservation
            Reservation reservation = Reservation.builder()
                    .id(id)
                    .zone(zone)
                    .owner(owner)
                    .quantity(quantity)
                    .createdAt(createdAt)
                    .expiresAt(expiresAt)
                    .status(status)
                    .build();
            
            mongoTemplate.insert(reservation);
            return true;
        }
        
        return false;
    }

    // Helper class for aggregation results
    private static class CountResult {
        private long totalQuantity;

        public long getTotalQuantity() {
            return totalQuantity;
        }

        public void setTotalQuantity(long totalQuantity) {
            this.totalQuantity = totalQuantity;
        }
    }
}