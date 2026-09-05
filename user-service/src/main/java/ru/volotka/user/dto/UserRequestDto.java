package ru.volotka.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserRequestDto {
    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 2, message = "Имя должно содержать минимум 2 символа")
    private String name;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    @Size(min = 5, message = "Email должен содержать минимум 5 символов")
    private String email;

    @NotNull(message = "Возраст не может быть пустым")
    @Min(value = 1, message = "Возраст должен быть от 1 до 120")
    @Max(value = 120, message = "Возраст должен быть от 1 до 120")
    private Integer age;
}
