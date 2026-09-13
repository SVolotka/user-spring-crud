package ru.volotka.user.service;

import ru.volotka.user.dto.UserRequestDto;
import ru.volotka.user.dto.UserResponseDto;

import java.util.List;

public interface UserService {

    UserResponseDto create(UserRequestDto userDto);

    UserResponseDto findById(Long id);

    UserResponseDto update(Long id, UserRequestDto userDto);

    UserResponseDto delete(Long id);

    List<UserResponseDto> findAll();
}
