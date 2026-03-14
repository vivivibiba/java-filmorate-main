package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
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
        return userStorage.create(user);
    }

    public User update(User user) {
        getUserOrThrow(user.getId());
        fillNameIfBlank(user);
        return userStorage.update(user);
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
    }

    public void removeFriend(int userId, int friendId) {
        getUserOrThrow(userId);
        getUserOrThrow(friendId);
        friendStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(int userId) {
        getUserOrThrow(userId);
        List<Integer> friendIds = friendStorage.getFriendIds(userId);
        List<User> friends = new ArrayList<>();
        for (Integer friendId : friendIds) {
            User friend = userStorage.findById(friendId);
            if (friend != null) {
                friends.add(friend);
            }
        }
        return friends;
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        getUserOrThrow(userId);
        getUserOrThrow(otherId);
        List<Integer> commonFriendIds = friendStorage.getCommonFriendIds(userId, otherId);
        List<User> commonFriends = new ArrayList<>();
        for (Integer friendId : commonFriendIds) {
            User friend = userStorage.findById(friendId);
            if (friend != null) {
                commonFriends.add(friend);
            }
        }
        return commonFriends;
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
