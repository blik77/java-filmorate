package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    private FilmController fc;
    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory vf = Validation.buildDefaultValidatorFactory()) {
            validator = vf.getValidator();
        }

        InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();
        InMemoryUserStorage userStorage = new InMemoryUserStorage();

        FilmService filmService = new FilmService(filmStorage, userStorage);

        fc = new FilmController(filmService);

        Film film = new Film();
        film.setId(1L);
        film.setName("Test Film");
        film.setDescription("Test Film Description");
        film.setReleaseDate(LocalDate.of(2025, 1, 1));
        film.setDuration(11);

        fc.createFilm(film);
    }

    @Test
    void createFilm_whenFilmValid_returnFilm() {
        Film newFilm = new Film();
        newFilm.setName("film");
        newFilm.setDescription("Test Film Description");
        newFilm.setReleaseDate(LocalDate.of(2025, 1, 1));
        newFilm.setDuration(11);

        Set<ConstraintViolation<Film>> violations = validator.validate(newFilm);
        Film film = fc.createFilm(newFilm);

        assertTrue(violations.isEmpty());
        assertNotNull(film.getId());
        assertEquals(film.getName(), newFilm.getName());
        assertEquals(film.getDescription(), newFilm.getDescription());
        assertEquals(film.getReleaseDate(), newFilm.getReleaseDate());
        assertEquals(film.getDuration(), newFilm.getDuration());
        assertTrue(fc.getAllFilms().contains(newFilm));
    }

    @Test
    void createFilm_whenFilmNameEmpty_returnError() {
        Film newFilm = new Film();
        newFilm.setName("");
        newFilm.setDescription("Test Film Description");
        newFilm.setReleaseDate(LocalDate.of(2025, 1, 1));
        newFilm.setDuration(11);

        Set<ConstraintViolation<Film>> violations = validator.validate(newFilm);
        String message = violations.iterator().next().getMessage();

        assertEquals(Film.ERROR_FILM_NAME_EMPTY, message);
        assertFalse(fc.getAllFilms().size() > 1);
    }

    @Test
    void createFilm_whenFilmNameNull_returnError() {
        Film newFilm = new Film();
        newFilm.setName(null);
        newFilm.setDescription("Test Film Description");
        newFilm.setReleaseDate(LocalDate.of(2025, 1, 1));
        newFilm.setDuration(11);

        Set<ConstraintViolation<Film>> violations = validator.validate(newFilm);
        String message = violations.iterator().next().getMessage();

        assertEquals(Film.ERROR_FILM_NAME_EMPTY, message);
        assertFalse(fc.getAllFilms().size() > 1);
    }

    @Test
    void createFilm_whenFilmDescriptionLong_returnError() {
        String description = "Test Film Description".repeat(11);
        Film newFilm = new Film();
        newFilm.setName("film");
        newFilm.setDescription(description);
        newFilm.setReleaseDate(LocalDate.of(2025, 1, 1));
        newFilm.setDuration(11);

        Set<ConstraintViolation<Film>> violations = validator.validate(newFilm);
        String message = violations.iterator().next().getMessage();

        assertEquals(Film.ERROR_FILM_DESCRIPTION_MAX_LENGTH, message);
        assertFalse(fc.getAllFilms().size() > 1);
    }

    @Test
    void createFilm_whenFilmReleaseDateBefore_returnError() {
        Film newFilm = new Film();
        newFilm.setName("film");
        newFilm.setDescription("Test Film Description");
        newFilm.setReleaseDate(LocalDate.of(1111, 1, 1));
        newFilm.setDuration(11);

        Set<ConstraintViolation<Film>> violations = validator.validate(newFilm);
        String message = violations.iterator().next().getMessage();

        assertEquals(Film.ERROR_MIN_FILM_RELEASE_DATE, message);
        assertFalse(fc.getAllFilms().size() > 1);
    }

    @Test
    void createFilm_whenFilmDurationZero_returnError() {
        Film newFilm = new Film();
        newFilm.setName("film");
        newFilm.setDescription("Test Film Description");
        newFilm.setReleaseDate(LocalDate.of(2025, 1, 1));
        newFilm.setDuration(0);

        Set<ConstraintViolation<Film>> violations = validator.validate(newFilm);
        String message = violations.iterator().next().getMessage();

        assertEquals(Film.ERROR_FILM_DURATION_MIN, message);
        assertFalse(fc.getAllFilms().size() > 1);
    }

    @Test
    void createFilm_whenFilmDurationNegative_returnError() {
        Film newFilm = new Film();
        newFilm.setName("film");
        newFilm.setDescription("Test Film Description");
        newFilm.setReleaseDate(LocalDate.of(2025, 1, 1));
        newFilm.setDuration(-11);

        Set<ConstraintViolation<Film>> violations = validator.validate(newFilm);
        String message = violations.iterator().next().getMessage();

        assertEquals(Film.ERROR_FILM_DURATION_MIN, message);
        assertFalse(fc.getAllFilms().size() > 1);
    }

    @Test
    void createFilm_whenFilmDurationNull_returnError() {
        Film newFilm = new Film();
        newFilm.setName("film");
        newFilm.setDescription("Test Film Description");
        newFilm.setReleaseDate(LocalDate.of(2025, 1, 1));
        newFilm.setDuration(null);

        Set<ConstraintViolation<Film>> violations = validator.validate(newFilm);
        String message = violations.iterator().next().getMessage();

        assertEquals(Film.ERROR_FILM_DURATION_MIN, message);
        assertFalse(fc.getAllFilms().size() > 1);
    }

    @Test
    void updateFilm_whenFilmValid_returnFilm() {
        Film newFilm = new Film();
        newFilm.setId(1L);
        newFilm.setName("film");
        newFilm.setDescription("Test Film Description");
        newFilm.setReleaseDate(LocalDate.of(2025, 1, 1));
        newFilm.setDuration(11);

        Set<ConstraintViolation<Film>> violations = validator.validate(newFilm);
        Film film = fc.updateFilm(newFilm);

        assertNotNull(film);
        assertTrue(violations.isEmpty());
        assertTrue(fc.getAllFilms().contains(film));
        assertFalse(fc.getAllFilms().size() > 1);

        assertEquals(1, film.getId());
        assertEquals(film.getName(), newFilm.getName());
        assertEquals(film.getDescription(), newFilm.getDescription());
        assertEquals(film.getReleaseDate(), newFilm.getReleaseDate());
        assertEquals(film.getDuration(), newFilm.getDuration());
    }

    @Test
    void updateFilm_whenFilmIdWrong_returnError() {
        Film newFilm = new Film();
        newFilm.setId(2L);
        newFilm.setName("film");
        newFilm.setDescription("Test Film Description");
        newFilm.setReleaseDate(LocalDate.of(2025, 1, 1));
        newFilm.setDuration(11);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> fc.updateFilm(newFilm));

        assertEquals(InMemoryFilmStorage.ERROR_FILM_ID, exception.getMessage());
        assertFalse(fc.getAllFilms().size() > 1);
    }

    @Test
    void updateFilm_whenFilmNameEmpty_returnError() {
        Film newFilm = new Film();
        newFilm.setId(1L);
        newFilm.setName("");
        newFilm.setDescription("Test Film Description");
        newFilm.setReleaseDate(LocalDate.of(2025, 1, 1));
        newFilm.setDuration(11);

        Set<ConstraintViolation<Film>> violations = validator.validate(newFilm);
        String message = violations.iterator().next().getMessage();

        assertEquals(Film.ERROR_FILM_NAME_EMPTY, message);
        assertFalse(fc.getAllFilms().size() > 1);
    }

    @Test
    void updateFilm_whenFilmDescriptionLong_returnError() {
        String description = "Test Film Description 2".repeat(11);
        Film newFilm = new Film();
        newFilm.setId(1L);
        newFilm.setName("film");
        newFilm.setDescription(description);
        newFilm.setReleaseDate(LocalDate.of(2025, 1, 1));
        newFilm.setDuration(11);

        Set<ConstraintViolation<Film>> violations = validator.validate(newFilm);
        String message = violations.iterator().next().getMessage();

        assertEquals(Film.ERROR_FILM_DESCRIPTION_MAX_LENGTH, message);
        assertFalse(fc.getAllFilms().size() > 1);
    }

    @Test
    void updateFilm_whenFilmReleaseDateBefore_returnError() {
        Film newFilm = new Film();
        newFilm.setId(1L);
        newFilm.setName("film");
        newFilm.setDescription("Test Film Description");
        newFilm.setReleaseDate(LocalDate.of(1111, 1, 1));
        newFilm.setDuration(11);

        Set<ConstraintViolation<Film>> violations = validator.validate(newFilm);
        String message = violations.iterator().next().getMessage();

        assertEquals(Film.ERROR_MIN_FILM_RELEASE_DATE, message);
        assertFalse(fc.getAllFilms().size() > 1);
    }

    @Test
    void updateFilm_whenFilmDurationNegative_returnError() {
        Film newFilm = new Film();
        newFilm.setId(1L);
        newFilm.setName("film");
        newFilm.setDescription("Test Film Description");
        newFilm.setReleaseDate(LocalDate.of(2025, 1, 1));
        newFilm.setDuration(-11);


        Set<ConstraintViolation<Film>> violations = validator.validate(newFilm);
        String message = violations.iterator().next().getMessage();

        assertEquals(Film.ERROR_FILM_DURATION_MIN, message);
        assertFalse(fc.getAllFilms().size() > 1);
    }
}