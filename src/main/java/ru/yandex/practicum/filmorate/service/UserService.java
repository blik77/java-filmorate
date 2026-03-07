package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void addUserFriend(Long id, Long friendId) {
        userStorage.addUserFriend(id, friendId);
    }

    public void removeUserFriend(Long id, Long friendId) {
        userStorage.removeUserFriend(id, friendId);
    }

    public Set<User> getUserFriends(Long id) {
        return userStorage.getUserFriends(id);
    }

    public Set<User> getCommonUserFriends(Long id, Long otherId) {
        return userStorage.getCommonUserFriends(id, otherId);
    }
}
