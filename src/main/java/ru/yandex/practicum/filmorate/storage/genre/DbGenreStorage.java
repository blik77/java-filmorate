package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Component
public class DbGenreStorage implements GenreStorage {
    public static final String GENRE_NOT_FOUND = "Жанр не найден";

    public static final String QUERY_GET_ALL_GENRES = "SELECT * FROM genres";
    public static final String QUERY_GET_GENRE_BY_ID = "SELECT * FROM genres WHERE id=?";

    public static final String QUERY_GET_GENRES_FOR_FILMS = """
        SELECT fg.film_id, g.id, g.name FROM film_genres fg
        JOIN genres g ON fg.genre_id = g.id WHERE fg.film_id IN (%s) ORDER BY g.id
    """;

    private final JdbcTemplate jdbcTemplate;

    public DbGenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Genre> getAll() {
        return jdbcTemplate.query(QUERY_GET_ALL_GENRES, new GenreRowMapper());
    }

    @Override
    public Genre findById(Long id) {
        return jdbcTemplate.query(QUERY_GET_GENRE_BY_ID, new GenreRowMapper(), id)
            .stream()
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException(GENRE_NOT_FOUND));
    }

    @Override
    public Collection<Genre> findAllByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();

        String inSql = String.join(",", Collections.nCopies(ids.size(), "?"));

        String sql = "SELECT * FROM genres WHERE id IN (" + inSql + ") ORDER BY id";

        return jdbcTemplate.query(sql, new GenreRowMapper(), ids.toArray());
    }

    @Override
    public void loadGenresForFilms(List<Film> films) {
        if (films.isEmpty()) return;

        List<Long> filmIds = films.stream().map(Film::getId).toList();

        String inSql = String.join(",", Collections.nCopies(filmIds.size(), "?"));

        String sql = QUERY_GET_GENRES_FOR_FILMS.formatted(inSql);

        Map<Long, Set<Genre>> filmGenresMap = new HashMap<>();

        jdbcTemplate.query(sql, rs -> {
            Long filmId = rs.getLong("film_id");

            Genre genre = new Genre(rs.getLong("id"), rs.getString("name"));

            filmGenresMap.computeIfAbsent(filmId, k -> new LinkedHashSet<>()).add(genre);
        }, filmIds.toArray());

        for (Film film : films) {
            film.setGenres(filmGenresMap.getOrDefault(film.getId(), new LinkedHashSet<>()));
        }
    }
}