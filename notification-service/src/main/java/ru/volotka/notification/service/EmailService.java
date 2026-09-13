package ru.volotka.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.volotka.common.dto.UserEventDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendNotification(UserEventDto event) {
        log.info("Попытка отправки уведомления на {} (тип: {})", event.getEmail(), event.getOperationType());

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(event.getEmail());
        message.setSubject("Уведомление об аккаунте");
        message.setText(event.getOperationType().getNotificationText());

        try {
            mailSender.send(message);
            log.info("✅ Письмо успешно отправлено на {}", event.getEmail());
        } catch (Exception e) {
            log.error("❌ Ошибка при отправке письма: {}", e.getMessage());
        }
    }
}
