package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    Film createFilm(Film film);

    Film getFilmById(Long id);

    Film updateFilm(Film film);

    Collection<Film> getAllFilms();

    void addFilmLike(Long filmId, Long userId);

    void removeFilmLike(Long filmId, Long userId);

    List<Film> getPopularFilms(int count);
}
