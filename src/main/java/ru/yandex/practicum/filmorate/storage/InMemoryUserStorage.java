package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    public static final String ERROR_USER_ID = "Пользователь не найден";

    private final Map<Long, User> users = new HashMap<>();
    private Long newUserId = 1L;

    @Override
    public User createUser(User user) {
        user.setId(newUserId++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUserById(Long id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException(ERROR_USER_ID);
        }
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException(ERROR_USER_ID);
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public void addUserFriend(Long id, Long friendId) {
        User user = getUserById(id);
        User friend = getUserById(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(id);
    }

    @Override
    public void removeUserFriend(Long id, Long friendId) {
        User user = getUserById(id);
        User friend = getUserById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
    }

    @Override
    public Set<User> getUserFriends(Long id) {
        User user = getUserById(id);

        Set<User> result = new HashSet<>();
        for (Long friendId : user.getFriends()) {
            result.add(getUserById(friendId));
        }
        return result;
    }

    @Override
    public Set<User> getCommonUserFriends(Long id, Long otherId) {
        User user = getUserById(id);
        User other = getUserById(otherId);

        Set<User> result = new HashSet<>();

        for (Long friendId : user.getFriends()) {
            if (other.getFriends().contains(friendId)) {
                result.add(getUserById(friendId));
            }
        }

        return result;
    }
}
