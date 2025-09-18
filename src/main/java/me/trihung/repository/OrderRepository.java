package me.trihung.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import me.trihung.dto.EventTypeRevenueDto;
import me.trihung.dto.OrderDto;
import me.trihung.dto.RevenueDataDto;
import me.trihung.dto.TopEventDto;
import me.trihung.entity.Order;
import me.trihung.entity.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
	@Query("""
			SELECT new me.trihung.dto.OrderDto(
			    o.id,
			    z.id,
			    u.id,
			    o.quantity,
			    o.totalAmount,
			    o.createdAt,
			    z.price,
			    z.name,
			    e.eventName
			)
			FROM Order o
			JOIN o.zone z
			JOIN z.event e
			JOIN o.owner u
			WHERE u = :owner
			""")
	Page<OrderDto> findOrderDtosByOwner(@Param("owner") User owner, Pageable pageable);

	@Query("""
			    SELECT new me.trihung.dto.RevenueDataDto(
			        CAST(FUNCTION('TO_CHAR', o.createdAt, 'YYYY-MM') AS string),
			        SUM(o.totalAmount),
			        COUNT(o.id),
			        COUNT(DISTINCT o.zone.event.id)
			    )
			    FROM Order o
			    WHERE o.createdAt BETWEEN :startDate AND :endDate
			    GROUP BY FUNCTION('TO_CHAR', o.createdAt, 'YYYY-MM')
			    ORDER BY FUNCTION('TO_CHAR', o.createdAt, 'YYYY-MM') ASC
			""")
	List<RevenueDataDto> findRevenueDataByDateRange(@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate);

	// Lấy doanh thu theo loại sự kiện
	@Query("""
			    SELECT new me.trihung.dto.EventTypeRevenueDto(
			        o.zone.event.eventCategory,
			        SUM(o.quantity),
			        SUM(o.totalAmount)
			    )
			    FROM Order o
			    WHERE o.createdAt BETWEEN :startDate AND :endDate
			    AND (:eventType IS NULL OR o.zone.event.eventCategory = :eventType)
			    GROUP BY o.zone.event.eventCategory
			""")
	List<EventTypeRevenueDto> findEventTypeRevenue(@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate, @Param("eventType") String eventType);

	// Lấy các sự kiện hàng đầu theo doanh thu
	@Query(value = """
			SELECT
			    e.event_name as name,
			    SUM(o.total_amount) as revenue,
			    SUM(o.quantity) as tickets,
			    CASE
			        WHEN (e.start_date + e.start_time) < NOW() THEN 'completed'
			        WHEN (e.start_date + e.start_time) > NOW() THEN 'upcoming'
			        ELSE 'active'
			    END as status
			FROM orders o
			JOIN zones z ON o.zone_id = z.id
			JOIN events e ON z.event_id = e.id
			WHERE o.created_at BETWEEN :startDate AND :endDate
			GROUP BY e.event_name, e.start_date, e.start_time
			ORDER BY revenue DESC
			""", nativeQuery = true)
	List<TopEventDto> findTopEvents(@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate, Pageable pageable);

}
