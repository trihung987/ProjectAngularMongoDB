package me.trihung.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import me.trihung.dto.OrderDto;
import me.trihung.entity.Order;
import me.trihung.entity.Zone;
import me.trihung.entity.Event;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface OrderMapper extends BaseMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(source = "id", target = "id", qualifiedByName = "stringToUuid")
    @Mapping(source = "zone.id", target = "zoneId", qualifiedByName = "stringToUuid")
    @Mapping(source = "owner.id", target = "ownerId", qualifiedByName = "stringToUuid")
    @Mapping(target = "priceZone", source = "zone.price")
    @Mapping(target = "nameZone", source = "zone.name")
    @Mapping(target = "nameEvent", ignore = true) // This will be handled by the aggregation pipeline
    OrderDto toDto(Order order);
}
