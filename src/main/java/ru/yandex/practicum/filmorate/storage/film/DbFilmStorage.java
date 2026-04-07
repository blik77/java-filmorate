package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Component
public class DbFilmStorage implements FilmStorage {
    public static final String FILM_NOT_FOUND = "Фильм не найден";

    public static final String QUERY_GET_ALL_FILMS =
        "SELECT f.*, m.name AS mpa_name FROM films f JOIN mpa_ratings m ON f.mpa_id = m.id";
    public static final String QUERY_GET_FILM_BY_ID =
        "SELECT f.*, m.name AS mpa_name FROM films f JOIN mpa_ratings m ON f.mpa_id = m.id WHERE f.id = ?";
    public static final String QUERY_CREATE_FILM =
        "INSERT INTO films(name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
    public static final String QUERY_GET_MAX_FILM_ID = "SELECT MAX(id) FROM films";
    public static final String QUERY_UPDATE_FILM =
        "UPDATE films SET name=?, description=?, release_date=?, duration=?, mpa_id=? WHERE id=?";
    public static final String QUERY_DELETE_GENRE_FOR_FILM = "DELETE FROM film_genres WHERE film_id=?";
    public static final String QUERY_ADD_FILM_LIKE = "INSERT INTO likes(film_id, user_id) VALUES (?, ?)";
    public static final String QUERY_REMOVE_FILM_LIKE = "DELETE FROM likes WHERE film_id=? AND user_id=?";
    public static final String QUERY_GET_POPULAR_FILMS = """
        SELECT f.*, m.name AS mpa_name, COUNT(l.user_id) AS likes_count
        FROM films f JOIN mpa_ratings m ON f.mpa_id = m.id
        LEFT JOIN likes l ON f.id = l.film_id
        GROUP BY f.id, m.name ORDER BY likes_count DESC LIMIT ?
    """;
    public static final String QUERY_LOAD_GENRES =
        "SELECT g.id, g.name FROM genres g JOIN film_genres fg ON g.id = fg.genre_id WHERE fg.film_id = ? ORDER BY g.id";
    public static final String QUERY_SAVE_GENRES =
        "MERGE INTO film_genres (film_id, genre_id) KEY(film_id, genre_id) VALUES (?, ?)";

    private final JdbcTemplate jdbcTemplate;

    public DbFilmStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getAllFilms() {
        return jdbcTemplate.query(QUERY_GET_ALL_FILMS, new FilmRowMapper());
    }

    @Override
    public Film getFilmById(Long id) {
        Film film = jdbcTemplate.query(QUERY_GET_FILM_BY_ID, new FilmRowMapper(), id)
            .stream()
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException(DbFilmStorage.FILM_NOT_FOUND));

        loadGenres(film);

        return film;
    }

    @Override
    public Film createFilm(Film film) {
        jdbcTemplate.update(QUERY_CREATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );

        Long id = jdbcTemplate.queryForObject(QUERY_GET_MAX_FILM_ID, Long.class);
        film.setId(id);

        saveGenres(film);

        return getFilmById(id);
    }

    @Override
    public Film updateFilm(Film film) {
        int updated = jdbcTemplate.update(QUERY_UPDATE_FILM,
            film.getName(),
            film.getDescription(),
            film.getReleaseDate(),
            film.getDuration(),
            film.getMpa().getId(),
            film.getId()
        );

        if (updated == 0) {
            throw new NotFoundException(FILM_NOT_FOUND);
        }

        jdbcTemplate.update(QUERY_DELETE_GENRE_FOR_FILM, film.getId());
        saveGenres(film);

        return getFilmById(film.getId());
    }

    @Override
    public void addFilmLike(Long filmId, Long userId) {
        jdbcTemplate.update(QUERY_ADD_FILM_LIKE, filmId, userId);
    }

    @Override
    public void removeFilmLike(Long filmId, Long userId) {
        jdbcTemplate.update(QUERY_REMOVE_FILM_LIKE, filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return jdbcTemplate.query(QUERY_GET_POPULAR_FILMS, new FilmRowMapper(), count);
    }

    private void loadGenres(Film film) {
        List<Genre> genres = jdbcTemplate.query(QUERY_LOAD_GENRES, new GenreRowMapper(), film.getId());
        film.setGenres(new LinkedHashSet<>(genres));
    }

    private void saveGenres(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) return;

        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(QUERY_SAVE_GENRES, film.getId(), genre.getId());
        }
    }
}