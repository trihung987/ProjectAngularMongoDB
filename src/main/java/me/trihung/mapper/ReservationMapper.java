package me.trihung.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import me.trihung.dto.ReservationDto;
import me.trihung.entity.Reservation;

@Mapper(componentModel = "spring")
public interface ReservationMapper extends BaseMapper {

    ReservationMapper INSTANCE = Mappers.getMapper(ReservationMapper.class);

    @Mapping(target = "id", source = "id", qualifiedByName = "stringToUuid")
    @Mapping(target = "zoneId", source = "zone.id", qualifiedByName = "stringToUuid")
    @Mapping(target = "ownerId", source = "owner.id", qualifiedByName = "stringToUuid")
    @Mapping(target = "priceZone", source = "zone.price")
    @Mapping(target = "nameZone", source = "zone.name")
    @Mapping(target = "nameEvent", ignore = true) // This will need to be handled in service layer
    ReservationDto toDto(Reservation reservation); 

    @Mapping(target = "zone", ignore = true) // Handle in service layer
    @Mapping(target = "owner", ignore = true) // Handle in service layer
    @Mapping(target = "id", source = "id", qualifiedByName = "uuidToString")
    Reservation toEntity(ReservationDto dto);
}
