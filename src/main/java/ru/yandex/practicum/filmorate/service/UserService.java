package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        setNameToLoginIfBlank(user);
        User createdUser = userStorage.create(user);
        log.info("Создан пользователь с id {}", createdUser.getId());
        return createdUser;
    }

    public User update(User user) {
        setNameToLoginIfBlank(user);
        User updatedUser = userStorage.update(user);
        log.info("Обновлён пользователь с id {}", updatedUser.getId());
        return updatedUser;
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(int id) {
        if (id <= 0) {
            throw new ValidationException("Id пользователя должен быть положительным");
        }
        return userStorage.findById(id);
    }

    public void addFriend(int id, int friendId) {
        if (id == friendId) {
            throw new ValidationException("Нельзя добавить себя в друзья");
        }

        User user = findById(id);
        User friend = findById(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(id);

        log.info("Пользователь с id {} добавил в друзья пользователя с id {}", id, friendId);
    }

    public void removeFriend(int id, int friendId) {
        if (id == friendId) {
            return;
        }

        User user = findById(id);
        User friend = findById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);

        log.info("Пользователь с id {} удалил из друзей пользователя с id {}", id, friendId);
    }

    public List<User> getFriends(int id) {
        User user = findById(id);

        return user.getFriends().stream()
                .map(userStorage::findById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int id, int otherId) {
        User user = findById(id);
        User otherUser = findById(otherId);

        Set<Integer> commonFriends = new LinkedHashSet<>(user.getFriends());
        commonFriends.retainAll(otherUser.getFriends());

        return commonFriends.stream()
                .map(userStorage::findById)
                .collect(Collectors.toList());
    }

    private void setNameToLoginIfBlank(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
