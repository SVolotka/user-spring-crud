package ru.volotka.user.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(NotFoundException e) {
        log.warn("Не найдено: {}", e.getMessage());
        return ApiError.builder()
                .status("NOT_FOUND")
                .reason("Запрашиваемый объект не найден.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(ConflictException e) {
        log.warn("Конфликт: {}", e.getMessage());
        return ApiError.builder()
                .status("CONFLICT")
                .reason("Нарушение целостности данных.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(ValidationException e) {
        log.warn("Ошибка валидации: {}", e.getMessage());
        return ApiError.builder()
                .status("BAD_REQUEST")
                .reason("Некорректно составленный запрос.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        log.warn("Ошибка валидации: {}", e.getMessage());
        String message = e.getBindingResult().getAllErrors().getFirst().getDefaultMessage();
        return ApiError.builder()
                .status("BAD_REQUEST")
                .reason("Некорректно составленный запрос.")
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleAll(Exception e) {
        log.error("Непредвиденная ошибка", e);
        return ApiError.builder()
                .status("INTERNAL_SERVER_ERROR")
                .reason("Непредвиденная ошибка")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
