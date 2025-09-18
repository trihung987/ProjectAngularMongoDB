package me.trihung.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ValidationException;
import me.trihung.dto.EventDto;
import me.trihung.dto.EventPageResponse;
import me.trihung.dto.request.EventRequest;
import me.trihung.entity.Event;
import me.trihung.entity.User;
import me.trihung.enums.EventStatus;
import me.trihung.exception.BadRequestException;
import me.trihung.exception.UnauthorizedException;
import me.trihung.helper.SecurityHelper;
import me.trihung.mapper.EventMapper;
import me.trihung.repository.EventRepository;
import me.trihung.repository.OrganizerRepository;
import me.trihung.repository.VenueRepository;
import me.trihung.repository.ZoneRepository;
import me.trihung.service.EventService;
import me.trihung.service.FileStorageService; // Import the new service
import me.trihung.util.IdGenerator;

@Service
public class EventServiceImpl implements EventService {

	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private EventMapper eventMapper;

	@Autowired
	private FileStorageService fileStorageService;
	
	@Autowired
	private SecurityHelper securityHelper;

	@Autowired
	private OrganizerRepository organizerRepository;

	@Autowired
	private VenueRepository venueRepository;

	@Autowired
	private ZoneRepository zoneRepository;

	@Transactional
	public EventDto createEvent(EventRequest requestDto) {
		// Các validation bắt buộc khi publish
		validateEventImage(requestDto.getEventImage());
		validateEventImage(requestDto.getEventBanner());
		if (requestDto.getOrganizer() != null) {
			validateEventImage(requestDto.getOrganizer().getLogo());
		}

		return saveOrUpdateEvent(requestDto, EventStatus.PUBLISHED);
	}

	@Override
	@Transactional
	public EventDto saveDraft(EventRequest requestDto) {
		return saveOrUpdateEvent(requestDto, EventStatus.DRAFT);
	}

	private EventDto saveOrUpdateEvent(EventRequest requestDto, EventStatus status) {
		User user = securityHelper.getCurrentUser();
		// Ensure user has an ID (should already exist)
		if (user.getId() == null) {
			throw new IllegalStateException("Current user has no ID");
		}
		
		Event event;
		if (requestDto.getId() == null) {
			event = eventMapper.toEvent(requestDto);
			event.setId(IdGenerator.generateId()); // Ensure event has an ID
			event.setOwner(user);
		} else {
			event = eventRepository.findById(requestDto.getId()).orElseThrow(
					() -> BadRequestException.message("Không tìm thấy sự kiện với id: " + requestDto.getId()));
			// Cập nhật các trường từ DTO vào entity đã tồn tại (đã setup bỏ qua các field
			// của dto = null k ghi đè vào entity)
			validateOwner(event);
			eventMapper.updateEventFromRequest(requestDto, event);
		}
		
		event.setStatus(status);
		System.out.println("end time"+event.getEndTime());
		if (requestDto.getEventImage() != null) {
			validateEventImage(requestDto.getEventImage());
			String imageUrl = fileStorageService.storeFile(requestDto.getEventImage());
			event.setEventImage(imageUrl);
		}
		if (requestDto.getEventBanner() != null) {
			validateEventImage(requestDto.getEventBanner());
			String bannerUrl = fileStorageService.storeFile(requestDto.getEventBanner());
			event.setEventBanner(bannerUrl);
		}
		// Handle organizer data - either with or without logo
		if (requestDto.getOrganizer() != null) {
			// Ensure organizer exists and has an ID
			if (event.getOrganizer() == null) {
				event.setOrganizer(new me.trihung.entity.Organizer());
				event.getOrganizer().setId(IdGenerator.generateId()); // Ensure organizer has an ID
			}
			
			// Update organizer fields from request
			if (requestDto.getOrganizer().getName() != null) {
				event.getOrganizer().setName(requestDto.getOrganizer().getName());
			}
			if (requestDto.getOrganizer().getBio() != null) {
				event.getOrganizer().setBio(requestDto.getOrganizer().getBio());
			}
			
			// Handle logo if provided
			if (requestDto.getOrganizer().getLogo() != null) {
				validateEventImage(requestDto.getOrganizer().getLogo());
				String logoUrl = fileStorageService.storeFile(requestDto.getOrganizer().getLogo());
				event.getOrganizer().setLogo(logoUrl);
			}
			
			// Save organizer BEFORE saving event to avoid DBRef null ID issue
			event.setOrganizer(organizerRepository.save(event.getOrganizer()));
		}

		// Handle venue data - save venue before event to avoid DBRef null ID issue
		if (event.getVenue() != null) {
			// Ensure venue has an ID
			if (event.getVenue().getId() == null) {
				event.getVenue().setId(IdGenerator.generateId());
			}
			
			// Save venue BEFORE saving event to avoid DBRef null ID issue
			event.setVenue(venueRepository.save(event.getVenue()));
		}

		// Handle zones - save each zone as independent document with eventId reference
		if (event.getZones() != null) {
			event.getZones().forEach(zone -> {
				// Ensure zone has an ID
				if (zone.getId() == null) {
					zone.setId(IdGenerator.generateId());
				}
				// Set the eventId reference
				zone.setEvent(event);
				// Save zone as independent document
				zoneRepository.save(zone);
			});
		}

		Event savedEvent = eventRepository.save(event);
		return eventMapper.toEventResponseDto(savedEvent);
	}

