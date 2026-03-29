package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Set;

public interface UserStorage {
    User createUser(User user);

    User getUserById(Long id);

    User updateUser(User user);

    Collection<User> getAllUsers();

    void addUserFriend(Long id, Long friendId);

    void removeUserFriend(Long id, Long friendId);

    Set<User> getUserFriends(Long id);

    Set<User> getCommonUserFriends(Long id, Long otherId);
}
