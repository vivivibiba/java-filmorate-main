package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public final class FilmValidator {

    private static final LocalDate FIRST_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private FilmValidator() {
    }

    public static void validate(Film film) {
        if (film == null) {
            throw new ValidationException("Фильм не передан");
        }

        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Описание фильма не может быть длиннее 200 символов");
        }

        if (film.getReleaseDate() == null) {
            throw new ValidationException("Дата релиза не указана");
        }

        if (film.getReleaseDate().isBefore(FIRST_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        if (film.getDuration() == null || film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }
}
