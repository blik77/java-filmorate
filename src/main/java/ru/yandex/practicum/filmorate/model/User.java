package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@EqualsAndHashCode(of = { "id" })
public class User {
    public static final String ERROR_USER_EMAIL = "Электронная почта не может быть пустой и должна содержать символ @";
    public static final String ERROR_USER_LOGIN = "Логин не может быть пустым и содержать пробелы";
    public static final String ERROR_USER_BIRTHDAY = "Дата рождения не может быть в будущем";

    private Long id;

    @NotBlank(message = ERROR_USER_EMAIL)
    @Email(message = ERROR_USER_EMAIL)
    private String email;

    @NotBlank(message = ERROR_USER_LOGIN)
    @Pattern(regexp = "\\S+", message = ERROR_USER_LOGIN)
    private String login;

    private String name;

    @Past(message = ERROR_USER_BIRTHDAY)
    private LocalDate birthday;

    private Set<Long> friends = new HashSet<>();
}
