package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@EqualsAndHashCode(of = { "id" })
public class Film {
    public static final String ERROR_FILM_NAME_EMPTY = "Название не может быть пустым";
    public static final int MAX_FILM_DESCRIPTION_LENGTH = 200;
    public static final String ERROR_FILM_DESCRIPTION_MAX_LENGTH =
            "Максимальная длина описания — " + MAX_FILM_DESCRIPTION_LENGTH + " символов";
    public static final String ERROR_FILM_DURATION_MIN =
            "Продолжительность фильма должна быть положительным числом";
    public static final LocalDate MIN_FILM_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    public static final String ERROR_MIN_FILM_RELEASE_DATE = "Дата релиза — не раньше 28 декабря 1895 года года";

    private Long id;

    @NotBlank(message = ERROR_FILM_NAME_EMPTY)
    private String name;

    @Size(max = MAX_FILM_DESCRIPTION_LENGTH, message = ERROR_FILM_DESCRIPTION_MAX_LENGTH)
    private String description;

    private LocalDate releaseDate;

    @NotNull(message = ERROR_FILM_DURATION_MIN)
    @Positive(message = ERROR_FILM_DURATION_MIN)
    private Integer duration;

    private Set<Long> likes = new HashSet<>();

    @AssertTrue(message = ERROR_MIN_FILM_RELEASE_DATE)
    public boolean isReleaseDateValid() {
        if (releaseDate == null) {
            return false;
        }
        return !releaseDate.isBefore(MIN_FILM_RELEASE_DATE);
    }
}
