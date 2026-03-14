package ru.yandex.practicum.filmorate.storage.friend;

import java.util.List;

public interface FriendStorage {

    void addFriend(int userId, int friendId);

    void removeFriend(int userId, int friendId);

    List<Integer> getFriendIds(int userId);

    List<Integer> getCommonFriendIds(int userId, int otherId);
}
