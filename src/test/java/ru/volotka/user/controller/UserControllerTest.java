package ru.volotka.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.volotka.user.dto.UserRequestDto;
import ru.volotka.user.dto.UserResponseDto;
import ru.volotka.user.exception.ConflictException;
import ru.volotka.user.exception.NotFoundException;
import ru.volotka.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    void shouldCreateUser() throws Exception {
        UserRequestDto requestDto = new UserRequestDto("Test", "test@example.com", 25);
        UserResponseDto responseDto = new UserResponseDto(1L, "Test", "test@example.com", 25, LocalDateTime.now());

        when(userService.create(any(UserRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.age").value(25))
                .andExpect(jsonPath("$.createdAt").exists());

        verify(userService, times(1)).create(any(UserRequestDto.class));
    }

    @Test
    void shouldReturnBadRequestWhenCreateInvalidUser() throws Exception {
        UserRequestDto invalidDto = new UserRequestDto("", "bad-email", null); // невалидные данные

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).create(any(UserRequestDto.class));
    }

    @Test
    void shouldReturnConflictWhenEmailTakenOnCreate() throws Exception {
        UserRequestDto requestDto = new UserRequestDto("Test", "taken@example.com", 25);
        when(userService.create(any(UserRequestDto.class))).thenThrow(new ConflictException("Email уже занят"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("CONFLICT"))
                .andExpect(jsonPath("$.message").value("Email уже занят"));
    }

    @Test
    void shouldFindAllUsers() throws Exception {
        UserResponseDto user1 = new UserResponseDto(1L, "User1", "user1@example.com", 20, LocalDateTime.now());
        UserResponseDto user2 = new UserResponseDto(2L, "User2", "user2@example.com", 25, LocalDateTime.now());
        when(userService.findAll()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));

        verify(userService, times(1)).findAll();
    }

    @Test
    void shouldFindUserById() throws Exception {
        UserResponseDto user = new UserResponseDto(1L, "Test", "test@example.com", 25, LocalDateTime.now());
        when(userService.findById(1L)).thenReturn(user);

        mockMvc.perform(get("/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test"));

        verify(userService, times(1)).findById(1L);
    }

    @Test
    void shouldReturnNotFoundWhenUserMissing() throws Exception {
        when(userService.findById(999L)).thenThrow(new NotFoundException("Пользователь с id=999 не найден"));

        mockMvc.perform(get("/users/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Пользователь с id=999 не найден"));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        UserRequestDto requestDto = new UserRequestDto("Updated", "updated@example.com", 30);
        UserResponseDto responseDto = new UserResponseDto(1L, "Updated", "updated@example.com", 30, LocalDateTime.now());
        when(userService.update(eq(1L), any(UserRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated"));

        verify(userService, times(1)).update(eq(1L), any(UserRequestDto.class));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingMissingUser() throws Exception {
        UserRequestDto requestDto = new UserRequestDto("Updated", "updated@example.com", 30);
        when(userService.update(eq(999L), any(UserRequestDto.class)))
                .thenThrow(new NotFoundException("Пользователь с id=999 не найден"));

        mockMvc.perform(put("/users/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        UserResponseDto deletedUser = new UserResponseDto(1L, "Dave", "dave@example.com", 25, LocalDateTime.now());
        when(userService.delete(1L)).thenReturn(deletedUser);

        mockMvc.perform(delete("/users/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).delete(1L);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingMissingUser() throws Exception {
        doThrow(new NotFoundException("Пользователь с id=999 не найден"))
                .when(userService).delete(999L);

        mockMvc.perform(delete("/users/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Пользователь с id=999 не найден"));
    }
}