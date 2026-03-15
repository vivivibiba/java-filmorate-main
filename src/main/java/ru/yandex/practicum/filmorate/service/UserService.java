package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    public UserService(
            @Qualifier("userDbStorage") UserStorage userStorage,
            FriendStorage friendStorage
    ) {
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
    }

    public User create(User user) {
        fillNameIfBlank(user);
        User createdUser = userStorage.create(user);
        log.info("Создан пользователь с id {}", createdUser.getId());
        return createdUser;
    }

    public User update(User user) {
        getUserOrThrow(user.getId());
        fillNameIfBlank(user);
        User updatedUser = userStorage.update(user);
        log.info("Обновлён пользователь с id {}", updatedUser.getId());
        return updatedUser;
    }

    public void delete(int id) {
        userStorage.delete(id);
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(int id) {
        return getUserOrThrow(id);
    }

    public void addFriend(int userId, int friendId) {
        getUserOrThrow(userId);
        getUserOrThrow(friendId);
        friendStorage.addFriend(userId, friendId);
        log.info("Пользователь {} добавил в друзья пользователя {}", userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        getUserOrThrow(userId);
        getUserOrThrow(friendId);
        friendStorage.removeFriend(userId, friendId);
        log.info("Пользователь {} удалил из друзей пользователя {}", userId, friendId);
    }

    public List<User> getFriends(int userId) {
        getUserOrThrow(userId);
        List<Integer> friendIds = friendStorage.getFriendIds(userId);
        if (friendIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<User> usersFromStorage = userStorage.findAllByIds(friendIds);
        Map<Integer, User> usersById = new HashMap<>();
        for (User user : usersFromStorage) {
            usersById.put(user.getId(), user);
        }

        List<User> result = new ArrayList<>();
        for (Integer friendId : friendIds) {
            User friend = usersById.get(friendId);
            if (friend != null) {
                result.add(friend);
            }
        }
        return result;
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        getUserOrThrow(userId);
        getUserOrThrow(otherId);
        List<Integer> commonFriendIds = friendStorage.getCommonFriendIds(userId, otherId);
        if (commonFriendIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<User> usersFromStorage = userStorage.findAllByIds(commonFriendIds);
        Map<Integer, User> usersById = new HashMap<>();
        for (User user : usersFromStorage) {
            usersById.put(user.getId(), user);
        }

        List<User> result = new ArrayList<>();
        for (Integer friendId : commonFriendIds) {
            User friend = usersById.get(friendId);
            if (friend != null) {
                result.add(friend);
            }
        }
        return result;
    }

    private User getUserOrThrow(int id) {
        User user = userStorage.findById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        return user;
    }

    private void fillNameIfBlank(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
