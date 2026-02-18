package ru.yandex.practicum.filmorate.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.Errors;
import ru.yandex.practicum.filmorate.exception.ValidationException;

@Slf4j
public class ModelUtil {
    public static void checkError(Errors errors) {
        if (errors.getFieldErrorCount() > 0) {
            errors.getFieldErrors().forEach(error -> log.error(error.getDefaultMessage()));
            throw new ValidationException(errors.getFieldError().getDefaultMessage());
        }
    }
}