	@Override
	@Transactional(readOnly = true)
	public EventPageResponse getEventsPaged(
	        int page,
	        int size,
	        String status,
	        String search,
	        String sortBy,
	        String sortDirection,
	        boolean hasOwner
	) {
		
		User user = null;
		if (hasOwner)
			user = securityHelper.getCurrentUser();
	    Sort sort = Sort.unsorted();
	    if (sortBy != null && !sortBy.isBlank() && !"totalSeats".equalsIgnoreCase(sortBy)) {
	        sort = Sort.by("ASC".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
	    }

	    Pageable pageable = PageRequest.of(page, size, sort);
	    Page<Event> eventPage;
	    EventStatus filterStatus = null;
	    if (status != null && !"ALL".equalsIgnoreCase(status)) {
	        filterStatus = EventStatus.valueOf(status);
	    }


	    if (search != null && !search.isBlank()) {
	        if (filterStatus != null && hasOwner) {
	            eventPage = eventRepository.findByOwnerAndStatusAndEventNameContainingIgnoreCase(user, filterStatus, search, pageable);
	        } else if (filterStatus != null) {
	            eventPage = eventRepository.findByStatusAndEventNameContainingIgnoreCase(filterStatus, search, pageable);
	        } else if (hasOwner) {
	            eventPage = eventRepository.findByOwnerAndEventNameContainingIgnoreCase(user, search, pageable);
	        } else {
	            eventPage = eventRepository.findByEventNameContainingIgnoreCase(search, pageable);
	        }
	    } else {
	        if (filterStatus != null && hasOwner) {
	            eventPage = eventRepository.findByOwnerAndStatus(user, filterStatus, pageable);
	        } else if (filterStatus != null) {
	            eventPage = eventRepository.findByStatus(filterStatus, pageable);
	        } else if (hasOwner) {
	            eventPage = eventRepository.findByOwner(user, pageable);
	        } else {
	            eventPage = eventRepository.findAll(pageable);
	        }
	    }
	    
	    List<Event> sortedEvents = eventPage.getContent();
	    if ("totalSeats".equalsIgnoreCase(sortBy)) {
	        Comparator<Event> comparator = Comparator.comparingInt(
	                e -> e.getZones().stream()
	                        .mapToInt(z -> z.getMaxTickets() != null ? z.getMaxTickets() : 0)
	                        .sum()
	        );
	        if ("DESC".equalsIgnoreCase(sortDirection)) {
	            comparator = comparator.reversed();
	        }
	        sortedEvents = sortedEvents.stream()
	                .sorted(comparator)
	                .toList();
	    }

	    return new EventPageResponse(
	            eventMapper.toEventResponseDtoList(sortedEvents),
	            eventPage.getTotalElements(),
	            eventPage.getTotalPages(),
	            eventPage.getNumber(),
	            eventPage.getSize()
	    );
	}

	@Override
	@Transactional(readOnly = true)
	public List<EventDto> getAllEvents() {
		List<Event> events = eventRepository.findAll();
		return eventMapper.toEventResponseDtoList(events);
	}

	@Override
	@Transactional(readOnly = true)
	public EventDto getEventById(String id) {
		Event event = eventRepository.findById(id)
				.orElseThrow(() -> BadRequestException.message("Không tìm thấy event với id: " + id));
		if (event.getStatus()!=EventStatus.PUBLISHED) {
			validateOwner(event);			
		}
		return eventMapper.toEventResponseDto(event);
	}

	@Override
	@Transactional
	public void deleteEvent(String id) {
		Event event = eventRepository.findById(id)
				.orElseThrow(() -> BadRequestException.message("Không tìm thấy event với id: " + id));
		validateOwner(event);
		eventRepository.deleteById(id);
	}
	
	public void validateOwner(Event event) {
		User user = securityHelper.getCurrentUser();
		if (!user.equals(event.getOwner()))
			throw UnauthorizedException.message("Bạn không phải chủ sở hữu sự kiện này");
	}

	public void validateEventImage(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new ValidationException("File ảnh là bắt buộc");
		}
		// 5MB in bytes
		if (file.getSize() > 5 * 1024 * 1024) {
			throw new ValidationException("Kích thước file tối đa 5MB");
		}
		String contentType = file.getContentType();
		if (contentType == null || !List.of("image/jpeg", "image/png", "image/webp").contains(contentType)) {
			throw new ValidationException("Chỉ chấp nhận file JPG, PNG, WEBP");
		}
	}
}