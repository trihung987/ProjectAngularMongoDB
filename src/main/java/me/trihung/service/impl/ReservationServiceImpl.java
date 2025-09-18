package me.trihung.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

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
import me.trihung.service.OrderService;
import me.trihung.service.ReservationService;

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

	// Thời gian giữ vé mặc định (15 phút)
	private static final int HOLD_MINUTES = 15;
	private static final int PENDING_MINUTES = 10;

	// Giữ vé 15p
	@Transactional
	public ReservationDto holdTickets(HoldReservationRequest holdReservationRequest) {
		UUID zoneId = holdReservationRequest.getZoneId();
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
		Reservation reservation = Reservation.builder()
				.zone(zone)
				.quantity(quantity)
				.owner(owner)
				.createdAt(LocalDateTime.now())
				.expiresAt(LocalDateTime.now().plusMinutes(HOLD_MINUTES))
				.status(ReservationStatus.HOLD)
				.id(UUID.randomUUID())
				.build();
		int result = reservationRepository.tryInsertReservation(reservation.getId(), zone.getId(), owner.getId(), quantity, reservation.getCreatedAt(),
				reservation.getExpiresAt(), reservation.getStatus().name());
		if (result == 0)
			throw BadRequestException.message("Không đủ số lượng vé trong zone này, số vé còn lại đang được giữ chỗ chờ đợi thanh toán. Vui lòng thử lại sau");
		ReservationDto reservationDto = ReservationMapper.INSTANCE.toDto(reservation);
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
	public ReservationDto getReservationById(UUID reservationId) {
	    Reservation reservation = reservationRepository.findById(reservationId)
	            .orElseThrow(() -> BadRequestException.message("Không tìm thấy giữ chỗ"));
	    return ReservationMapper.INSTANCE.toDto(reservation);
	}


	@Transactional
	public Reservation markAsPendingPayment(UUID reservationId) {
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
	public OrderDto confirmReservation(UUID reservationId) {
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
	public void cancelReservation(UUID reservationId) {
		reservationRepository.deleteById(reservationId);
	}
}
