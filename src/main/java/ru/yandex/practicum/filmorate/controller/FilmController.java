package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static ru.yandex.practicum.filmorate.util.ModelUtil.checkError;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private static final LocalDate MIN_FILM_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    public static final String ERROR_MIN_FILM_RELEASE_DATE =
        "Дата релиза — не раньше " + MIN_FILM_RELEASE_DATE.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")) + " года";
    public static final String ERROR_FILM_ID = "Фильм не найден";

    private final Map<Long, Film> films = new HashMap<>();

    private long newFilmId = 1;

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("getAllFilms: {}", films.size());
        return films.values();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film, Errors errors) {
        checkData(film, errors);

        film.setId(newFilmId++);

        films.put(film.getId(), film);
        log.info("createFilm: {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film, Errors errors) {
        if (film.getId() == null || !films.containsKey(film.getId())) {
            log.error(ERROR_FILM_ID);
            throw new ValidationException(ERROR_FILM_ID);
        }

        checkData(film, errors);

        films.put(film.getId(), film);
        log.info("updateFilm: {}", film);
        return film;
    }

    private void checkData(Film film, Errors errors) {
        checkError(errors);

        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(MIN_FILM_RELEASE_DATE)) {
            log.error(ERROR_MIN_FILM_RELEASE_DATE);
            throw new ValidationException(ERROR_MIN_FILM_RELEASE_DATE);
        }
    }
}
