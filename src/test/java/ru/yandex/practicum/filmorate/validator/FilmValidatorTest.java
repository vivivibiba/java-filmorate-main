package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmValidatorTest {

    @Test
    void shouldPassWhenFilmIsValid() {
        Film film = makeValidFilm();

        assertDoesNotThrow(() -> FilmValidator.validate(film));
    }

    @Test
    void shouldThrowWhenNameIsBlank() {
        Film film = makeValidFilm();
        film.setName(" ");

        assertThrows(ValidationException.class, () -> FilmValidator.validate(film));
    }

    @Test
    void shouldThrowWhenDescriptionIsTooLong() {
        Film film = makeValidFilm();
        film.setDescription("a".repeat(201));

        assertThrows(ValidationException.class, () -> FilmValidator.validate(film));
    }

    @Test
    void shouldPassWhenDescriptionIs200Chars() {
        Film film = makeValidFilm();
        film.setDescription("a".repeat(200));

        assertDoesNotThrow(() -> FilmValidator.validate(film));
    }

    @Test
    void shouldThrowWhenReleaseDateIsBeforeFirstMovieDate() {
        Film film = makeValidFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        assertThrows(ValidationException.class, () -> FilmValidator.validate(film));
    }

    @Test
    void shouldPassWhenReleaseDateIsOnFirstMovieDate() {
        Film film = makeValidFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 28));

        assertDoesNotThrow(() -> FilmValidator.validate(film));
    }

    @Test
    void shouldThrowWhenDurationIsNotPositive() {
        Film film = makeValidFilm();
        film.setDuration(0);

        assertThrows(ValidationException.class, () -> FilmValidator.validate(film));
    }

    private Film makeValidFilm() {
        Film film = new Film();
        film.setName("Test film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);
        return film;
    }
}
