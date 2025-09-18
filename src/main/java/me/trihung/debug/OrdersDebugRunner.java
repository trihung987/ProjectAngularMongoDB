package me.trihung.debug;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import me.trihung.dto.OrderPageResponse;
import me.trihung.entity.User;
import me.trihung.repository.UserRepository;
import me.trihung.service.OrderService;
import lombok.extern.slf4j.Slf4j;

/**
 * Debug utility to test the getOrdersPaged functionality
 * This can be enabled to test the fix manually
 */
@Component
@Slf4j
public class OrdersDebugRunner implements CommandLineRunner {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private UserRepository userRepository;
    
    // Set this to true to enable debug testing
    private static final boolean ENABLE_DEBUG = false;

    @Override
    public void run(String... args) throws Exception {
        if (!ENABLE_DEBUG) {
            log.debug("Debug runner disabled");
            return;
        }
        
        log.info("=== Starting Orders Debug Test ===");
        
        try {
            // Test with a sample user if available
            User firstUser = userRepository.findAll().stream().findFirst().orElse(null);
            
            if (firstUser != null) {
                log.info("Testing with user: {} (ID: {})", firstUser.getUsername(), firstUser.getId());
                
                // Test the getOrdersPaged method
                Pageable pageable = PageRequest.of(0, 10);
                OrderPageResponse response = orderService.getOrdersPaged(0, 10, null, "DESC");
                
                log.info("Orders found: {}", response.getTotalElements());
                log.info("Orders in current page: {}", response.getContent().size());
                
                response.getContent().forEach(order -> {
                    log.info("Order: {} - Quantity: {} - Amount: {} - Zone: {} - Event: {}", 
                             order.getId(), order.getQuantity(), order.getTotalAmount(), 
                             order.getNameZone(), order.getNameEvent());
                });
                
            } else {
                log.warn("No users found in database for testing");
            }
            
        } catch (Exception e) {
            log.error("Error during debug test: {}", e.getMessage(), e);
        }
        
        log.info("=== Orders Debug Test Complete ===");
    }
}