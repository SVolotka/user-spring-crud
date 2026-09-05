package ru.volotka.notification.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.volotka.common.dto.UserEventDto;
import ru.volotka.notification.service.EmailService;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationKafkaListener {

    private final EmailService emailService;

    @KafkaListener(
            topics = "user-events-topic",
            groupId = "notification-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(UserEventDto event) {
        log.info("Получено событие из Kafka: {}", event);
        emailService.sendNotification(event);
    }
}
