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

    private final JdbcTemplate jdbcTemplate;

    public DbFilmStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Film> getAllFilms() {
        String sql = """
            SELECT f.*, m.name AS mpa_name
            FROM films f
            JOIN mpa_ratings m ON f.mpa_id = m.id
        """;

        List<Film> films = jdbcTemplate.query(sql, new FilmRowMapper());
        loadGenresForFilms(films);
        return films;
    }

    @Override
    public Film getFilmById(Long id) {
        String sql = """
            SELECT f.*, m.name AS mpa_name
            FROM films f
            JOIN mpa_ratings m ON f.mpa_id = m.id
            WHERE f.id = ?
        """;

        Film film = jdbcTemplate.query(sql, new FilmRowMapper(), id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Фильм не найден"));

        loadGenres(film);

        return film;
    }

    @Override
    public Film createFilm(Film film) {
        String sql = """
            INSERT INTO films(name, description, release_date, duration, mpa_id)
            VALUES (?, ?, ?, ?, ?)
        """;

        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );

        Long id = jdbcTemplate.queryForObject("SELECT MAX(id) FROM films", Long.class);
        film.setId(id);

        saveGenres(film);

        return getFilmById(id);
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = """
            UPDATE films
            SET name=?, description=?, release_date=?, duration=?, mpa_id=?
            WHERE id=?
        """;

        int updated = jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        if (updated == 0) {
            throw new NotFoundException("Фильм не найден");
        }

        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id=?", film.getId());
        saveGenres(film);

        return getFilmById(film.getId());
    }

    @Override
    public void addFilmLike(Long filmId, Long userId) {
        String sql = "INSERT INTO likes(film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeFilmLike(Long filmId, Long userId) {
        String sql = "DELETE FROM likes WHERE film_id=? AND user_id=?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = """
            SELECT f.*, m.name AS mpa_name, COUNT(l.user_id) AS likes_count
            FROM films f
            JOIN mpa_ratings m ON f.mpa_id = m.id
            LEFT JOIN likes l ON f.id = l.film_id
            GROUP BY f.id, m.name
            ORDER BY likes_count DESC
            LIMIT ?
        """;

        List<Film> films = jdbcTemplate.query(sql, new FilmRowMapper(), count);

        loadGenresForFilms(films);

        return films;
    }

    private void loadGenresForFilms(List<Film> films) {
        if (films.isEmpty()) return;

        List<Long> filmIds = films.stream()
                .map(Film::getId)
                .toList();

        String inSql = String.join(",", Collections.nCopies(filmIds.size(), "?"));

        String sql = """
                    SELECT fg.film_id, g.id, g.name
                    FROM film_genres fg
                    JOIN genres g ON fg.genre_id = g.id
                    WHERE fg.film_id IN (%s)
                    ORDER BY g.id
                """.formatted(inSql);

        Map<Long, Set<Genre>> filmGenresMap = new HashMap<>();

        jdbcTemplate.query(sql, rs -> {
            Long filmId = rs.getLong("film_id");

            Genre genre = new Genre(
                    rs.getLong("id"),
                    rs.getString("name")
            );

            filmGenresMap
                    .computeIfAbsent(filmId, k -> new LinkedHashSet<>())
                    .add(genre);
        }, filmIds.toArray());

        for (Film film : films) {
            film.setGenres(filmGenresMap.getOrDefault(film.getId(), new LinkedHashSet<>()));
        }
    }

    private void loadGenres(Film film) {
        String sql = """
            SELECT g.id, g.name
            FROM genres g
            JOIN film_genres fg ON g.id = fg.genre_id
            WHERE fg.film_id = ?
            ORDER BY g.id
        """;

        List<Genre> genres = jdbcTemplate.query(sql, new GenreRowMapper(), film.getId());
        film.setGenres(new LinkedHashSet<>(genres));
    }

    private void saveGenres(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) return;

        String sql = "MERGE INTO film_genres (film_id, genre_id) KEY(film_id, genre_id) VALUES (?, ?)";
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(sql, film.getId(), genre.getId());
        }
    }
}