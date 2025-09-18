package me.trihung.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.trihung.dto.OrderDto;
import me.trihung.dto.ReservationDto;
import me.trihung.dto.request.HoldReservationRequest;
import me.trihung.entity.Reservation;
import me.trihung.entity.User;
import me.trihung.entity.Zone;
import me.trihung.enums.ReservationStatus;
import me.trihung.exception.BadRequestException;
import me.trihung.helper.SecurityHelper;
import me.trihung.mapper.OrderMapper;
import me.trihung.mapper.ReservationMapper;
import me.trihung.repository.ReservationRepository;
import me.trihung.repository.ZoneRepository;
import me.trihung.repository.EventRepository;
import me.trihung.entity.Event;
import me.trihung.service.OrderService;
import me.trihung.service.ReservationService;
import me.trihung.util.IdGenerator;

@Service
public class ReservationServiceImpl implements ReservationService {
	@Autowired
	private ZoneRepository zoneRepository;
	@Autowired
	private ReservationRepository reservationRepository;
	@Autowired
	private SecurityHelper securityHelper;
	@Autowired
	private OrderService orderService;
	@Autowired
	private EventRepository eventRepository;

	// Thời gian giữ vé mặc định (15 phút)
	private static final int HOLD_MINUTES = 15;
	private static final int PENDING_MINUTES = 10;

	// Giữ vé 15p
	@Transactional
	public ReservationDto holdTickets(HoldReservationRequest holdReservationRequest) {
		String zoneId = holdReservationRequest.getZoneId();
		int quantity = holdReservationRequest.getQuantity();
		Zone zone = zoneRepository.findById(zoneId)
				.orElseThrow(() -> BadRequestException.message("Không tìm thấy zone"));

		// Vé khả dụng = maxTickets - soldTickets - reserved
//		int reserved = reservationRepository.countActiveReservations(zoneId);
//		int available = zone.getMaxTickets() - zone.getSoldTickets() - reserved;
//
//		if (available < quantity) {
//			throw BadRequestException.message("Không đủ số lượng vé trong zone này");
//		}
		User owner = securityHelper.getCurrentUser();
		
		// Generate ID for the reservation
		String reservationId = IdGenerator.generateId();
		
		Reservation reservation = Reservation.builder()
				.id(reservationId)
				.zone(zone)
				.quantity(quantity)
				.owner(owner)
				.createdAt(LocalDateTime.now())
				.expiresAt(LocalDateTime.now().plusMinutes(HOLD_MINUTES))
				.status(ReservationStatus.HOLD)
				.build();
		
		boolean result = reservationRepository.tryInsertReservation(
				reservationId, zone, owner, quantity, 
				reservation.getCreatedAt(), reservation.getExpiresAt(), reservation.getStatus());
		if (!result)
			throw BadRequestException.message("Không đủ số lượng vé trong zone này, số vé còn lại đang được giữ chỗ chờ đợi thanh toán. Vui lòng thử lại sau");
		
		ReservationDto reservationDto = ReservationMapper.INSTANCE.toDto(reservation);
		
		// Fetch and set the event name
		if (zone.getEventId() != null) {
			Event event = eventRepository.findById(zone.getEventId()).orElse(null);
			if (event != null) {
				reservationDto.setNameEvent(event.getEventName());
			}
		}
		
		return reservationDto;
	}
//	 @Param("zoneId") UUID zoneId,
//     @Param("ownerId") UUID ownerId,
//     @Param("quantity") int quantity,
//     @Param("createdAt") LocalDateTime createdAt,
//     @Param("expiresAt") LocalDateTime expiresAt,
//     @Param("status") ReservationStatus status
	@Override
	@Transactional(readOnly = true)
	public ReservationDto getReservationById(String reservationId) {
	    Reservation reservation = reservationRepository.findById(reservationId)
	            .orElseThrow(() -> BadRequestException.message("Không tìm thấy giữ chỗ"));
	    
	    ReservationDto reservationDto = ReservationMapper.INSTANCE.toDto(reservation);
	    
	    // Fetch and set the event name
	    Zone zone = reservation.getZone();
	    if (zone != null && zone.getEventId() != null) {
	        Event event = eventRepository.findById(zone.getEventId()).orElse(null);
	        if (event != null) {
	            reservationDto.setNameEvent(event.getEventName());
	        }
	    }
	    
	    return reservationDto;
	}


	@Transactional
	public Reservation markAsPendingPayment(String reservationId) {
		Reservation reservation = reservationRepository.findById(reservationId)
				.orElseThrow(() -> BadRequestException.message("Không tìm thấy giữ chỗ"));

		// Chỉ cho phép chuyển sang trạng thái chờ thanh toán nếu đang HOLD
		if (reservation.getStatus() != ReservationStatus.HOLD) {
			throw BadRequestException.message("Giữ chỗ đang trong trạng thái chờ thanh toán hoặc đã bị hủy");
		}

		// Cập nhật trạng thái + thời gian hết hạn mới
		reservation.setStatus(ReservationStatus.PENDING_PAYMENT);
		reservation.setExpiresAt(LocalDateTime.now().plusMinutes(PENDING_MINUTES));

		// Lưu lại vào DB
		return reservationRepository.save(reservation);
	}

	// xác nhận thành công
	@Transactional
	public OrderDto confirmReservation(String reservationId) {
		Reservation reservation = reservationRepository.findById(reservationId)
				.orElseThrow(() -> BadRequestException.message("Giữ chỗ không tìm thấy"));

		if (reservation.getExpiresAt().isBefore(LocalDateTime.now())) {
			throw BadRequestException.message("Giữ chỗ đã hết hạn");
		}
		
		if (reservation.getStatus() != ReservationStatus.HOLD) {
			throw BadRequestException.message("Giữ chỗ này không trong trạng thái thanh toán");
		}

		Zone zone = reservation.getZone();
		

		// Cộng vào soldTickets đã bán
		zone.setSoldTickets(zone.getSoldTickets() + reservation.getQuantity());
		zoneRepository.save(zone);
		
		
		reservationRepository.delete(reservation);
		
		OrderDto orderDto = orderService.createOrderFromReservation(reservation);
		return orderDto;
	}

	
	@Transactional
	public void cancelReservation(String reservationId) {
		reservationRepository.deleteById(reservationId);
	}
}
