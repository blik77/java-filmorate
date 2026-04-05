package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    Collection<Film> getAllFilms();

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Film getFilmById(Long id);

    void addFilmLike(Long filmId, Long userId);

    void removeFilmLike(Long filmId, Long userId);

    List<Film> getPopularFilms(int count);
}