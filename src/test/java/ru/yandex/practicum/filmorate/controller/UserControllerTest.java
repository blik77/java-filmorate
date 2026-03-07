package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    private UserController uc;
    private Validator validator;

    Errors processErrors(User user) {
        Errors errors = new BeanPropertyBindingResult(user, "user");
        for (ConstraintViolation<User> v : validator.validate(user)) {
            errors.rejectValue(v.getPropertyPath().toString(), "invalid", v.getMessage());
        }
        return errors;
    }

    @BeforeEach
    void setUp() {
        uc = new UserController();
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
        User user = User.builder()
            .id(1L)
            .email("test@yandex.ru")
            .login("testLogin")
            .name("testName")
            .birthday(LocalDate.of(2011, 11, 11))
            .build();

        Errors errors = new BeanPropertyBindingResult(user, "user");
        uc.createUser(user, errors);
    }

    @Test
    void createUser_whenUserValid_returnUser() {
        User newUser = User.builder()
            .email("test2@yandex.ru")
            .login("testLogin2")
            .name("testName2")
            .birthday(LocalDate.of(2011, 11, 11))
            .build();

        Errors errors = new BeanPropertyBindingResult(newUser, "user");
        Set<ConstraintViolation<User>> violations = validator.validate(newUser);
        User user = uc.createUser(newUser, errors);

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
        User newUser = User.builder()
            .email("")
            .login("testLogin2")
            .name("testName2")
            .birthday(LocalDate.of(2011, 11, 11))
            .build();

        Errors errors = processErrors(newUser);
        ValidationException exception = assertThrows(ValidationException.class, () -> uc.createUser(newUser, errors));

        assertEquals(User.ERROR_USER_EMAIL, exception.getMessage());
        assertFalse(uc.getAllUsers().size() > 1);
    }

    @Test
    void createUser_whenUserEmailWrong_returnError() {
        User newUser = User.builder()
            .email("testemail")
            .login("testLogin2")
            .name("testName2")
            .birthday(LocalDate.of(2011, 11, 11))
            .build();

        Errors errors = processErrors(newUser);
        ValidationException exception = assertThrows(ValidationException.class, () -> uc.createUser(newUser, errors));

        assertEquals(User.ERROR_USER_EMAIL, exception.getMessage());
        assertFalse(uc.getAllUsers().size() > 1);
    }

    @Test
    void createUser_whenUserEmailNull_returnError() {
        User newUser = User.builder()
            .email(null)
            .login("testLogin2")
            .name("testName2")
            .birthday(LocalDate.of(2011, 11, 11))
            .build();

        Errors errors = processErrors(newUser);
        ValidationException exception = assertThrows(ValidationException.class, () -> uc.createUser(newUser, errors));

        assertEquals(User.ERROR_USER_EMAIL, exception.getMessage());
        assertFalse(uc.getAllUsers().size() > 1);
    }

    @Test
    void createUser_whenUserLoginEmpty_returnError() {
        User newUser = User.builder()
            .email("test2@yandex.ru")
            .login("")
            .name("testName2")
            .birthday(LocalDate.of(2011, 11, 11))
            .build();

        Errors errors = processErrors(newUser);
        ValidationException exception = assertThrows(ValidationException.class, () -> uc.createUser(newUser, errors));

        assertEquals(User.ERROR_USER_LOGIN, exception.getMessage());
        assertFalse(uc.getAllUsers().size() > 1);
    }

    @Test
    void createUser_whenUserLoginNull_returnError() {
        User newUser = User.builder()
            .email("test2@yandex.ru")
            .login(null)
            .name("testName2")
            .birthday(LocalDate.of(2011, 11, 11))
            .build();

        Errors errors = processErrors(newUser);
        ValidationException exception = assertThrows(ValidationException.class, () -> uc.createUser(newUser, errors));

        assertEquals(User.ERROR_USER_LOGIN, exception.getMessage());
        assertFalse(uc.getAllUsers().size() > 1);
    }

    @Test
    void createUser_whenUserLoginSpace_returnError() {
        User newUser = User.builder()
            .email("test2@yandex.ru")
            .login("test Login2")
            .name("testName2")
            .birthday(LocalDate.of(2011, 11, 11))
            .build();

        Errors errors = processErrors(newUser);
        ValidationException exception = assertThrows(ValidationException.class, () -> uc.createUser(newUser, errors));

        assertEquals(User.ERROR_USER_LOGIN, exception.getMessage());
        assertFalse(uc.getAllUsers().size() > 1);
    }

    @Test
    void createUser_whenUserNameEmpty_returnUser() {
        User newUser = User.builder()
            .email("test2@yandex.ru")
            .login("testLogin2")
            .name("")
            .birthday(LocalDate.of(2011, 11, 11))
            .build();

        Errors errors = new BeanPropertyBindingResult(newUser, "user");
        Set<ConstraintViolation<User>> violations = validator.validate(newUser);
        User user = uc.createUser(newUser, errors);

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
        User newUser = User.builder()
            .email("test2@yandex.ru")
            .login("testLogin2")
            .name(null)
            .birthday(LocalDate.of(2011, 11, 11))
            .build();

        Errors errors = new BeanPropertyBindingResult(newUser, "user");
        Set<ConstraintViolation<User>> violations = validator.validate(newUser);
        User user = uc.createUser(newUser, errors);

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
        User newUser = User.builder()
            .email("test2@yandex.ru")
            .login("testLogin2")
            .name("testName2")
            .birthday(LocalDate.now())
            .build();

        Errors errors = processErrors(newUser);
        ValidationException exception = assertThrows(ValidationException.class, () -> uc.createUser(newUser, errors));

        assertEquals(User.ERROR_USER_BIRTHDAY, exception.getMessage());
        assertFalse(uc.getAllUsers().size() > 1);
    }

    @Test
    void updateUser_whenUserValid_returnUser() {
        User newUser = User.builder()
            .id(1L)
            .email("test2@yandex.ru")
            .login("testLogin2")
            .name("testName2")
            .birthday(LocalDate.of(2011, 11, 11))
            .build();

        Errors errors = new BeanPropertyBindingResult(newUser, "user");
        Set<ConstraintViolation<User>> violations = validator.validate(newUser);
        User user = uc.updateUser(newUser, errors);

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
        User newUser = User.builder()
            .id(2L)
            .email("test2@yandex.ru")
            .login("testLogin2")
            .name("testName2")
            .birthday(LocalDate.of(2011, 11, 11))
            .build();

        Errors errors = processErrors(newUser);
        ValidationException exception = assertThrows(ValidationException.class, () -> uc.updateUser(newUser, errors));

        assertEquals(UserController.ERROR_USER_ID, exception.getMessage());
        assertFalse(uc.getAllUsers().size() > 1);
    }

    @Test
    void updateUser_whenUserEmailEmpty_returnError() {
        User newUser = User.builder()
            .id(1L)
            .email("")
            .login("testLogin2")
            .name("testName2")
            .birthday(LocalDate.of(2011, 11, 11))
            .build();

        Errors errors = processErrors(newUser);
        ValidationException exception = assertThrows(ValidationException.class, () -> uc.updateUser(newUser, errors));

        assertEquals(User.ERROR_USER_EMAIL, exception.getMessage());
        assertFalse(uc.getAllUsers().size() > 1);
    }

    @Test
    void updateUser_whenUserEmailWrong_returnError() {
        User newUser = User.builder()
            .id(1L)
            .email("testemail")
            .login("testLogin2")
            .name("testName2")
            .birthday(LocalDate.of(2011, 11, 11))
            .build();

        Errors errors = processErrors(newUser);
        ValidationException exception = assertThrows(ValidationException.class, () -> uc.updateUser(newUser, errors));

        assertEquals(User.ERROR_USER_EMAIL, exception.getMessage());
        assertFalse(uc.getAllUsers().size() > 1);
    }

    @Test
    void updateUser_whenUserEmailNull_returnError() {
        User newUser = User.builder()
            .id(1L)
            .email(null)
            .login("testLogin2")
            .name("testName2")
            .birthday(LocalDate.of(2011, 11, 11))
            .build();

        Errors errors = processErrors(newUser);
        ValidationException exception = assertThrows(ValidationException.class, () -> uc.updateUser(newUser, errors));

        assertEquals(User.ERROR_USER_EMAIL, exception.getMessage());
        assertFalse(uc.getAllUsers().size() > 1);
    }

    @Test
    void updateUser_whenUserLoginEmpty_returnError() {
        User newUser = User.builder()
            .id(1L)
            .email("test2@yandex.ru")
            .login("")
            .name("testName2")
            .birthday(LocalDate.of(2011, 11, 11))
            .build();

        Errors errors = processErrors(newUser);
        ValidationException exception = assertThrows(ValidationException.class, () -> uc.updateUser(newUser, errors));

        assertEquals(User.ERROR_USER_LOGIN, exception.getMessage());
        assertFalse(uc.getAllUsers().size() > 1);
    }

    @Test
    void updateUser_whenUserLoginNull_returnError() {
        User newUser = User.builder()
            .id(1L)
            .email("test2@yandex.ru")
            .login(null)
            .name("testName2")
            .birthday(LocalDate.of(2011, 11, 11))
            .build();

        Errors errors = processErrors(newUser);
        ValidationException exception = assertThrows(ValidationException.class, () -> uc.updateUser(newUser, errors));

        assertEquals(User.ERROR_USER_LOGIN, exception.getMessage());
        assertFalse(uc.getAllUsers().size() > 1);
    }

    @Test
    void updateUser_whenUserLoginSpace_returnError() {
        User newUser = User.builder()
            .id(1L)
            .email("test2@yandex.ru")
            .login("test Login2")
            .name("testName2")
            .birthday(LocalDate.of(2011, 11, 11))
            .build();

        Errors errors = processErrors(newUser);
        ValidationException exception = assertThrows(ValidationException.class, () -> uc.updateUser(newUser, errors));

        assertEquals(User.ERROR_USER_LOGIN, exception.getMessage());
        assertFalse(uc.getAllUsers().size() > 1);
    }

    @Test
    void updateUser_whenUserNameEmpty_returnUser() {
        User newUser = User.builder()
            .id(1L)
            .email("test2@yandex.ru")
            .login("testLogin2")
            .name("")
            .birthday(LocalDate.of(2011, 11, 11))
            .build();

        Errors errors = new BeanPropertyBindingResult(newUser, "user");
        Set<ConstraintViolation<User>> violations = validator.validate(newUser);
        User user = uc.updateUser(newUser, errors);

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
        User newUser = User.builder()
            .id(1L)
            .email("test2@yandex.ru")
            .login("testLogin2")
            .name(null)
            .birthday(LocalDate.of(2011, 11, 11))
            .build();

        Errors errors = new BeanPropertyBindingResult(newUser, "user");
        Set<ConstraintViolation<User>> violations = validator.validate(newUser);
        User user = uc.updateUser(newUser, errors);

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
        User newUser = User.builder()
            .id(1L)
            .email("test2@yandex.ru")
            .login("testLogin2")
            .name("testName2")
            .birthday(LocalDate.now())
            .build();

        Errors errors = processErrors(newUser);
        ValidationException exception = assertThrows(ValidationException.class, () -> uc.updateUser(newUser, errors));

        assertEquals(User.ERROR_USER_BIRTHDAY, exception.getMessage());
        assertFalse(uc.getAllUsers().size() > 1);

    }
}