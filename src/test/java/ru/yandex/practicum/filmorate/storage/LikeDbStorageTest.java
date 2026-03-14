package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class, FilmDbStorage.class, LikeDbStorage.class})
@Sql({"classpath:schema.sql", "classpath:data.sql"})
class LikeDbStorageTest {

    @Autowired
    private UserDbStorage userStorage;

    @Autowired
    private FilmDbStorage filmStorage;

    @Autowired
    private LikeDbStorage likeStorage;

    @Test
    void shouldReturnPopularFilms() {
        int userId = userStorage.create(makeUser("u@test.ru", "u")).getId();

        int filmId1 = filmStorage.create(makeFilm("f1")).getId();
        int filmId2 = filmStorage.create(makeFilm("f2")).getId();

        likeStorage.addLike(filmId2, userId);

        assertThat(likeStorage.getPopularFilmIds(2)).containsExactly(filmId2, filmId1);
    }

    @Test
    void shouldRemoveLike() {
        int userId = userStorage.create(makeUser("u@test.ru", "u")).getId();

        int filmId1 = filmStorage.create(makeFilm("f1")).getId();
        int filmId2 = filmStorage.create(makeFilm("f2")).getId();

        likeStorage.addLike(filmId2, userId);
        likeStorage.removeLike(filmId2, userId);

        assertThat(likeStorage.getPopularFilmIds(2)).containsExactly(filmId1, filmId2);
    }

    private User makeUser(String email, String login) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName(login);
        user.setBirthday(LocalDate.of(2000, 1, 1));
        return user;
    }

    private Film makeFilm(String name) {
        Film film = new Film();
        film.setName(name);
        film.setDescription("d");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);
        return film;
    }
}
