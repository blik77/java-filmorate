package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    private static final LocalDate FIRST_FILM_DATE = LocalDate.of(1895, 12, 28);

    public FilmService(FilmStorage filmStorage,
                       UserStorage userStorage,
                       GenreStorage genreStorage,
                       MpaStorage mpaStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    public void addFilmLike(Long filmId, Long userId) {
        filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);

        filmStorage.addFilmLike(filmId, userId);
    }

    public void removeFilmLike(Long filmId, Long userId) {
        filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);

        filmStorage.removeFilmLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }


    private void prepareFilm(Film film) {
        if (film.getMpa() != null) {
            film.setMpa(mpaStorage.findById(film.getMpa().getId()));
        } else {
            throw new IllegalArgumentException("MPA должен быть указан");
        }

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {

            List<Long> ids = film.getGenres().stream()
                    .map(Genre::getId)
                    .toList();

            Collection<Genre> genresFromDb;

            try {
                genresFromDb = genreStorage.findAllByIds(ids);
            } catch (Exception e) {
                throw new NoSuchElementException("Жанр не найден");
            }

            if (genresFromDb.size() != ids.size()) {
                throw new NoSuchElementException("Жанр не найден");
            }

            film.setGenres(new LinkedHashSet<>(genresFromDb));
        } else {
            film.setGenres(new LinkedHashSet<>());
        }

        validateReleaseDate(film.getReleaseDate());
    }

    public Film createFilm(Film film) {
        prepareFilm(film);
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        prepareFilm(film);
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(final Long id) {
        return filmStorage.getFilmById(id);
    }

    private void validateReleaseDate(LocalDate releaseDate) {
        if (releaseDate.isBefore(FIRST_FILM_DATE)) {
            throw new IllegalArgumentException("Дата релиза не может быть раньше 28.12.1895");
        }
    }
}