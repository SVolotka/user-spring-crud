package ru.volotka.notification.service;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import ru.volotka.common.dto.UserEventDto;
import ru.volotka.common.enums.OperationType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = {
        "spring.mail.host=localhost",
        "spring.mail.port=3025",
        "spring.mail.username=",
        "spring.mail.password=",
        "spring.mail.properties.mail.smtp.auth=false",
        "spring.mail.properties.mail.smtp.starttls.enable=false",
        "spring.kafka.listener.auto-startup=false"
})
public class EmailServiceIntegrationTest {

    @Autowired
    private EmailService emailService;

    @Autowired
    private GreenMail greenMail;

    @BeforeEach
    public void setUp() {
        greenMail.start();
    }

    @AfterEach
    public void tearDown() {
        greenMail.stop();
    }

    @TestConfiguration
    static class GreenMailConfig {
        @Bean
        @Primary
        public GreenMail greenMail() {
            return new GreenMail(ServerSetupTest.SMTP);
        }
    }

    @Test
    void shouldSendCreateNotificationEmail() throws Exception {
        UserEventDto event = UserEventDto.builder()
                .email("newUser@mail.ru")
                .operationType(OperationType.CREATE)
                .build();

        emailService.sendNotification(event);

        boolean emailReceived = greenMail.waitForIncomingEmail(2000, 1);
        assertTrue(emailReceived, "Письмо не было получено в течение 2 секунд");

        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(1, receivedMessages.length, "Должно быть получено ровно 1 письмо");

        MimeMessage msg = receivedMessages[0];
        assertEquals("newUser@mail.ru", msg.getAllRecipients()[0].toString());
        assertEquals("Уведомление об аккаунте", msg.getSubject());

        String body = msg.getContent().toString();

        assertTrue(body.contains("Здравствуйте!"),
                "Текст письма не содержит 'Здравствуйте!'. Реальный текст: " + body);
        assertTrue(body.contains("успешно создан"),
                "Текст письма не содержит 'успешно создан'. Реальный текст: " + body);
    }

    @Test
    void shouldSendDeleteNotificationEmail() throws Exception {
        UserEventDto event = UserEventDto.builder()
                .email("deletedUser@mail.ru")
                .operationType(OperationType.DELETE)
                .build();

        emailService.sendNotification(event);

        boolean emailReceived = greenMail.waitForIncomingEmail(2000, 1);
        assertTrue(emailReceived, "Письмо не было получено в течение 2 секунд");

        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(1, receivedMessages.length);

        MimeMessage msg = receivedMessages[0];
        String body = msg.getContent().toString();

        assertTrue(body.contains("Здравствуйте!"),
                "Текст письма не содержит 'Здравствуйте!'. Реальный текст: " + body);
        assertTrue(body.contains("удалён"),
                "Текст письма не содержит 'удалён'. Реальный текст: " + body);
    }
}