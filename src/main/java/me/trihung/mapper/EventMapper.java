package me.trihung.mapper;

import me.trihung.dto.EventDto;
import me.trihung.dto.request.EventRequest;
import me.trihung.entity.Event;
import me.trihung.entity.Zone;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring", uses = {ZoneMapper.class})
public interface EventMapper extends BaseMapper {

    default LocalTime map(String time) {
        if (time == null || time.isBlank()) {
            return null;
        }
        return LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
    }

    default String map(LocalTime time) {
        if (time == null) {
            return null;
        }
        return time.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "eventImage", ignore = true)
    @Mapping(target = "eventBanner", ignore = true)
    @Mapping(target = "organizer.logo", ignore = true)
    @Mapping(target = "status", ignore = true)
    // MapStruct sẽ tự động sử dụng ZoneMapper cho trường 'zones'
    Event toEvent(EventRequest requestDto);
 
    // MapStruct sẽ tự động sử dụng ZoneMapper cho trường 'zones'
    @Mapping(target = "eventStatus", source = "status")
    @Mapping(target = "id", source = "id", qualifiedByName = "stringToUuid")
    @Mapping(target = "ownerId", source = "owner.id", qualifiedByName = "stringToUuid")
    EventDto toEventResponseDto(Event event);
 
    List<EventDto> toEventResponseDtoList(List<Event> events);
    
    //Bỏ qua giá trị nào null từ src (request) không ghi đè lên target hiện có
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "eventImage", ignore = true)
    @Mapping(target = "eventBanner", ignore = true)
    @Mapping(target = "organizer.logo", ignore = true)
    @Mapping(target = "id", source = "id", qualifiedByName = "uuidToString")
    void updateEventFromRequest(EventRequest requestDto, @MappingTarget Event event);

    @AfterMapping
    default void calculateTotals(@MappingTarget EventDto dto, Event event) {
        if (event.getZones() != null && !event.getZones().isEmpty()) {
            // Cập nhật để dùng getMaxTickets()
            int totalSeats = event.getZones().stream()
                .mapToInt(zone -> zone.getMaxTickets() != null ? zone.getMaxTickets() : 0)
                .sum();
            
            BigDecimal totalRevenue = event.getZones().stream()
                .map(zone -> {
                    BigDecimal price = zone.getPrice() != null ? zone.getPrice() : BigDecimal.ZERO;
                    // Cập nhật để dùng getMaxTickets()
                    BigDecimal tickets = new BigDecimal(zone.getMaxTickets() != null ? zone.getMaxTickets() : 0);
                    return price.multiply(tickets);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            dto.setTotalSeats(totalSeats);
            dto.setTotalRevenue(totalRevenue);
        } else {
            dto.setTotalSeats(0);
            dto.setTotalRevenue(BigDecimal.ZERO);
        }
    }
}