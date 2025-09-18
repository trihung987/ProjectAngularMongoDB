package me.trihung.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.stereotype.Repository;

import me.trihung.dto.EventTypeRevenueDto;
import me.trihung.dto.OrderDto;
import me.trihung.dto.RevenueDataDto;
import me.trihung.dto.TopEventDto;
import me.trihung.entity.Order;
import me.trihung.entity.User;

@Repository
public interface OrderRepository extends MongoRepository<Order, String>, CustomOrderRepository {
	
	// Simple query methods that Spring Data MongoDB can handle automatically
	Page<Order> findByOwner(User owner, Pageable pageable);
	
	List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
