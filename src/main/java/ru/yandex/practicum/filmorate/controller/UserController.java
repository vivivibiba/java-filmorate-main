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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Integer, User> users = new LinkedHashMap<>();
    private int nextId = 1;

    @PostMapping
    public User create(@RequestBody User user) {
        UserValidator.validate(user);

        user.setId(nextId++);
        users.put(user.getId(), user);

        log.info("Создан пользователь с id {}", user.getId());
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        UserValidator.validate(user);

        if (user.getId() == null || user.getId() <= 0) {
            throw new ValidationException("Id пользователя должен быть положительным");
        }

        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        }

        users.put(user.getId(), user);

        log.info("Обновлён пользователь с id {}", user.getId());
        return user;
    }

    @GetMapping
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
}
