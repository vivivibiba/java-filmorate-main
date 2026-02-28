package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Integer, Film> films = new LinkedHashMap<>();
    private int nextId = 1;

    @PostMapping
    public Film create(@RequestBody Film film) {
        FilmValidator.validate(film);

        film.setId(nextId++);
        films.put(film.getId(), film);

        log.info("Добавлен фильм с id {}", film.getId());
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        FilmValidator.validate(film);

        if (film.getId() == null || film.getId() <= 0) {
            throw new ValidationException("Id фильма должен быть положительным");
        }

        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
        }

        films.put(film.getId(), film);

        log.info("Обновлён фильм с id {}", film.getId());
        return film;
    }

    @GetMapping
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }
}
