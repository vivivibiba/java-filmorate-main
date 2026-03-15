package ru.yandex.practicum.filmorate.storage.like;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(int filmId, int userId) {
        String sql = "MERGE INTO likes (film_id, user_id) KEY (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        jdbcTemplate.update("DELETE FROM likes WHERE film_id = ? AND user_id = ?", filmId, userId);
    }

    @Override
    public List<Integer> getPopularFilmIds(int count) {
        String sql = "SELECT f.film_id "
                + "FROM films f "
                + "LEFT JOIN likes l ON f.film_id = l.film_id "
                + "GROUP BY f.film_id "
                + "ORDER BY COUNT(l.user_id) DESC, f.film_id "
                + "LIMIT ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("film_id"), count);
    }
}
