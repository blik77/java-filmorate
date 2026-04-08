package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.DbGenreStorage;
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
    private static final String MPA_MUST_HAVE = "MPA должен быть указан";

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
        List<Film> films = filmStorage.getPopularFilms(count);

        addGenreInfo(films);

        return films;
    }

    public List<Film> getAllFilms() {
        List<Film> films = filmStorage.getAllFilms();

        addGenreInfo(films);

        return films;
    }

    private void prepareFilm(Film film) {
        if (film.getMpa() != null) {
            film.setMpa(mpaStorage.findById(film.getMpa().getId()));
        } else {
            throw new IllegalArgumentException(MPA_MUST_HAVE);
        }

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {

            List<Long> ids = film.getGenres().stream()
                    .map(Genre::getId)
                    .toList();

            Collection<Genre> genresFromDb;

            try {
                genresFromDb = genreStorage.findAllByIds(ids);
            } catch (Exception e) {
                throw new NoSuchElementException(DbGenreStorage.GENRE_NOT_FOUND);
            }

            if (genresFromDb.size() != ids.size()) {
                throw new NoSuchElementException(DbGenreStorage.GENRE_NOT_FOUND);
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

    private void addGenreInfo(List<Film> films) {
        List<Long> filmIds = films.stream().map(Film::getId).toList();

        Map<Long, Set<Genre>> filmGenresMap = genreStorage.loadGenresForFilms(filmIds);

        for (Film film : films) {
            film.setGenres(filmGenresMap.getOrDefault(film.getId(), new LinkedHashSet<>()));
        }
    }
}