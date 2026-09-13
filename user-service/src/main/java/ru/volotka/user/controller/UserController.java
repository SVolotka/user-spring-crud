package ru.volotka.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.volotka.user.dto.UserRequestDto;
import ru.volotka.user.dto.UserResponseDto;
import ru.volotka.user.service.UserService;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "Users", description = "Операции с пользователями (CRUD + HATEOAS)")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "Создать нового пользователя")
    @ApiResponse(responseCode = "201", description = "Пользователь успешно создан")
    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
    @ApiResponse(responseCode = "409", description = "Email уже занят")
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody UserRequestDto userDto) {
        log.info("Запрос на создание пользователя: {}", userDto);
        UserResponseDto response = userService.create(userDto);

        response.add(linkTo(methodOn(UserController.class).findById(response.getId())).withSelfRel());
        response.add(linkTo(methodOn(UserController.class).findAll()).withRel("users"));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Получить список всех пользователей")
    @ApiResponse(responseCode = "200", description = "Список пользователей")
    public ResponseEntity<CollectionModel<UserResponseDto>> findAll() {
        log.info("Запрос на получение всех пользователей");
        List<UserResponseDto> users = userService.findAll();
        users.forEach(user -> user.add(
                linkTo(methodOn(UserController.class).findById(user.getId())).withSelfRel()
        ));

        CollectionModel<UserResponseDto> collectionModel = CollectionModel.of(users);
        collectionModel.add(linkTo(methodOn(UserController.class).findAll()).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить пользователя по ID")
    @ApiResponse(responseCode = "200", description = "Пользователь найден")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    public ResponseEntity<UserResponseDto> findById(@PathVariable Long id) {
        log.info("Запрос на получение пользователя с id: {}", id);
        UserResponseDto response = userService.findById(id);

        response.add(linkTo(methodOn(UserController.class).findById(id)).withSelfRel());
        response.add(linkTo(methodOn(UserController.class).findAll()).withRel("users"));

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить данные пользователя")
    @ApiResponse(responseCode = "200", description = "Пользователь обновлён")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    @ApiResponse(responseCode = "409", description = "Email уже занят")
    public ResponseEntity<UserResponseDto> update(@PathVariable Long id,
                                                  @Valid @RequestBody UserRequestDto userDto) {
        log.info("Запрос на обновление пользователя с id: {}", id);
        UserResponseDto response = userService.update(id, userDto);

        response.add(linkTo(methodOn(UserController.class).findById(id)).withSelfRel());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить пользователя")
    @ApiResponse(responseCode = "204", description = "Пользователь успешно удалён")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Запрос на удаление пользователя с id: {}", id);
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
