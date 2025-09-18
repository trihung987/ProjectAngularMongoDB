package me.trihung.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import me.trihung.dto.UserDTO;
import me.trihung.dto.request.SignUpRequest;
import me.trihung.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper { 
	UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
	
	UserDTO toDto (User user);
	
	@Mapping(target = "avatarUrl", ignore = true)
	@Mapping(target = "fullName", source = "fullname") 
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "roles", ignore = true)
	User toEntity (SignUpRequest signUpRequest);

}