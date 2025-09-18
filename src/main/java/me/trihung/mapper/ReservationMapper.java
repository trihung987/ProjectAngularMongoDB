package me.trihung.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import me.trihung.dto.ReservationDto;
import me.trihung.entity.Reservation;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    ReservationMapper INSTANCE = Mappers.getMapper(ReservationMapper.class);

    @Mapping(target = "zoneId", source = "zone.id")
    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "priceZone", source = "zone.price")
    @Mapping(target = "nameZone", source = "zone.name")
    @Mapping(target = "nameEvent", source = "zone.event.eventName")
    ReservationDto toDto(Reservation reservation); 

    @Mapping(target = "zone.id", source = "zoneId")
    Reservation toEntity(ReservationDto dto);
}
