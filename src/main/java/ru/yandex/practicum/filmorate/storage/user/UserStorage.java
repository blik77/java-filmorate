package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {

    Collection<User> getAllUsers();

    User createUser(User user);

    boolean updateUser(User user);

    User getUserById(Long id);

    void addUserFriend(Long userId, Long friendId);

    void removeUserFriend(Long userId, Long friendId);

    List<User> getUserFriends(Long userId);

    List<User> getCommonUserFriends(Long userId, Long otherId);
}
