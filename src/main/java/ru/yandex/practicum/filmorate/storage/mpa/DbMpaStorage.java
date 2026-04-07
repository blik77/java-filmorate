package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;
import java.util.NoSuchElementException;

@Component
public class DbMpaStorage implements MpaStorage {
    public static final String MPA_NOT_FOUND = "MPA не найден";

    public static final String QUERY_GET_ALL_MPA = "SELECT * FROM mpa_ratings";
    public static final String QUERY_GET_MPA_BY_ID = "SELECT * FROM mpa_ratings WHERE id=?";

    private final JdbcTemplate jdbcTemplate;

    public DbMpaStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<MpaRating> getAll() {
        return jdbcTemplate.query(QUERY_GET_ALL_MPA, new MpaRowMapper());
    }

    @Override
    public MpaRating findById(Long id) {
        return jdbcTemplate.query(QUERY_GET_MPA_BY_ID, new MpaRowMapper(), id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(MPA_NOT_FOUND));
    }
}