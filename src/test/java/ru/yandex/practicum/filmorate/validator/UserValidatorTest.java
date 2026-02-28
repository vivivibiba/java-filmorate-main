package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserValidatorTest {

    @Test
    void shouldPassWhenUserIsValid() {
        User user = makeValidUser();

        assertDoesNotThrow(() -> UserValidator.validate(user));
    }

    @Test
    void shouldThrowWhenEmailIsBlank() {
        User user = makeValidUser();
        user.setEmail(" ");

        assertThrows(ValidationException.class, () -> UserValidator.validate(user));
    }

    @Test
    void shouldThrowWhenEmailHasNoAt() {
        User user = makeValidUser();
        user.setEmail("testmail");

        assertThrows(ValidationException.class, () -> UserValidator.validate(user));
    }

    @Test
    void shouldThrowWhenLoginIsBlank() {
        User user = makeValidUser();
        user.setLogin(" ");

        assertThrows(ValidationException.class, () -> UserValidator.validate(user));
    }

    @Test
    void shouldThrowWhenLoginHasSpaces() {
        User user = makeValidUser();
        user.setLogin("lo gin");

        assertThrows(ValidationException.class, () -> UserValidator.validate(user));
    }

    @Test
    void shouldSetNameToLoginWhenNameIsBlank() {
        User user = makeValidUser();
        user.setName(" ");

        UserValidator.validate(user);

        assertEquals(user.getLogin(), user.getName());
    }

    @Test
    void shouldThrowWhenBirthdayInFuture() {
        User user = makeValidUser();
        user.setBirthday(LocalDate.now().plusDays(1));

        assertThrows(ValidationException.class, () -> UserValidator.validate(user));
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
