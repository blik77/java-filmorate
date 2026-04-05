package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;

@Component
public class DbGenreStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public DbGenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Genre> getAll() {
        return jdbcTemplate.query("SELECT * FROM genres", new GenreRowMapper());
    }

    @Override
    public Genre findById(Long id) {
        return jdbcTemplate.query("SELECT * FROM genres WHERE id=?",
                        new GenreRowMapper(), id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Жанр не найден"));
    }

    @Override
    public Collection<Genre> findAllByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();

        String inSql = String.join(",", Collections.nCopies(ids.size(), "?"));

        String sql = "SELECT * FROM genres WHERE id IN (" + inSql + ") ORDER BY id";

        return jdbcTemplate.query(sql, new GenreRowMapper(), ids.toArray());
    }
}