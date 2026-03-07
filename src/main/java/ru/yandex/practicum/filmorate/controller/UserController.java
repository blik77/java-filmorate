package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static ru.yandex.practicum.filmorate.util.ModelUtil.checkError;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    public static final String ERROR_USER_ID = "Фильм не найден";

    private final Map<Long, User> users = new HashMap<>();

    private long newUserId = 1;

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("getAllUsers: {}", users.size());
        return users.values();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user, Errors errors) {
        checkData(user, errors);

        user.setId(newUserId++);

        users.put(user.getId(), user);
        log.info("createUser: {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user, Errors errors) {
        if (user.getId() == null || !users.containsKey(user.getId())) {
            log.error(ERROR_USER_ID);
            throw new ValidationException(ERROR_USER_ID);
        }

        checkData(user, errors);

        users.put(user.getId(), user);
        log.info("updateUser: {}", user);
        return user;
    }

    private void checkData(User user, Errors errors) {
        checkError(errors);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
