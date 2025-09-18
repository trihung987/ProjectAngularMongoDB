package me.trihung.controller;

import lombok.RequiredArgsConstructor;
import me.trihung.dto.OrderDto;
import me.trihung.dto.ReservationDto;
import me.trihung.dto.request.HoldReservationRequest;
import me.trihung.service.ReservationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {
	
	@Autowired
    private ReservationService reservationService;
    
    
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Giữ chỗ cho đơn vé")
    @PostMapping("/hold")
    public ResponseEntity<ReservationDto> holdReservation(@RequestBody HoldReservationRequest request) {
        System.out.println("zoneid"+request.getZoneId());
    	ReservationDto reservation = reservationService.holdTickets(request);
        
        return ResponseEntity.ok(reservation);
    }
    
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Lấy thông tin giữ chỗ theo ID")
    @GetMapping("/{id}")
    public ResponseEntity<ReservationDto> getReservation(@PathVariable String id) {
        ReservationDto reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(reservation);
    }

    @Operation(summary = "Xác thực đã hoàn thành")
    @PostMapping("/{id}/confirm")
    public ResponseEntity<OrderDto> confirmReservation(@PathVariable String id) {
        
        return ResponseEntity.ok(reservationService.confirmReservation(id));
    }

    @Operation(summary = "Hủy giữ chỗ")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelReservation(@PathVariable String id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }
}
