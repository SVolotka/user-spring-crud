package ru.volotka.common.enums;

import lombok.Getter;

@Getter
public enum OperationType {
    CREATE("Здравствуйте! Ваш аккаунт на сайте был успешно создан."),
    DELETE("Здравствуйте! Ваш аккаунт был удалён.");

    private final String notificationText;

    OperationType(String notificationText) {
        this.notificationText = notificationText;
    }
}
