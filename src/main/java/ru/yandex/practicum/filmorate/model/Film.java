package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = { "id" })
public class Film {
    private static final int MAX_FILM_DESCRIPTION_LENGTH = 200;

    public static final String ERROR_FILM_NAME_EMPTY = "Название не может быть пустым";
    public static final String ERROR_FILM_DESCRIPTION_MAX_LENGTH =
            "Максимальная длина описания — " + MAX_FILM_DESCRIPTION_LENGTH + " символов";
    public static final String ERROR_FILM_DURATION_MIN =
            "Продолжительность фильма должна быть положительным числом";

    private Long id;

    @NotBlank(message = ERROR_FILM_NAME_EMPTY)
    private String name;

    @Size(max = MAX_FILM_DESCRIPTION_LENGTH, message = ERROR_FILM_DESCRIPTION_MAX_LENGTH)
    private String description;

    private LocalDate releaseDate;

    @NotNull(message = ERROR_FILM_DURATION_MIN)
    @Positive(message = ERROR_FILM_DURATION_MIN)
    private Integer duration;
}
