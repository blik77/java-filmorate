package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addUserFriend(final Long userId, final Long friendId) {
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        userStorage.addUserFriend(userId, friendId);
    }

    public void removeUserFriend(Long userId, Long friendId) {
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);

        userStorage.removeUserFriend(userId, friendId);
    }

    public List<User> getUserFriends(Long userId) {
        userStorage.getUserById(userId);
        return userStorage.getUserFriends(userId);
    }

    public List<User> getCommonUserFriends(Long userId, Long otherId) {
        userStorage.getUserById(userId);
        userStorage.getUserById(otherId);

        return userStorage.getCommonUserFriends(userId, otherId);
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User createUser(final User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return userStorage.createUser(user);
    }

    public User updateUser(final User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        boolean updated = userStorage.updateUser(user);
        if (!updated) {
            throw new NoSuchElementException("Пользователь не найден");
        }

        return user;
    }

    public User getUserById(final Long id) {
        return userStorage.getUserById(id);
    }

}