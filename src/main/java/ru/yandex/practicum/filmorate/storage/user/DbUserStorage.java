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

    public static final String QUERY_GET_ALL_USERS = "SELECT * FROM users";
    public static final String QUERY_CREATE_USERS =
        "INSERT INTO users(email, login, name, birthday) VALUES (?, ?, ?, ?)";
    public static final String QUERY_UPDATE_USERS =
        "UPDATE users SET email=?, login=?, name=?, birthday=? WHERE id=?";
    public static final String QUERY_GET_USER_BY_ID = "SELECT * FROM users WHERE id=?";
    public static final String QUERY_ADD_USER_FRIEND =
        "MERGE INTO friendships (user_id, friend_id, status) KEY(user_id, friend_id) VALUES (?, ?, 'CONFIRMED')";
    public static final String QUERY_REMOVE_USER_FRIEND = "DELETE FROM friendships WHERE user_id=? AND friend_id=?";
    public static final String QUERY_GET_USER_FRIENDS =
        "SELECT u.* FROM users u JOIN friendships f ON u.id = f.friend_id WHERE f.user_id = ?";
    public static final String QUERY_GET_COMMON_USER_FRIENDS =
        "SELECT u.* FROM users u WHERE u.id IN (SELECT f1.friend_id FROM friendships f1 JOIN friendships f2 ON f1.friend_id = f2.friend_id WHERE f1.user_id = ? AND f2.user_id = ?)";

    private final JdbcTemplate jdbcTemplate;

    public DbUserStorage(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> getAllUsers() {
        return jdbcTemplate.query(QUERY_GET_ALL_USERS, new UserRowMapper());
    }

    @Override
    public User createUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(QUERY_CREATE_USERS, new String[]{"id"});
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
        int updated = jdbcTemplate.update(QUERY_UPDATE_USERS,
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
        return jdbcTemplate.query(QUERY_GET_USER_BY_ID, new UserRowMapper(), id)
            .stream()
            .findFirst()
            .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }

    @Override
    public void addUserFriend(Long userId, Long friendId) {
        jdbcTemplate.update(QUERY_ADD_USER_FRIEND, userId, friendId);
    }

    @Override
    public void removeUserFriend(Long userId, Long friendId) {
        jdbcTemplate.update(QUERY_REMOVE_USER_FRIEND, userId, friendId);
    }

    @Override
    public List<User> getUserFriends(Long userId) {
        return jdbcTemplate.query(QUERY_GET_USER_FRIENDS, new UserRowMapper(), userId);
    }

    @Override
    public List<User> getCommonUserFriends(Long userId, Long otherId) {
        return jdbcTemplate.query(QUERY_GET_COMMON_USER_FRIENDS, new UserRowMapper(), userId, otherId);
    }
}