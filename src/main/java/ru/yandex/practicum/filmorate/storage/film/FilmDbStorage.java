package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film create(Film film) {
        String sql = "INSERT INTO films(name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            film.setId(keyHolder.getKey().intValue());
        }

        updateFilmGenres(film);

        return findById(film.getId());
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films "
                + "SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? "
                + "WHERE film_id = ?";

        int updated = jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        if (updated == 0) {
            return null;
        }

        updateFilmGenres(film);
        return findById(film.getId());
    }

    @Override
    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM films WHERE film_id = ?", id);
    }

    @Override
    public List<Film> findAll() {
        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, "
                + "m.mpa_id, m.name AS mpa_name "
                + "FROM films f "
                + "JOIN mpa m ON f.mpa_id = m.mpa_id "
                + "ORDER BY f.film_id";

        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getInt("film_id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getInt("duration"));

            Mpa mpa = new Mpa();
            mpa.setId(rs.getInt("mpa_id"));
            mpa.setName(rs.getString("mpa_name"));
            film.setMpa(mpa);

            return film;
        });

        for (Film film : films) {
            film.setGenres(getGenresByFilmId(film.getId()));
        }

        return films;
    }

    @Override
    public Film findById(int id) {
        try {
            String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, "
                    + "m.mpa_id, m.name AS mpa_name "
                    + "FROM films f "
                    + "JOIN mpa m ON f.mpa_id = m.mpa_id "
                    + "WHERE f.film_id = ?";

            Film film = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                Film result = new Film();
                result.setId(rs.getInt("film_id"));
                result.setName(rs.getString("name"));
                result.setDescription(rs.getString("description"));
                result.setReleaseDate(rs.getDate("release_date").toLocalDate());
                result.setDuration(rs.getInt("duration"));

                Mpa mpa = new Mpa();
                mpa.setId(rs.getInt("mpa_id"));
                mpa.setName(rs.getString("mpa_name"));
                result.setMpa(mpa);

                return result;
            }, id);

            if (film != null) {
                film.setGenres(getGenresByFilmId(film.getId()));
            }

            return film;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private void updateFilmGenres(Film film) {
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());

        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }

        Set<Integer> genreIds = new HashSet<>();
        for (Genre genre : film.getGenres()) {
            if (genre != null && genre.getId() != null) {
                genreIds.add(genre.getId());
            }
        }

        String sql = "INSERT INTO film_genres(film_id, genre_id) VALUES (?, ?)";
        for (Integer genreId : genreIds) {
            jdbcTemplate.update(sql, film.getId(), genreId);
        }
    }

    private List<Genre> getGenresByFilmId(int filmId) {
        String sql = "SELECT g.genre_id, g.name "
                + "FROM film_genres fg "
                + "JOIN genres g ON fg.genre_id = g.genre_id "
                + "WHERE fg.film_id = ? "
                + "ORDER BY g.genre_id";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, filmId);
    }
}
