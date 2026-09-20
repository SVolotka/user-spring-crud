package ru.volotka.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.volotka.user.dto.UserRequestDto;
import ru.volotka.user.dto.UserResponseDto;
import ru.volotka.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "Users", description = "User management operations")
public class UserController {

    private final UserService userService;

    private EntityModel<UserResponseDto> toModel(UserResponseDto user) {
        return EntityModel.of(user,
                linkTo(methodOn(UserController.class).findById(user.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).findAll()).withRel("users"),
                linkTo(methodOn(UserController.class).delete(user.getId())).withRel("delete")
        );
    }

    @PostMapping
    @Operation(summary = "Create a new user")
    @ApiResponse(responseCode = "201", description = "User successfully created")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "409", description = "Email already exists")
    public ResponseEntity<EntityModel<UserResponseDto>> create(@Valid @RequestBody UserRequestDto userDto) {
        log.info("Request to create user: {}", userDto);
        UserResponseDto created = userService.create(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toModel(created));
    }

    @GetMapping
    @Operation(summary = "Get all users")
    @ApiResponse(responseCode = "200", description = "List of users")
    public ResponseEntity<CollectionModel<EntityModel<UserResponseDto>>> findAll() {
        log.info("Request to get all users");
        List<EntityModel<UserResponseDto>> userModels = userService.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<UserResponseDto>> collectionModel = CollectionModel.of(userModels);
        collectionModel.add(linkTo(methodOn(UserController.class).findAll()).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<EntityModel<UserResponseDto>> findById(@PathVariable Long id) {
        log.info("Request to get user with id: {}", id);
        return ResponseEntity.ok(toModel(userService.findById(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user data")
    @ApiResponse(responseCode = "200", description = "User updated")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "409", description = "Email already exists")
    public ResponseEntity<EntityModel<UserResponseDto>> update(@PathVariable Long id,
                                                               @Valid @RequestBody UserRequestDto userDto) {
        log.info("Request to update user with id: {}", id);
        return ResponseEntity.ok(toModel(userService.update(id, userDto)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user")
    @ApiResponse(responseCode = "204", description = "User successfully deleted")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Request to delete user with id: {}", id);
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}