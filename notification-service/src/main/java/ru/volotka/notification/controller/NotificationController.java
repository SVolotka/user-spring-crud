package ru.volotka.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.volotka.common.dto.UserEventDto;
import ru.volotka.notification.service.EmailService;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/notifications")
@Tag(name = "Notifications", description = "Отправка уведомлений")
public class NotificationController {

    private final EmailService emailService;

    @PostMapping("/send")
    @Operation(summary = "Отправить уведомление вручную",
            description = "Отправляет email с уведомлением в зависимости от типа операции (CREATE/DELETE)")
    @ApiResponse(responseCode = "200", description = "Уведомление отправлено")
    public ResponseEntity<String> sendManualNotification(@RequestBody UserEventDto event) {
        log.info("Получен запрос на отправку уведомления: email = {}, type = {}",
                event.getEmail(), event.getOperationType());

        emailService.sendNotification(event);
        return ResponseEntity.ok("Уведомление успешно отправлено на email = " + event.getEmail());
    }
}
