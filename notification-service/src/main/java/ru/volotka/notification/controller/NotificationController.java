package ru.volotka.notification.controller;

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
public class NotificationController {

    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendManualNotification(@RequestBody UserEventDto event) {
        log.info("Получен запрос на отправку уведомления: email = {}, type = {}", event.getEmail(), event.getOperationType());

        emailService.sendNotification(event);
        return ResponseEntity.ok("Уведомление успешно отправлено на email = " + event.getEmail());
    }
}
