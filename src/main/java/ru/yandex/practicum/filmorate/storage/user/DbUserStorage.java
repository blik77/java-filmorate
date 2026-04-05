package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.List;

@Component
public class DbUserStorage implements UserStorage {
    public static final String USER_NOT_FOUND = "Пользователь не найден";

    private final JdbcTemplate jdbcTemplate;

    public DbUserStorage(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    @Override
    public User createUser(User user) {
        String sql = "INSERT INTO users(email, login, name, birthday) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    @Override
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET email=?, login=?, name=?, birthday=? WHERE id=?";

        int updated = jdbcTemplate.update(sql,
            user.getEmail(),
            user.getLogin(),
            user.getName(),
            user.getBirthday(),
            user.getId()
        );

        return updated > 0;
    }

    @Override
    public User getUserById(Long id) {
        String sql = "SELECT * FROM users WHERE id=?";

        return jdbcTemplate.query(sql, new UserRowMapper(), id)
            .stream()
            .findFirst()
            .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }

    @Override
    public void addUserFriend(Long userId, Long friendId) {
        String sql = "MERGE INTO friendships (user_id, friend_id, status) KEY(user_id, friend_id) VALUES (?, ?, 'CONFIRMED')";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void removeUserFriend(Long userId, Long friendId) {
        String sql = "DELETE FROM friendships WHERE user_id=? AND friend_id=?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public List<User> getUserFriends(Long userId) {
        String sql = "SELECT u.* FROM users u JOIN friendships f ON u.id = f.friend_id WHERE f.user_id = ?";
        return jdbcTemplate.query(sql, new UserRowMapper(), userId);
    }

    @Override
    public List<User> getCommonUserFriends(Long userId, Long otherId) {
        String sql = "SELECT u.* FROM users u WHERE u.id IN (SELECT f1.friend_id FROM friendships f1 JOIN friendships f2 ON f1.friend_id = f2.friend_id WHERE f1.user_id = ? AND f2.user_id = ?)";

        return jdbcTemplate.query(sql, new UserRowMapper(), userId, otherId);
    }
}