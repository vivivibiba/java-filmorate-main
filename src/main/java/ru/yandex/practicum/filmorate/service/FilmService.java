package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film create(Film film) {
        Film createdFilm = filmStorage.create(film);
        log.info("Добавлен фильм с id {}", createdFilm.getId());
        return createdFilm;
    }

    public Film update(Film film) {
        Film updatedFilm = filmStorage.update(film);
        log.info("Обновлён фильм с id {}", updatedFilm.getId());
        return updatedFilm;
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(int id) {
        if (id <= 0) {
            throw new ValidationException("Id фильма должен быть положительным");
        }
        return filmStorage.findById(id);
    }

    public void addLike(int filmId, int userId) {
        Film film = findById(filmId);
        userStorage.findById(userId);

        film.getLikes().add(userId);
        log.info("Пользователь с id {} поставил лайк фильму с id {}", userId, filmId);
    }

    public void removeLike(int filmId, int userId) {
        Film film = findById(filmId);
        userStorage.findById(userId);

        film.getLikes().remove(userId);
        log.info("Пользователь с id {} удалил лайк у фильма с id {}", userId, filmId);
    }

    public List<Film> getPopular(int count) {
        if (count <= 0) {
            throw new ValidationException("Параметр count должен быть положительным");
        }

        return filmStorage.findAll().stream()
                .sorted(Comparator.comparing((Film film) -> film.getLikes().size())
                        .reversed()
                        .thenComparing(Film::getId))
                .limit(count)
                .collect(Collectors.toList());
    }
}
