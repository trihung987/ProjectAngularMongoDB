package me.trihung.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import me.trihung.dto.OrderDto;
import me.trihung.entity.Order;

@Mapper
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(source = "zone.id", target = "zoneId")
    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(target = "priceZone", source = "zone.price")
    @Mapping(target = "nameZone", source = "zone.name")
    @Mapping(target = "nameEvent", source = "zone.event.eventName")
    OrderDto toDto(Order order);
}
