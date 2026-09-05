package ru.volotka.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.volotka.common.dto.UserEventDto;
import ru.volotka.common.enums.OperationType;
import ru.volotka.user.dto.UserRequestDto;
import ru.volotka.user.dto.UserResponseDto;
import ru.volotka.user.entity.User;
import ru.volotka.user.exception.ConflictException;
import ru.volotka.user.exception.NotFoundException;
import ru.volotka.user.kafka.KafkaProducerService;
import ru.volotka.user.mapper.UserMapper;
import ru.volotka.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final KafkaProducerService kafkaProducerService;

    @Override
    @Transactional
    public UserResponseDto create(UserRequestDto userDto) {
        log.info("Создание пользователя с email: {}", userDto.getEmail());
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new ConflictException("Email уже занят");
        }

        User createdUser = userMapper.toEntity(userDto);
        createdUser = userRepository.save(createdUser);
        log.info("Создан пользователь с id: {}", createdUser.getId());

        UserEventDto event = UserEventDto.builder()
                .email(createdUser.getEmail())
                .operationType(OperationType.CREATE)
                .build();
        kafkaProducerService.sendUserEvent(event);
        return userMapper.toResponseDto(createdUser);
    }

    @Override
    public UserResponseDto findById(Long id) {
        log.info("Поиск пользователя с id: {}", id);
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));

        return userMapper.toResponseDto(existingUser);
    }

    @Override
    @Transactional
    public UserResponseDto update(Long id, UserRequestDto userDto) {
        log.info("Обновление пользователя с id: {}", id);
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));

        if (!existingUser.getEmail().equals(userDto.getEmail()) && userRepository.existsByEmail(userDto.getEmail())) {
            log.warn("Попытка обновить email на уже занятый: {}", userDto.getEmail());
            throw new ConflictException("Email уже занят");
        }
        existingUser.setName(userDto.getName());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setAge(userDto.getAge());
        userRepository.save(existingUser);
        log.info("Обновлен пользователь с id: {}", id);
        return userMapper.toResponseDto(existingUser);
    }

    @Override
    @Transactional
    public UserResponseDto delete(Long id) {
        log.info("Удаление пользователя с id: {}", id);
        User deletedUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));

        userRepository.delete(deletedUser);
        log.info("Удален пользователь с id: {}", id);

        UserEventDto event = UserEventDto.builder()
                .email(deletedUser.getEmail())
                .operationType(OperationType.DELETE)
                .build();
        kafkaProducerService.sendUserEvent(event);
        return userMapper.toResponseDto(deletedUser);
    }

    @Override
    public List<UserResponseDto> findAll() {
        log.info("Получение всех пользователей");
        return userRepository.findAll().stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
