package ru.volotka.user.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.volotka.common.dto.UserEventDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private static final String USER_EVENTS_TOPIC = "user-events-topic";

    private final KafkaTemplate<String, UserEventDto> kafkaTemplate;

    public void sendUserEvent(UserEventDto event) {
        log.info("Отправка события в Kafka: email={}, type={}", event.getEmail(), event.getOperationType());
        kafkaTemplate.send(USER_EVENTS_TOPIC, event.getEmail(), event);
    }
}
