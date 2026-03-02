package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UserValidationTest {

    private static Validator validator;

    @BeforeAll
    static void initValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void shouldPassWhenUserIsValid() {
        User user = makeValidUser();

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenEmailIsBlank() {
        User user = makeValidUser();
        user.setEmail(" ");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void shouldFailWhenEmailHasNoAt() {
        User user = makeValidUser();
        user.setEmail("testmail");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void shouldFailWhenLoginIsBlank() {
        User user = makeValidUser();
        user.setLogin(" ");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("login")));
    }

    @Test
    void shouldFailWhenLoginHasSpaces() {
        User user = makeValidUser();
        user.setLogin("lo gin");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("login")));
    }

    @Test
    void shouldFailWhenBirthdayInFuture() {
        User user = makeValidUser();
        user.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("birthday")));
    }

    private User makeValidUser() {
        User user = new User();
        user.setEmail("mail@test.ru");
        user.setLogin("login");
        user.setName("Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        return user;
    }
}
