package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("createUser: {}", user);
        return userService.createUser(user);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        log.info("getUserById: {}", id);
        return userService.getUserById(id);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("updateUser: {}", user);
        return userService.updateUser(user);
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("getAllUsers: {}", userService.getAllUsers().size());
        return userService.getAllUsers();
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addUserFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("addUserFriend: {}", id + " для " + friendId);
        userService.addUserFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeUserFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("removeUserFriend: {}", id + " для " + friendId);
        userService.removeUserFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getUserFriends(@PathVariable Long id) {
        log.info("getUserFriends: {}", "для " + id);
        return userService.getUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonUserFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("getCommonUserFriends: {}", id + " для " + otherId);
        return userService.getCommonUserFriends(id, otherId);
    }
}
