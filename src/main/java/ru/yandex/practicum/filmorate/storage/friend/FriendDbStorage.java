package ru.yandex.practicum.filmorate.storage.friend;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FriendDbStorage implements FriendStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(int userId, int friendId) {
        String sql = "MERGE INTO friends (user_id, friend_id) KEY (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        jdbcTemplate.update("DELETE FROM friends WHERE user_id = ? AND friend_id = ?", userId, friendId);
    }

    @Override
    public List<Integer> getFriendIds(int userId) {
        String sql = "SELECT friend_id FROM friends WHERE user_id = ? ORDER BY friend_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("friend_id"), userId);
    }

    @Override
    public List<Integer> getCommonFriendIds(int userId, int otherId) {
        String sql = "SELECT f1.friend_id "
                + "FROM friends f1 "
                + "JOIN friends f2 ON f1.friend_id = f2.friend_id "
                + "WHERE f1.user_id = ? AND f2.user_id = ? "
                + "ORDER BY f1.friend_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("friend_id"), userId, otherId);
    }
}
