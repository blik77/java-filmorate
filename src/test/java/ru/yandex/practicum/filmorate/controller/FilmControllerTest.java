package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;


import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    private FilmController fc;
    private Validator validator;

    Errors processErrors(Film film) {
        Errors errors = new BeanPropertyBindingResult(film, "film");
        for (ConstraintViolation<Film> v : validator.validate(film)) {
            errors.rejectValue(v.getPropertyPath().toString(), "invalid", v.getMessage());
        }
        return errors;
    }

    @BeforeEach
    void setUp() {
        try (ValidatorFactory vf = Validation.buildDefaultValidatorFactory()) {
            validator = vf.getValidator();
        }
        fc = new FilmController();
        Film film = Film.builder()
            .id(1L)
            .name("Test Film")
            .description("Test Film Description")
            .releaseDate(LocalDate.of(2025, 1, 1))
            .duration(11)
            .build();

        Errors errors = new BeanPropertyBindingResult(film, "film");
        fc.createFilm(film, errors);
    }

    @Test
    void createFilm_whenFilmValid_returnFilm() {
        Film newFilm = Film.builder()
            .name("film")
            .description("Test Film Description")
            .releaseDate(LocalDate.of(2025, 1, 1))
            .duration(11)
            .build();

        Errors errors = new BeanPropertyBindingResult(newFilm, "film");
        Set<ConstraintViolation<Film>> violations = validator.validate(newFilm);
        Film film = fc.createFilm(newFilm, errors);

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
        Film newFilm = Film.builder()
            .name("")
            .description("Test Film Description")
            .releaseDate(LocalDate.of(2025, 1, 1))
            .duration(11)
            .build();

        Errors errors = processErrors(newFilm);
        ValidationException exception = assertThrows(ValidationException.class, () -> fc.createFilm(newFilm, errors));

        assertEquals(Film.ERROR_FILM_NAME_EMPTY, exception.getMessage());
        assertFalse(fc.getAllFilms().size() > 1);
    }

    @Test
    void createFilm_whenFilmNameNull_returnError() {
        Film newFilm = Film.builder()
            .name(null)
            .description("Test Film Description")
            .releaseDate(LocalDate.of(2025, 1, 1))
            .duration(11)
            .build();

        Errors errors = processErrors(newFilm);
        ValidationException exception = assertThrows(ValidationException.class, () -> fc.createFilm(newFilm, errors));

        assertEquals(Film.ERROR_FILM_NAME_EMPTY, exception.getMessage());
        assertFalse(fc.getAllFilms().size() > 1);
    }

    @Test
    void createFilm_whenFilmDescriptionLong_returnError() {
        String description = "Test Film Description".repeat(11);
        Film newFilm = Film.builder()
            .name("Test Film 2")
            .description(description)
            .releaseDate(LocalDate.of(2025, 1, 1))
            .duration(11)
            .build();

        Errors errors = processErrors(newFilm);
        ValidationException exception = assertThrows(ValidationException.class, () -> fc.createFilm(newFilm, errors));

        assertEquals(Film.ERROR_FILM_DESCRIPTION_MAX_LENGTH, exception.getMessage());
        assertFalse(fc.getAllFilms().size() > 1);
    }

    @Test
    void createFilm_whenFilmReleaseDateBefore_returnError() {
        Film newFilm = Film.builder()
            .name("Test Film 2")
            .description("Test Film Description 2")
            .releaseDate(LocalDate.of(1111, 1, 1))
            .duration(11)
            .build();

        Errors errors = processErrors(newFilm);
        ValidationException exception = assertThrows(ValidationException.class, () -> fc.createFilm(newFilm, errors));

        assertEquals(FilmController.ERROR_MIN_FILM_RELEASE_DATE, exception.getMessage());
        assertFalse(fc.getAllFilms().size() > 1);
    }

    @Test
    void createFilm_whenFilmDurationZero_returnError() {
        Film newFilm = Film.builder()
            .name("Test Film 2")
            .description("Test Film Description 2")
            .releaseDate(LocalDate.of(2025, 1, 1))
            .duration(0)
            .build();

        Errors errors = processErrors(newFilm);
        ValidationException exception = assertThrows(ValidationException.class, () -> fc.createFilm(newFilm, errors));

        assertEquals(Film.ERROR_FILM_DURATION_MIN, exception.getMessage());
        assertFalse(fc.getAllFilms().size() > 1);
    }

    @Test
    void createFilm_whenFilmDurationNegative_returnError() {
        Film newFilm = Film.builder()
            .name("Test Film 2")
            .description("Test Film Description 2")
            .releaseDate(LocalDate.of(2025, 1, 1))
            .duration(-11)
            .build();

        Errors errors = processErrors(newFilm);
        ValidationException exception = assertThrows(ValidationException.class, () -> fc.createFilm(newFilm, errors));

        assertEquals(Film.ERROR_FILM_DURATION_MIN, exception.getMessage());
        assertFalse(fc.getAllFilms().size() > 1);
    }

    @Test
    void createFilm_whenFilmDurationNull_returnError() {
        Film newFilm = Film.builder()
            .name("Test Film 2")
            .description("Test Film Description 2")
            .releaseDate(LocalDate.of(2025, 1, 1))
            .duration(null)
            .build();

        Errors errors = processErrors(newFilm);
        ValidationException exception = assertThrows(ValidationException.class, () -> fc.createFilm(newFilm, errors));

        assertEquals(Film.ERROR_FILM_DURATION_MIN, exception.getMessage());
        assertFalse(fc.getAllFilms().size() > 1);
    }

    @Test
    void updateFilm_whenFilmValid_returnFilm() {
        Film newFilm = Film.builder()
            .id(1L)
            .name("Test Film 2")
            .description("Test Film Description 2")
            .releaseDate(LocalDate.of(2025, 1, 1))
            .duration(11)
            .build();

        Errors errors = new BeanPropertyBindingResult(newFilm, "film");
        Set<ConstraintViolation<Film>> violations = validator.validate(newFilm);
        Film film = fc.updateFilm(newFilm, errors);

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
        Film newFilm = Film.builder()
            .id(2L)
            .name("Test Film 2")
            .description("Test Film Description 2")
            .releaseDate(LocalDate.of(2025, 1, 1))
            .duration(11)
            .build();

        Errors errors = processErrors(newFilm);
        ValidationException exception = assertThrows(ValidationException.class, () -> fc.updateFilm(newFilm, errors));

        assertEquals(FilmController.ERROR_FILM_ID, exception.getMessage());
        assertFalse(fc.getAllFilms().size() > 1);
    }

    @Test
    void updateFilm_whenFilmNameEmpty_returnError() {
        Film newFilm = Film.builder()
            .id(1L)
            .name("")
            .description("Test Film Description 2")
            .releaseDate(LocalDate.of(2025, 1, 1))
            .duration(11)
            .build();

        Errors errors = processErrors(newFilm);
        ValidationException exception = assertThrows(ValidationException.class, () -> fc.updateFilm(newFilm, errors));

        assertEquals(Film.ERROR_FILM_NAME_EMPTY, exception.getMessage());
        assertFalse(fc.getAllFilms().size() > 1);
    }

    @Test
    void updateFilm_whenFilmDescriptionLong_returnError() {
        String description = "Test Film Description 2".repeat(11);
        Film newFilm = Film.builder()
            .id(1L)
            .name("Test Film 2")
            .description(description)
            .releaseDate(LocalDate.of(2025, 1, 1))
            .duration(11)
            .build();

        Errors errors = processErrors(newFilm);
        ValidationException exception = assertThrows(ValidationException.class, () -> fc.updateFilm(newFilm, errors));

        assertEquals(Film.ERROR_FILM_DESCRIPTION_MAX_LENGTH, exception.getMessage());
        assertFalse(fc.getAllFilms().size() > 1);
    }

    @Test
    void updateFilm_whenFilmReleaseDateBefore_returnError() {
        Film newFilm = Film.builder()
            .id(1L)
            .name("Test Film 2")
            .description("Test Film Description 2")
            .releaseDate(LocalDate.of(1111, 1, 1))
            .duration(11)
            .build();

        Errors errors = processErrors(newFilm);
        ValidationException exception = assertThrows(ValidationException.class, () -> fc.updateFilm(newFilm, errors));

        assertEquals(FilmController.ERROR_MIN_FILM_RELEASE_DATE, exception.getMessage());
        assertFalse(fc.getAllFilms().size() > 1);
    }

    @Test
    void updateFilm_whenFilmDurationNegative_returnError() {
        Film newFilm = Film.builder()
            .id(1L)
            .name("Test Film 2")
            .description("Test Film Description 2")
            .releaseDate(LocalDate.of(2025, 1, 1))
            .duration(-11)
            .build();

        Errors errors = processErrors(newFilm);
        ValidationException exception = assertThrows(ValidationException.class, () -> fc.updateFilm(newFilm, errors));

        assertEquals(Film.ERROR_FILM_DURATION_MIN, exception.getMessage());
        assertFalse(fc.getAllFilms().size() > 1);
    }
}