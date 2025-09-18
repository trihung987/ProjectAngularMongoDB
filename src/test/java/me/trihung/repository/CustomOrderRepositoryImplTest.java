package me.trihung.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import me.trihung.dto.OrderDto;
import me.trihung.entity.Order;
import me.trihung.entity.User;
import me.trihung.entity.Zone;

@ExtendWith(MockitoExtension.class)
public class CustomOrderRepositoryImplTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private CustomOrderRepositoryImpl customOrderRepository;

    private User testUser;
    private Order testOrder;
    private Zone testZone;
    private Pageable testPageable;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("user123")
                .username("testuser")
                .email("test@example.com")
                .build();

        testZone = Zone.builder()
                .id("zone123")
                .name("VIP Zone")
                .price(new BigDecimal("100.00"))
                .eventId("event123")
                .build();

        testOrder = Order.builder()
                .id("order123")
                .owner(testUser)
                .zone(testZone)
                .quantity(2)
                .totalAmount(new BigDecimal("200.00"))
                .createdAt(LocalDateTime.now())
                .build();

        testPageable = PageRequest.of(0, 10);
    }

    @Test
    void testFindOrderDtosByOwnerSuccess() {
        // Given
        List<Order> orders = Arrays.asList(testOrder);
        when(mongoTemplate.find(any(Query.class), eq(Order.class))).thenReturn(orders);
        when(mongoTemplate.count(any(Query.class), eq(Order.class))).thenReturn(1L);

        // When
        Page<OrderDto> result = customOrderRepository.findOrderDtosByOwner(testUser, testPageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        
        OrderDto orderDto = result.getContent().get(0);
        assertEquals("order123", orderDto.getId());
        assertEquals("user123", orderDto.getOwnerId());
        assertEquals("zone123", orderDto.getZoneId());
        assertEquals(2, orderDto.getQuantity());
        assertEquals(new BigDecimal("200.00"), orderDto.getTotalAmount());

        // Verify MongoTemplate was called
        verify(mongoTemplate, times(1)).find(any(Query.class), eq(Order.class));
        verify(mongoTemplate, times(1)).count(any(Query.class), eq(Order.class));
    }

    @Test
    void testFindOrderDtosByOwnerEmptyResult() {
        // Given
        when(mongoTemplate.find(any(Query.class), eq(Order.class))).thenReturn(List.of());
        when(mongoTemplate.count(any(Query.class), eq(Order.class))).thenReturn(0L);

        // When
        Page<OrderDto> result = customOrderRepository.findOrderDtosByOwner(testUser, testPageable);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getContent().size());

        verify(mongoTemplate, times(1)).find(any(Query.class), eq(Order.class));
        verify(mongoTemplate, times(1)).count(any(Query.class), eq(Order.class));
    }

    @Test
    void testFindOrderDtosByOwnerWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(1, 5); // Second page, 5 items per page
        List<Order> orders = Arrays.asList(testOrder);
        when(mongoTemplate.find(any(Query.class), eq(Order.class))).thenReturn(orders);
        when(mongoTemplate.count(any(Query.class), eq(Order.class))).thenReturn(10L);

        // When
        Page<OrderDto> result = customOrderRepository.findOrderDtosByOwner(testUser, pageable);

        // Then
        assertNotNull(result);
        assertEquals(10, result.getTotalElements());
        assertEquals(2, result.getTotalPages()); // 10 items / 5 per page = 2 pages
        assertEquals(1, result.getNumber()); // Current page number
        assertEquals(5, result.getSize()); // Page size

        verify(mongoTemplate, times(1)).find(any(Query.class), eq(Order.class));
        verify(mongoTemplate, times(1)).count(any(Query.class), eq(Order.class));
    }
}