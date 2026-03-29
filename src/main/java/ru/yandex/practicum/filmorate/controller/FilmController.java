package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("createFilm: {}", film);
        return filmService.createFilm(film);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        log.info("getFilmById: {}", id);
        return filmService.getFilmById(id);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("updateFilm: {}", film);
        return filmService.updateFilm(film);
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("getAllFilms: {}", filmService.getAllFilms().size());
        return filmService.getAllFilms();
    }

    @PutMapping("/{id}/like/{userId}")
    public void addFilmLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("addFilmLike: {}", id + " для " + userId);
        filmService.addFilmLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeFilmLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("removeFilmLike: {}", id + " для " + userId);
        filmService.removeFilmLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("getPopularFilms: {}", count);
        return filmService.getPopularFilms(count);
    }
}
