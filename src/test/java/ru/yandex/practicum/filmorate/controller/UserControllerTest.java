package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.DbUserStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    private UserController uc;
    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }

        DbUserStorage userStorage = new DbUserStorage();

        UserService userService = new UserService(userStorage);

        uc = new UserController(userService);

        User user = new User();
        user.setId(1L);
        user.setEmail("test@yandex.ru");
        user.setLogin("testLogin");
        user.setName("testName");
        user.setBirthday(LocalDate.of(2011, 11, 11));
        uc.createUser(user);
    }

    @Test
    void createUser_whenUserValid_returnUser() {
        User newUser = new User();
        newUser.setEmail("test2@yandex.ru");
        newUser.setLogin("testLogin2");
        newUser.setName("testName2");
        newUser.setBirthday(LocalDate.of(2011, 11, 11));

        Set<ConstraintViolation<User>> violations = validator.validate(newUser);
        User user = uc.createUser(newUser);

        assertTrue(violations.isEmpty());
        assertNotNull(user);
        assertEquals(2, user.getId());
        assertEquals(user.getEmail(), newUser.getEmail());
        assertEquals(user.getLogin(), newUser.getLogin());
        assertEquals(user.getBirthday(), newUser.getBirthday());
        assertTrue(uc.getAllUsers().contains(user));
    }

    @Test
    void createUser_whenUserEmailEmpty_returnError() {
        User newUser = new User();
        newUser.setEmail("");
        newUser.setLogin("testLogin2");
        newUser.setName("testName2");
        newUser.setBirthday(LocalDate.of(2011, 11, 11));

        Set<ConstraintViolation<User>> violations = validator.validate(newUser);
        String message = violations.iterator().next().getMessage();

        assertEquals(User.ERROR_USER_EMAIL, message);
        assertFalse(uc.getAllUsers().size() > 1);
    }

    @Test
    void createUser_whenUserEmailWrong_returnError() {
        User newUser = new User();
        newUser.setEmail("test2yandex.ru");
        newUser.setLogin("testLogin2");
        newUser.setName("testName2");
        newUser.setBirthday(LocalDate.of(2011, 11, 11));

        Set<ConstraintViolation<User>> violations = validator.validate(newUser);
        String message = violations.iterator().next().getMessage();

        assertEquals(User.ERROR_USER_EMAIL, message);
        assertFalse(uc.getAllUsers().size() > 1);
    }

    @Test
    void createUser_whenUserEmailNull_returnError() {
        User newUser = new User();
        newUser.setEmail(null);
        newUser.setLogin("testLogin2");
        newUser.setName("testName2");
        newUser.setBirthday(LocalDate.of(2011, 11, 11));

        Set<ConstraintViolation<User>> violations = validator.validate(newUser);
        String message = violations.iterator().next().getMessage();

        assertEquals(User.ERROR_USER_EMAIL, message);
        assertFalse(uc.getAllUsers().size() > 1);
    }

    @Test
    void createUser_whenUserLoginEmpty_returnError() {
        User newUser = new User();
        newUser.setEmail("test2@yandex.ru");
        newUser.setLogin("");
        newUser.setName("testName2");
        newUser.setBirthday(LocalDate.of(2011, 11, 11));

        Set<ConstraintViolation<User>> violations = validator.validate(newUser);
        String message = violations.iterator().next().getMessage();

        assertEquals(User.ERROR_USER_LOGIN, message);
        assertFalse(uc.getAllUsers().size() > 1);
    }

    @Test
    void createUser_whenUserLoginNull_returnError() {
        User newUser = new User();
        newUser.setEmail("test2@yandex.ru");
        newUser.setLogin(null);
        newUser.setName("testName2");
        newUser.setBirthday(LocalDate.of(2011, 11, 11));

        Set<ConstraintViolation<User>> violations = validator.validate(newUser);
        String message = violations.iterator().next().getMessage();

        assertEquals(User.ERROR_USER_LOGIN, message);
        assertFalse(uc.getAllUsers().size() > 1);
    }

    @Test
    void createUser_whenUserLoginSpace_returnError() {
        User newUser = new User();
        newUser.setEmail("test2@yandex.ru");
        newUser.setLogin("test Login2");
        newUser.setName("testName2");
        newUser.setBirthday(LocalDate.of(2011, 11, 11));

        Set<ConstraintViolation<User>> violations = validator.validate(newUser);
        String message = violations.iterator().next().getMessage();

        assertEquals(User.ERROR_USER_LOGIN, message);
        assertFalse(uc.getAllUsers().size() > 1);
    }

    @Test
    void createUser_whenUserNameEmpty_returnUser() {
        User newUser = new User();
        newUser.setEmail("test2@yandex.ru");
        newUser.setLogin("testLogin2");
        newUser.setName("");
        newUser.setBirthday(LocalDate.of(2011, 11, 11));

        Set<ConstraintViolation<User>> violations = validator.validate(newUser);
        User user = uc.createUser(newUser);

        assertTrue(violations.isEmpty());
        assertNotNull(user);
        assertEquals(2, user.getId());
        assertEquals(user.getEmail(), newUser.getEmail());
        assertEquals(user.getLogin(), newUser.getLogin());
        assertEquals(user.getName(), newUser.getLogin());
        assertEquals(user.getBirthday(), newUser.getBirthday());
        assertTrue(uc.getAllUsers().contains(user));
    }

    @Test
    void createUser_whenUserNameNull_returnUser() {
        User newUser = new User();
        newUser.setEmail("test2@yandex.ru");
        newUser.setLogin("testLogin2");
        newUser.setName(null);
        newUser.setBirthday(LocalDate.of(2011, 11, 11));

        Set<ConstraintViolation<User>> violations = validator.validate(newUser);
        User user = uc.createUser(newUser);

        assertTrue(violations.isEmpty());
        assertNotNull(user);
        assertEquals(2, user.getId());
        assertEquals(user.getEmail(), newUser.getEmail());
        assertEquals(user.getLogin(), newUser.getLogin());
        assertEquals(user.getName(), newUser.getLogin());
        assertEquals(user.getBirthday(), newUser.getBirthday());
        assertTrue(uc.getAllUsers().contains(user));
    }

    @Test
    void createUser_whenUserBirthdayNotPast_returnError() {
        User newUser = new User();
        newUser.setEmail("test2@yandex.ru");
        newUser.setLogin("testLogin2");
        newUser.setName("testName2");
        newUser.setBirthday(LocalDate.now());

        Set<ConstraintViolation<User>> violations = validator.validate(newUser);
        String message = violations.iterator().next().getMessage();

        assertEquals(User.ERROR_USER_BIRTHDAY, message);
        assertFalse(uc.getAllUsers().size() > 1);
    }

    @Test
    void updateUser_whenUserValid_returnUser() {
        User newUser = new User();
        newUser.setId(1L);
        newUser.setEmail("test2@yandex.ru");
        newUser.setLogin("testLogin2");
        newUser.setName("testName2");
        newUser.setBirthday(LocalDate.of(2011, 11, 11));

        Set<ConstraintViolation<User>> violations = validator.validate(newUser);
        User user = uc.updateUser(newUser);

        assertTrue(violations.isEmpty());
        assertNotNull(user);
        assertTrue(uc.getAllUsers().contains(user));
        assertFalse(uc.getAllUsers().size() > 1);

        assertEquals(1, user.getId());
        assertEquals(user.getEmail(), newUser.getEmail());
        assertEquals(user.getLogin(), newUser.getLogin());
        assertEquals(user.getName(), newUser.getName());
        assertEquals(user.getBirthday(), newUser.getBirthday());
    }

    @Test
    void updateUser_whenUserIdWrong_returnError() {
        User newUser = new User();
        newUser.setId(2L);
        newUser.setEmail("test2@yandex.ru");
        newUser.setLogin("testLogin2");
        newUser.setName("testName2");
        newUser.setBirthday(LocalDate.of(2011, 11, 11));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> uc.updateUser(newUser));

        assertEquals(InMemoryUserStorage.ERROR_USER_ID, exception.getMessage());
        assertFalse(uc.getAllUsers().size() > 1);
    }

    @Test
    void updateUser_whenUserEmailEmpty_returnError() {
        User newUser = new User();
        newUser.setId(1L);
        newUser.setEmail("");
        newUser.setLogin("testLogin2");
        newUser.setName("testName2");
        newUser.setBirthday(LocalDate.of(2011, 11, 11));

        Set<ConstraintViolation<User>> violations = validator.validate(newUser);
        String message = violations.iterator().next().getMessage();

        assertEquals(User.ERROR_USER_EMAIL, message);
        assertFalse(uc.getAllUsers().size() > 1);
    }

    @Test
    void updateUser_whenUserEmailWrong_returnError() {
        User newUser = new User();
        newUser.setId(1L);
        newUser.setEmail("test2yandex.ru");
        newUser.setLogin("testLogin2");
        newUser.setName("testName2");
        newUser.setBirthday(LocalDate.of(2011, 11, 11));

        Set<ConstraintViolation<User>> violations = validator.validate(newUser);
        String message = violations.iterator().next().getMessage();

        assertEquals(User.ERROR_USER_EMAIL, message);
        assertFalse(uc.getAllUsers().size() > 1);
    }

    @Test
    void updateUser_whenUserEmailNull_returnError() {
        User newUser = new User();
        newUser.setId(1L);
        newUser.setEmail(null);
        newUser.setLogin("testLogin2");
        newUser.setName("testName2");
        newUser.setBirthday(LocalDate.of(2011, 11, 11));

        Set<ConstraintViolation<User>> violations = validator.validate(newUser);
        String message = violations.iterator().next().getMessage();

        assertEquals(User.ERROR_USER_EMAIL, message);
        assertFalse(uc.getAllUsers().size() > 1);
    }

    @Test
    void updateUser_whenUserLoginEmpty_returnError() {
        User newUser = new User();
        newUser.setId(1L);
        newUser.setEmail("test2@yandex.ru");
        newUser.setLogin("");
        newUser.setName("testName2");
        newUser.setBirthday(LocalDate.of(2011, 11, 11));

        Set<ConstraintViolation<User>> violations = validator.validate(newUser);
        String message = violations.iterator().next().getMessage();

        assertEquals(User.ERROR_USER_LOGIN, message);
        assertFalse(uc.getAllUsers().size() > 1);
    }

    @Test
    void updateUser_whenUserLoginNull_returnError() {
        User newUser = new User();
        newUser.setId(1L);
        newUser.setEmail("test2@yandex.ru");
        newUser.setLogin(null);
        newUser.setName("testName2");
        newUser.setBirthday(LocalDate.of(2011, 11, 11));

        Set<ConstraintViolation<User>> violations = validator.validate(newUser);
        String message = violations.iterator().next().getMessage();

        assertEquals(User.ERROR_USER_LOGIN, message);
        assertFalse(uc.getAllUsers().size() > 1);
    }

    @Test
    void updateUser_whenUserLoginSpace_returnError() {
        User newUser = new User();
        newUser.setId(1L);
        newUser.setEmail("test2@yandex.ru");
        newUser.setLogin("test Login2");
        newUser.setName("testName2");
        newUser.setBirthday(LocalDate.of(2011, 11, 11));

        Set<ConstraintViolation<User>> violations = validator.validate(newUser);
        String message = violations.iterator().next().getMessage();

        assertEquals(User.ERROR_USER_LOGIN, message);
        assertFalse(uc.getAllUsers().size() > 1);
    }

    @Test
    void updateUser_whenUserNameEmpty_returnUser() {
        User newUser = new User();
        newUser.setId(1L);
        newUser.setEmail("test2@yandex.ru");
        newUser.setLogin("testLogin2");
        newUser.setName("");
        newUser.setBirthday(LocalDate.of(2011, 11, 11));

        Set<ConstraintViolation<User>> violations = validator.validate(newUser);
        User user = uc.updateUser(newUser);

        assertNotNull(user);
        assertTrue(violations.isEmpty());
        assertFalse(uc.getAllUsers().size() > 1);
        assertTrue(uc.getAllUsers().contains(user));

        assertEquals(1, user.getId());
        assertEquals(user.getEmail(), newUser.getEmail());
        assertEquals(user.getLogin(), newUser.getLogin());
        assertEquals(user.getName(), newUser.getLogin());
        assertEquals(user.getBirthday(), newUser.getBirthday());
    }

    @Test
    void updateUser_whenUserNameNull_returnUser() {
        User newUser = new User();
        newUser.setId(1L);
        newUser.setEmail("test2@yandex.ru");
        newUser.setLogin("testLogin2");
        newUser.setName(null);
        newUser.setBirthday(LocalDate.of(2011, 11, 11));

        Set<ConstraintViolation<User>> violations = validator.validate(newUser);
        User user = uc.updateUser(newUser);

        assertNotNull(user);
        assertTrue(violations.isEmpty());
        assertFalse(uc.getAllUsers().size() > 1);
        assertTrue(uc.getAllUsers().contains(user));

        assertEquals(1, user.getId());
        assertEquals(user.getEmail(), newUser.getEmail());
        assertEquals(user.getLogin(), newUser.getLogin());
        assertEquals(user.getName(), newUser.getLogin());
        assertEquals(user.getBirthday(), newUser.getBirthday());
    }

    @Test
    void updateUser_whenUserBirthdayNotPast_returnError() {
        User newUser = new User();
        newUser.setId(1L);
        newUser.setEmail("test2@yandex.ru");
        newUser.setLogin("testLogin2");
        newUser.setName("testName2");
        newUser.setBirthday(LocalDate.now());

        Set<ConstraintViolation<User>> violations = validator.validate(newUser);
        String message = violations.iterator().next().getMessage();

        assertEquals(User.ERROR_USER_BIRTHDAY, message);
        assertFalse(uc.getAllUsers().size() > 1);

    }
}