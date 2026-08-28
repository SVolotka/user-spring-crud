package ru.volotka.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.volotka.user.dto.UserRequestDto;
import ru.volotka.user.dto.UserResponseDto;
import ru.volotka.user.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User toEntity(UserRequestDto dto);

    UserResponseDto toResponseDto(User user);
}
